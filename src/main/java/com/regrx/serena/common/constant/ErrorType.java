package com.regrx.serena.common.constant;

public enum ErrorType {
    // Error code section
    DOWNLOAD_ERROR_CODE(601),
    PARSE_ERROR_CODE(602),
    PROCESSING_ERROR_CODE(603),
    IO_ERROR_CODE(604),
    BREED_ERROR_CODE(605);

    private final int code;

    ErrorType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
