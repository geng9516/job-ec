package con.chin.mapper;

import con.chin.pojo.query.ItemQuery;
import org.apache.ibatis.annotations.Mapper;
import con.chin.pojo.Item;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface ItemMapper {

    int saveItem (Item item);

    void updateItem(Item item);

    List<Item> findAllItem();

    Item findItem(Item item);

    //产品id,产品path,产品名,店铺名模糊查询
    List<Item> findItemBySearchConditions(ItemQuery itemQuery);

    //SiteShop查询
    List<Item> findItemBySiteShop(ItemQuery itemQuery);

    //获取产品code
    List<String> findItemCodeByPath(String path);

    //判读产品是否已采集


}
