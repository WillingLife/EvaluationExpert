# 评估专家 · 数据库表结构

## 题目（Question）
- 继承策略：表按类型分表（TPT：Table‑Per‑Type）。
- 题型覆盖：单选、多选、填空、简答、画图（图片答案）、简答组合（多道简答共用材料）。
- 外键策略：不在数据库层声明外键；仅通过“逻辑外键”在文档中标注，由应用层保证一致性与完整性。
- 分值策略：题目模型不存分值，分值在试卷相关表维护。

### 表清单
- `question`（题目基表）
- `question_option`（选择题选项表，服务于单/多选）
- `question_single`（单选题子表）
- `question_multiple`（多选题子表）
- `question_fill_blank`（填空题子表：题级信息）
- `question_fill_blank_item`（填空题子表：每空配置与答案规则）
- `question_short_answer`（简答题子表）
- `question_drawing`（画图题子表，图片答案）
- `question_group`（简答题组合：父表，承载共用材料）
---

### question（题目基表）
用于承载所有题目公共属性。

字段：
- `id` BIGINT，主键
- `type` VARCHAR(32) NOT NULL，题型：`single` | `multiple` | `fill_blank` | `short_answer` | `group`
- `course_id` BIGINT NOT NULL，逻辑外键：`course_id → course.id`
- `stem` TEXT NOT NULL，题干/材料（可含富文本标记）
- `analysis` TEXT NULL，解析/答案说明
- `difficulty` TINYINT NOT NULL DEFAULT 3，难度（1~5）
- `author_id` BIGINT NOT NULL，出题人；逻辑外键：`author_id → user.id`
- `active` BOOLEAN NOT NULL DEFAULT TRUE，启用状态
- `deleted` BOOLEAN NOT NULL DEFAULT FALSE，逻辑删除
- `create_time` DATETIME NOT NULL，创建时间
- `update_time` DATETIME NOT NULL，更新时间

备注：`type` 与各子表一一对应；每条题目在且仅在一个子表存在记录（由应用层保证）。

---

### question_option（选择题选项表）
服务于单/多选题的选项集合。

字段：
- `id` BIGINT，主键
- `question_id` BIGINT NOT NULL，逻辑外键：`question_id → question.id`
- `content` TEXT NOT NULL，选项内容（可含富文本/图片引用）
- `correct` BOOLEAN NOT NULL DEFAULT FALSE，是否为正确选项（单选仅一条为 TRUE；多选可多条）
- `sort_order` INT NOT NULL DEFAULT 0，显示顺序

约束与索引（建议）：
- 唯一约束：`(question_id, sort_order)` 或业务内唯一键（若存在 `option_key`，可用 `(question_id, option_key)`）
- 索引：`question_id`

---

### question_single（单选题子表）
标识单选题的类型归属与可选的类型级配置。

字段：
- `question_id` BIGINT，主键；逻辑外键：`question_id → question.id`

业务规则：
- 在 `question_option` 中，且仅应有一条 `correct = TRUE` 的选项
- `question.type = 'single'`

---

### question_multiple（多选题子表）
标识多选题的类型归属与可选的类型级配置。

字段：
- `question_id` BIGINT，主键；逻辑外键：`question_id → question.id`

业务规则：
- 在 `question_option` 中可存在多条 `correct = TRUE` 的选项
- `question.type = 'multiple'`

---

### question_fill_blank（填空题子表：题级信息）
字段：
- `question_id` BIGINT，主键；逻辑外键：`question_id → question.id`
- `blank_count` INT NOT NULL，空格数量（应与 `question_fill_blank_item.blank_index` 数量一致）

业务规则：
- `question.type = 'fill_blank'`

---

### question_fill_blank_item（填空题子表：每空配置）
每一空一行，支持文本或正则匹配等规则。

字段：
- `id` BIGINT，主键
- `question_id` BIGINT NOT NULL，逻辑外键：`question_id → question.id`
- `blank_index` INT NOT NULL，从 1 开始连续编号
- `answer_rule_type` VARCHAR(16) NOT NULL，`text` | `regex`，答案匹配规则类型

约束与索引（建议）：
- 唯一约束：`(question_id, blank_index)`，避免重复空位
- 索引：`question_id`


### question_fill_blank_answer(填空题答案表)

字段：
- `fill_blank_id` BIGINT NOT NULL，逻辑外键：`question_fill_blank_id → question_fill_blank_item.id`
- `answer` TEXT NOT NULL，参考答案

---

### question_short_answer（简答题子表）
字段：
- `question_id` BIGINT，主键；逻辑外键：`question_id → question.id`
- `answer` TEXT NOT NULL，参考答案/评分要点
- `criteria` TEXT NULL，评分标准/参考评分细则

业务规则：
- `question.type = 'short_answer'`

---

### question_group（简答题组合）
用于承载一组简答题共享的材料/场景文本等信息。

字段：
- `id` BIGINT，主键
- `group_question_id` BIGINT NOT NULL，组合父题；逻辑外键：`group_question_id → question.id`
- `child_question_id` BIGINT NOT NULL，子题（简答题）；逻辑外键：`child_question_id → question.id`

约束与索引（建议）：
- 唯一约束：`(group_question_id, child_question_id)`，避免同一组合内重复关联
- 索引：`group_question_id`，`child_question_id`

业务规则：
- `group_question_id` 对应的 `question.type = 'short_answer_group'`
- `child_question_id` 对应的 `question.type = 'short_answer'`
- 组合父题与子题的启用/删除状态由应用层保证一致性（例如删除父题时同时清理映射）

---

### knowledge_node（知识节点表）
字段
- `id` BIGINT，主键
- `external_id` BIGINT NULL，外部的id
- `course_id` BIGINT NOT NULL，逻辑外键：`course_id → course.id`
- `name` VARCHAR(255) NOT NULL，知识点名称

---

### question_knowledge（题目-知识点映射表）
字段：
- `question_id` BIGINT NOT NULL，逻辑外键：`question_id → question.id`
- `knowledge_node_id` BIGINT NOT NULL，逻辑外键：`knowledge_node_id → knowledge_node.id`
- `weight` DECIMAL(5,2) NOT NULL DEFAULT 1.0，权重（用于评估知识点相关度）

---

### 统一约定与校验要点
- 逻辑外键仅在文档层标注，不在数据库层声明外键约束；由应用层（服务/DAO）保证插入、更新和删除时的一致性。
- `question.type` 与子表存在性保持一致：插入题目时创建对应子表记录；变更题型需迁移子表数据或禁止变更。
- 删除策略：优先使用 `question.deleted` 逻辑删除；相关子表、选项与组合映射保持一致状态。
- 计分：
  - 分值不存于题目模型，统一由“试卷‑题目关联”维护（例如 `exam_paper_item`）。
  - 选择题：依据 `question_option.correct` 判定正确性，得分按试卷配置计算。
  - 填空题：依据 `question_fill_blank_item` 的每空规则判定正确性；如需每空权重，请在试卷维度维护（例如 `paper_question_blank_weight`）。
  - 简答/画图：通常人工或规则化评分，分值也由试卷配置。
  - 简答组合：按组合父题下子题明细在试卷中的设定计分（本表不存分值）。

