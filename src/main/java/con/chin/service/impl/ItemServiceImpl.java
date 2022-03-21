package con.chin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import con.chin.mapper.ItemCategoryMapper;
import con.chin.mapper.ItemMapper;
import con.chin.pojo.query.ItemQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import con.chin.pojo.Item;
import con.chin.service.ItemService;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private ItemCategoryMapper itemCategoryMapper;

    @Override
    @Transactional
    public int saveItem(Item item) {
        //设置产品种类番号(条件はpathとサイト名)
        Map<String,String> map = new HashMap<>();
        map.put("itempath",item.getItemPath());
        map.put("shopName",item.getSiteName());
        Integer itemCategorCode = itemCategoryMapper.findItemCategoryByPath(map);
        item.setItemCategoryCode(itemCategorCode);

        //设置检索条件
        Item item1 = new Item();
        item1.setOldItemCode(item.getOldItemCode());
        Item oldItem = itemMapper.findItem(item1);

        //保存产品数据
        if(oldItem == null){
            int res = itemMapper.saveItem(item);

            return res;
        }
        //更新产品数据
        //如果是更新并且itemimage是空的就赋值进去
        if(oldItem.getImage() == null){
            item.setImage("/images/itemphoto/" + oldItem.getItemCode() + ".jpg");
        }
        item.setCreated(null);
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        item.setUpdated(now);
        itemMapper.updateItem(item);
        //为了更新的话不需要更新照片
        return -1;
    }

    @Override
    public void updateItem(Item item) {



    }

    //查取所以结果
    @Override
    public List<Item> findAllItem() {
        return itemMapper.findAllItem();
    }

    //
    @Override
    public Item findItem(Item item) {
        return itemMapper.findItem(item);
    }

    //分页
    @Override
    public PageInfo<Item> findItemByItemCode(ItemQuery itemQuery) {
        //启动PageInfo
        PageHelper.startPage(itemQuery.getPageNum(),itemQuery.getPageSize());
        return new PageInfo<Item>(itemMapper.findItemByItemCode(itemQuery));
    }

    //获取产品code
    @Override
    public List<String> findItemCodeByPath(String path) {
        return itemMapper.findItemCodeByPath(path);
    }

}
