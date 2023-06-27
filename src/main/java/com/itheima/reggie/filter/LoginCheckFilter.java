package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户登录
 */

@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        log.info("拦截到请求：{}", request);
        //获取请求的Uri
        String requestURI = request.getRequestURI();
        //判断请求路径是否需要处理 部分为了swagger-ui查看使用
        String[] urls = new String[]{
                "/swagger-resources/**",
                "/v2/api-docs",
                "/csrf",
                "/swagger-ui.html",
                "/webjars/**",
                "/employee/page",
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
        };

        boolean check = check(urls, requestURI);
        //放行
        if (check) {
            log.info("本次请求{}不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        //不放行 判断登录状态 登录直接放行
        if (request.getSession().getAttribute("employee") != null) {
            log.info("用户已登录，用户id为:{}", request.getSession().getAttribute("employee"));

            Long employeeId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(employeeId);

            long id = Thread.currentThread().getId();
            log.info("线程id:{}" ,id);

            filterChain.doFilter(request, response);
            return;
        }
        //未登录拦截
        log.info("未登录拦截");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSONObject.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    /**
     * 匹配路径，检查本次请求是否需要放行
     *
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
