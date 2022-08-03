package con.chin.service.impl;


import con.chin.mapper.EcSiteShopAndItemMapper;
import con.chin.pojo.EcSiteShopAndItem;
import con.chin.service.EcSiteShopAndItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EcSiteShopAndItemServiceImpl implements EcSiteShopAndItemService {

    @Autowired
    private EcSiteShopAndItemMapper ecSiteShopAndItemMapper;

    //追加
    @Override
    public void importItemToEcsiteShop(List<String> itemCodeList, String ecSiteShop) {

        EcSiteShopAndItem ecSiteShopAndItem = new EcSiteShopAndItem();
        for (String itemCode : itemCodeList) {
            ecSiteShopAndItem.setItemId(itemCode);
            ecSiteShopAndItem.setShopName(ecSiteShop);
            List<EcSiteShopAndItem> ecSiteShopAndItemList = findEcSiteShopAndItem(itemCode);
            if (ecSiteShopAndItemList.size() > 0) {
                continue;
            } else {
                ecSiteShopAndItemMapper.importItemToEcsiteShop(ecSiteShopAndItem);
            }
        }
    }

    //itemid检索
    @Override
    public List<EcSiteShopAndItem> findEcSiteShopAndItem(String itemCode) {
        return ecSiteShopAndItemMapper.findEcSiteShopAndItem(itemCode);
    }
}
