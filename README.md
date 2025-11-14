# 开发用简略API文档

- [查看作业API](docs/draft-api/assignment.md)

- [查看题库管理API](docs/draft-api/question_manage.md)  

- [查看学生考试API](docs/draft-api/student_exam.md)  

- [查看教师考试API](docs/draft-api/teacher_exam.md)

---

## 更新记录：
- 2025/11/14： 大幅简化试题API部分，取消组合题型
- 2025/11/14: 删除题库管理API中教师新增题目API，当type为填空题时，details中去掉answer_rule_type字段
[跳转](docs/draft-api/question_manage.md#1教师新增题目)
- 2025/11/14：勘误，修改学生考试API中学生提获取考卷请求体示例,原先`course_id`字段改为`exam_id`字段
[跳转](docs/draft-api/student_exam.md#1学生获取考试试卷)
- 2025/11/14:为简化后端实现，学生考试API中学生提交考试答案API在请求体中`sections`部分新增`question_type`字段。删除无用部分
[跳转](docs/draft-api/student_exam.md#2学生提交考试答案)