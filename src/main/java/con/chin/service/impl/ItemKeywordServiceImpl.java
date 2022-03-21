package con.chin.service.impl;

import con.chin.mapper.ItemKeywordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import con.chin.pojo.ItemKeyword;
import con.chin.service.ItemKeywordService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class ItemKeywordServiceImpl implements ItemKeywordService {

    @Autowired
    private ItemKeywordMapper itemKeywordMapper;


    @Override
    public void save(List<ItemKeyword> itemKeywordList) {
        //检索条件设置
        ItemKeyword itemKeyword1 = new ItemKeyword();
        for (ItemKeyword itemKeyword : itemKeywordList) {
            itemKeyword1.setProductCategory(itemKeyword.getProductCategory());
            itemKeyword1.setKeyword(itemKeyword.getKeyword());
            //检索
            List<ItemKeyword> keywordList = this.findItemKeyword(itemKeyword1);

            if (keywordList.size() == 0) {
                itemKeywordMapper.saveKeyword(itemKeyword);
                keywordList.clear();
            }else{
                //更新keyword使用次数
                itemKeyword.setCountKeyword(keywordList.get(0).getCountKeyword()+1);
                this.updateItemKeywordCount(itemKeyword);
                keywordList.clear();
            }
        }
    }

    @Override
    public List<ItemKeyword> findItemKeyword(ItemKeyword itemKeyword) {

        List<ItemKeyword> KeywordList =  itemKeywordMapper.findItemKeyword(itemKeyword);

        return KeywordList;
    }

    //更新keyword使用次数
    @Override
    public void updateItemKeywordCount(ItemKeyword itemKeyword) {

        itemKeywordMapper.updateItemKeywordCount(itemKeyword);

    }
}
