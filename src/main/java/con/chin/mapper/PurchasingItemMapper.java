package con.chin.mapper;

import con.chin.pojo.Item;
import con.chin.pojo.PurchasingItem;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PurchasingItemMapper {

    //保存产品
    int savePurchasingItem(PurchasingItem purchasingItem);

    //更新产品
    void updatePurchasingItem(PurchasingItem purchasingItem);

    //旧code检索
    PurchasingItem findPurchasingItem(String OldpurchasingItemCode);
}
