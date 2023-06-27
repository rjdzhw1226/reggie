package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 员工后台
 */

@Slf4j
@RestController
@RequestMapping("/employee")
@Api(value = "用户接口", tags = "用户管理相关的接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param req
     * @param employee
     * @return
     */
    @PostMapping("/login")
    @ApiImplicitParam(name = "login", value = "员工登录")
    @ResponseBody
    public R<Employee> login(HttpServletRequest req, @RequestBody Employee employee){
        //password MD5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //根据页面提交的username 查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        String str = "";
        //查询不到 返回登陆失败
        if(emp == null){
            str = "查无此人";
            return R.error("登录失败：" + str);
        }

        //密码比对 不一致返回登陆失败
        if(!emp.getPassword().equals(password)){
            str = "密码错误";
            return R.error("登录失败：" + str);
        }

        //查看员工账号状态 0返回登录失败
        if(emp.getStatus() == 0){
            str = "账号已禁用";
            return R.error("登录失败：" + str);
        }

        //登录成功 加入session
        req.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     * @param req
     * @return
     */
    @PostMapping("/logout")
    @ApiImplicitParam(name = "logout", value = "员工退出")
    public R<String> logout(HttpServletRequest req) {
        //清理session中的员工id
        req.getSession().removeAttribute("employee");
        return R.success("退出登录成功");
    }

    /**
     * 员工新增
     * @param req
     * @param employee
     * @return
     */
    @PostMapping
    @ApiImplicitParam(name = "save", value = "员工新增")
    public R<String> save(HttpServletRequest req, @RequestBody Employee employee) {
        log.info("新增员工，员工信息：{}", employee.toString());
        long id = Thread.currentThread().getId();
        log.info("线程id:{}" , id);
        //初始密码加密
        String passwordSecret = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(passwordSecret);
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        Long empId = (Long) req.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    @ApiImplicitParam(name = "pageSearch", value = "员工信息分页查询")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);
        //分页构造器
        Page pageInfo = new Page(page,pageSize);
        //构造一个条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加一个过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        //封装返回对象
        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    @ApiImplicitParam(name = "update", value = "根据id修改员工信息")
    public R<String> update(HttpServletRequest req, @RequestBody Employee employee){
        log.info(employee.toString());
        long id = Thread.currentThread().getId();
        log.info("线程id:{}" , id);
//        Long empId = (Long) req.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息！");
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }

    /**
     * 员工删除
     * @param id
     * @return
     */
    @GetMapping("/delete/{id}")
    public R<String> deleteById(@PathVariable Long id){
        employeeService.removeById(id);
        return R.success("员工删除成功");
    }



}
