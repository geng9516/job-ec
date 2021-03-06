package con.chin.util;

import con.chin.pojo.Item;
import con.chin.pojo.ItemKeyword;
import con.chin.pojo.SiteShop;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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
        item.setItemCode("e" + itemCode1);
        //旧商品code
        List<Selectable> nodes = html.css("div#itm_cat li").nodes();
        Selectable selectable1 = nodes.get(nodes.size() - 1);
        String oldItemCode = selectable1.css("div.elRowData p", "text").toString();
        item.setOldItemCode(oldItemCode);
        //path取得
        String path = Jsoup.parse(html.css("div.mdItemSubInformation div.elRowData li.elListItem").nodes().get(1).css("ul").toString()).text();
        path = path.replace(" ", ":");
        if (path.contains("ベビー、キッズ、マタニティ:マタニティウエア:パンツ:、デニム")) {
            path = "ベビー、キッズ、マタニティ:マタニティウエア:パンツ、デニム";
        }
        item.setItemPath(path);
        //产品名称
        String productName = html.css("div.mdItemName p.elName", "text").toString();
        //把「"」去除
        productName = productName.replaceAll("\"", "").replaceAll("”", "").replaceAll("　", " ");
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
        Integer price = Integer.parseInt(html.css("span.elPriceNumber", "text").nodes().get(0).toString().replace(",", "").replace("円", "").trim());
        if (price != null) {
            //原本价格
            item.setPrice(price);
            //售卖价格
            item.setSalePrice(price);
        }

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

    /**
     * 17网
     *
     * @param page
     * @param
     */
    public static void save17zwdItemInfo(Page page) {
        //获取chromeDriver
        WebDriver driver = ChromeDriverUtil.getChromeDriver();
        driver.get(page.getUrl().nodes().get(0).toString());
        WebDriverWait wait = new WebDriverWait(driver, 10);
        try {
            /**
             * WebDriver自带了一个智能等待的方法。 dr.manage().timeouts().implicitlyWait(arg0, arg1）；
             * Arg0：等待的时间长度，int 类型 ； Arg1：等待时间的单位 TimeUnit.SECONDS 一般用秒作为单位。
             */
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i =0;i<=10;i++){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //下拉到页面1000位置
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,1000000)");
        }

        // 使用JavaScript的scrollTo方法和document.body.scrollHeight参数，将页面的滚动条华东到页面的最下方
//        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,document.body.scrollHeight)");

//        Long last_height = (Long) ((JavascriptExecutor) driver).executeScript("return document.documentElement.scrollHeight");
//        long height = last_height / 10;
//        for (int i = 0; i < 10; i++) {
//            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0," + height + ")");
////            Long new_height = (Long) ((JavascriptExecutor) driver).executeScript("return document.documentElement.scrollHeight");
////            if(new_height == last_height){
////                break;
////            }
//
//        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //拿到整个页面
        String pageSource = driver.getPageSource();
        // 退出浏览器
        driver.quit();

        Document jsoup = Jsoup.parse(pageSource);
        Item item = new Item();
        //商品详情对象
        //サイト名
        item.setSiteName("17网");
        //店铺名
        Elements select = jsoup.select("div.index-shopHeadName-1-4ltjAR80CMhuNF_gszTO span");
        String storeName = select.text();
        //店铺名不为空
        if (storeName != null || !"".equals(storeName)) {
            //ショップ名
            item.setShopName(storeName);
        }
        //新商品code
        String itemCode = UUID.randomUUID().toString();
        String itemCode1 = itemCode.substring(itemCode.lastIndexOf("-") + 1, itemCode.length() - 1);
        item.setItemCode("z" + itemCode1);
        //旧商品code
        String oldItemCode = jsoup.select("div.index-properiesValue-31gHDGZJNZoaAcprhVh_VL").first().text();
        item.setOldItemCode(storeName + "-" + oldItemCode);
        //产品名称
        String productName = jsoup.select("div.index-titleBox-25FtwMm1mlJ1I5-D4fNk4A span").text();
        //把「"」去除
        productName = productName.replaceAll("\"", "").replaceAll("”", "");
        item.setItemName(productName);
        //产品价格
        String purchasePrice1 = "";
        String purchasePrice2 = "";
        String purchasePrice3 = "";
        String price = jsoup.select("div.index-hintContentPriceContainer-9yHgpjLQAWtesjoMrhZbs").text();
        if (price.contains("~")) {
            purchasePrice1 = price.substring(price.indexOf("：") + 1, price.indexOf(".")).trim();
            purchasePrice2 = price.substring(price.indexOf("~") + 2, price.lastIndexOf(".")).trim();
        } else {
            purchasePrice1 = price.substring(price.lastIndexOf("：") + 1, price.lastIndexOf(".")).trim();
        }

        if (purchasePrice2 != "") {
            item.setPurchasePrice(Integer.parseInt(purchasePrice2));
            item.setPrice(Integer.parseInt(purchasePrice2));
            item.setSalePrice(Integer.parseInt(purchasePrice2));
        } else {
            item.setPurchasePrice(Integer.parseInt(purchasePrice1));
            item.setPrice(Integer.parseInt(purchasePrice1));
            item.setSalePrice(Integer.parseInt(purchasePrice1));
        }

        //option1和value1选项
        String optionName1 = jsoup.select("div.index-skuContainer-3zeTJNpf3KMb13z5qHXs-3 div.index-detailLabel-2eygBLosYtq2jUH88CzmrH").first().text();
        String value1 = "";
        Elements option1s = null;
        option1s = jsoup.select("div.index-skuContainer-3zeTJNpf3KMb13z5qHXs-3 div.index-root-5aRY4YSSVLDra4GNXddxN");
        //选项有照片时
        if (option1s.size() > 0) {
            for (Element option : option1s) {
                Elements title = option.getElementsByAttributeStarting("title");
                //有照片的情况
                value1 += title.get(0).attr("title") + " ";
            }
            //只有文字时
        } else {
            option1s = jsoup.select("div.index-skuContainer-3zeTJNpf3KMb13z5qHXs-3 div.index-detailColorLabel-UB46G54BIzgjnOweEpn3l");
            for (Element option : option1s) {
//                Elements title = Jsoup.parse(value1Node.toString()).getElementsByAttributeStarting("title");
                //有照片的情况
                value1 += option.select("div").text() + " ";
            }
        }
        //把最后的空格去除
