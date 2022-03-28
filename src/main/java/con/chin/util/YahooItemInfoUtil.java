package con.chin.util;

import con.chin.pojo.Item;
import con.chin.pojo.ItemKeyword;
import con.chin.pojo.SiteShop;
import con.chin.service.ItemService;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class YahooItemInfoUtil {

    public static void saveItemInfo(Page page) {
        //商品详情对象
        Item item = new Item();
        //解析页面
        Html html = page.getHtml();
        //店铺名
        String storeName = html.css("div.mdBreadCrumb a").nodes().get(0).css("span", "text").toString();
        //サイト名
        item.setSiteName("yahoo");
        //ショップ名
        item.setShopName(storeName);
        //新商品code
        String itemCode = UUID.randomUUID().toString();
        String itemCode1 = itemCode.substring(itemCode.lastIndexOf("-") + 1, itemCode.length() - 1);
        item.setItemCode(itemCode1);
        //旧商品code
        List<Selectable> nodes = html.css("div#itm_cat li").nodes();
        Selectable selectable1 = nodes.get(nodes.size() - 1);
        String oldItemCode = selectable1.css("div.elRowData p", "text").toString();
        item.setOldItemCode(oldItemCode);
        //path取得
        String path = Jsoup.parse(html.css("div.mdItemSubInformation div.elRowData li.elListItem").nodes().get(1).css("ul").toString()).text();
        item.setItemPath(path.replace(" ", ":"));
        //产品名称
        String productName = html.css("div.mdItemName p.elName", "text").toString();
        item.setItemName(productName);
        //标题
        String headlin = html.css("div.mdItemName p.elCatchCopy", "text").toString();
        item.setHeadline(headlin);
        //产品价格
        item.setPrice(Integer.parseInt(html.css("span.elPriceNumber", "text").nodes().get(0).toString().replace(",", "").replace("円", "").trim()));
        //产品详情说明文
        String mdItemDescription = html.css("div.mdItemDescription p").toString().replaceAll("<br>", "\n");
        item.setExplanation(mdItemDescription.replace("<p>", "").replace("</p>", ""));
        //option选项有没有判断
        List<Selectable> optionNodes = html.css("div.elTableInner thead.elTableHeader th").nodes();
        List<Selectable> options = html.css("div.mdOrderOptions li").nodes();
        if (optionNodes.size() > 0) {
            //选择项不同
            String option = html.css("div.elHeaderMain p.elHeaderCaption", "text").toString();
            String text = html.css("div.elHeaderMain p.elHeaderNote", "text").toString();
            //value模块
            String elTableInne = html.css("div.elTableInner").toString();
            //value1
            String value1 = Jsoup.parse(elTableInne).select("thead.elTableHeader").text();
            //value2
            String value2 = Jsoup.parse(elTableInne).select("tbody.elTableBody span.elTableWord").text();
            //是否包含"×"
            if (option.contains("×")) {
                //option1
                item.setOption1(option.substring(option.lastIndexOf("×") + 1, option.length()));
                //option2
                item.setOption2(option.substring(0, option.lastIndexOf("×")));
                //value1
                item.setValue1(value1);
                //value2
                item.setValue2(value2);
            } else if (!option.contains("×") && "以下の一覧からご希望の商品を選択してください".equals(text)) {
                //option1
                item.setOption1("オプション");
                //value1
                item.setValue1(value2);
            }
            //其他的选项
            for (int i = 0; i <= options.size() - 1; i++) {
                switch (i) {
                    case 0:
                        //option2
                        if (item.getOption2() == null) {
                            String option2 = options.get(i).css("p.elOptionHeading", "text").toString();
                            item.setOption2(option2);
                            //value2
                            String value22 = Jsoup.parse(options.get(i).toString()).select("select option").text();
                            item.setValue2(value22);
                        } else {
                            //option3
                            String option3 = options.get(i).css("p.elOptionHeading", "text").toString();
                            item.setOption3(option3);
                            //value3
                            String value3 = Jsoup.parse(options.get(i).toString()).select("select option").text();
                            item.setValue3(value3);
                        }

                        break;
                    case 1:
                        if (item.getOption3() == null) {
                            //option3
                            String option3 = options.get(i).css("p.elOptionHeading", "text").toString();
                            item.setOption3(option3);
                            //value3
                            String value3 = Jsoup.parse(options.get(i).toString()).select("select option").text();
                            item.setValue3(value3);
                        } else {
                            //option4
                            String option4 = options.get(i).css("p.elOptionHeading", "text").toString();
                            item.setOption4(option4);
                            //value4
                            String value4 = Jsoup.parse(options.get(i).toString()).select("select option").text();
                            item.setValue4(value4);
                        }
                        break;
                    case 2:
                        if(item.getOption4() == null){
                            //option4
                            String option4 = options.get(i).css("p.elOptionHeading", "text").toString();
                            item.setOption4(option4);
                            //value4
                            String value4 = Jsoup.parse(options.get(i).toString()).select("select option").text();
                            item.setValue4(value4);
                        }else{
                            //option5
                            String option5 = options.get(i).css("p.elOptionHeading", "text").toString();
                            item.setOption5(option5);
                            //value5
                            String value5 = Jsoup.parse(options.get(i).toString()).select("select option").text();
                            item.setValue5(value5);
                        }
                        break;
                    case 3:
                        if(item.getOption5() == null){
                            //option5
                            String option5 = options.get(i).css("p.elOptionHeading", "text").toString();
                            item.setOption5(option5);
                            //value5
                            String value5 = Jsoup.parse(options.get(i).toString()).select("select option").text();
                            item.setValue5(value5);
                        }
                        break;
                }
            }
            //只有其他选项的话走这边
        } else {

            for (int i = 0; i <= options.size() - 1; i++) {
                switch (i) {
                    case 0:
                        //option1
                        String option1 = options.get(i).css("p.elOptionHeading", "text").toString();
                        item.setOption1(option1);
                        //value1
                        String value1 = Jsoup.parse(options.get(i).toString()).select("select option").text();
                        item.setValue1(value1);
                        break;
                    case 1:
                        //option2
                        String option2 = options.get(i).css("p.elOptionHeading", "text").toString();
                        item.setOption2(option2);
                        //value2
                        String value2 = Jsoup.parse(options.get(i).toString()).select("select option").text();
                        item.setValue2(value2);
                        break;
                    case 2:
                        //option3
                        String option3 = options.get(i).css("p.elOptionHeading", "text").toString();
                        item.setOption3(option3);
                        //value3
                        String value3 = Jsoup.parse(options.get(i).toString()).select("select option").text();
                        item.setValue3(value3);
                        break;
                    case 3:
                        //option4
                        String option4 = options.get(i).css("p.elOptionHeading", "text").toString();
                        item.setOption4(option4);
                        //value4
                        String value4 = Jsoup.parse(options.get(i).toString()).select("select option").text();
                        item.setValue4(value4);
                        break;
                    case 4:
                        //option5
                        String option5 = options.get(i).css("p.elOptionHeading", "text").toString();
                        item.setOption5(option5);
                        //value5
                        String value5 = Jsoup.parse(options.get(i).toString()).select("select option").text();
                        item.setValue5(value5);
                        break;
                }
            }
        }
        //获取带前页的URL
        item.setUrl5(page.getUrl().nodes().get(0).toString());
        item.setImage("/images/itemphoto/" + item.getItemCode() + ".jpg");
        //是否编辑(0:否,1:是)
        item.setFlog(0);
        //创建时间
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        item.setCreated(now);
        //アップデート時間
        item.setUpdatetime(now);
        //終了時間
        item.setEndDate("2099-12-31 23:59:59");
        //主照片
        List<String> photoAll = html.css("div.mdItemImage ul.elThumbnailItems").css("img", "src").all();
        //产品情报照片/SP
        //https://item-shopping.c.yimg.jp/i/l/takahashihonpo_21-030-t00247_18
        String caption = "<img src='https://item-shopping.c.yimg.jp/i/n/seiunstore_" + itemCode1 + "' width='100%'/><br>";
        for (int i = 1; i <= photoAll.size() - 1; i++) {
            caption += "<img src='https://item-shopping.c.yimg.jp/i/l/seiunstore_" + itemCode1 + "_" + i + ".jpg' width='100%'/><br>";
        }
        item.setCaption(caption);
        //产品数据保存
        page.putField("item", item);
        //照片下载
        Map<String, Object> map = new HashMap<>();
        map.put("photoAll", photoAll);
        map.put("itemCode", item.getItemCode());
        map.put("itemPath", item.getItemPath());
        page.putField("photoDownload", map);
        //商品检索关键字对象
        List<ItemKeyword> itemKeywordList = new ArrayList<>();
        String keywords = productName + headlin;
        String[] s = keywords.split(" ");
        for (String s1 : s) {
            //关键字对象填充
            ItemKeyword itemKeyword = new ItemKeyword();
            itemKeyword.setProductCategory(path.replace(" ", ":"));
            itemKeyword.setKeyword(s1);
            itemKeyword.setCountKeyword(1);
            itemKeywordList.add(itemKeyword);
        }
        //关键字数据库永久化
        page.putField("itemKeywordList", itemKeywordList);
        //siteshopinfo保存
        SiteShop siteShop = new SiteShop();
        siteShop.setShopName(storeName);
        page.putField("siteShop",siteShop);
    }
}

















