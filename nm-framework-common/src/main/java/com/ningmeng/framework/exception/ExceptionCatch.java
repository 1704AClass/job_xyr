package com.ningmeng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.ningmeng.framework.model.response.CommonCode;
import com.ningmeng.framework.model.response.ResponseResult;
import com.ningmeng.framework.model.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


//控制器增强类 类似于AOP通知
@ControllerAdvice
public class ExceptionCatch {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionCatch.class);

    //Throwable 异常总类 -分支-
    // error (重大异常：内存语出，内存泄漏，电脑死机)
    // exception (程序员关注的异常) 检查时异常 运行时异常
    //ImmutableMap的特点的一旦创建不可改变，并且线程安全
    // key ? 异常类 value? 错误码以及信息
    //使用EXCEPTIONS存放异常类型和错误代码的映射，
    private static ImmutableMap<Class<? extends Throwable>,ResultCode> EXCEPTIONS;
    //使用builder来构建一个异常类型和错误代码的异常
    protected static ImmutableMap.Builder<Class<? extends Throwable>,ResultCode> builder = ImmutableMap.builder();

    static{
        //在这里加入一些基础的异常类型判断
        //以后在这维护错误类型
        builder.put(HttpMessageNotReadableException.class,CommonCode.HTTP_ERROR);
    }

    //异常控制器 如果捕获到异常 就触发下面的方法
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseResult customException(CustomException e) {
        LOGGER.error("catch exception : {}\r\nexception: ",e.getMessage(), e);

        return new ResponseResult(e.getResultCode());
    }


    //非自定义异常捕获类 捕获Exception异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult exception(Exception e) {
        LOGGER.error("catch exception : {}\r\nexception: ",e.getMessage(), e);

        if(EXCEPTIONS == null)
            //构建Map集合
            EXCEPTIONS = builder.build();

        final ResultCode resultCode = EXCEPTIONS.get(e.getClass());

        if (resultCode != null) {
            return new ResponseResult(resultCode);
        }

        return new ResponseResult(CommonCode.SERVER_ERROR);

    }

    //解决自定义异常
    //1、我们在map中配置HttpMessageNotReadableException和错误代码。
    //2、在异常捕获类中对Exception异常进行捕获，并从map中获取异常类型对应的错误代码，
    // 如果存在错误代码则返回此错误，否则统一返回99999错误
}
