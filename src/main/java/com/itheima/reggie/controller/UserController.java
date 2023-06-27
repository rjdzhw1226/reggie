package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.UserDto;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.MailService;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    /**
     * 用户登录发送验证码
     * @param user
     * @param request
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody UserDto user, HttpServletRequest request) {
        log.info("{}",user);
        //获取手机号
        String phone = user.getPhone();
        String mail = user.getMail();

        if(StringUtils.isNotEmpty(phone)) {
            //随机生成验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info(code);

            //调用阿里云短信服务
            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);

            //将验证码进行保存
            request.getSession().setAttribute(phone,code);

            //TODO 通过邮件发送验证码
            mailService.sendMail(mail,code);
            //将验证码存入redis,并设置为五分钟后过期
            //redisTemplate.opsForValue().set("msg",code,5, TimeUnit.MINUTES);

            return R.success("手机验证码短信发送成功");
        }

        return R.error("手机短信验证码发送失败");
    }

    /**
     * 用户登录
     * @param map
     * @param request
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpServletRequest request) {
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();

//        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
//        lqw.eq(User::getPhone,phone);
//        User user = userService.getOne(lqw);

        //方便自己测试，避开验证码请求
        if(StringUtils.isNotEmpty(code) && "123456".equals(code)) {
            return loginSuccess(request, phone);
        }

        //取出session中的验证码
        String codeInSession = (String) request.getSession().getAttribute(phone);

        //从redis中获取验证码
        //String codeInSession = (String) redisTemplate.opsForValue().get("msg");

        //进行验证码比对
        if(codeInSession != null && code.equals(codeInSession)){
            //登录成功后删除redis中缓存的验证码
            //redisTemplate.delete("msg");
            request.getSession().removeAttribute(phone);
//            request.getSession().setAttribute("user",user.getId());
            //相同则登陆成功
            //判断当前手机号是否为新用户，是新用户则自动完成注册
            return loginSuccess(request,phone);
        }

        return R.error("登录失败");
    }

    private R<User> loginSuccess(HttpServletRequest request, String phone) {
        //判断当前手机号是否为新用户，是新用户则自动完成注册
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getPhone,phone);
        User user = userService.getOne(lqw);
        if(user == null) {
            user = new User();
            user.setPhone(phone);
            userService.save(user);
        }
        //登录成功，将用户id存到session中，防止过滤器进行过滤
        request.getSession().setAttribute("user",user.getId());
        return R.success(user);
    }

    /**
     * 用户退出登录
     * @return
     */
    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return R.success("退出登录");
    }
}
