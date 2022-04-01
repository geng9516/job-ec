package con.chin.service.impl;

import con.chin.pojo.OrderItemInfo;
import con.chin.service.OrderItemInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemInfoServiceImpl implements OrderItemInfoService {

    //获取相同的orderid的orderiteminfo
    @Override
    public List<OrderItemInfo> findOrderItemInfo(String orderId) {
        return null;
    }
}
