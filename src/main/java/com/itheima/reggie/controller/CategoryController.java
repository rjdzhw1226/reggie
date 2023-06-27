package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategorySerive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategorySerive categorySerive;

    /**
     * 新增分类 套餐
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:{}",category);
        categorySerive.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        //分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //排序
        queryWrapper.orderByDesc(Category::getSort);
        //分页查询
        categorySerive.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除分类，id为：{}",ids);
        //categorySerive.removeById(id);
        categorySerive.remove(ids);
        return R.success("分类信息删除成功");
    }

    /**
     * 修改分类
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categorySerive.updateById(category);
        return R.success("分类信息修改成功");
    }

    /**
     * 按条件查询
     * @param category
     * @return
     */
    @RequestMapping("/list")
    public R<List<Category>> list(Category category) {
        //创建条件过滤器
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper();
        lqw.eq(category.getType()!=null,Category::getType,category.getType());
        lqw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        //查询
        List<Category> list = categorySerive.list(lqw);
        return R.success(list);
    }

}
