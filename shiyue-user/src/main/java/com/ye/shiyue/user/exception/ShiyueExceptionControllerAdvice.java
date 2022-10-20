package com.ye.shiyue.user.exception;

import com.ye.shiyue.common.utils.Result;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.ye.shiyue.user.controller")
public class ShiyueExceptionControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result handleValidException(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        Map<String,String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach((fieldError -> {
            errorMap.put(fieldError.getField(),fieldError.getDefaultMessage());
        }));
        return Result.fail(errorMap);
    }

    @ExceptionHandler(value = Throwable.class)
    public Result handleException(Throwable e){
        return Result.fail(e.getMessage());
    }
}
