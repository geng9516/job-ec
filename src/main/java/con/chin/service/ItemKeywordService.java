package con.chin.service;

import con.chin.mapper.ItemKeywordMapper;
import con.chin.pojo.ItemKeyword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ItemKeywordService {

    //アイテムキーワード保存
    void save(List<ItemKeyword> itemKeywordList);

    //アイテムキーワード検索
    List<ItemKeyword> findItemKeyword(ItemKeyword itemKeyword);

    void updateItemKeywordCount(ItemKeyword itemKeyword);


}
