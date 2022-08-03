package con.chin.mapper;

import con.chin.pojo.EcSiteShopAndItem;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface EcSiteShopAndItemMapper {

    //追加
    void importItemToEcsiteShop(EcSiteShopAndItem ecSiteShopAndItem);

    //itemid检索
    List<EcSiteShopAndItem> findEcSiteShopAndItem(String itemCode);

}
