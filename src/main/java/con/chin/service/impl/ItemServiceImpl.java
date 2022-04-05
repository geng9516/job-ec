package con.chin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import con.chin.mapper.ItemCategoryMapper;
import con.chin.mapper.ItemMapper;
import con.chin.mapper.OrderItemInfoMapper;
import con.chin.pojo.OrderItemInfo;
import con.chin.pojo.query.ItemInfoQuery;
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

    @Autowired
    private OrderItemInfoMapper orderItemInfoMapper;

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
    public PageInfo<Item> findItemBySearchConditions(ItemInfoQuery itemInfoQuery) {
        //启动PageInfo
        PageHelper.startPage(itemInfoQuery.getPageNum(), itemInfoQuery.getPageSize());
        return new PageInfo<Item>(itemMapper.findItemBySearchConditions(itemInfoQuery));
    }

    //SiteShop查询
    @Override
    public PageInfo<Item> findItemBySiteShop(ItemInfoQuery itemInfoQuery) {
        //启动PageInfo
        PageHelper.startPage(itemInfoQuery.getPageNum(), itemInfoQuery.getPageSize());
        return new PageInfo<Item>(itemMapper.findItemBySearchConditions(itemInfoQuery));
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
        //判断编辑层次
        int flog = 0;
        //item不为空的时候做更新
        //itemName不为空时
        if (item.getItemName() != null && item.getItemName() != "") {
            oldItem.setItemName(item.getItemName());
            flog++;
        } else if (oldItem.getItemName() != null && oldItem.getItemName() != "") {
            flog++;
        }
        //进货价
        if (item.getPurchasePrice() != null) {
            oldItem.setPurchasePrice(item.getPurchasePrice());
            flog++;
        } else if (oldItem.getPurchasePrice() != null) {
            flog++;
        }
        //送料
        if (item.getDelivery() != null) {
            oldItem.setDelivery(item.getDelivery());
            flog++;
        } else if (oldItem.getDelivery() != null) {
            flog++;
        }
        //卖价
        if (item.getSalePrice() != null) {
            oldItem.setSalePrice(item.getSalePrice());
            flog++;
        } else if (oldItem.getSalePrice() != null) {
            flog++;
        }
        //进货url1
        if (item.getUrl1() != null && item.getUrl1() != "") {
            oldItem.setUrl1(item.getUrl1());
            flog++;
        } else if (oldItem.getUrl1() != null && oldItem.getUrl1() != "") {
            flog++;
        }
        //进货url2
        if (item.getUrl2() != null && item.getUrl2() != "") {
            oldItem.setUrl2(item.getUrl2());
            flog++;
        } else if (oldItem.getUrl2() != null && oldItem.getUrl2() != "") {
            flog++;
        }
        //进货url3
        if (item.getUrl3() != null && item.getUrl3() != "") {
            oldItem.setUrl3(item.getUrl3());
            flog++;
        } else if (oldItem.getUrl3() != null && oldItem.getUrl3() != "") {
            flog++;
        }
        //当前时间
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        //如果是flog>=10并且产品还未失效的话就属于编辑完成  产品是否属于编辑
        if (flog >= 5 && "2099-12-31 23:59:59".equals(oldItem.getEndDate())) {
            oldItem.setFlog(1);
            oldItem.setUpdatetime(now);
            return itemMapper.setItemSalePrice(oldItem);
        } else {
            oldItem.setUpdatetime(now);
            return itemMapper.setItemSalePrice(oldItem);
        }
    }

    //新itemcode查询
    @Override
    public Item findItemByItemCode(Item item) {
        return itemMapper.findItemByItemCode(item);
    }

    //多个itemid查询
    @Override
    public List<Item> findItemByItemCodes(List<String> itemCodeList) {

        //csv下载后状态改为3
        for (String itemCode : itemCodeList) {
            //查询条件
            Item item = new Item();
            item.setItemCode(itemCode);
            //检索需要下载的item是否是已编辑的
            //保存检索到的item
            Item resItem = new Item();
            resItem = itemMapper.findItemByItemCode(item);
            //下载的item的状态不是未编辑的话改为已下载状态
            if (resItem.getFlog() == 1) {
                //2 表示已下载
                resItem.setFlog(2);
                //把状态改为已下载
                itemMapper.setItemSalePrice(resItem);
            }
        }
        //返回需要下载的iteminfo
        return itemMapper.findItemByItemCodes(itemCodeList);
    }

    //削除option值
    @Override
    public int deleteOption(Item item) {
//        //当前时间
//        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Item oldItem = new Item();
        oldItem = this.findItemByItemCode(item);
//        oldItem.setUpdatetime(now);

        if (item.getOption1() != null && item.getOption1() != "") {
            oldItem.setOption1(null);
            oldItem.setValue1(null);
        }
        if (item.getOption2() != null && item.getOption2() != "") {
            oldItem.setOption2(null);
            oldItem.setValue2(null);
        }
        if (item.getOption3() != null && item.getOption3() != "") {
            oldItem.setOption3(null);
            oldItem.setValue3(null);
        }
        if (item.getOption4() != null && item.getOption4() != "") {
            oldItem.setOption4(null);
            oldItem.setValue4(null);
        }
        if (item.getOption5() != null && item.getOption5() != "") {
            oldItem.setOption5(null);
            oldItem.setValue5(null);
        }
        int res = itemMapper.deleteOption(oldItem);
        if(res == 1){
            System.out.println("成功");
        }else {
            System.out.println("失敗");
        }

        return itemMapper.deleteOption(oldItem);
    }

    //更新option值
    @Override
    public int updateOption(Item item) {
//        //当前时间
//        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Item oldItem = new Item();
        oldItem = this.findItemByItemCode(item);
//        oldItem.setUpdatetime(now);

        if (item.getOption1() != null && item.getOption1() != "") {
            oldItem.setOption1(item.getOption1());
            oldItem.setValue1(item.getValue1());
        }
        if (item.getOption2() != null && item.getOption2() != "") {
            oldItem.setOption2(item.getOption2());
            oldItem.setValue2(item.getValue2());
        }
        if (item.getOption3() != null && item.getOption3() != "") {
            oldItem.setOption3(item.getOption3());
            oldItem.setValue3(item.getValue3());
        }
        if (item.getOption4() != null && item.getOption4() != "") {
            oldItem.setOption4(item.getOption4());
            oldItem.setValue4(item.getValue4());
        }
        if (item.getOption5() != null && item.getOption5() != "") {
            oldItem.setOption5(item.getOption5());
            oldItem.setValue5(item.getValue5());
        }
        return itemMapper.updateOption(oldItem);
    }

