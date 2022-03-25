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

    //已编辑并且没有失效的产品
    List<Item> findAllValidItem();

    Item findItem(Item item);

    //产品id,产品path,产品名,店铺名模糊查询
    List<Item> findItemBySearchConditions(ItemQuery itemQuery);

    //SiteShop查询
    List<Item> findItemBySiteShop(ItemQuery itemQuery);

    //获取产品code
    List<String> findItemCodeByPath(String path);

    //判读产品是否已采集

    List<Item> findAll();

    void setdate(Item item);

    //删除产品
    int deleteItem(Item item);

    //価格変更
    int setItemSalePrice(Item item);



}
