package con.chin.service;

import com.github.pagehelper.PageInfo;
import con.chin.pojo.Item;
import con.chin.pojo.query.ItemInfoQuery;

import java.util.List;
import java.util.Map;

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

    //SiteShop查询并下载
    List<Item> downloadFindItemBySiteShop(Map<String, String> map);

    //获取产品code
    List<String> findItemCodeByPath(String path);

    List<Item> findAll();

    //删除产品
    int deleteItem(Item item);

    //删除多个产品
    int deleteItems(List<String> itemCodeList);

    //修改值
    int setItemInfo(Item item);

    //修改多个值
    Integer setIteminfos(List<Item> itemList, Map<String, String> map);

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
    int updateItemByCsv(List<Item> itemList);

    //修改itemflog值
    int setItemFlog(List<Item> itemList);

    //修改多个itemflog值
    int setItemFlogs(List<Item> itemList);

    //获取新爬取的产品
    List<Item> findItemByStatus(Integer flog);

    //下载查询结果集
    List<Item> downloadFindItemBysearchConditions(Map<String, String> map);


    //---------------------------------------------------------------------------------------------------------

    //数据错误时做更新使用
    void setdate(Item item);


}
