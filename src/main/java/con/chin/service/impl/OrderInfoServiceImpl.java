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
    public PageInfo<Order> findAllOrderInfo(OrderInfoQuery orderInfoQuery) {

        List<Order> orderList = new ArrayList<>();
        List<OrderInfo> orderInfoList = orderInfoMapper.findAllOrderInfo(orderInfoQuery);

        for (OrderInfo orderInfo : orderInfoList) {
            Order order = new Order();

            List<Item> itemList = new ArrayList<>();
            List<OrderItemInfo> orderItemInfoList = orderItemInfoMapper.findOrderItemInfoByOrderId(orderInfo.getOrderId());

            for (OrderItemInfo orderItemInfo : orderItemInfoList) {
                Item item = new Item();
                Item item1 = new Item();
                item1.setItemCode(orderItemInfo.getItemId());
                item = itemMapper.findItemByItemCode(item1);
                itemList.add(item);
            }
            order.setItemList(itemList);
            order.setOrderInfo(orderInfo);
            order.setOrderItemInfoList(orderItemInfoList);
            orderList.add(order);
        }
        int i = 0;
        for (Order order : orderList) {
            for (Item item : order.getItemList()) {
                String s = item.getImage();
                System.out.println(s);

            }
            System.out.println("      次数" + i++);
        }

        PageHelper.startPage(orderInfoQuery.getPageNum(), orderInfoQuery.getPageSize());

        return new PageInfo<Order>(orderList);
    }
}
