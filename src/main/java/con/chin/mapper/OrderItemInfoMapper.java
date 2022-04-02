package con.chin.mapper;

import con.chin.pojo.OrderItemInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OrderItemInfoMapper {

    //获取所以的orderiteminfo
    List<OrderItemInfo> findOrderItemInfo();

    //获取相同的orderid的orderiteminfo
    List<OrderItemInfo> findOrderItemInfoByOrderId(String orderId);


    //-----------------------------------------------------------------------------------------------

    //更新
    int updateOrderItemInfoById(OrderItemInfo orderItemInfo);



}
