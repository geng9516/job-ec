package con.chin.service;

import con.chin.pojo.EcSiteShopAndItem;

import java.util.List;

public interface EcSiteShopAndItemService {

    //追加
    void importItemToEcsiteShop(List<String> itemCodeList,String ecSiteShop);

    //itemid检索
    List<EcSiteShopAndItem> findEcSiteShopAndItem(String itemCode);

}
