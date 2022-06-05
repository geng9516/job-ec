package con.chin.mapper;

import con.chin.pojo.ItemKeyword;
import javassist.compiler.ast.Keyword;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ItemKeywordMapper{

    //保存关键字
    int saveKeyword(ItemKeyword itemKeyword);

    //检索关键字
    List<ItemKeyword> findItemKeyword(ItemKeyword itemKeyword);

    //更新keyword使用次数
    void updateItemKeywordCount(ItemKeyword itemKeyword);

    List<ItemKeyword> findGoodItemKeyword(ItemKeyword itemKeyword);
}
