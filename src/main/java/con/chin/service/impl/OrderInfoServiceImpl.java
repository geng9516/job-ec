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

        List<Order> orderList = new ArrayList<>();

        List<OrderInfo> orderInfoList = orderInfoMapper.findAllOrderInfo(orderInfoQuery);

        for (OrderInfo orderInfo : orderInfoList) {
            Order order = new Order();
            Item item = new Item();
            List<Item> itemList = new ArrayList<>();
            List<OrderItemInfo> orderItemInfoList = orderItemInfoMapper.findOrderItemInfoByOrderId(orderInfo.getOrderId());

            for (OrderItemInfo orderItemInfo : orderItemInfoList) {
                Item item1 = new Item();
                item1.setItemCode(orderItemInfo.getItemId());
                item = itemMapper.findItemByItemCode(item1);

            }
            order.setOrderInfo(orderInfo);
            order.setOrderItemInfoList(orderItemInfoList);
            order.setItem(item);
            orderList.add(order);
        }
//        for (Order order : orderList) {
//            String orderId = order.getItem().getImage();
//            System.out.println(orderId);
//        }
        return new PageInfo<OrderInfo>(orderInfoList);
    }
}
