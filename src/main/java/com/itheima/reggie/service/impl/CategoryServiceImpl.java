package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategorySerive;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategorySerive {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类
     * @param id
     */
    @Override
    public void remove(Long id) {
        //查询当前分类是否关联 关联则抛出异常
        LambdaQueryWrapper<Dish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(queryWrapper1);
        if(count1 > 0){
            //抛出异常
            throw new CustomException("分类已关联菜品");
        }
        LambdaQueryWrapper<Setmeal> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(queryWrapper2);
        if(count2 > 0){
            //抛出异常
            throw new CustomException("分类已关联套餐");
        }
        //正常删除分类
        super.removeById(id);

    }
}
