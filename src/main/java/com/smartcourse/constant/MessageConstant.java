package com.smartcourse.constant;

/**
 * 统一管理业务异常消息常量
 */
public final class MessageConstant {

    private MessageConstant() {}

    // 通用
    public static final String QUESTION_TYPE_EMPTY = "题型不能为空";
    public static final String DETAILS_MISSING = "缺少题目详情";

    // 选择题
    public static final String CHOICE_OPTIONS_EMPTY = "选择题选项不能为空";
    public static final String SINGLE_CHOICE_CORRECT_COUNT_INVALID = "单选题必须且仅有一个正确选项";
    public static final String MULTIPLE_CHOICE_NO_CORRECT = "多选题至少有一个正确选项";

    // 填空题
    public static final String BLANK_ANSWER_EMPTY = "填空题答案不能为空";

    // 简答题
    public static final String SHORT_ANSWER_ANSWER_EMPTY = "简答题参考答案不能为空";

    // 其他通用业务
    public static final String DATA_PARSE_ERROR = "数据解析错误";
    public static final String QUESTION_TYPE_INVALID = "题目类型错误";
}
