package com.smartcourse.exception;

import co.elastic.clients.elasticsearch.xpack.usage.Base;

public class JsonParseException extends BaseException {

    public JsonParseException(String message) {
        super(message);
    }
}
