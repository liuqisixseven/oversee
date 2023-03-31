package com.chd.common.exception;

public class AssertBizException {

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new BizException(message);
        }
    }
}
