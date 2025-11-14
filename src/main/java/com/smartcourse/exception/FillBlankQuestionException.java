package com.smartcourse.exception;

/**
 * 填空题相关异常：结构非法、某空答案缺失等。
 * 具体消息由业务层填充。
 */
public class FillBlankQuestionException extends BaseException {
    public FillBlankQuestionException(String message) {
        super(message);
    }
    public FillBlankQuestionException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}