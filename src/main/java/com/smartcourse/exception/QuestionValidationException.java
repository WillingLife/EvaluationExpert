package com.smartcourse.exception;

/**
 * 题目通用校验异常：类型缺失、类型非法、缺少通用 details 等。
 * 具体消息由业务层传入，避免一个类对应一个文案。
 */
public class QuestionValidationException extends BaseException {
    public QuestionValidationException(String message) {
        super(message);
    }
    public QuestionValidationException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}