# 考卷（Exam）数据库表结构

## 1. 设计说明
- 主键与逻辑外键均为 `bigint`（自增或分布式 ID 皆可）
- 不抽题：考卷题目由人工指定，发布后不随规则变化
- 每份考卷的题目唯一：同一考卷内禁止重复题目（见唯一索引）
- 仅保留逻辑外键：只保存对方表的 `id`（`bigint`），数据库层面不建外键约束
- 统一审计与软删字段：`created_at`/`updated_at`/`deleted`（不使用以 `is` 开头的字段）
- 题型覆盖：与题库一致，支持单选、多选、填空、简答（含组合）、画图五大类；考卷支持任意排列组合与顺序变更。
- 评分策略：分值与评分完全由考卷维度维护；多选题支持多种得分策略，支持分节默认与题目级覆盖。
- 顶部信息：考卷顶部的注意事项与参考数据由独立表维护（不改动主表）。

## 2. 表清单
- `exam`：考卷主表（元数据）【保持不变】
- `exam_class`：考卷与班级映射表（多对多）【新增】
- `exam_section`：考卷分卷/大题块（分组、排序、默认多选计分策略）【字段扩展】
- `exam_item`：考卷与题目的明细（静态选题、题目级多选计分策略、组合题映射）
- `exam_score`：学生考试成绩表
- `exam_score_item`：学生题目成绩明细表

### 2.1 `exam`（考卷主表）
- `id` bigint，主键
- `name` string，考卷名称
- `description` string，可选
- `notice` text，注意事项（富文本可选）
- `course_id` bigint，逻辑外键（课程）；逻辑外键：`course_id → course.id`
- `total_score` decimal(8,2)，总分（发布或保存时校验 = 明细之和）
- `duration_minutes` int，考试时长（分钟）
- `start_time` datetime，考试开始时间
- `pass_score` decimal(8,2)，及格线，可选
- `shuffle_questions` boolean，是否乱序题目（呈现层）
- `shuffle_options` boolean，是否乱序选项（仅选择题）
- `status` string，`draft`/`published`/`archived`
- `version` int，版本号
- `creator` bigint，逻辑外键（创建者）；逻辑外键：`creator → user.id`
- `create_time` datetime
- `update_time` datetime
- `deleted` boolean


### 2.2 `exam_class` （考卷 班级映射表）
- `exam_id` bigint，逻辑外键：`exam_id → exam_paper.id`
- `class_id` bigint，逻辑外键：`class_id → class.id`



### 2.3 `exam_section`（分组/大题块）
- `id` bigint，主键
- `exam_id` bigint，逻辑外键：`exam_id → exam.id`
- `title` string，分组/大题标题（前端展示可自定义，如“单选题”“高分单选题”）
- `question_type` string，题型编码（如 `single`/`multiple`/`fill_blank`/`short_answer`，四大题型之一）
- `description` string，可选
- `choice_score` decimal(8,2)，如果为选择题（单选/多选），则该题型每题分值
- `order_no` int，显示顺序（从 1 递增，控制五大题型或自定义块的顺序）
- `note` string，可选
- `choice_negative_score` decimal(8,2)，选择题错题扣分，默认 0，可选
- `multiple_strategy` string，多选题计分策略（本节级），可选；见“多选计分策略”枚举
- `multiple_strategy_conf` json，策略参数（如漏选扣分、比例/保留小数/最小分等），可选
- `creator` bigint，逻辑外键：`creator → user.id`
- `create_time` datetime
- `update_time` datetime
- `deleted` boolean



说明：
- 用于支持“五大题进行拆分，前端只看到这种拆分，支持自定义名称”。不同分值策略可通过分节与题目级覆盖实现。

### 2.4 `exam_item`（考卷题目明细，静态选题）
- `id` bigint，主键
- `section_id` bigint，逻辑外键：`section_id → exam_section.id`
- `question_id` bigint，逻辑外键：`question_id → question.id`
- `question_type` string，冗余题型编码（便于渲染/校验）
- `score` decimal(8,2)，该题在本卷中的分值，选择题可以不填写（使用分节 `choice_score`）
- `order_no` int，题目顺序（从 1 递增，节内排序；最终顺序=分节 `order_no` + 题目 `order_no`）
- `required` boolean，是否必答
- `negative_score` decimal(8,2)，错题扣分，默认 0，可选
- `metadata_json` json，额外渲染/展示配置，可选


可选的额外唯一性（若要求题目不能被任何其它考卷复用）：
- 全局唯一：`uk_item_question_global(question_id)`

