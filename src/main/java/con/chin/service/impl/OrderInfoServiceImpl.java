package con.chin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import con.chin.mapper.ItemMapper;
import con.chin.mapper.OrderInfoMapper;
import con.chin.mapper.OrderItemInfoMapper;
import con.chin.pojo.Item;
import con.chin.pojo.OrderInfo;
import con.chin.pojo.OrderItemInfo;
import con.chin.pojo.query.OrderInfoQuery;
import con.chin.pojo.result.Order;
import con.chin.service.OrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderInfoServiceImpl implements OrderInfoService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderItemInfoMapper orderItemInfoMapper;

    @Autowired
    private ItemMapper itemMapper;

    //把所有的Orderinfo取得
    @Override
    @Transactional
    public PageInfo<OrderInfo> findAllOrderInfo(OrderInfoQuery orderInfoQuery) {

        PageHelper.startPage(orderInfoQuery.getPageNum(), orderInfoQuery.getPageSize());
        List<OrderInfo> orderList = orderInfoMapper.findAllOrderInfo(orderInfoQuery);
        return new PageInfo<OrderInfo>(orderList);
    }
}
