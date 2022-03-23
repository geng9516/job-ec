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

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
        //当前时间
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        //设置产品种类番号(条件はpathとサイト名)
        Map<String, String> map = new HashMap<>();
        map.put("itempath", item.getItemPath());
        map.put("shopName", item.getSiteName());
        Integer itemCategorCode = itemCategoryMapper.findItemCategoryByPath(map);
        item.setItemCategoryCode(itemCategorCode);

        //设置检索条件
        Item item1 = new Item();
        item1.setOldItemCode(item.getOldItemCode());
        Item oldItem = itemMapper.findItem(item1);

        //保存产品数据
        if (oldItem == null) {
            int res = itemMapper.saveItem(item);
            System.out.println("登录一件产品:  " + item.getItemCode() + "   时间为 : " + now);
            return res;
        }
        //更新产品数据
        //如果是更新并且itemimage是空的就赋值进去
        if (oldItem.getImage() == null) {
            item.setImage("/images/itemphoto/" + oldItem.getItemCode() + ".jpg");
        }
        //作成時間更新しないように
        item.setCreated(null);
        //更新時間
        if (oldItem.getUpdatetime() == null) {
            item.setUpdatetime(now);
        }
        //商品情報更新
        itemMapper.updateItem(item);
        System.out.println("更新一件产品:  " + oldItem.getItemCode() + "   时间为 : " + now);
        //为了更新的话不需要更新照片
        return -1;
    }

    //item更新
    @Override
    public void updateItem(Item item) {
        itemMapper.updateItem(item);
    }

    //已编辑并且没有失效的产品
    @Override
    public List<Item> findAllValidItem() {
        return itemMapper.findAllValidItem();
    }

    //
    @Override
    public Item findItem(Item item) {
        return itemMapper.findItem(item);
    }

    //分页
    @Override
    public PageInfo<Item> findItemBySearchConditions(ItemQuery itemQuery) {
        //启动PageInfo
        PageHelper.startPage(itemQuery.getPageNum(), itemQuery.getPageSize());
        return new PageInfo<Item>(itemMapper.findItemBySearchConditions(itemQuery));
    }

    //SiteShop查询
    @Override
    public PageInfo<Item> findItemBySiteShop(ItemQuery itemQuery) {
        //启动PageInfo
        PageHelper.startPage(itemQuery.getPageNum(), itemQuery.getPageSize());
        return new PageInfo<Item>(itemMapper.findItemBySiteShop(itemQuery));
    }

    //获取产品code
    @Override
    public List<String> findItemCodeByPath(String path) {
        return itemMapper.findItemCodeByPath(path);
    }

    @Override
    public List<Item> findAll() {
        return itemMapper.findAll();
    }

    @Override
    public void setdate() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<Item> itemList = this.findAll();
        for (Item item : itemList) {
            if (item.getUpdatetime() == null) {
                item.setUpdatetime(now);
            }
            item.setEndDate("2099-12-31 23:59:59");
            itemMapper.setdate(item);
        }
    }

    //删除产品
    @Override
    public int deleteItem(Item item) {
        return itemMapper.deleteItem(item);
    }


}
