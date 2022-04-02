package con.chin.pojo.result;

import con.chin.pojo.Item;
import con.chin.pojo.OrderInfo;
import con.chin.pojo.OrderItemInfo;
import lombok.Data;

import java.util.List;

@Data
public class Order {

    private Long id;

    private OrderInfo orderInfo;

    private List<OrderItemInfo> orderItemInfoList;

    private List<Item> itemList;

}




