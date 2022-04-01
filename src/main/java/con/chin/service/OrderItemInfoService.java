package con.chin.service;

import con.chin.pojo.OrderItemInfo;

import java.util.List;

public interface OrderItemInfoService {

    //获取相同的orderid的orderiteminfo
    List<OrderItemInfo> findOrderItemInfo(String orderId);


}
