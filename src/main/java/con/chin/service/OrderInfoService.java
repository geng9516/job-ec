package con.chin.service;

import com.github.pagehelper.PageInfo;
import con.chin.pojo.OrderInfo;
import con.chin.pojo.query.OrderInfoQuery;
import con.chin.pojo.result.Order;

import java.util.List;

public interface OrderInfoService {

    //把所有的Orderinfo取得
    PageInfo<OrderInfo> findAllOrderInfo(OrderInfoQuery orderInfoQuery);
}
