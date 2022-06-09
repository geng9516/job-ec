package con.chin.util.createItemInfo;

import con.chin.pojo.Item;
import con.chin.pojo.SiteShop;
import con.chin.util.ChromeDriverUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import us.codecraft.webmagic.Page;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Create17zwdItemInfo {

    public static String ITEMCODE = "";

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
        try {
            /**
             * WebDriver自带了一个智能等待的方法。 dr.manage().timeouts().implicitlyWait(arg0, arg1）；
             * Arg0：等待的时间长度，int 类型 ； Arg1：等待时间的单位 TimeUnit.SECONDS 一般用秒作为单位。
             */
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i <= 15; i++) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //下拉到页面1000位置
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,1000000)");
        }
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
        Elements storeNameElement = jsoup.select("div.index-shopHeadName-1-4ltjAR80CMhuNF_gszTO span");
        String storeName = storeNameElement.first().text();
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
        productName = productName.replaceAll("\"", "").replaceAll("”", "").replaceAll(" ", "").replaceAll("　", "");
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

        Elements elements = jsoup.select("div.index-root-18be6rFhXn87nebHWkNwG1").select("p");
        String element = "";
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).text() != null && !"".equals(elements.get(i).text())) {

                element += elements.get(i).text();
                //サイズ名
                element = element.replaceAll("衣长", "着長").replaceAll("胸围", "バスト")
                        .replaceAll("肩宽", "肩幅").replaceAll("袖长", "袖丈")
                        .replaceAll("高腰围", "ハイウエスト").replaceAll("腰围", "ウエスト")
                        .replaceAll("裙长", "着長").replaceAll("下摆围", "ヘムライン")
                        .replaceAll("摆围", "ヘムライン");
                //サイズ
                element =element.replaceAll("均码", "F").replaceAll("码", "");
                //换行
                element += "\n";

            }
        }//衣长 前57后67cm 　胸围118cm 　 肩宽 46cm 袖长52cm
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
                "カラー：" + value1 + "\n" + "\n" +
                "サイズ(cm)：" + value2 + "\n" + "\n" +
                (element == "" ? "" : element) + "\n" + "\n" +
                "原産国:中国" + "\n" + "\n" +
                "☆詳細なサイズは写真のサイズ表を参考ください。" + "\n" +
                "※PC環境や撮影状況などの違いにより実際のお色とは若干異なる場合がございます。" + "\n" +
                "※サイズは平置き計測となっておりますので、1〜2の誤差が生じる場合がございます。 ";

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
