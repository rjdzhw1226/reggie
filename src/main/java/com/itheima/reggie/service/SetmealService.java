package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.dto.SetmealDto;


import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 保存套餐信息，并同时套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    public void deleteWithDish(List<Long> ids);

    public void updateByIdsStatus(List<Long> ids,int status);

    public SetmealDto selectById(Long id);

    public void updateWithDish(SetmealDto setmealDto);
}
