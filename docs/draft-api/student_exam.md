# 学生考试API

## 目录
[1.学生获取考试试卷](#1学生获取考试试卷)  
[2.学生提交考试答案](#2学生提交考试答案)  
[3.获取考试成绩](#3获取考试成绩)  
[4.获取考试成绩总结](#4获取考试成绩总结)  
[5.获取考试知识点掌握情况](#5获取考试知识点掌握情况)  
[6.学生获取指定课程的考试列表](#6学生获取指定课程的考试列表)

## 1.学生获取考试试卷
请求路径/student/exam/exam-paper

请求方法：GET

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
          "student_answer": [
            12
          ]
        }
      ]
    },
    {
      "section_id": 2,
      "questions": [
        {
          "question_id": 2,
          "student_answer": [
            "30"
          ]
        }
      ]
    },
    {
      "section_id": 3,
      "questions": [
        {
          "question_id": 3,
          "student_answer": "机器学习是通过数据训练模型，从而使计算机能够自动识别模式并进行预测。"
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
          "student_answer": [
            12
          ],
          "correct_answer": [
            12
          ],
          "question_stem": "请问1+1=？",
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
          "student_answer": [
            "30"
          ],
          "correct_answer": [
            "365"
          ],
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
          "full_score": 5,
          "student_score": 3,
          "student_answer": "机器学习是通过数据训练模型，从而使计算机能够自动识别模式并进行预测。",
          "answer": "机器学习的基本原理包括数据收集、特征提取、模型选择、训练与验证等步骤。",
          "remark": "答案较为完整，但缺少对模型选择的描述。",
          "question_stem": "请简述机器学习的基本原理。"
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
