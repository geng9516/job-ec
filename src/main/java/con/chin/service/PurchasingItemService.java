package con.chin.service;

import con.chin.pojo.Item;
import con.chin.pojo.PurchasingItem;

import java.util.List;

public interface PurchasingItemService {

    //保存产品
    int savePurchasingItem (PurchasingItem purchasingItem);

    //把flog为0的全部查到
    List<PurchasingItem> findPurchasingItemByFlog0();

}
