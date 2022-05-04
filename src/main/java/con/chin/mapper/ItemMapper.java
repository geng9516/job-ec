package con.chin.mapper;

import con.chin.pojo.query.ItemInfoQuery;
import org.apache.ibatis.annotations.Mapper;
import con.chin.pojo.Item;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface ItemMapper {

    //保存产品
    int saveItem (Item item);

    //更新产品
    int updateItem(Item item);

    //已编辑并且没有失效的产品
    List<Item> findAllValidItem();

    //查询旧itemCode的产品
    Item findItem(Item item);

    //产品id,产品path,产品名,店铺名模糊查询
    List<Item> findItemBySearchConditions(ItemInfoQuery itemInfoQuery);

    //SiteShop查询
    List<Item> findItemBySiteShop(ItemInfoQuery itemInfoQuery);

    //获取产品code
    List<String> findItemCodeByPath(String path);

    //删除产品
    int deleteItem(Item item);

    //删除多个产品
    int deleteItems(List<String> itemCodeList);

    //后期修改时,价格,进货url,状态,送料,进货价等等
    int setItemSalePrice(Item item);

    //新itemcode查询
    Item findItemByItemCode(Item item);

    //新itemcode查询
    Item findItemsByItemCode(Item item);

    //多个itemid查询
    List<Item> findItemByItemCodes(List<String> itemCodeList);

    //删除option值
    int deleteOption(Item item);

    //更新option值
    int updateOption(Item item);

    //检索所有
    List<Item> findAll();

    //csv文件更新数据或追加数据
    int updateItemByCsv(Item item);

    //修改itemflog值
    int setItemFlog(Item item);

    //修改多个itemflog值
    int setItemFlogs(List<Item> itemList);

    //获取新爬取的产品
    List<Item> findNewDownloaded(Integer flog);

//---------------------------------------------------------------------------------------------------------

    //数据错误时做更新使用
    void setdate(Item item);

}
