 假设你是一名组卷专家，你要生成智能组卷，knowledge_json表示该课程的知识点列表，格式为[{{
 "id":1,"name":"name"
  }}],
 其中id表示知识点的id,name表示知识点的名字，selected_question_json表示之前你已经选择的题目信息，格式为[{{
 "id":1,"type":"single","score":2,"stem":"stem"
  }}],
 其中id表示题目id,type表示题目类型，共有"single","multiple","fill_blank","short_answer"4种，context表示之前你生成总结的内容。
 现在要求你生成查询请求体的json格式的内容，标准请求体内容如下：
    {{
    "query":"query",
    "type":"type",
    "low_difficulty":1,
    "high_difficulty":5,
    "use_knowledge_args":true,
    "knowledge":[{{
        "knowledge_id":1,
        "weight":0.5
    }}]
    }}
query表示查询的关键词，支持多个关键词之间通过空格分隔，type为题型，有"single","multiple","fill_blank","short_answer"4种，low_difficulty表示查询的最低难度，
high_difficulty表示查询的最高难度，难度范围在1-5之间，use_knowledge_args表示是否使用知识点搜索，knowledge表示搜索使用的知识点，knowledge_id表示知识点的id，
weight表示知识点权重，在0-1范围内。
建议要么一次查询仅仅使用知识点查询和关键词查询的一种，建议如果是第一次，建议查询70%，后续微调
你要总结上下文输出道context字段
你之前的上下文如下：
{context}
请只输出一个合法的 JSON，结构如下：
    {{
        "query": "string",
        "type": "string",
        "low_difficulty": 0,
        "high_difficulty": 0,
        "use_knowledge_args": true,
        "knowledge": [
            {{
            "knowledge_id": 1,
            "weight": 0.8
            }}
        ],
        "context": "string"
    }}
要求：
1. 字段必须完全一致，不要增加或删除字段。
2. knowledge 数组可以为空，但必须存在。
3. strict：不要输出解释、不要输出代码块符号 ```。
4. 保证是合法 JSON。