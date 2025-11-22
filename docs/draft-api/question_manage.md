# 题库管理API

## 目录
[1.教师新增题目](#1教师新增题目)  
[2.教师查询题目](#2教师查询题目)  
[3.教师修改题目](#3教师修改题目)  
[4.教师删除题目](#4教师删除题目)

[5.教师查询题目（详细版）](#5教师查询题目详细版)

---
## 1.教师新增题目
请求路径：`teacher/question/add`

请求方法：`POST`

请求体示例：
```json
{
  "teacher_id": 1,
  "course_id": 101,
  "type": "short_answer",
  "stem": "题干",
  "difficulty": 5,
  "analysis": "解析内容",
  "details": {}
}
```

当为单选/多选题时details为：
```json
{
    "options": [
        {"content": "选项A", "correct": true,"sort_order":1},
        {"content": "选项B", "correct": false,"sort_order":2},
        {"content": "选项C", "correct": true,"sort_order":3},
        {"content": "选项D", "correct": false,"sort_order":4}
    ]
}
```

当为填空题时details为：

```json
{
  "blank_count": 2,
  "blanks": [
    {
      "blank_index": 1,
      "answer": ["答案1","备选答案1"]
    }
  ]
}
```

当为简答题时details为：

```json
{
  "answer": "参考答案内容",
  "criteria": "评分标准内容"
}
```
---

## 2.教师查询题目

请求路径：`teacher/question/query`

请求方法：`GET`

请求体实例：
```json
{
  "course_id": 101,
  "type": "single",
  "difficulty": "3",
  "query_text": ["关键词","关键词"],
  "precision": true,
  "pageSize": 10,
  "pageNum": 1
}
```

响应体示例（details见上）：
```json
{ "list": [
  {
    "question_id": 1001,
    "course_id": 101,
    "type": "short_answer",
    "stem": "题干",
    "difficulty": 3,
    "analysis": "解析内容",
    "details": {}
  }
],
  "total": 50,
  "pageNum": 1,
  "pageSize": 10,
  "totalPages": 5
}
```

## 3.教师修改题目
请求路径：`teacher/question/update`

请求方法：`PUT`

请求体实例：
```json
{
  "question_id": 1001,
  "stem": "修改后的题干",
  "difficulty": 3,
  "analysis": "修改后的解析内容",
  "details": {}
}
```

---

## 4.教师删除题目
请求路径：`teacher/question/delete`

请求方法：`DELETE`

请求体实例：
```json
{
  "question_id": 1001
}
```

## 5.教师查询题目（详细版）

请求路径: `/teacher/question/query-advance`

请求方法：`POST`

请求体:
```json
{
    "course_id": 1,
    "query": "项目经理",
    "synonymy":false,
    "fuzzy":false,
    "type":null,
    "low_difficulty":null,
    "high_difficulty":null,
    "search_answer":false,
    "use_vector":true,
    "author_id":null,
    "use_knowledge_args":true,
    "knowledge":[],
    "reranker":
    {
        "rerank_strategy":"wighted_fusion",
        "keyword_weight":1.0,
        "vector_weight":2.0,
        "knowledge_weight":0.0,
        "cf_score":0.0
    },
    "page_number":11
}
```

`synonymy`表示是否启用同义词查询

`fuzzy`表示是否使用模糊查询

`search_answer`表示是否查询答案相关信息

`use_vector`表示是否使用向量进行增强检索

`useKnowledge_args`表示是否根据知识点查询

`knowledge`列表存放{"knowledge_id":1,"weight":0.5}的json对象

`rerank_strategy`表示reranker模式现在支持2种模式：
- `wighted_fusion`加权融合模式
- `wrrf`加权倒数排名融合模式
  
注：`cf_score`暂时未实现

返回体：
```json
{
"list": [
  {
    "question_id": 1001,
    "course_id": 101,
    "type": "short_answer",
    "stem": "题干",
    "difficulty": 3,
    "analysis": "解析内容",
    "details": {},
    "vector_get": false,
    "knowledge_get": false,
    "cf_get": false
  }
],
"has_more": false
}
```

details与上方兼容

在stem字段中，部分检索结果使用```<em>项目经理</em>```进行高亮，前端注意

`vector_get`表示该检索结果是否是向量检索升级得到的（关键词检索无法得到），前端需要标识

`knowledge_get`表示该检索结果是否是知识点检索升级得到的（关键词检索无法得到），前端需要标识









