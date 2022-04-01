package con.chin.pojo.result;

import con.chin.pojo.Item;
import con.chin.pojo.OrderInfo;
import con.chin.pojo.OrderItemInfo;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;


@Data
public class Order {

    private Long id;

    private OrderInfo orderInfo;

    private List<OrderItemInfo> orderItemInfoList;

    private Item item;

}




