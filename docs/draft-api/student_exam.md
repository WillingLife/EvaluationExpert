# 学生考试API

## 目录

## 1.学生获取考试试卷
请求路径/student/exam/exam-paper

请求方法：GET

请求头示例：
```json
{
  "student_id": "学生ID",
  "course_id": "课程ID"
}
```
返回体：

```json
{
  "exam_id": 1,
  "exam_name": "考试名称",
  "exam_notice": "考生注意：\n1.考试答题前，务必将自己的姓名、准考证号用黑色字迹的签字笔或钢笔写在答题卷上",
  "start_time": "2024-01-01T09:00:00",
  "duration_minutes": 120,
  "sections": [
    {
      "section_id": 1,
      "title": "单选题",
      "question_type": "single",
      "question_number": 20,
      "description": "本小节共20题，每题2分，共40分",
      "questions": [
        {
          "question_id": 1,
          "question_stem": "请问1+1=？",
          "score": 2,
          "options": [
            {"option_id": 11, "content": "1"},
            {"option_id": 12, "content": "2"},
            {"option_id": 13, "content": "3"},
            {"option_id": 14, "content": "4"}
          ]
        }
      ]
    },
    {
      "section_id": 2,
      "title": "填空题",
      "question_type": "fill_blank",
      "question_number": 10,
      "description": "本小节共10题，每题2分，共20分",
      "questions": [
        {
          "question_id": 2,
          "score": 2,
          "blank_count": 1,
          "question_stem": "请填写地球绕太阳一周的时间是____天。"
        }
      ]
    },
    {
      "section_id": 3,
      "title": "简答题",
      "question_type": "short_answer",
      "question_number": 5,
      "description": "本小节共5题，共40分",
      "questions": [
        {
          "is_group": false,
          "group_id": null,
          "group_stem": null,
          "questions": [
            {
              "question_id": 3,
              "score": 5,
              "question_stem": "请简述机器学习的基本原理。"
            }
          ]
        },
        {
          "is_group": true,
          "group_id": 1,
          "group_stem": "静夜思\n床前看月光，疑是地上霜。\n举头望明月，低头思故乡。\n阅读以上材料，回答下列问题。",
          "questions": [
            {
              "question_id": 4,
              "score": 5,
              "question_stem": "这首诗的作者是谁？"
            },
            {
              "question_id": 5,
              "score": 10,
              "question_stem": "请分析这首诗的意境。"
            }
          ]
        }
      ]
    }
  ]
}
```


## 2.学生提交考试答案

请求方法：POST

请求路径/student/exam/submit

请求头示例：
```json
{
  "student_id": "学生ID",
  "exam_id": "考试ID"
}
```

请求体示例：
```json
{
  "exam_id": 1,
  "student_id": 1001,
  "start_time": "2024-01-01T09:00:00",
  "submit_time": "2024-01-01T11:30:00",
  "sections": [
    {
      "section_id": 1,
      "questions": [
        {
          "question_id": 1,
          "student_answer": [12]
        }
      ]
    },
    {
      "section_id": 2,
      "questions": [
        {
          "question_id": 2,
          "student_answer": ["30"]
        }
      ]
    },
    {
      "section_id": 3,
      "questions": [
        {
          "is_group": false,
          "group_id": null,
          "questions": [
            {
              "question_id": 3,
              "student_answer": "机器学习是通过数据训练模型，从而使计算机能够自动识别模式并进行预测。"
            }
          ]
        },
        {
          "is_group": true,
          "group_id": 1,
          "questions": [
            {
              "question_id": 4,
              "student_answer": "李白"
            },
            {
              "question_id": 5,
              "student_answer": "这首诗表达了作者对故乡的思念之情，意境优美。"
            }
          ]
        }
      ]
    }
  ]
}
```

## 3.获取考试成绩

请求方法：GET

请求路径/student/exam/score/details

请求头示例：
```json
{
  "student_id": "学生ID",
  "exam_id": "考试ID"
}
```
返回体：

