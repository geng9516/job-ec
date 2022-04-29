package con.chin.service;

import com.github.pagehelper.PageInfo;
import con.chin.pojo.Item;
import con.chin.pojo.query.ItemInfoQuery;

import java.util.List;

public interface ItemService {

    //アイテムを保存
    int saveItem(Item item);

    //アイテム更新
    void updateItem(Item item);

    //アイテム検索ALL
    List<Item> findAllValidItem();

    //アイテム検索
    Item findItem(Item item);

    //产品id,产品path,产品名,店铺名模糊查询
    PageInfo<Item> findItemBySearchConditions(ItemInfoQuery itemInfoQuery);

    //SiteShop查询
    PageInfo<Item> findItemBySiteShop(ItemInfoQuery itemInfoQuery);

    //获取产品code
    List<String> findItemCodeByPath(String path);

    List<Item> findAll();

    //删除产品
    int deleteItem(Item item);

    //删除多个产品
    int deleteItems(List<String> itemCodeList);

    //価格変更
    int setItemSalePrice(Item item);

    //新itemcode查询
    Item findItemByItemCode(Item item);

    //多个itemid查询
    List<Item> findItemByItemCodes(List<String> stringList);

    //多个itemid查询
    List<Item> findItemByItemCodeAll(List<String> stringList);

    //删除option值
    int deleteOption(Item item);

    //更新option值
    int updateOption(Item item);

    //csv文件更新数据或追加数据
    int updateItemByCsv(Item item);

    //修改itemflog值
    int setItemFlog(List<Item> itemList);

    //---------------------------------------------------------------------------------------------------------

    //数据错误时做更新使用
    void setdate();
}
