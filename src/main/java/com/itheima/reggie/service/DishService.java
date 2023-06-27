package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.dto.DishDto;


public interface DishService extends IService<Dish> {

    //新增菜品，并保存对应的口味
    public void saveWithFlavor(DishDto dishDto);

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    public DishDto getByIdWithFlavor(Long id);

    //修改菜品以及对应的口味信息
    void updateWithFlavor(DishDto dishDto);

    void deleteByIdsWithFlavor(Long[] ids);

    Dish getId(Long id);
}
