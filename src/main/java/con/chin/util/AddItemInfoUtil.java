package con.chin.util;

import con.chin.pojo.Item;
import con.chin.pojo.ItemKeyword;
import con.chin.pojo.PurchasingItem;
import con.chin.pojo.SiteShop;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class AddItemInfoUtil {

    public static void saveYahooItemInfo(Page page) {
        //解析页面
        Html html = page.getHtml();

        //商品详情对象
        Item item = new Item();

        //サイト名
        item.setSiteName("yahoo");
        //店铺名
        String storeName = html.css("div.mdBreadCrumb a").nodes().get(0).css("span", "text").toString();
        if (storeName != null || !"".equals(storeName)) {
            //ショップ名
            item.setShopName(storeName);
        }
        //新商品code
        String itemCode = UUID.randomUUID().toString();
        String itemCode1 = itemCode.substring(itemCode.lastIndexOf("-") + 1, itemCode.length() - 1);
        item.setItemCode("E" + itemCode1);
        //旧商品code
        List<Selectable> nodes = html.css("div#itm_cat li").nodes();
        Selectable selectable1 = nodes.get(nodes.size() - 1);
        String oldItemCode = selectable1.css("div.elRowData p", "text").toString();
        item.setOldItemCode(oldItemCode);
        //path取得
        String path = Jsoup.parse(html.css("div.mdItemSubInformation div.elRowData li.elListItem").nodes().get(1).css("ul").toString()).text();
        path = path.replace(" ", ":");
        item.setItemPath(path);
        //产品名称
        String productName = html.css("div.mdItemName p.elName", "text").toString();
        //把「"」去除
        productName = productName.replaceAll("\"", "").replaceAll("”", "").replaceAll("　"," ");
        //修改itemname
        productName = SetDataUtil.setItemName(productName, path);
        item.setItemName(productName);
        //标题
        String headlin = html.css("div.mdItemName p.elCatchCopy", "text").toString();
        if (headlin != null && !"".equals(headlin)) {
            //把「"」去除
            headlin = headlin.replaceAll("\"", "").replaceAll("”", "");
            item.setHeadline(headlin);
        }
        //产品价格
        item.setPrice(Integer.parseInt(html.css("span.elPriceNumber", "text").nodes().get(0).toString().replace(",", "").replace("円", "").trim()));
        //产品详情说明文
        String mdItemDescription = html.css("div.mdItemDescription p").toString();
        if (mdItemDescription != null && !"".equals(mdItemDescription)) {
            //把不要的文字删除
            mdItemDescription = mdItemDescription.replaceAll("<br>", "\n").replaceAll("<p>", "").replace("</p>", "").replaceAll("\"", "");
            item.setExplanation(mdItemDescription);
        }
        //option选项有没有判断
        List<Selectable> optionNodes = html.css("div.elTableInner thead.elTableHeader th").nodes();
        List<Selectable> options = html.css("div.mdOrderOptions li").nodes();
        //选择项不同
        String option = html.css("div.elHeaderMain p.elHeaderCaption", "text").toString();
        String text = html.css("div.elHeaderMain p.elHeaderNote", "text").toString();
        if (optionNodes.size() > 0 && (option.contains("×") || "以下の一覧からご希望の商品を選択してください".equals(text))) {
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
                        if (item.getOption4() == null) {
                            //option4
                            String option4 = options.get(i).css("p.elOptionHeading", "text").toString();
                            item.setOption4(option4);
                            //value4
                            String value4 = Jsoup.parse(options.get(i).toString()).select("select option").text();
                            item.setValue4(value4);
                        } else {
                            //option5
                            String option5 = options.get(i).css("p.elOptionHeading", "text").toString();
                            item.setOption5(option5);
                            //value5
                            String value5 = Jsoup.parse(options.get(i).toString()).select("select option").text();
                            item.setValue5(value5);
                        }
                        break;
                    case 3:
                        if (item.getOption5() == null) {
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
        //未下载
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
        String caption = "<img src='https://item-shopping.c.yimg.jp/i/l/seiunstore_" + itemCode1 + "' width='100%'/><br>";
        //产品照片的数量
        int photoAllSize = photoAll.size() >= 21 ? 20 : photoAll.size();
        for (int i = 1; i <= photoAllSize; i++) {
            caption += "<img src='https://item-shopping.c.yimg.jp/i/l/seiunstore_" + itemCode1 + "_" + i + "' width='100%'/><br>";
        }
        item.setCaption(caption);
        //产品数据保存
        page.putField("item", item);
        //照片下载
        Map<String, Object> map = new HashMap<>();
        map.put("photoAll", photoAll);
        map.put("itemCode", item.getItemCode());
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
        page.putField("siteShop", siteShop);
    }

    //17网
    public static void save17zwdItemInfo(Page page) {
        //解析页面
        Html html = page.getHtml();

        //商品详情对象
        PurchasingItem purchasingItem = new PurchasingItem();

        //サイト名
        purchasingItem.setSiteName("17网");
        //店铺名
        String storeName = "";
        List<Selectable> storeNameNodes = html.css("div.index-shopHeadName-1-4ltjAR80CMhuNF_gszTO span").nodes();
        //店铺为实力制造
        if (storeNameNodes.size() > 0) {
            storeName = storeNameNodes.get(0).css("span", "text").toString();
            //店铺为其他
        }
        //店铺名不为空
        if (storeName != null || !"".equals(storeName)) {
            //ショップ名
            purchasingItem.setShopName(storeName);
        }
        //新商品code
        String itemCode = UUID.randomUUID().toString();
        String itemCode1 = itemCode.substring(itemCode.lastIndexOf("-") + 1, itemCode.length() - 1);
        purchasingItem.setItemCode("Z" + itemCode1);
        //旧商品code
        String oldItemCode = html.css("div.index-properiesValue-31gHDGZJNZoaAcprhVh_VL").nodes().get(0).css("div", "text").toString();
        purchasingItem.setOldItemCode(storeName + "-" + oldItemCode);
        //产品名称
        String productName = html.css("div.index-titleBox-25FtwMm1mlJ1I5-D4fNk4A").css("span", "text").toString();
        //把「"」去除
        productName = productName.replaceAll("\"", "").replaceAll("”", "");
        purchasingItem.setItemName(productName);
        //产品详情
        String explanation = "";
        List<Selectable> explanationNodes = html.css("div.index-label-2CoA0zl4u3q99XtnCaBNNX").nodes();
        for (Selectable explanationNode : explanationNodes) {
            explanation += explanationNode.css("div", "text").toString();
            explanation = explanation + "\n";
        }
//        //产品详情说明文
//        List<Selectable> captionNodes = html.css("div.detail-desc").nodes();
//        purchasingItem.setExplanation(explanation);
        //产品价格
//        String purchasePrice = "";
//        String price = html.css("div.index-hintContentPriceContainer-9yHgpjLQAWtesjoMrhZbs").nodes().get(1).toString();
//        if (price.contains("-")) {
//            purchasePrice = price.substring(price.indexOf("-") + 1, price.lastIndexOf(".")).trim();
//        } else {
//            purchasePrice = price.substring(0, price.lastIndexOf(".")).trim();
//        }
//        purchasingItem.setPurchasePrice(Integer.parseInt(purchasePrice));


        //option1和value1选项
        String optionName1 = html.css("div.index-skuContainer-3zeTJNpf3KMb13z5qHXs-3 div.index-detailLabel-2eygBLosYtq2jUH88CzmrH", "text").toString();
        String value1 = "";
        List<Selectable> value1Nodes = null;
        value1Nodes = html.css("div.index-skuContainer-3zeTJNpf3KMb13z5qHXs-3 div.index-root-5aRY4YSSVLDra4GNXddxN").nodes();
        //选项有照片时
        if (value1Nodes.size() > 0) {
            for (Selectable value1Node : value1Nodes) {
                Elements title = Jsoup.parse(value1Node.toString()).getElementsByAttributeStarting("title");
                //有照片的情况
                value1 += title.get(0).attr("title") + " ";

                System.out.println(value1);
            }
            //只有文字时
        } else {
            value1Nodes = html.css("div.index-skuContainer-3zeTJNpf3KMb13z5qHXs-3 div.index-detailColorLabel-UB46G54BIzgjnOweEpn3l").nodes();
            for (Selectable value1Node : value1Nodes) {
//                Elements title = Jsoup.parse(value1Node.toString()).getElementsByAttributeStarting("title");
                //有照片的情况
                value1 += value1Node.css("div", "text").toString() + " ";
            }
        }
        //把最后的空格去除
        value1 = value1.substring(0, value1.lastIndexOf(" "));
        purchasingItem.setOption1(optionName1);
        purchasingItem.setValue1(value1);
        //option2和value2选项
        String optionName2 = html.css("div.index-skuSizeMain-SqR_LeK4sbORxDMWUEz3k div.index-detailLabel-2eygBLosYtq2jUH88CzmrH", "text").toString();
        String value2 = "";
        //所有的value2
        List<Selectable> value2Nodes = html.css("div.index-root-18be6rFhXn87nebHWkNwG1 p").nodes();
        for (Selectable value2Node : value2Nodes) {
//            value2Node
        }


        //把最后的空格去除 尺码为均码时改为[F]符号
        value2 = value2.substring(0, value2.lastIndexOf(" ")).replaceAll("均码", "F");
        purchasingItem.setOption1(optionName2);
        purchasingItem.setValue1(value2);

        //获取带前页的URL
        purchasingItem.setUrl1(page.getUrl().nodes().get(0).toString());
        purchasingItem.setImage("/images/itemphoto/" + purchasingItem.getItemCode() + ".jpg");
        //未下载
        purchasingItem.setFlog(0);
        //创建时间
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        purchasingItem.setCreated(now);
        //アップデート時間
        purchasingItem.setUpdatetime(now);
        //产品数据保存
        page.putField("purchasingItem", purchasingItem);
        //主照片
        List<String> photoAll = html.css("div.mdItemImage ul.elThumbnailItems").css("img", "src").all();
        //照片下载
//        Map<String, Object> map = new HashMap<>();
//        map.put("photoAll", photoAll);
//        map.put("itemCode", purchasingItem.getItemCode());
//        page.putField("photoDownload", map);

    }

    //搜款网
//    public static void saveVVICItemInfo(Page page) {
//        //解析页面
//        Html html = page.getHtml();
//
//        //商品详情对象
//        PurchasingItem purchasingItem = new PurchasingItem();
//
//        //サイト名
//        purchasingItem.setSiteName("搜款网");
//        //店铺名
//        String storeName = "";
//        List<Selectable> storeNameNodes = html.css("div.slzz-stall-head-name a").nodes();
//        //店铺为实力制造
//        if (storeNameNodes.size() > 0) {
//            storeName = storeNameNodes.get(0).css("h1", "text").toString();
//            //店铺为其他
//        } else {
//            storeNameNodes = html.css("div.stall-head-name a").nodes();
//            storeName = storeNameNodes.get(0).css("h1", "text").toString();
//        }
//        //店铺名不为空
//        if (storeName != null || !"".equals(storeName)) {
//            //ショップ名
//            purchasingItem.setShopName(storeName);
//        }
//        //新商品code
//        String itemCode = UUID.randomUUID().toString();
//        String itemCode1 = itemCode.substring(itemCode.lastIndexOf("-") + 1, itemCode.length() - 1);
//        purchasingItem.setItemCode("V" + itemCode1);
//        //旧商品code
//        String oldItemCode = html.css("div.detail-info div.detail-info-line").nodes().get(0).css("dd", "text").toString();
//        purchasingItem.setOldItemCode(storeName + "-" + oldItemCode);
//        //产品名称
//        String productName = html.css("div.detail div.detail-name").css("h1", "text").toString();
//        //把「"」去除
//        productName = productName.replaceAll("\"", "").replaceAll("”", "");
//        purchasingItem.setItemName(productName);
//        //产品详情
//        String explanation = "";
//        List<Selectable> explanationNodes = html.css("div.right-side div.tab-content ul.attrs li").nodes();
//        for (Selectable explanationNode : explanationNodes) {
//            explanation += explanationNode.css("li", "text").toString();
//            explanation = explanation + "\n";
//        }
//        //产品详情说明文
//        List<Selectable> captionNodes = html.css("div.detail-desc").nodes();
//        purchasingItem.setExplanation(explanation);
//        //产品价格
//        String purchasePrice = "";
//        String price = html.css("div.detail-price dd", "text").nodes().get(0).toString();
//        if (price.contains("-")) {
//            purchasePrice = price.substring(price.indexOf("-") + 1, price.lastIndexOf(".")).trim();
//        } else {
//            purchasePrice = price.substring(0, price.lastIndexOf(".")).trim();
//        }
//        purchasingItem.setPurchasePrice(Integer.parseInt(purchasePrice));
//        //option1和value1选项
//        List<Selectable> option1Nodes = html.css("div.detail-info-line dl dt").nodes();
//        String optionName1 = option1Nodes.get(2).css("dt", "text").toString();
//        String value1 = "";
//        List<Selectable> value1Nodes = html.css("div.detail-info-line dd.choice li").nodes();
//        for (Selectable value1Node : value1Nodes) {
//            //临时存储变量
//            String value = "";
//            // 追加料金ある場合
//            if (price.contains("-")) {
//                value = value1Node.css("a").toString();
//                value1 += value.substring(value.lastIndexOf("=") + 2, value.lastIndexOf("\"")) + "追加料金あり" + " ";
//                //ない場合
//            } else {
//                value1 += value1Node.css("a", "text").toString() + " ";
//            }
//        }
//        //把最后的空格去除
//        value1 = value1.substring(0, value1.lastIndexOf(" "));
//        purchasingItem.setOption1(optionName1);
//        purchasingItem.setValue1(value1);
//        //option2和value2选项
//        List<Selectable> option2Nodes = html.css("div.detail-info-row dl dt").nodes();
//        String optionName2 = option2Nodes.get(0).css("dt", "text").toString();
//        String value2 = "";
//        for (Selectable explanationNode : explanationNodes) {
//            //临时存储变量
//            String value = "";
//            value += explanationNode.css("li", "text").toString();
//            if (value.contains("尺码") || value.contains("尺寸")) {
//                value2 += value.substring(value.indexOf(":") + 1, value.length()) + " ";
//            }
//        }
//        //把最后的空格去除 尺码为均码时改为[F]符号
//        value2 = value2.substring(0, value2.lastIndexOf(" ")).replaceAll("均码","F");
//        purchasingItem.setOption1(optionName2);
//        purchasingItem.setValue1(value2);
//
//        //获取带前页的URL
//        purchasingItem.setUrl1(page.getUrl().nodes().get(0).toString());
//        purchasingItem.setImage("/images/itemphoto/" + purchasingItem.getItemCode() + ".jpg");
//        //未下载
//        purchasingItem.setFlog(0);
//        //创建时间
//        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//        purchasingItem.setCreated(now);
//        //アップデート時間
//        purchasingItem.setUpdatetime(now);
//        //产品数据保存
//        page.putField("purchasingItem", purchasingItem);
//        //主照片
//        List<String> photoAll = html.css("div.mdItemImage ul.elThumbnailItems").css("img", "src").all();
//        //照片下载
////        Map<String, Object> map = new HashMap<>();
////        map.put("photoAll", photoAll);
////        map.put("itemCode", purchasingItem.getItemCode());
////        page.putField("photoDownload", map);
//
//    }

    public static void saveVVICItemInfo(Page page) {

        //获取chromeDriver
//        WebDriver chromeDriver = ChromeDriverUtil.getFirefoxDriver();
//        chromeDriver.get(page.getUrl().nodes().get(0).toString());
//        try {
//            /**
//             * WebDriver自带了一个智能等待的方法。 dr.manage().timeouts().implicitlyWait(arg0, arg1）；
//             * Arg0：等待的时间长度，int 类型 ； Arg1：等待时间的单位 TimeUnit.SECONDS 一般用秒作为单位。
//             */
//            chromeDriver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        // 新建一个action
//        Actions action = new Actions(chromeDriver);
//
//        WebElement nc_1_n1z = chromeDriver.findElement(By.id("nc_1_n1z"));
//        System.out.println(nc_1_n1z.toString());
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
////        action.clickAndHold(nc_1_n1z).perform();
//
//        action.dragAndDropBy(nc_1_n1z,338,0 ).perform();
//
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        //获取内嵌div的位置 并设置滚动条移动的值
//        WebElement waf_nc_wrapper = chromeDriver.findElement(By.id("WAF_NC_WRAPPER"));
//        // 调用js方法
//        ChromeDriverUtil.scrollTo(waf_nc_wrapper,chromeDriver);


//        chromeDriver.quit();// 退出浏览器


//        //解析页面
//        Html html = page.getHtml();
//
//        //商品详情对象
//        PurchasingItem purchasingItem = new PurchasingItem();
//
//        //サイト名
//        purchasingItem.setSiteName("搜款网");
//        //店铺名
//        String storeName = "";
//        List<Selectable> storeNameNodes = html.css("div.slzz-stall-head-name a").nodes();
//        //店铺为实力制造
//        if (storeNameNodes.size() > 0) {
//            storeName = storeNameNodes.get(0).css("h1", "text").toString();
//            //店铺为其他
//        } else {
//            storeNameNodes = html.css("div.stall-head-name a").nodes();
//            storeName = storeNameNodes.get(0).css("h1", "text").toString();
//        }
//        //店铺名不为空
//        if (storeName != null || !"".equals(storeName)) {
//            //ショップ名
//            purchasingItem.setShopName(storeName);
//        }
//        //新商品code
//        String itemCode = UUID.randomUUID().toString();
//        String itemCode1 = itemCode.substring(itemCode.lastIndexOf("-") + 1, itemCode.length() - 1);
//        purchasingItem.setItemCode("V" + itemCode1);
//        //旧商品code
//        String oldItemCode = html.css("div.detail-info div.detail-info-line").nodes().get(0).css("dd", "text").toString();
//        purchasingItem.setOldItemCode(storeName + "-" + oldItemCode);
//        //产品名称
//        String productName = html.css("div.detail div.detail-name").css("h1", "text").toString();
//        //把「"」去除
//        productName = productName.replaceAll("\"", "").replaceAll("”", "");
//        purchasingItem.setItemName(productName);
//        //产品详情
//        String explanation = "";
//        List<Selectable> explanationNodes = html.css("div.right-side div.tab-content ul.attrs li").nodes();
//        for (Selectable explanationNode : explanationNodes) {
//            explanation += explanationNode.css("li", "text").toString();
//            explanation = explanation + "\n";
//        }
//        //产品详情说明文
//        List<Selectable> captionNodes = html.css("div.detail-desc").nodes();
//        purchasingItem.setExplanation(explanation);
//        //产品价格
//        String purchasePrice = "";
//        String price = html.css("div.detail-price dd", "text").nodes().get(0).toString();
//        if (price.contains("-")) {
//            purchasePrice = price.substring(price.indexOf("-") + 1, price.lastIndexOf(".")).trim();
//        } else {
//            purchasePrice = price.substring(0, price.lastIndexOf(".")).trim();
//        }
//        purchasingItem.setPurchasePrice(Integer.parseInt(purchasePrice));
//        //option1和value1选项
//        List<Selectable> option1Nodes = html.css("div.detail-info-line dl dt").nodes();
//        String optionName1 = option1Nodes.get(2).css("dt", "text").toString();
//        String value1 = "";
//        List<Selectable> value1Nodes = html.css("div.detail-info-line dd.choice li").nodes();
//        for (Selectable value1Node : value1Nodes) {
//            //临时存储变量
//            String value = "";
//            // 追加料金ある場合
//            if (price.contains("-")) {
//                value = value1Node.css("a").toString();
//                value1 += value.substring(value.lastIndexOf("=") + 2, value.lastIndexOf("\"")) + "追加料金あり" + " ";
//                //ない場合
//            } else {
//                value1 += value1Node.css("a", "text").toString() + " ";
//            }
//        }
//        //把最后的空格去除
//        value1 = value1.substring(0, value1.lastIndexOf(" "));
//        purchasingItem.setOption1(optionName1);
//        purchasingItem.setValue1(value1);
//        //option2和value2选项
//        List<Selectable> option2Nodes = html.css("div.detail-info-row dl dt").nodes();
//        String optionName2 = option2Nodes.get(0).css("dt", "text").toString();
//        String value2 = "";
//        for (Selectable explanationNode : explanationNodes) {
//            //临时存储变量
//            String value = "";
//            value += explanationNode.css("li", "text").toString();
//            if (value.contains("尺码") || value.contains("尺寸")) {
//                value2 += value.substring(value.indexOf(":") + 1, value.length()) + " ";
//            }
//        }
//        //把最后的空格去除 尺码为均码时改为[F]符号
//        value2 = value2.substring(0, value2.lastIndexOf(" ")).replaceAll("均码","F");
//        purchasingItem.setOption1(optionName2);
//        purchasingItem.setValue1(value2);
//
//        //获取带前页的URL
//        purchasingItem.setUrl1(page.getUrl().nodes().get(0).toString());
//        purchasingItem.setImage("/images/itemphoto/" + purchasingItem.getItemCode() + ".jpg");
//        //未下载
//        purchasingItem.setFlog(0);
//        //创建时间
//        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//        purchasingItem.setCreated(now);
//        //アップデート時間
//        purchasingItem.setUpdatetime(now);
//        //产品数据保存
//        page.putField("purchasingItem", purchasingItem);
//        //主照片
//        List<String> photoAll = html.css("div.mdItemImage ul.elThumbnailItems").css("img", "src").all();
        //照片下载
//        Map<String, Object> map = new HashMap<>();
//        map.put("photoAll", photoAll);
//        map.put("itemCode", purchasingItem.getItemCode());
//        page.putField("photoDownload", map);

    }
}

















