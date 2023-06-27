package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.OrderDto;
import com.itheima.reggie.entity.Orders;

public interface OrderService extends IService<Orders> {
    Page<OrderDto> empPage(int page, int pageSize, String number, String beginTime, String endTime);

    void submit(Orders orders);


    boolean removeByIdAndStatus(Long id);
}