多选计分策略（建议枚举及含义）：
- `all_or_nothing_if_miss`：存在漏选或错选即不得分；全对得满分。
- `fixed_on_miss`：存在任意漏选时得固定分（参数：`score_on_any_miss`）。
- `proportional`：按选对比例计分=满分×(选对数/正确数)；可配置保留小数、最小/最大分等。
- `deduct_per_miss`：按漏选数逐个扣分（参数：`miss_deduct_per`，可叠加最小分）。
- `deduct_per_wrong`：按错选数逐个扣分（参数：`wrong_deduct_per`，可叠加最小分）。
- `custom`：自定义公式或规则（由 `*_strategy_conf` 携带）。

优先级：分节级 `default_multiple_strategy` > 系统默认（建议为 `all_or_nothing_if_miss`）。

参数建议（`*_strategy_conf` 示例键）：
- `score_on_any_miss`、`miss_deduct_per`、`wrong_deduct_per`、`min_score`、`max_score`、`rounding`（`ceil|floor|round`）、`scale_precision`。

详细解释：

1.存在漏选或错选即不得分；全对得满分
```json
{
  "multiple_strategy":"all_or_nothing_if_miss"
}
```
2.如果学生的选择是正确答案的子集（即只有漏选，没有错选），则得到一个固定的分数。如果存在错选，则为0分。
```json
{
  "multiple_strategy":"fixed_on_miss",
  "multiple_strategy_conf": {
    "score_on_any_miss": 2
  }
}
```
3.按比例计分

含义: 根据选对的选项数量占正确答案总数的比例来计算得分。

配置:
- min_score (number, 可选): 该策略下的最低得分，默认为0。
- max_score (number, 可选): 该策略下的最高得分，默认为题目满分。
- rounding (string, 可选): 小数处理方式，可以是 ceil (向上取整), floor (向下取整), 或 round (四舍五入)。
- scale_precision (integer, 可选): 保留的小数位数。

```json
{
  "multiple_strategy": "proportional",
  "multiple_strategy_conf": {
    "min_score": 0,
    "max_score": 5,
    "rounding": "floor",
    "scale_precision": 0
  }
}
```
计算函数:`min(max_score, max(min_score, round(满分 * (选对选项数 / 正确选项总数), scale_precision)))`

4.按漏选扣分

含义: 每漏选一个正确选项，就从满分中扣除一定的分数。

配置:
- `miss_deduct_per` (number): 每漏选一个选项所扣除的分数。
- `min_score` (number, 可选): 该策略下的最低得分，防止无限扣分。

```json
{
  "multiple_strategy": "deduct_per_miss",
  "multiple_strategy_conf": {
    "miss_deduct_per": 2,
    "min_score": 0
  }
}
```
计算函数:`max(min_score, 满分 - (漏选数量 * miss_deduct_per))`

5.按错选扣分
含义: 只要选了任何一个错误选项，就开始扣分。通常情况下，如果存在错选，则直接判为0分，但这里可以配置得更灵活。
配置:
- wrong_deduct_per (number): 每错选一个选项所扣除的分数。
- min_score (number, 可选): 该策略下的最低得分。
```json
{
  "multiple_strategy": "deduct_per_wrong",
  "multiple_strategy_conf": {
    "wrong_deduct_per": 2,
    "min_score": 0
  }
}
```
计算函数:`max(min_score, 满分 - (错选数量 * wrong_deduct_per))`

6.自定义公式或规则
含义: 允许教师或系统管理员定义自己的评分规则，可能是基于复杂的业务逻辑或特定需求。
配置:
- `fomula` (string): 自定义的评分公式，可能需要特定的解析器来执行。
- `rounding` (string, 可选): 小数处理方式。
- `scale_precision` (integer, 可选): 保留的小数位数。
- `min_score` (number, 可选): 该策略下的最低得分。
- `max_score` (number, 可选): 该策略下的最高得分。

`fomula`支持参数为：
- `score` 表示满分
- `count`表示选项总数
- `correctly_selected_count`表示用户选择的选项中，属于正确答案的数量
- `missed_correct_count`表示正确答案中，用户没有选择的数量（漏选）
- `incorrectly_selected_count`表示用户选择的选项中，不属于正确答案的数量（错选）
- `correctly_ignored_count`表示所有错误选项中，用户没有选择的数量（正确地避开）
和 +,-,*,/ 等基本运算符，以及括号。





组合题说明：
- 组合题（材料+多道简答）在题库由 `question_group` 维护映射；在考卷端通过 `group_question_id` 将相关子题归并展示与排序，`group_question_id` 对应父材料题 `question.id`。


### 2.5 `exam_score`（学生成绩）
- `id` bigint，主键
- `exam_id` bigint，逻辑外键：`exam_id → exam.id`
- `student_id` bigint，逻辑外键：`student_id → student.id`
- `total_score` decimal(8,2)，总分
- `status` string，`in_progress`/`submitted`/`graded`/`invalid`
- `start_time` datetime，开始作答时间，可空
- `submit_time` datetime，提交时间，可空
- `grade_time` datetime，评分完成时间，可空
- `duration_seconds` int，实际用时（秒），可空
- `grader` bigint，逻辑外键（评分人）；逻辑外键：`grader → user.id`，可空
- `detail_json` json，题目得分明细快照（按 `exam_item` 维度），可空
- `note` string，可空
- `create_time` datetime
- `update_time` datetime
- `deleted` boolean

