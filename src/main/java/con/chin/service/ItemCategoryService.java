package con.chin.service;

import con.chin.pojo.ItemCategory;

import java.util.List;
import java.util.Map;

public interface ItemCategoryService {

    //检索Allpath
    List<ItemCategory> findAllItemPath();

    //检索path
    Integer findItemCategoryByPath(Map<String,String> map);

    //检索path
    String selectItemCategory(ItemCategory itemCategory);

    //检索byLikePath
    List<ItemCategory> selectItemCategoryByLikePath(Map<String,String> map);

    //检索by别名
    List<ItemCategory> selectItemCategoryByCategoryAlias(String categoryAlias);

    //检索别名
    String selectCategoryAliasByItemPath(String itemPath);

    //保存path
    Integer saveItemCategory(List<ItemCategory> categoryList);

    //更新path
    Integer updateItemCategory(List<ItemCategory> categoryList);
}
