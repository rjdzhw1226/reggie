package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * SQL异常处理方法
     * @param e
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException e){
        log.info(e.getMessage());
        //判断当前异常信息 是否存在重复关键字
        String eMessage = e.getMessage();
        if(eMessage.contains("Duplicate entry")){
            String[] sp = eMessage.split(" ");
            String errorMsg = sp[2] + "已存在";
            return R.error(errorMsg);
        }else{
            return R.error("未知错误");
        }
    }

    /**
     * 分类与菜品或套餐有关联时，不能删除，则抛出此异常
     * @param exception
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException exception) {

        log.info("异常信息==>{}",exception.getMessage());
        return R.error(exception.getMessage());
    }
}
