package com.smartcourse.exception;

/**
 * 选择题相关异常：选项为空、结构不合法、正确数不符合规则等。
 * 具体消息由业务层填充。
 */
public class ChoiceQuestionException extends BaseException {
    public ChoiceQuestionException(String message) {
        super(message);
    }
    public ChoiceQuestionException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}