# 教师管理考试API

## 目录
- [1.教师AI自动组卷](#1教师ai自动组卷)  
- [2.修改试卷](#2修改试卷)  
- [3.发布考试](#3发布考试)  
- [4.教师评分](#4教师评分)  
- [5.教师查看学生答案](#5教师查看学生答案)
- [6.教师获取指定课程的所有试卷](#6教师获取指定课程的所有试卷)

## 1.教师AI自动组卷

请求路径：`teacher/exam/make/ai-generate`

请求方法：POST

请求头示例：
```json
{
  "teacher_id": "教师ID",
  "course_id": "课程ID",
  "knowledge": [
    {
      "name": "知识点A",
      "weight": 0.5
    }
  ],
  "difficulty_level": "medium",
  "total_score": 150,
  "details": {
    "single_choice": {
      "question_number": 20,
      "score_per_question": 2,
      "all_score": 40
    },
    "multi_choice": {
      "question_number": 10,
      "score_per_question": 3,
      "all_score": 30
    },
    "fill_blank": {
      "question_number": 10,
      "score_per_question": 2,
      "all_score": 20
    },
    "short_answer": {
      "question_number": 5,
      "score_per_question": null,
      "all_score": 60
    }
  }
}
```


返回体：


```json
{
  "course_id": 1,
  "knowledge": [
    {
      "name": "知识点A",
      "weight": 0.5
    }
  ],
  "difficulty_level": "medium",
  "total_score": 150,
  "exam_name": "考试名称",
  "exam_notice": "考生注意：\n1.考试答题前，务必将自己的姓名、准考证号用黑色字迹的签字笔或钢笔写在答题卷上",
  "sections": [
    {
      "order_number": 1,
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
            {
              "option_id": 11,
              "content": "1"
            },
            {
              "option_id": 12,
              "content": "2"
            },
            {
              "option_id": 13,
              "content": "3"
            },
            {
              "option_id": 14,
              "content": "4"
            }
          ]
        }
      ]
    },
    {
      "order_number": 2,
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
      "order_number": 3,
      "title": "简答题",
      "question_type": "short_answer",
      "question_number": 5,
      "description": "本小节共5题，共40分",
      "questions": [
        {
          "question_id": 3,
          "score": 5,
          "question_stem": "请简述机器学习的基本原理。"
        }
      ]
    }
  ]
}
```


## 2.修改试卷

**📌 Important Notice:** 在AI自动组卷后，返回的数据不携带`exam_id`，当教师在前端使用AI组卷或创建新试卷自己组卷时，向后端保存试卷信息时，`exam_id`为空，后端会自己创建新exam；
当教师修改已存在的试卷时，需携带对应的`exam_id`。

当老师修改题目时，前端应该先向后端发送新增题目请求，获取新的`question_id`，再将新的`question_id`传给修改试卷接口。

请求路径：/teacher/exam/make/save

请求方式：POST

请求体：
```json
{
  "exam_id": "考试ID，可以为空",
  "course_id": 1,
  "teacher_id": "教师ID",
  "description": "修改后的试卷描述",
  "exam_name": "考试名称",
  "exam_notice": "考生注意：\n1.考试答题前，务必将自己的姓名、准考证号用黑色字迹的签字笔或钢笔写在答题卷上",
  "start_time": "2024-01-01T09:00:00",
  "duration_minutes": 120,
  "total_score": 150,
  "pass_score": 90,
  "shuffle_questions": false,
  "shuffle_options": false,
  "version": 1,
  "sections": [
    {
      "section_id": null,
      "order_no": 1,
      "title": "单选题",
      "question_type": "single",
      "question_number": 20,
      "choice_score": 2,
      "choice_negative_score": 0,
      "questions": [
        {
          "exam_item_id": null,
          "question_id": 1
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
          "score": 2
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
          "question_id": 3,
          "score": 5
        }
      ]
    }
  ]
}
```


## 3.发布考试
请求路径：`teacher/exam/publish`

请求方法：POST

请求体示例：
```json
{
  "teacher_id": "教师ID",
  "course_id": 101,
  "exam_id": 1001,
  "start_time": "2024-01-01T09:00:00",
  "duration_minutes": 120,
  "class_ids": [201, 202, 203]
}
```

## 4.教师评分

请求路径：`teacher/exam/grade/submit`

请求方法：POST

请求头示例：
```json
{
  "teacher_id": "教师ID",
  "exam_score_id": "考试评分ID",
  "grades": [
    {
      "question_id": 3,
      "score": 4,
      "remark": "答案较为完整，但缺少对模型选择的描述。"
    }
  ]
}
```

## 5.教师查看学生答案

请求路径：`teacher/exam/grage/student-answers`

请求方法：GET

请求体示例：
```json
{
  "teacher_id": "教师ID",
  "exam_id": "考试ID",
  "student_id": "学生ID"
}
```


返回体示例：
```json
{
  "questions": [
    {
      "question_id": 3,
      "full_score": 5,
      "student_score": 3,
      "student_answer": "机器学习是通过数据训练模型，从而使计算机能够自动识别模式并进行预测。",
      "answer": "机器学习的基本原理包括数据收集、特征提取、模型选择、训练与验证等步骤。",
      "criteria": "评分标准内容"
    }
  ]
}
```

## 6.教师获取指定课程的所有试卷
请求路径：`teacher/exam/get-all`

请求方法：GET

请求体示例：
```json
{
  "teacher_id": "教师ID",
  "course_id": "课程ID"
}
```


返回体示例：
```json
{
  "exams": [
    {
      "exam_id": 1001,
      "exam_name": "期中考试",
      "description": "这是期中考试的试卷描述",
      "total_score": 150,
      "start_time": "2024-01-01T09:00:00",
      "duration_minutes": 120,
      "status": "draft",
      "create_time": "2024-10-01T10:00:00",
      "update_time": "2024-10-05T15:30:00",
      "classes": [
        {
          "class_id": 201,
          "class_name": "计算机科学与技术2021级1班"
        }
      ]
    }
  ]
}
```

`status`包括draft（草稿）、published（已发布）、completed（已结束）、graded（已评分）等状态。


 
