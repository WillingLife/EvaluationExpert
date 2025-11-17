### 创建 question document（基础版本）

默认索引结构，用于存储题干/答案原文以及分片后的向量。适合只需要 IK 分词、向量检索的场景。

```
PUT /question_bank
{
  "settings": {
    "index": {
      "number_of_shards": 1,
      "number_of_replicas": 1
    }
  },
  "mappings": {
    "properties": {
      "question_text": {
        "type": "text",
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart"
      },
      "question_vector_chunks": {
        "type": "nested",
        "properties": {
          "chunk_id": { "type": "integer" },
          "chunk_text": { "type": "text" },
          "chunk_vector": {
            "type": "dense_vector",
            "dims": 1024,
            "index": true,
            "similarity": "cosine"
          }
        }
      },
      "answer_text": {
        "type": "text",
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart"
      },
      "answer_vector_chunks": {
        "type": "nested",
        "properties": {
          "chunk_id": { "type": "integer" },
          "chunk_text": { "type": "text" },
          "chunk_vector": {
            "type": "dense_vector",
            "dims": 1024,
            "index": true,
            "similarity": "cosine"
          }
        }
      },
      "course_id": { "type": "keyword" },
      "difficulty": { "type": "float" },
      "author_id": { "type": "keyword" },
      "type": { "type": "keyword" }
    }
  }
}
```

### 扩展：为 question/answer 添加同义词字段(最终版)

引入 multi-field 并自定义 `ik_synonym` analyzer（基于 IK + 同义词 token filter）。查询时可对 `question_text.syn`、`answer_text.syn` 发 `match` 即可启用同义词策略，而不会影响默认字段。

```
PUT /question_bank
{
  "settings": {
    "analysis": {
      "filter": {
        "sc_synonyms": {
          "type": "synonym_graph",
          "synonyms_path": "analysis/synonyms.txt"
        }
      },
      "analyzer": {
        "ik_synonym": {
          "tokenizer": "ik_smart",
          "filter": ["lowercase", "sc_synonyms"]
        }
      }
    },
    "index": {
      "number_of_shards": 1,
      "number_of_replicas": 1
    }
  },
  "mappings": {
    "properties": {
      "question_text": {
        "type": "text",
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart",
        "fields": {
          "syn": {
            "type": "text",
            "analyzer": "ik_synonym",
            "search_analyzer": "ik_synonym"
          }
        }
      },
      "answer_text": {
        "type": "text",
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart",
        "fields": {
          "syn": {
            "type": "text",
            "analyzer": "ik_synonym",
            "search_analyzer": "ik_synonym"
          }
        }
      },
      "question_vector_chunks": {
        "type": "nested",
        "properties": {
          "chunk_id": { "type": "integer" },
          "chunk_text": { "type": "text" },
          "chunk_vector": {
            "type": "dense_vector",
            "dims": 1024,
            "index": true,
            "similarity": "cosine"
          }
        }
      },
      "answer_vector_chunks": {
        "type": "nested",
        "properties": {
          "chunk_id": { "type": "integer" },
          "chunk_text": { "type": "text" },
          "chunk_vector": {
            "type": "dense_vector",
            "dims": 1024,
            "index": true,
            "similarity": "cosine"
          }
        }
      },
      "course_id": { "type": "keyword" },
      "difficulty": { "type": "float" },
      "author_id": { "type": "keyword" },
      "type": { "type": "keyword" }
    }
  }
}
```

### 查询示例：一次请求组合精确/同义词/模糊策略

service 层根据入参决定是否把“同义词”“模糊”子查询加入 `should` 列表：

```
POST /question_bank/_search
{
  "query": {
    "bool": {
      "filter": [
        { "term": { "course_id": "CS101" } }
      ],
      "should": [
        { "match": { "question_text": { "query": "哈希表 冲突", "boost": 2 } } },
        { "match": { "question_text.syn": { "query": "哈希表 冲突", "boost": 1 } } },
        { "match": { "question_text": { "query": "哈希表 冲突", "fuzziness": "AUTO", "prefix_length": 1, "boost": 0.5 } } }
      ],
      "minimum_should_match": 1
    }
  },
  "highlight": {
    "fields": {
      "question_text": {},
      "answer_text": {}
    }
  }
}
```

- `should[0]`：基础 IK 查询；
- `should[1]`：同义词字段；
- `should[2]`：模糊查询（只在需要时加入，未启用就去掉这一项）。

根据业务开关增删子查询，就能在一次请求里完成多策略混合召回和高亮。
bge-rerank
MonoT5
BERT cross-encoder