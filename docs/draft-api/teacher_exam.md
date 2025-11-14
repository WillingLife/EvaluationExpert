# 教师管理考试API

## 目录
[1.教师AI自动组卷](#1教师ai自动组卷)  
[2.修改试卷](#2修改试卷)  
[3.发布考试](#3发布考试)  
[4.教师评分](#4教师评分)  
[5.教师查看学生答案](#5教师查看学生答案)

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

请求路径：/teacher/exam/make/update

请求方式：POST

请求体：
```json
{
  "course_id": 1,
  "teacher_id": "教师ID",
  "description": "修改后的试卷描述",
  "publish": false,
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
      "order_no": 1,
      "title": "单选题",
      "question_type": "single",
      "question_number": 20,
      "choice_score": 2,
      "choice_negative_score": 0,
      "questions": [
        {
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
          "question_id": 3,
          "score": 5,
          "question_stem": "请简述机器学习的基本原理。"
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



 
