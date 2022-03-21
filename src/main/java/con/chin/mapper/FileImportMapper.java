package con.chin.mapper;

import con.chin.pojo.OrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface FileImportMapper {

    //订单信息保存
    int savaOrderInfo(OrderInfo orderInfo);

    //订单号查找订单
    OrderInfo findOrderInfoByOrderId(OrderInfo orderInfo);

    //更新订单
    int updataOderInfo(OrderInfo orderInfo);
}
