package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategorySerive;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.utils.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private CategorySerive categoryService;

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping()
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 分页查询 （菜品）
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        //设置分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //设置条件构造器
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper();
        lqw.eq(name != null,Dish::getName,name);
        dishService.page(pageInfo, lqw);

        //将dish分页查询除records的信息全部拷贝到dishDtoPage中
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        //根据dish中的分类id查询数据给dishDto内的categoryName赋值
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            //item.setImage(basePath + "/" +item.getImage());
            DishDto dishDto = new DishDto();
            //将item的值拷贝到dishDto内
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();

            Category category = categoryService.getById(categoryId);
            if(category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        //设置dishDto的records值
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getByIdWithFlavor(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping()
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    /**
     * 批量起售和批量停售
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateByIdsWithStatus(@PathVariable int status,Long[] ids) {
        List<Dish> list = new ArrayList<>();
        for (Long id : ids) {
            Dish dish = new Dish();
            dish.setId(id);
            dish.setStatus(status);
            list.add(dish);
        }
        //根据id修改菜品状态
        dishService.updateBatchById(list,ids.length);

        return R.success("修改成功");
    }
    /**
     * 删除菜品和对应的口味信息 图片
     * @param ids
     * @return
     */
    @DeleteMapping()
    public R<String> deleteByIds(Long[] ids) {
        //删图
        String str = "";
        for (Long id : ids) {
            Dish dish = dishService.getId(id);
            if(dish != null){
                str = dish.getImage();
                ImageUtils.deleteImg(basePath+"/" , str);
                //删除信息
                dishService.deleteByIdsWithFlavor(ids);
            }else {
                return R.error("没图，删除失败");
            }
        }
        return R.success("删除成功");
    }

    /**
     * 根据条件查询菜品
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {
        //设置条件构造器
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        //设置条件
        lqw.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId())
                .eq(Dish::getStatus,1)
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lqw);
        return R.success(list);
    }
}
