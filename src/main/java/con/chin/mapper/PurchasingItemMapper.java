package con.chin.mapper;

import con.chin.pojo.Item;
import con.chin.pojo.PurchasingItem;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface PurchasingItemMapper {

    //保存产品
    int savePurchasingItem(PurchasingItem purchasingItem);

    //更新产品
    void updatePurchasingItem(PurchasingItem purchasingItem);

    //旧code检索
    PurchasingItem findPurchasingItem(String OldpurchasingItemCode);

    //把flog为0的全部查到
    List<PurchasingItem> findPurchasingItemByFlog0();
}
