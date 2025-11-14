# 题库管理API

## 目录
[1.教师新增题目](#1教师新增题目)  
[2.教师查询题目](#2教师查询题目)  
[3.教师修改题目](#3教师修改题目)  
[4.教师删除题目](#4教师删除题目)

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
      "answer_rule_type": "text",
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

当为组合题时details为：

```json
{
  "questions": [1001,1002,1003]
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





