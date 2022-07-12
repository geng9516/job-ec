package con.chin.service.impl;

import con.chin.mapper.ItemCategoryMapper;
import con.chin.util.SetDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import con.chin.pojo.ItemCategory;
import con.chin.service.ItemCategoryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemCategoryServiceImpl implements ItemCategoryService {

    @Autowired
    private ItemCategoryMapper itemCategoryMapper;

    //检索path
    @Override
    public Integer findItemCategoryByPath(Map<String, String> map) {
        return itemCategoryMapper.findItemCategoryByPath(map);
    }

    //检索path
    @Override
    public String selectItemCategory(ItemCategory itemCategory) {
        return itemCategoryMapper.selectItemCategory(itemCategory);
    }

    //检索byLikePath
    @Override
    public List<ItemCategory> selectItemCategoryByLikePath(Map<String, String> map) {
        return itemCategoryMapper.selectItemCategoryByLikePath(map);
    }

    //检索by别名
    @Override
    public List<ItemCategory> selectItemCategoryByCategoryAlias(String categoryAlias) {
        return itemCategoryMapper.selectItemCategoryByCategoryAlias(categoryAlias);
    }

    //保存path
    @Override
    public Integer saveItemCategory(List<ItemCategory> categoryList) {
        return itemCategoryMapper.saveItemCategory(categoryList);
    }

    //    更新path
    @Override
    public Integer updateItemCategory(List<ItemCategory> categoryList) {

        for (ItemCategory itemCategory : categoryList) {
            List<ItemCategory> updateCategoryList = new ArrayList<>();
            List<ItemCategory> savaCategoryList = new ArrayList<>();

            String itemPath = itemCategory.getItempath();
            itemPath = itemPath.substring(0, itemPath.lastIndexOf(":"));
            //设置产品种类番号(条件はpathとサイト名)
            Map<String, String> map = new HashMap<>();
            map.put("itempath", itemPath);
            map.put("kinds", "yahoo");
            List<ItemCategory> itemCategoryList = selectItemCategoryByLikePath(map);

            List<ItemCategory> itemCategoryList1 = new ArrayList<>();
            for (ItemCategory category : itemCategoryList) {
                String itemPath1 = category.getItempath();
                itemPath1 = itemPath1.replaceAll(itemPath + ":", "");
                if (itemPath1.contains(":")) {
                    continue;
                }
                itemCategoryList1.add(category);
            }

            String str = SetDataUtil.getRandom26Alphabet();
            //设置产品种类番号(条件はpathとサイト名)
            Map<String, String> map1 = new HashMap<>();
            map1.put("categoryAlias", str);
            map1.put("itempath", itemCategory.getItempath());
            List<ItemCategory> itemCategoryList2 = selectItemCategoryByCategoryAlias(str);
            while (itemCategoryList2.size() > 0) {
                itemCategoryList2 = null;
                //设置产品种类番号(条件はpathとサイト名)
                Map<String, String> map2 = new HashMap<>();
                map2.put("categoryAlias", str);
                map1.put("itempath", itemCategory.getItempath());
                str = SetDataUtil.getRandom26Alphabet();
                itemCategoryList2 = selectItemCategoryByCategoryAlias(str);
            }
            for (int i = 0; i < itemCategoryList1.size(); i++) {

                int f = i;
                String str1 = str + "-" + (f + 1);
                itemCategoryList1.get(i).setCategoryAlias(str1);
                updateCategoryList.add(itemCategoryList1.get(i));
            }
            if (updateCategoryList.size() > 0) {
                itemCategoryMapper.updateItemCategory(updateCategoryList);
            }

        }
        Integer flog = null;
//        if(savaCategoryList.size() > 0){
//            flog = saveItemCategory(savaCategoryList);
//        }
        return flog;
    }

    //更新path
//    @Override
//    public Integer updateItemCategory(List<ItemCategory> categoryList) {
//        List<ItemCategory> updateCategoryList = new ArrayList<>();
//        List<ItemCategory> savaCategoryList = new ArrayList<>();
//        for (ItemCategory itemCategory : categoryList) {
//            //设置产品种类番号(条件はpathとサイト名)
//            String itempath = selectItemCategory(itemCategory);
//            if(itempath != null && !"".equals(itempath)){
//                updateCategoryList.add(itemCategory);
//            }else {
//                savaCategoryList.add(itemCategory);
//            }
//        }
//        Integer flog = null;
//        if(savaCategoryList.size() > 0){
//            flog = saveItemCategory(savaCategoryList);
//        }
//        if(updateCategoryList.size() > 0){
//            flog = itemCategoryMapper.updateItemCategory(updateCategoryList);
//        }
//
//        return flog;
//    }


}
