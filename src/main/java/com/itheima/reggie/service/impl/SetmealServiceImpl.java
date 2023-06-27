package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐
 */
@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 保存套餐信息，并同时套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐信息
        this.save(setmealDto);

        //为setmealDishes中的setmealId赋值
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐信息，并删除对应的套餐和菜品关系
     * @param ids
     */
    @Override
    public void deleteWithDish(List<Long> ids) {
        //判断套餐状态，确认是否能够删除   起售的套餐不能删除，停售的才允许删除
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();

        //设置条件
        lqw.in(Setmeal::getId,ids).eq(Setmeal::getStatus,1);

        //执行查询
        int count = this.count(lqw);
        if(count > 0) {
            //本次删除有起售的套餐，不能删除，返回业务异常
            throw  new CustomException("套餐正在售卖中，不能删除");
        }

        //删除套餐信息
        this.removeByIds(ids);

        //删除套餐和菜品的关系信息
        LambdaQueryWrapper<SetmealDish> lqwDish = new LambdaQueryWrapper<>();
        lqwDish.in(SetmealDish::getDishId,ids);
        setmealDishService.remove(lqwDish);
    }

    /**
     * 批量批售和停售套餐
     * @param ids
     */
    @Override
    public void updateByIdsStatus(List<Long> ids, int status) {
        List<Setmeal> list = new ArrayList<>();
        for (Long id : ids) {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(id);
            setmeal.setStatus(status);
            list.add(setmeal);
        }
        //批量修改状态
        this.updateBatchById(list, ids.size());
    }

    /**
     * 根据id查询套餐相关信息
     * @param id
     * @return
     */
    @Override
    public SetmealDto selectById(Long id) {
        //查询套餐信息
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        //拷贝套餐信息
        BeanUtils.copyProperties(setmeal,setmealDto);

        //查询套餐和菜品的关系
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(lqw);
        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }

    /**
     * 修改套餐，以及套餐和菜品的关系
     * @param setmealDto
     */
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //修改套餐
        this.updateById(setmealDto);

        //删除之前的套餐和菜品的关系
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(lqw);

        //新增套餐和菜品的关系
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            //设置套餐id
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }
}
