package con.chin.mapper;

import con.chin.pojo.OrderInfo;
import con.chin.pojo.query.OrderInfoQuery;
import con.chin.pojo.result.Order;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OrderInfoMapper {

    //把所有的Orderinfo取得
    List<OrderInfo> findAllOrderInfo(OrderInfoQuery orderInfoQuery);


}
