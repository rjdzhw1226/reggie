package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品
 */
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishMapper dishMapper;


    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品数据
        this.save(dishDto);
        //获取菜品id  保存后生成的id会存在于dishDto对象中
        Long id = dishDto.getId();
        //给对应的口味设置dish_id
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());
        //保存菜品口味到对应的菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        DishDto dishDto = new DishDto();
        //查询菜品信息
        Dish dish = this.getById(id);
        BeanUtils.copyProperties(dish, dishDto);
        //创建条件构造器
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper();
        lqw.eq(DishFlavor::getDishId, id);
        //查询口味信息
        List<DishFlavor> list = dishFlavorService.list(lqw);
        dishDto.setFlavors(list);

        return dishDto;
    }

    /**
     * 修改菜品以及对应的口味信息
     * @param dishDto
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //1、修改菜品信息
        this.updateById(dishDto);

        //2、删除口味表之前的信息
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(lqw);

        //3、新增当前修改的口味信息
        //获取提交的口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        //设置口味表的dish_id
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 删除菜品信息，以及对应的口味信息   （逻辑删除）
     * @param ids
     */
    @Override
    public void deleteByIdsWithFlavor(Long[] ids) {
        //删除菜品信息
        this.removeByIds(Arrays.asList(ids));
        //删除口味信息
//        for (Long id : ids) {
//            LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
//            lqw.eq(DishFlavor::getDishId,id);
//            dishFlavorService.remove(lqw);
//        }
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.in(DishFlavor::getDishId,Arrays.asList(ids));
        dishFlavorService.remove(lqw);
    }

    @Override
    public Dish getId(Long id) {
        Dish dish = dishMapper.getId(id);
        return dish;
    }
}