//---------------------------------------------------------------------------------------------------------

    //数据错误时做更新使用
    @Override
    public void setdate() {
//        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        int i = 0;
//        List<Item> itemList = this.findAll();
        String[] s = "a072395e097 9750f6cf3bf 07d114b2e34 00fd0cb2bd9 0b2fa53549c 00cb0ba7fbe 600738a1094 4cf7781f75f 319f61f9f42 462356a9ffe 3b13df1bf63 0601ed6e6c3 8ab3d92b61d 76adf29fba9 a160e565af1 ea1f05dace4 7bc2cddcbbf ffc193e9f32 3b609be1e91 f3f73ed4a00 7bc0f1ee323 075c57abf67 3ee43468fdd fa13ccf07d2 9e3b5e4503a 6d7e453fd5e 9e4b831bb0c 9bc59b50464 88ee297af79 6.56948E+11 b3672aa108b a4c357ca471 4f8af733b06 5807cc8ccc3 e334d261442 13417fae780 33358146657 1d0ca5fa0ab 9070f13690c f65c199e262 973fe4021c0 b1793d3b091 8147f9daa09 045ebc44133 94907a4121e d1a8764a99f 489924d0073 9a4cfe527d8 794fdfa154c 1770c76d8b3 7b9b185acb3 e9ff5451ad1 b89806dee97 5ca2a8e882f 115d6497659 f444f4ccc17 4585e2cfef1 5c8d5ae7965 fa02a22b1f2 213396d43eb 673bb3e50b4 e7fe49f6440 a29c081cb82 0d5526955db e3dff173750 109a4fe1fc2 0f243658cd3 631c7bc9060 6d4dbb87686 3c2337edffa 78e34a93e16 012a540af63 92104be6450 dbf4a714a21 d9b3a76da48 916ac39166b 83afeaa5b18 16af9db686d a12dd690ece ef0e4f0187d 6d707e326ca 2ba74389796 934a1de69a7 f3f23587a13 1fea111ad8b 6034a86f431 fdcab7feed8 75d4a923bac 3e550a7504f ed58829340a de94d12dfc0 d1e1f52434b 8eadd64dcad d65fda0f138 33cd5f563aa e7bcff0dbd2 235f1dc1e29 fed1387486e fd8af0af788 1ad58d04c2d e11f519a0a5 a337676f354 01b5b9b28a2 b26e290ab7b 5ac94d8ce7d 5c248dd79f5".split(" ");
        List<OrderItemInfo> orderItemInfoList = orderItemInfoMapper.findOrderItemInfo();
        for (int e = 0; e < orderItemInfoList.size(); e++) {
            orderItemInfoList.get(e).setItemId(s[e]);
            orderItemInfoMapper.updateOrderItemInfoById(orderItemInfoList.get(e));
            System.out.println("完成 " + i++ + " 件了");
        }
    }


}
