package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 自定义元数据对象处理器
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        //HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        //metaObject.setValue("createUser", (Long) req.getSession().getAttribute("employee"));
        //metaObject.setValue("updateUser", (Long) req.getSession().getAttribute("employee"));
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
        metaObject.setValue("isDeleted", 0);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        //HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        log.info(metaObject.toString());
        metaObject.setValue("updateTime", LocalDateTime.now());
        //metaObject.setValue("updateUser", (Long) req.getSession().getAttribute("employee"));
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
