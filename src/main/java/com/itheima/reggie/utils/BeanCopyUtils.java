package com.itheima.reggie.utils;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class BeanCopyUtils {
    public BeanCopyUtils() {
    }

    /**
     * 复制单个Bean属性
     * @param source
     * @param clazz
     * @return
     */
    public static <V,T> T copyBean(V source,Class<T> clazz) {
        T vo = null;
        try {
            //获取目标对象
            vo = clazz.newInstance();
            //复制属性
            BeanUtils.copyProperties(source, vo);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return vo;
    }

    /**
     * 拷贝List型的Bean集合
     * @param list
     * @param clazz
     * @param <V>
     * @param <T>
     * @return
     */
    public static <V,T> List<T> copyBeanList(List<V> list,Class<T> clazz) {
        return list.stream()
                .map(o ->
                    copyBean(o,clazz)
                ).collect(Collectors.toList());
    }



}
