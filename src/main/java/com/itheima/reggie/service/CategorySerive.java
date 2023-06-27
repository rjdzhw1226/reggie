package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;


public interface CategorySerive extends IService<Category> {
    public void remove(Long id);
}
