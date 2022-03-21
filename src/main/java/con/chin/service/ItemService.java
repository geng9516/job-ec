package con.chin.service;

import con.chin.pojo.Item;

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

    //获取产品code
    List<String> findItemCodeByPath(String path);



}
