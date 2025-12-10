package com.smartcourse.exception;

import com.smartcourse.constant.MessageConstant;

public class DataParseException extends BaseException {
    public DataParseException() {
        super(MessageConstant.DATA_PARSE_ERROR);
    }
    public DataParseException(String message) {
        super(message);
    }
}