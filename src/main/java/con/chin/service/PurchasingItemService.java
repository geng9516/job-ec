package con.chin.service;

import com.github.pagehelper.PageInfo;
import con.chin.pojo.Item;
import con.chin.pojo.PurchasingItem;
import con.chin.pojo.query.PurchasingItemQuery;

import java.util.List;

public interface PurchasingItemService {

    //保存产品
    int savePurchasingItem (PurchasingItem purchasingItem);

    //把flog为0的全部查到
    List<PurchasingItem> findPurchasingItemByFlog0();

    //产品id,产品path,产品名,店铺名模糊查询
    PageInfo<PurchasingItem> findPurchasingItemQueryBySearchConditions(PurchasingItemQuery purchasingItemQuery);

}
