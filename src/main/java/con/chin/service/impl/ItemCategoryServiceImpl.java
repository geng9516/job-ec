package con.chin.service.impl;

import con.chin.mapper.ItemCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import con.chin.pojo.ItemCategory;
import con.chin.service.ItemCategoryService;

import java.util.List;
import java.util.Map;

@Service
public class ItemCategoryServiceImpl implements ItemCategoryService {

    @Autowired
    private ItemCategoryMapper itemCategoryMapper;

    @Override
    public void save(ItemCategory itemCategory) {

    }

    @Override
    public List<ItemCategory> findItemCategory(ItemCategory itemCategory) {
        return null;
    }


    @Override
    public Integer findItemCategoryByPath(Map<String,String> map) {
        return itemCategoryMapper.findItemCategoryByPath(map);
    }


}
