package com.ningmeng.framework.exception;

import com.ningmeng.framework.model.response.ResultCode;

public class CustomExceptionCast {

    //使用此静态方法抛出自定义异常
    public static void cast(ResultCode resultCode){
        throw new CustomException(resultCode);
    }
}
