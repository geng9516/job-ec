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

    //保存产品和更新
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

    //查询旧itemCode的产品
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

    //删除产品
    @Override
    public int deleteItem(Item item) {
        return itemMapper.deleteItem(item);
    }

    //删除多个产品
    @Override
    public int deleteItems(List<String> itemCodeList) {
        //结果值
        int res = 0;
        //封装查询条件
        Item item = new Item();
        for (String itemCode : itemCodeList) {
            item.setItemCode(itemCode);
            //调用删除一个item方法
            res = itemMapper.deleteItem(item);
        }
        //返回结果
        return res;
    }

    //item情報変更
    @Override
    @Transactional
    public int setItemSalePrice(Item item) {
        Item oldItem = new Item();
        oldItem = this.findItemByItemCode(item);
        //item不为空的时候做更新
        //进货价
        if (item.getPurchasePrice() != null) {
            oldItem.setPurchasePrice(item.getPurchasePrice());
        }

        //送料
        if (item.getDelivery() != null) {
            oldItem.setDelivery(item.getDelivery());
        }

        //卖价
        if (item.getSalePrice() != null) {
            oldItem.setSalePrice(item.getSalePrice());
        }

        //进货url1
        if (item.getUrl1() != null && item.getUrl1() != "") {
            oldItem.setUrl1(item.getUrl1());
        }

        //进货url2
        if (item.getUrl2() != null && item.getUrl2() != "") {
            oldItem.setUrl2(item.getUrl2());
        }

        //进货url3
        if (item.getUrl3() != null && item.getUrl3() != "") {
            oldItem.setUrl3(item.getUrl3());
        }

        //产品是否属于编辑
        //判断编辑层次
        int flog = 0;
        //进货价
        if (oldItem.getPurchasePrice() != null) {
            flog++;
        }
        //送料
        if (oldItem.getDelivery() != null) {
            flog++;
        }
        //卖价
        if (oldItem.getSalePrice() != null) {
            flog++;
        }
        //进货url1
        if (oldItem.getUrl1() != null && oldItem.getUrl1() != "") {
            flog++;
        }
        //进货url2
        if (oldItem.getUrl2() != null && oldItem.getUrl2() != "") {
            flog++;
        }
        //进货url3
        if (oldItem.getUrl3() != null && oldItem.getUrl3() != "") {
            flog++;
        }
        //产品名称
        if (oldItem.getItemName() != null && oldItem.getItemName() != "") {
            flog++;
        }
        //option1
        if (oldItem.getOption1() != null && oldItem.getOption1() != "") {
            flog++;
        }
        //value1
        if (oldItem.getValue1() != null && oldItem.getValue1() != "") {
            flog++;
        }
        //标题
        if (oldItem.getHeadline() != null & oldItem.getHeadline() != "") {
            flog++;
        }
        //产品详情信息
        if (oldItem.getCaption() != null && oldItem.getCaption() != "") {
            flog++;
        }
        //产品说明
        if (oldItem.getExplanation() != null && oldItem.getExplanation() != "") {
            flog++;
        }
        //如果是flog>=10并且产品还未失效的话就属于编辑完成
        if (flog >= 10 && oldItem.getEndDate() == "2099-12-31 23:59:59") {
            oldItem.setFlog(1);
            return itemMapper.setItemSalePrice(oldItem);
        } else {
            return itemMapper.setItemSalePrice(oldItem);
        }
    }

    //新itemcode查询
    @Override
    public Item findItemByItemCode(Item item) {
        return itemMapper.findItemByItemCode(item);
    }

//---------------------------------------------------------------------------------------------------------

    //数据错误时做更新使用
    @Override
    public void setdate() {
//        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<Item> itemList = this.findAll();
        for (Item item : itemList) {
            item.setImage("/images/itemphoto/" + item.getItemCode() + ".jpg");
            itemMapper.setdate(item);
            System.out.println(item.getItemCode());
        }
    }


}
