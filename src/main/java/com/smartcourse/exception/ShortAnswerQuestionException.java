package com.smartcourse.exception;

/**
 * 简答题相关异常：details缺失、参考答案为空等。
 * 具体消息由业务层填充。
 */
public class ShortAnswerQuestionException extends BaseException {
    public ShortAnswerQuestionException(String message) {
        super(message);
    }
    public ShortAnswerQuestionException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}