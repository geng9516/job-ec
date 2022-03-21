package con.chin.service;

import con.chin.pojo.ItemCategory;

import java.util.List;
import java.util.Map;

public interface ItemCategoryService {

    //アイテムカテゴリー保存
    void save(ItemCategory itemCategory);

    //アイテムキーワード検索
    List<ItemCategory> findItemCategory(ItemCategory itemCategory);

    Integer findItemCategoryByPath(Map<String,String> map);
}