//        value1 = value1.substring(0, value1.lastIndexOf(" "));
        item.setOption1(optionName1);
        item.setValue1(value1);
        //option2和value2选项
        String optionName2 = jsoup.select("div.index-skuContainer-3zeTJNpf3KMb13z5qHXs-3 div.index-detailLabel-2eygBLosYtq2jUH88CzmrH").last().text();
        String value2 = "";
        //所有的value2
        Elements option2s = jsoup.select("div.index-skuSizeBox-2BDxrWLWwF3-7gufa6quuu div.index-detailSizeSelectorLabel-r_oDoK-ujKFnJlE1Vg2QQ");
        for (Element option2 : option2s) {
            value2 += option2.text() + " ";
        }
        //把最后的空格去除 尺码为均码时改为[F]符号
//        value2 = value2.substring(0, value2.lastIndexOf(" "));
        value2 = value2.replaceAll("均码", "F");
        item.setOption2(optionName2);
        item.setValue2(value2);
        //产品详情
        String explanation = "";
        Elements explanations = jsoup.select("div.index-label-2CoA0zl4u3q99XtnCaBNNX");
        for (Element explanationElement : explanations) {
            String explanation1 = explanationElement.select("div").text();
            if (explanation1.contains("材质成分")) {
                explanation += explanation1;
                explanation = explanation + "\n";
            }
            if (explanation1.contains("面料")) {
                explanation += explanation1;
                explanation = explanation + "\n";
            }
            if (explanation1.contains("材质")) {
                explanation += explanation1;
                explanation = explanation + "\n";
            }

        }
        explanation = explanation +
                "素材　:" + "\n" + "\n" +
                "原産国:中国" + "\n" + "\n" +
                "カラー：" + value1 + "\n" + "\n" +
                "サイズ(cm)：" + value2 + "\n" + "\n" +
                "☆詳細なサイズは写真のサイズ表を参考ください。" + "\n" +
                "※PC環境や撮影状況などの違いにより実際のお色とは若干異なる場合がございます。";

        item.setExplanation(explanation);
        //获取带前页的URL
        item.setUrl1(page.getUrl().nodes().get(0).toString());
        item.setUrl5(page.getUrl().nodes().get(0).toString());
        item.setImage("/images/itemphoto/" + item.getItemCode() + ".jpg");
        //未下载
        item.setFlog(5);
        //创建时间
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        item.setCreated(now);
        //アップデート時間
        item.setUpdatetime(now);
        //产品数据保存
        page.putField("item", item);
        //主照片
        List<String> photoAll = new ArrayList<>();
        Elements imgs = jsoup.select("div.index-root-18be6rFhXn87nebHWkNwG1 img");
        for (Element img : imgs) {
            String imgUrl = img.attr("src");
            photoAll.add(imgUrl);
        }
        //照片下载
        Map<String, Object> map = new HashMap<>();
        map.put("photoAll", photoAll);
        map.put("itemCode", item.getItemCode());
        page.putField("purchasingItemhotPDownload", map);
        //siteshopinfo保存
        SiteShop siteShop = new SiteShop();
        siteShop.setShopName(storeName);
        page.putField("siteShop", siteShop);

    }

}

















