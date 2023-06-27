package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.OrderDto;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.mapper.OrderMapper;
import com.itheima.reggie.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {
    @Override
    public Page<OrderDto> empPage(int page, int pageSize, String number, String beginTime, String endTime) {
        return null;
    }

    @Override
    public void submit(Orders orders) {

    }

    @Override
    public boolean removeByIdAndStatus(Long id) {
        return false;
    }
}