```json
{
  "exam_id": 1,
  "student_id": 1001,
  "exam_name": "考试名称",
  "start_time": "2024-01-01T09:00:00",
  "submit_time": "2024-01-01T11:30:00",
  "grade_time": "2024-01-02T10:00:00",
  "total_score": 85,
  "sections": [
    {
      "section_id": 1,
      "title": "单选题",
      "question_type": "single",
      "question_number": 20,
      "questions": [
        {
          "question_id": 1,
          "full_score": 2,
          "student_score": 2,
          "student_answer": [12],
          "correct_answer": [12],
          "question_stem": "请问1+1=？",
          "options": [
            {"option_id": 11, "content": "1"},
            {"option_id": 12, "content": "2"},
            {"option_id": 13, "content": "3"},
            {"option_id": 14, "content": "4"}
          ]
        }
      ]
    },
    {
      "section_id": 2,
      "question_type": "fill_blank",
      "question_number": 10,
      "description": "本小节共10空，每空2分，共20分",
      "questions": [
        {
          "question_id": 2,
          "full_score": 2,
          "student_score": 0,
          "blank_count": 1,
          "student_answer": ["30"],
          "correct_answer": ["365"],
          "question_stem": "请填写地球绕太阳一周的时间是____天。"
        }
      ]
    },
    {
      "section_id": 3,
      "title": "简答题",
      "question_type": "short_answer",
      "question_number": 5,
      "description": "本小节共5题，共40分",
      "questions": [
        {
          "is_group": false,
          "group_id": null,
          "group_stem": null,
          "questions": [
            {
              "question_id": 3,
              "full_score": 5,
              "student_score": 3,
              "student_answer": "机器学习是通过数据训练模型，从而使计算机能够自动识别模式并进行预测。",
              "answer": "机器学习的基本原理包括数据收集、特征提取、模型选择、训练与验证等步骤。",
              "remark": "答案较为完整，但缺少对模型选择的描述。",
              "question_stem": "请简述机器学习的基本原理。"
            }
          ]
        },
        {
          "is_group": true,
          "group_id": 1,
          "group_stem": "静夜思\n床前看月光，疑是地上霜。\n举头望明月，低头思故乡。\n阅读以上材料，回答下列问题。",
          "questions": [
            {
              "question_id": 4,
              "full_score": 5,
              "student_score": 5,
              "student_answer": "李白",
              "answer": "李白",
              "remark": "回答正确。",
              "question_stem": "这首诗的作者是谁？"
            },
            {
              "question_id": 5,
              "full_score": 10,
              "student_score": 7,
              "student_answer": "这首诗表达了作者对故乡的思念之情，意境优美。",
              "answer": "诗中通过描写月光和霜的意象,表达了作者对故乡的深切思念，意境清幽。",
              "remark": "答案抓住了意境，但缺少具体意象的分析。",
              "question_stem": "请分析这首诗的意境。"
            }
          ]
        }
      ]
    }
  ]
}
```

## 4.获取考试成绩总结

请求方法：GET

请求路径/student/exam/score/summary

请求头示例：
```json
{
  "student_id": "学生ID",
  "exam_id": "考试ID"
}
```
返回体：

```json
{
  "exam_id": 1,
  "student_id": 1001,
  "exam_name": "考试名称",
  "start_time": "2024-01-01T09:00:00",
  "submit_time": "2024-01-01T11:30:00",
  "grade_time": "2024-01-02T10:00:00",
  "total_score": 85,
  "sections": [
    {
      "section_id": 1,
      "title": "单选题",
      "question_type": "single",
      "section_score": 20
    }
  ]
}
```

## 5.获取考试知识点掌握情况

请求方法：GET

请求路径/student/exam/knowledge-mastery

请求体示例：
```json
{
  "student_id": "学生ID",
  "exam_id": "考试ID"
}
```

返回体：

```json
{
  "student_id": "学生ID",
  "exam_id": "考试ID",
  "knowledge_points": [
    {
      "node_id": 101,
      "node_name": "知识点名称",
      "node_mastery_score": 85.5
    }
  ]
}
```

## 6.学生获取指定课程的考试列表

请求方法: GET

请求路径：/student/exam/list

请求体：

```json
{
  "student_id": 1,
  "course_id": 1,
  "list": [
    {
      "exam_id": 1,
      "exam_name": "考试名称",
      "status": "graded",
      "score": 89
    }
  ]
}

```
