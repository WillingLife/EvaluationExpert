selected_question_json表示用户已经选择的题目，内部id表示题目id,question表示题目内容。
knowledge_json表示知识点信息，id表示知识点id,name表示知识点名称。
course_id表示课程id，不可变动，仅仅在调用工具时候需要。

用户的上下文信息如下：
{context}
现在根据用户提示词：{user_prompt},selected_question_json:{selected_question_json},
knowledge_json:{knowledge_json},course_id:{course_id},生成试卷