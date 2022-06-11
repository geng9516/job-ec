package con.chin.service.impl;

import cn.hutool.core.util.ZipUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import con.chin.mapper.ItemCategoryMapper;
import con.chin.mapper.ItemMapper;
import con.chin.mapper.OrderItemInfoMapper;
import con.chin.pojo.ItemKeyword;
import con.chin.pojo.OrderItemInfo;
import con.chin.pojo.SiteShop;
import con.chin.pojo.query.ItemInfoQuery;
import con.chin.service.ItemCategoryService;
import con.chin.service.ItemKeywordService;
import con.chin.util.ItemInfoCsvExportUtil;
import con.chin.util.ItemPhotoCopyUtil;
import con.chin.util.SetDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import con.chin.pojo.Item;
import con.chin.service.ItemService;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private ItemCategoryMapper itemCategoryMapper;

    @Autowired
    private ItemCategoryService itemCategoryService;

    @Autowired
    private ItemKeywordService itemKeywordService;

    @Autowired
    private HttpSession httpSession;

    //保存产品和更新
    @Override
    public int saveItem(Item item) {

        //开始时间
        long start = System.currentTimeMillis();
        //当前时间
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (item.getItemPath() != null) {
            //设置产品种类番号(条件はpathとサイト名)
            Map<String, String> map = new HashMap<>();
            map.put("itempath", item.getItemPath());
            map.put("shopName", item.getSiteName());
            Integer itemCategorCode = itemCategoryMapper.findItemCategoryByPath(map);
            item.setItemCategoryCode(itemCategorCode);
        } else {
            item = SetDataUtil.setOptionAndValue(item);
        }


        //设置检索条件
        Item item1 = new Item();
        item1.setOldItemCode(item.getOldItemCode());
        Item oldItem = itemMapper.findItem(item1);

        //保存产品数据
        if (oldItem == null) {
            int res = itemMapper.saveItem(item);
            long end = System.currentTimeMillis();
            System.out.println("登录一件产品:    " + item.getItemCode() + "   时间为 : " + now + "    耗时：" + (end - start) + " ms");
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
        if (oldItem.getFlog() != 0 || oldItem.getFlog() != 5) {
            itemMapper.updateItem(item);
            //结束时间
            long end = System.currentTimeMillis();
            System.out.println("更新一件产品:    " + oldItem.getItemCode() + "   时间为 : " + now + "    耗时：" + (end - start) + " ms");
        } else {
            //结束时间
            long end = System.currentTimeMillis();
            System.out.println("不需要更新:    " + oldItem.getItemCode() + "   时间为 : " + now + "    耗时：" + (end - start) + " ms");
        }

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

    //SiteShop查询并下载
    @Override
    public List<Item> downloadFindItemBySiteShop(String siteShop) {
        return itemMapper.downloadFindItemBySiteShop(siteShop);
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

    //写真保存パス
    @Value("${ITEMPHOTO}")
    private String itemPhoto;

    //中国語写真保存パス
    @Value("${ITEMPHOTO2}")
    private String itemPhoto2;

    //删除产品
    @Override
    public int deleteItem(Item item) {
        //删除照片时的条件设置
        List<String> stringList = new ArrayList<>();
        stringList.add(item.getItemCode());
        //开始时间
        long start = System.currentTimeMillis();
        //调用删除照片方法
        ItemPhotoCopyUtil.read3(stringList, itemPhoto);
        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("产品code为: " + item.getItemCode() + " 的产品删除成功    耗时：" + (end - start) + " ms");
        return itemMapper.deleteItem(item);
    }

    @Value("${DETELECSVPATH}")
    private String deleteCsvPath;

    //删除多个产品
    @Override
    public int deleteItems(List<String> itemCodeList) {
        //结果值
        int res = 0;
        //取得要删除的csv文件
        List<Item> itemList = itemMapper.findItemByItemCodes(itemCodeList);
        //调用删除多个的
        res = itemMapper.deleteItems(itemCodeList);
        //开始时间
        long start = System.currentTimeMillis();
        //删除照片
        //调用删除照片方法
        ItemPhotoCopyUtil.read3(itemCodeList, itemPhoto);
//        ItemPhotoCopyUtil.read3(itemCodeList);
        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("删除多个产品照片成功    总耗时：" + (end - start) + " ms");
        //导出删除产品的csv文件
        ItemInfoCsvExportUtil.exportYahooItemInfoToCsv(itemList, deleteCsvPath, "data_del");
        //返回结果
        return res;
    }

    //item情報変更
    @Override
    @Transactional
    public int setItemInfo(Item item) {
        Item oldItem = new Item();
        oldItem = this.findItemByItemCode(item);
        //item不为空的时候做更新
        //headline
        if (item.getHeadline() != null && !"".equals(item.getHeadline())) {
            oldItem.setHeadline(item.getHeadline());
        }
        //itemName不为空时
        if (item.getItemName() != null && !"".equals(item.getItemName())) {
            oldItem.setItemName(item.getItemName());
        }
        //产品类别
        if (item.getItemPath() != null && !"".equals(item.getItemPath())) {
            oldItem.setItemPath(item.getItemPath());
        }
        //产品类别code
        if (item.getItemCategoryCode() != null) {
            oldItem.setItemCategoryCode(item.getItemCategoryCode());
        }
        //产品详细信息
        if (item.getExplanation() != null && !"".equals(item.getExplanation())) {
            oldItem.setExplanation(item.getExplanation());
        }
        //卖价
        if (item.getSalePrice() != null) {
            oldItem.setSalePrice(item.getSalePrice());
        }
        //进货url1
        if (item.getUrl1() != null && !"".equals(item.getUrl1())) {
            oldItem.setUrl1(item.getUrl1());
        }
        //进货url2
        if (item.getUrl2() != null && !"".equals(item.getUrl2())) {
            oldItem.setUrl2(item.getUrl2());
        }
        //进货url3
        if (item.getUrl3() != null && !"".equals(item.getUrl3())) {
            oldItem.setUrl3(item.getUrl3());
        }
        if (item.getOption1() != null && !"".equals(item.getOption1())) {
            oldItem.setOption1(item.getOption1());
            oldItem.setValue1(item.getValue1());
        }
        if (item.getOption2() != null && !"".equals(item.getOption2())) {
            oldItem.setOption2(item.getOption2());
            oldItem.setValue2(item.getValue2());
        }
        if (item.getOption3() != null && !"".equals(item.getOption3())) {
            oldItem.setOption3(item.getOption3());
            oldItem.setValue3(item.getValue3());
        }
        if (item.getOption4() != null && !"".equals(item.getOption4())) {
            oldItem.setOption4(item.getOption4());
            oldItem.setValue4(item.getValue4());
        }
        if (item.getOption5() != null && !"".equals(item.getOption5())) {
            oldItem.setOption5(item.getOption5());
            oldItem.setValue5(item.getValue5());
        }
        //当前时间
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        oldItem.setUpdatetime(now);
        return itemMapper.setItemInfo(oldItem);

    }

    //修改多个值
    @Override
    public int setIteminfos(List<Item> itemList, Map<String, String> map) {
        for (Item item : itemList) {
//------------------------------------------------------------------------
            //变量赋值
            //产品名
            String itemName = item.getItemName();
            //headline
            String headline = item.getHeadline();
            //卖价
            Integer salePrice = item.getSalePrice();
            //url1
            String url1 = item.getUrl1();
            //url2
            String url2 = item.getUrl2();
            //url3
            String url3 = item.getUrl3();
            //option1
            String option1 = item.getOption1();
            //option2
            String option2 = item.getOption2();
            //option3
            String option3 = item.getOption3();
            //option4
            String option4 = item.getOption4();
            //option5
            String option5 = item.getOption5();
            //value1
            String value1 = item.getValue1();
            //value2
            String value2 = item.getValue2();
            //value3
            String value3 = item.getValue3();
            //value4
            String value4 = item.getValue4();
            //value5
            String value5 = item.getValue5();
            //编辑状态
            String explanationKeyword = "";
            //产品详细信息
            String explanation = item.getExplanation();
            //产品状态
            Integer itemFlog = item.getFlog();
//------------------------------------------------------------------------
            //itemPath
            String itemPath = map.get("itemPath");
            if (itemPath != null && !"".equals(itemPath)) {
                if (itemFlog != null && itemFlog == 5 && itemPath != null && !"".equals(itemPath)) {
                    //设置产品种类番号(条件はpathとサイト名).

                    Map<String, String> map1 = new HashMap<>();
                    map.put("itempath", itemPath);
                    map.put("shopName", "yahoo");
                    Integer itemCategorCode = itemCategoryService.findItemCategoryByPath(map1);
                    item.setItemCategoryCode(itemCategorCode);
                    ItemKeyword itemKeyword = new ItemKeyword();
                    itemKeyword.setProductCategory(itemPath);
                    List<ItemKeyword> itemKeyword1 = itemKeywordService.findGoodItemKeyword(itemKeyword);
                    for (ItemKeyword keyword : itemKeyword1) {
                        explanationKeyword += keyword.getKeyword() + " ";
                    }
                    if (!explanation.contains("関連キーワード")) {
                        explanation = explanation + "\n" + "\n" +
                                "■関連キーワード：" + "\n" +
                                explanationKeyword;
                    }
                }
            }

            //产品名字
            itemName = itemName.replaceAll("　", " ");
            if (itemName != null && !"".equals(itemName) && !itemName.contains(" ")) {
                itemName = explanationKeyword;
                //把大写的空格改为小写的
                itemName = itemName.replaceAll("　", " ");
//            item.setItemName(itemName);
            } else if (itemName != null && !"".equals(itemName)) {
                //把大写的空格改为小写的
                itemName = itemName.replaceAll("　", " ");
                item.setItemName(itemName);
            }
            //如果headline有值的话
            if (headline != null && !"".equals(headline)) {
                //把大写的空格改为小写的
                headline = headline.replaceAll("　", " ");
                item.setHeadline(headline);
            } else {
                headline = explanationKeyword.replaceAll("　", " ");

                if (headline.length() > 30 && headline.contains(" ")) {
                    //把产品名称的长度调整
                    headline = SetDataUtil.setStrLength(headline, 30);
                }
                item.setHeadline(headline);
            }
            //商品情報
            if (explanation != null && !"".equals(explanation)) {
                //把数据中中文改为日文
                explanation = SetDataUtil.setDatetoJapanese(explanation);
                item.setExplanation(explanation);
            }
            //产品类别
            if (itemPath != null && !"".equals(itemPath)) {
                item.setItemPath(itemPath);
            }
            //如果卖价有修改的话
            if (salePrice != null) {
                Integer salePrice1 = salePrice;
                if (salePrice1 < 250) {
                    salePrice1 = SetDataUtil.setSalePrice(salePrice1);
                }
                item.setSalePrice(salePrice1);
            }
            //如果URL1有修改的话
            if (url1 != null && !"".equals(url1)) {
                item.setUrl1(url1);
            }
            //如果URL2有修改的话
            if (url2 != null && !"".equals(url2)) {
                item.setUrl2(url2);
            }
            //如果URL3有修改的话
            if (url3 != null && !"".equals(url3)) {
                item.setUrl3(url3);
            }
            //把option和value的值进行设定
            if ((option1 != null && !"".equals(option1)) && (value1 != null && !"".equals(value1))) {
                //有全角或半角空格全部去除
                option1 = option1.replaceAll("　", "").replaceAll(" ", "");
                option1 = SetDataUtil.setDatetoJapanese(option1);
                //有全角半角,",","、","/","-"全部去除
                value1 = value1.replaceAll("　", " ").replaceAll("、", " ").replaceAll(",", " ").replaceAll("/", " ").replaceAll("-", " ");
                value1 = SetDataUtil.setDatetoJapanese(value1);
                item.setOption1(option1);
                item.setValue1(value1);
            }
            if ((option2 != null && !"".equals(option2)) && (value2 != null && !"".equals(value2))) {
                //有全角或半角空格全部去除
                option2 = option2.replaceAll("　", "").replaceAll(" ", "");
                option2 = SetDataUtil.setDatetoJapanese(option2);
                //有全角半角,",","、","/","-"全部去除
                value2 = value2.replaceAll("　", " ").replaceAll("、", " ").replaceAll(",", " ").replaceAll("/", " ").replaceAll("-", " ");
                value2 = SetDataUtil.setDatetoJapanese(value2);
                item.setOption2(option2);
                item.setValue2(value2);
            }
            if ((option3 != null && !"".equals(option3)) && (value3 != null && !"".equals(value3))) {
                //有全角或半角空格全部去除
                option3 = option3.replaceAll("　", "").replaceAll(" ", "");
                option3 = SetDataUtil.setDatetoJapanese(option3);
                //有全角半角,",","、","/","-"全部去除
                value3 = value3.replaceAll("　", " ").replaceAll("、", " ").replaceAll(",", " ").replaceAll("/", " ").replaceAll("-", " ");
                value3 = SetDataUtil.setDatetoJapanese(value3);
                item.setOption3(option3);
                item.setValue3(value3);
            }
            if ((option4 != null && !"".equals(option4)) && (value4 != null && !"".equals(value4))) {
                //有全角或半角空格全部去除
                option4 = option4.replaceAll("　", "").replaceAll(" ", "");
                option4 = SetDataUtil.setDatetoJapanese(option4);
                //有全角半角,",","、","/","-"全部去除
                value4 = value4.replaceAll("　", " ").replaceAll("、", " ").replaceAll(",", " ").replaceAll("/", " ").replaceAll("-", " ");
                value4 = SetDataUtil.setDatetoJapanese(value4);
                item.setOption4(option4);
                item.setValue4(value4);
            }
            if ((option5 != null && !"".equals(option5)) && (value5 != null && !"".equals(value5))) {
                //有全角或半角空格全部去除
                option5 = option5.replaceAll("　", "").replaceAll(" ", "");
                option5 = SetDataUtil.setDatetoJapanese(option5);
                //有全角半角,",","、","/","-"全部去除
                value5 = value5.replaceAll("　", " ").replaceAll("、", " ").replaceAll(",", " ").replaceAll("/", " ").replaceAll("-", " ");
                value5 = SetDataUtil.setDatetoJapanese(value5);
                item.setOption5(option5);
                item.setValue5(value5);
            }
            //修改值
            setItemInfo(item);
        }
        return 0;
    }

    //新itemcode查询
    @Override
    public Item findItemByItemCode(Item item) {
        return itemMapper.findItemByItemCode(item);
    }

    //多个itemid查询
    @Override
    public List<Item> findItemByItemCodes(List<String> itemCodeList) {

        //返回需要下载的iteminfo
        return itemMapper.findItemByItemCodes(itemCodeList);
    }

    //多个itemid查询
    @Override
    public List<Item> findItemByItemCodeAll(List<String> itemCodeList) {
        //返回需要下载的iteminfo
        if (itemCodeList.size() > 1) {
            return itemMapper.findItemByItemCodes(itemCodeList);
        }
        Item item = new Item();
        List<Item> itemList = new ArrayList<>();
        item.setItemCode(itemCodeList.get(0));
        item = itemMapper.findItemByItemCode(item);
        //検索結果ある時だけ
        if (item != null) {
            itemList.add(item);
        }
        return itemList;

    }

    //削除option值
    @Override
    public int deleteOption(Item item) {
//        //当前时间
//        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Item oldItem = new Item();
        oldItem = this.findItemByItemCode(item);
//        oldItem.setUpdatetime(now);

        if (item.getOption1() != null && !"".equals(item.getOption1())) {
            oldItem.setOption1(null);
            oldItem.setValue1(null);
        }
        if (item.getOption2() != null && !"".equals(item.getOption2())) {
            oldItem.setOption2(null);
            oldItem.setValue2(null);
        }
        if (item.getOption3() != null && !"".equals(item.getOption3())) {
            oldItem.setOption3(null);
            oldItem.setValue3(null);
        }
        if (item.getOption4() != null && !"".equals(item.getOption4())) {
            oldItem.setOption4(null);
            oldItem.setValue4(null);
        }
        if (item.getOption5() != null && !"".equals(item.getOption5())) {
            oldItem.setOption5(null);
            oldItem.setValue5(null);
        }
        int res = itemMapper.deleteOption(oldItem);
        if (res == 1) {
            System.out.println("成功");
        } else {
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

        if (item.getOption1() != null && "".equals(item.getOption1())) {
            oldItem.setOption1(item.getOption1());
            oldItem.setValue1(item.getValue1());
        }
        if (item.getOption2() != null && "".equals(item.getOption2())) {
            oldItem.setOption2(item.getOption2());
            oldItem.setValue2(item.getValue2());
        }
        if (item.getOption3() != null && "".equals(item.getOption3())) {
            oldItem.setOption3(item.getOption3());
            oldItem.setValue3(item.getValue3());
        }
        if (item.getOption4() != null && "".equals(item.getOption4())) {
            oldItem.setOption4(item.getOption4());
            oldItem.setValue4(item.getValue4());
        }
        if (item.getOption5() != null && "".equals(item.getOption5())) {
            oldItem.setOption5(item.getOption5());
            oldItem.setValue5(item.getValue5());
        }
        return itemMapper.updateOption(oldItem);
    }

    //csv文件更新数据或追加数据
    @Override
    public int updateItemByCsv(Item item) {

        //当前时间
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Item oldItem = itemMapper.findItemByItemCode(item);
        if (oldItem == null) {
            item.setSiteName("gengye");
            item.setShopName("gengye");
            item.setUpdatetime(now);
            String itemCode = UUID.randomUUID().toString();
            String itemCode1 = itemCode.substring(itemCode.lastIndexOf("-") + 1, itemCode.length() - 1);
            item.setItemCode("l" + itemCode1);
//            item.setFlog(1);  不需要修改
            item.setCreated(now);
            item.setEndDate("2099-12-31 23:59:59");
            return itemMapper.saveItem(item);
        }

        oldItem.setUpdatetime(now);

        if (item.getItemPath() != null && !"".equals(item.getItemPath())) {
            oldItem.setItemPath(item.getItemPath());
        }

        if (item.getItemName() != null && !"".equals(item.getItemName())) {
            oldItem.setItemName(item.getItemName());
        }

        if (item.getItemCode() != null && !"".equals(item.getItemCode())) {
            oldItem.setItemCode(item.getItemCode());
        }

        if (item.getSalePrice() != null) {
            oldItem.setSalePrice(item.getSalePrice());
        }

        if (item.getOption1() != null && !"".equals(item.getOption1())) {
            oldItem.setOption1(item.getOption1());
        } else {
            oldItem.setOption1(null);
        }
        if (item.getOption2() != null && !"".equals(item.getOption2())) {
            oldItem.setOption2(item.getOption2());
        } else {
            oldItem.setOption2(null);
        }
        if (item.getOption3() != null && !"".equals(item.getOption3())) {
            oldItem.setOption3(item.getOption3());
        } else {
            oldItem.setOption3(null);
        }
        if (item.getOption4() != null && !"".equals(item.getOption4())) {
            oldItem.setOption4(item.getOption4());
        } else {
            oldItem.setOption4(null);
        }
        if (item.getOption5() != null && !"".equals(item.getOption5())) {
            oldItem.setOption5(item.getOption5());
        } else {
            oldItem.setOption5(null);
        }

        if (item.getValue1() != null && !"".equals(item.getValue1())) {
            oldItem.setValue1(item.getValue1());
        } else {
            oldItem.setValue1(null);
        }

        if (item.getValue2() != null && !"".equals(item.getValue2())) {
            oldItem.setValue2(item.getValue2());
        } else {
            oldItem.setValue2(null);
        }

        if (item.getValue3() != null && !"".equals(item.getValue3())) {
            oldItem.setValue3(item.getValue3());
        } else {
            oldItem.setValue3(null);
        }

        if (item.getValue4() != null && !"".equals(item.getValue4())) {
            oldItem.setValue4(item.getValue4());
        } else {
            oldItem.setValue4(null);
        }

        if (item.getValue5() != null && !"".equals(item.getValue5())) {
            oldItem.setValue5(item.getValue5());
        } else {
            oldItem.setValue5(null);
        }

        if (item.getHeadline() != null && !"".equals(item.getHeadline())) {
            oldItem.setHeadline(item.getHeadline());
        }

        if (item.getCaption() != null && !"".equals(item.getCaption())) {
            oldItem.setCaption(item.getCaption());
        }

        if (item.getExplanation() != null && !"".equals(item.getExplanation())) {
            oldItem.setExplanation(item.getExplanation());
        }

        if (item.getRelevantLinks() != null && !"".equals(item.getRelevantLinks())) {
            oldItem.setRelevantLinks(item.getRelevantLinks());
        }

        if (item.getItemCategoryCode() != null) {
            oldItem.setItemCategoryCode(item.getItemCategoryCode());
        }
        itemMapper.updateItemByCsv(oldItem);
        return -1;
    }

    @Override
    public int setItemFlog(List<Item> itemList) {
        if (itemList.size() > 0) {
            int res = 0;
            for (Item item : itemList) {
                //开始时间
                long start = System.currentTimeMillis();
                res += itemMapper.setItemFlog(item);
                //结束时间
                long end = System.currentTimeMillis();
                System.out.println("更改产品状态    总耗时：" + (end - start) + " ms");
            }
            return res;
        }
        return -1;
    }

    @Override
    public int setItemFlogs(List<Item> itemList) {
        return itemMapper.setItemFlogs(itemList);
    }

    //获取新爬取的产品
    @Override
    public List<Item> findNewDownloaded(Integer flog) {
        return itemMapper.findNewDownloaded(flog);
    }

    //下载查询结果集
    @Override
    public List<Item> downloadFindItemBysearchConditions(String searchConditions) {
        return itemMapper.downloadFindItemBysearchConditions(searchConditions);
    }


//---------------------------------------------------------------------------------------------------------

    //数据错误时做更新使用
    @Override
    public void setdate(Item item) {
        itemMapper.setdate(item);
    }


}
