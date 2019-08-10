package com.hfut.cqyzs.memorandum.utils;

import lombok.Data;

@Data
public class Message<T> {
    private String code;
    private String msg;
    private T data;

    public Message() {
    }

    public Message(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}

