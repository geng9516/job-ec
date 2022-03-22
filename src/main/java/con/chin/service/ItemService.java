package con.chin.service;

import com.github.pagehelper.PageInfo;
import con.chin.pojo.Item;
import con.chin.pojo.query.ItemQuery;

import java.util.List;

public interface ItemService {

    //アイテムを保存
    int saveItem(Item item);

    //アイテム更新
    void updateItem(Item item);

    //アイテム検索ALL
    List<Item> findAllItem();

    //アイテム検索
    Item findItem(Item item);

    PageInfo<Item> findItemByItemCode(ItemQuery itemQuery);

    //获取产品code
    List<String> findItemCodeByPath(String path);


    void setdata();
}
