package con.chin.mapper;

import con.chin.pojo.ItemCategory;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ItemCategoryMapper{

    //检索path
    Integer findItemCategoryByPath(Map<String,String> map);

    //检索path
    String selectItemCategory(ItemCategory itemCategory);

    //检索byLikePath
    List<ItemCategory> selectItemCategoryByLikePath(Map<String,String> map);

    //检索by别名
    List<ItemCategory> selectItemCategoryByCategoryAlias(String categoryAlias);

    //保存path
    Integer saveItemCategory(List<ItemCategory> categoryList);

    //更新path
    Integer updateItemCategory(List<ItemCategory> categoryList);

}
