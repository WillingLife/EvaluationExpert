# 作业API

## 目录

[1. 教师新增作业](#1教师新增作业)

[2. 教师修改作业](#2教师修改作业)

[3. 学生提交作业](#3学生提交作业)

[4. 学生查看作业个性化的评语和建议](#4学生查看作业个性化的评语和建议)

[5. 教师评价学生作业](#5教师评价学生作业)

[6. 教师某个课程的作业列表](#6教师某个课程的作业列表)

[7. 教师删除作业](#7教师删除作业)

[8. 学生获取某个课程的作业列表](#8学生获取某个课程的作业列表)

[9. 教师获取学生作业](#9教师获取学生作业)

[10. 教师润色作业要求](#10教师润色作业要求)

## 1.教师新增作业

请求路径: /teacher/assignment/add

请求方法：POST

请求体：
```json
{
  "teacher_id": 1,
  "course_id":1,
  "name": "作业名称",
  "description": "描述",
  "submit_limit": 2,
  "deadline": "2024-12-31T23:59:59Z"
}
```

返回体：
```json
{
  "assignment_id": 1
}
```

## 2.教师修改作业

请求路径: /teacher/assignment/update

请求方法：POST

请求体：
```json
{
  "assignment_id": 1,
  "name": "修改后的作业名称",
  "description": "修改后的描述",
  "submit_limit": 3,
  "deadline": "2025-01-15T23:59:59Z"
}
```

## 3.学生提交作业

请求路径：/student/assignment/submit

请求方法：POST

请求体：
part1:
Content-Type:application/json
```json
{
  "student_id": "学生ID",
  "assignment_id": "作业ID",
  "submission_time": "2024-12-15T10:00:00Z",
  "file_name": "homework1.pdf",
  "file_type": "application/pdf"
}
```
part2:
Content-Type:application/octet-stream
（二进制文件流）

返回：
```json
{
  "assignment_score_id": 1,
  "file_url": "https://example.com/submissions/homework1.pdf"
}
```



## 4.学生查看作业个性化的评语和建议

请求路径：/student/assignment/feedback

请求方法：GET

请求头示例：
```json
{
  "student_id": "学生ID",
  "assignment_id": "作业ID"
}
```

返回体

```json
{
  "assignment_score_id": 1,
  "dimensions": [
    {
      "name":"内容相关性",
      "score": 8,
      "remark": "内容较为贴切，但缺少具体实例支持。"
    }
  ],
  "ai-remark": "整体表现良好，建议增加更多实际案例分析以增强说服力。",
  "teacher-remark": "请注意逻辑结构的清晰度，适当增加段落衔接。"
}
```

## 5.教师评价学生作业

请求路径：/teacher/assignment/grade

请求方法：POST

请求体：
```json
{
  "teacher_id": "教师ID",
  "assignment_score_id": "作业评分ID",
  "score": 95,
  "teacher_remark": "优秀的作业，继续保持！"
}
```

## 6.教师某个课程的作业列表

请求路径：/teacher/assignment/list

请求方法：GET

请求体：
```json
{
  "teacher_id": 1,
  "course_id": 1
}
```

返回体：
```json
{
  "assignments": [
    {
      "assignment_id": 1,
      "name": "作业名称",
      "status": "open",
      "deadline": "2024-12-31T23:59:59Z"
    }
  ]
}
```

`status`见后端具体实现

## 7.教师删除作业

请求路径:/teacher/assignment/delete

请求方法：DELETE

请求体：
```json
{
  "teacher_id": 1,
  "course_id": 1,
  "assignment_id": 1
}
```

后端删除时如果`status`有要求可以实现

## 8.学生获取某个课程的作业列表

请求路径：/student/assignment/list

请求方法：GET

请求体：
```json
{
  "student_id": 1,
  "course_id": 1
}
```

返回体：
```json
{
  "assignments": [
    {
      "assignment_id": 1,
      "name": "作业名称",
      "status": "open",
      "deadline": "2024-12-31T23:59:59Z"
    }
  ]
}
```

## 9.教师获取学生作业

请求路径：/teacher/assignment/get

请求方法：GET

请求体：
```json
{
  "student_id": 1,
  "assignment_id":1
}
```

返回体：
```json
{
  "code":200,
  "msg":""
  "data": [
    {
      "name": "作业名称",
      "description": "作业内容"
      "assignment_url":"文档地址"
    }
  ]
}
```
## 10.教师润色作业要求

请求路径: `teacher/assignment/polish`

请求方法：POST

请求体：
```jaon
{
    "assignment":"我想让学生画一张甘特图，使用敏捷和迭代的思路画"
}
```

返回体：
```
{
    "code": 200,
    "msg": null,
    "data": "作业题目"
}
```
