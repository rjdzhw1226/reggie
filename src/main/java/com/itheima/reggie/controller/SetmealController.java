package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.CategorySerive;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategorySerive categoryService;

    @Autowired
    private DishService dishService;

    /**
     * 分页条件查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //设置分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        //设置条件构造器
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.like(name != null,Setmeal::getName,name)
                .orderByDesc(Setmeal::getUpdateTime);
        //执行查询
        setmealService.page(pageInfo,lqw);

        //拷贝分页数据
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();

        //处理records数据的拷贝
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            //套餐的分类id
            Long categoryId = item.getCategoryId();

            //根据id查询分类名称
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();

            //设置分类名称
            setmealDto.setCategoryName(categoryName);
            return setmealDto;

        }).collect(Collectors.toList());

        //设置records
        dtoPage.setRecords(list);

        return R.success(dtoPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        //删除套餐以及对应的套餐和菜品的关系
        setmealService.deleteWithDish(ids);
        return R.success("删除套餐成功");
    }


    @PostMapping("/status/{status}")
    //@CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> status(@PathVariable int status,@RequestParam List<Long> ids) {
        setmealService.updateByIdsStatus(ids,status);
        return R.success("状态修改成功");
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> selectById(@PathVariable Long id) {
        log.info("{}",id);
        //根据id进行查询
        SetmealDto setmealDto = setmealService.selectById(id);

        return R.success(setmealDto);
    }

    /**
     * 修改套餐，以及对应的套餐和菜品的关系
     * @param setmealDto
     * @return
     */
    @PutMapping
    //@CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        //修改套餐，以及对应的套餐和菜品的关系
        setmealService.updateWithDish(setmealDto);
        return R.success("修改套餐成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    //@Cacheable(value = "setmealCache",key="#setmeal.categoryId+'_'+#setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        //设置条件构造器
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId())
                .eq(Setmeal::getStatus,setmeal.getStatus());
        List<Setmeal> list = setmealService.list(lqw);

        return R.success(list);
    }

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    //@CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    @GetMapping("/dish/{id}")
    //这里前端是使用路径来传值的，要注意，不然你前端的请求都接收不到，就有点尴尬哈
    public R<List<Dish>> dish(@PathVariable("id") Long SetmealId){

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,SetmealId);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);

        LambdaQueryWrapper<Dish> queryWrapper2 = new LambdaQueryWrapper<>();
        ArrayList<Long> dishIdList = new ArrayList<>();
        for (SetmealDish setmealDish : list) {
            Long dishId = setmealDish.getDishId();
            dishIdList.add(dishId);
        }
        queryWrapper2.in(Dish::getId, dishIdList);
        List<Dish> dishList = dishService.list(queryWrapper2);

        return R.success(dishList);
    }

}