唯一索引：
- `uk_exam_student_attempt(exam_id, student_id, attempt_no)`

普通索引：
- `idx_exam(exam_id)`
- `idx_student(student_id)`

说明：
- 支持多次作答，按 `attempt_no` 区分；取最新或最高分由业务决定。
- `total_score` 应不超过 `exam.total_score`（应用层校验）。
- 仅保留逻辑外键，不建数据库外键约束。

### 2.6 `exam_score_item`（学生题目成绩明细）
- `id` bigint，主键
- `score_id` bigint，逻辑外键：`score_id → exam_score.id`
- `exam_item_id` bigint，逻辑外键：`exam_item_id → exam_item.id`
- `ai_score` decimal(8,2)，AI 自动评分得分，默认 0
- `score` decimal(8,2)，得分，默认 0
- `answer` json，学生作答标准化内容（选项、填空、简答文本、绘图数据等）
- `auto_judge_detail_json` json，自动判分过程/依据，可空
- `remark` string，评语/扣分说明，可空
- `grade_time` datetime，题目评分完成时间，可空
- `create_time` datetime
- `update_time` datetime
- `deleted` boolean

唯一索引：
- `uk_exam_student_attempt_item(exam_id, student_id, attempt_no, exam_item_id)`

普通索引：
- `idx_score(score_id)`
- `idx_question(question_id)`

说明：
- 一条记录对应学生一次作答中的“一道题”。
- 组合题的子题按各自 `exam_item_id` 分别计分；父材料题不单独计分。
- `answer_json` 与 `auto_judge_detail_json` 仅作快照，不做强校验；不建数据库外键约束。

---

### 2.7 `assignment`(作业主表)
- `id` bigint，主键
- `name` string，作业名称
- `description` string，可选
- `course_id` bigint，逻辑外键（课程）；逻辑外键：`course_id → course.id`
- `submit_limit` int，提交次数限制，0表示不限
- `status` string，`draft`/`published`/`archived`
- `version` int，版本号
- `creator` bigint，逻辑外键（创建者）；逻辑外键：`creator → user.id`
- `deadline` datetime，提交截止时间
- `create_time` datetime
- `update_time` datetime
- `deleted` boolean

---

### 2.8 `assignment_score`（学生作业成绩）
- `id` bigint，主键
- `assignment_id` bigint，逻辑外键：`assignment_id → assignment.id`
- `student_id` bigint，逻辑外键：`student_id → student.id`
- `score` decimal(8,2)，总分
- `status` string，`in_progress`/`submitted`/`graded`/`invalid`
- `submit_no` int，提交次数
- `submit_file_url` string，提交文件地址
- `grader` bigint，逻辑外键（评分人）；逻辑外键：`grader → user.id`，可空
- `note` string，可空
- `create_time` datetime
- `update_time` datetime
- `deleted` boolean

---

### 2.9 `assignment_class` （作业 班级映射表）
- `assignment_id` bigint，逻辑外键：`assignment_id → assignment.id`
- `class_id` bigint，逻辑外键：`class_id → class.id`

---

### 2.10 `assignment_remark` (作业评语表)
- `id` bigint，主键
- `assignment_score_id` bigint，逻辑外键：`assignment_score_id → assignment_score.id`
- `ai_remark` text，AI评语内容
- `teacher_remark` text，教师评语内容
- `create_time` datetime
- `update_time` datetime
- `deleted` boolean

---

### 2.11 `remark_dimension_group` （作业维度组合评价表）
- `id` bigint，主键
- `name` string，维度组名称
- `description` string，维度组描述
- `visibility` string，维度组可见性，`public`/`private`/`course`
- `creator` bigint，逻辑外键（创建者）；逻辑外键：`creator → user.id`
- `create_time` datetime
- `update_time` datetime
- `deleted` boolean

---

### 2.12 `remark_dimension` （单项作业评价维度）
- `id` bigint，主键
- `group_id` bigint，逻辑外键：`group_id → remark_dimension_group.id`
- `name` string，维度名称
- `description` string，维度描述

---

### 2.13 `assignment_dimension_remark` (作业成绩维度评语表)
- `id` bigint，主键
- `assignment_remark_id` bigint，逻辑外键：`assignment_remark_id → assignment_remark.id`
- `dimension_id` bigint，逻辑外键：`dimension_id → remark_dimension.id`
- `dimension_group_id` bigint，逻辑外键：`dimension_group_id → remark_dimension_group.id`
- `score` decimal(5,2)，维度分数
- `remark` text，维度评语内容
- `create_time` datetime
- `update_time` datetime
- `deleted` boolean
