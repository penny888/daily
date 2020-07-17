package com.daily.dto;

import lombok.Data;

@Data
public class Response<T> {

    private T data;

    private int code;

    private boolean success;

    private String msg;

    private long total;

    private String userToken;
}
