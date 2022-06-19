package con.chin.util;

import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
import con.chin.pojo.Item;
import con.chin.pojo.ItemKeyword;
import con.chin.service.ItemKeywordService;
import con.chin.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.CriteriaBuilder;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static con.chin.util.ItemPhotoCopyUtil.getItemPhotoSize;

@Component
public class ItemInfoCsvExportUtil {

    private static ItemInfoCsvExportUtil exportItemInfoCsvUtil;

    //??
    @PostConstruct
    public void init() {
        exportItemInfoCsvUtil = this;
    }

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemKeywordService itemKeywordService;

    private static String FILENAME;

    @Value("${FILENAME}")
    public void setItemphoto(String FILENAME) {
        this.FILENAME = FILENAME;
    }

    //yahooショップ商品のcsvファイルダウンロード
    public static void exportYahooItemInfoToCsv(List<Item> itemList, String filePath, String fileName) {
        //开始时间
        long start = System.currentTimeMillis();

        //properties文件的名字取得
        ResourceBundle bundle = ResourceBundle.getBundle(FILENAME);
        //IMG照片下载地址取得
        String itemCsvPath = bundle.getString("ITEMCSVPATH");
        //现在时间作为文件名(线程安全的)
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        now = now.replaceAll("-", "").replaceAll(":", "").replace(" ", "");
        PrintWriter printWriter = null;
        //产品option数据有问题时保存
        List<Item> itemList1 = new ArrayList<>();
        //数次数使用
        int count = 1;
        FileOutputStream fos = null;
        CsvWriter writer = null;
        try {
            //文件路径(项目指定更新)
            File file = new File(filePath + File.separator + fileName + now + ".csv");

            //文件不存在时创建
            if (file == null) {
                file.mkdir();
            }
            if (fileName.equals("chineseEdit")) {
                writer = CsvUtil.getWriter(file, CharsetUtil.CHARSET_UTF_8, true);
            } else {

                writer = CsvUtil.getWriter(file, Charset.forName("Shift-JIS"));
            }
            //商品コード 为了在下面读值
            String itemCode = null;

            //创建csv文件

            List<String[]> writeLine = new ArrayList<>();
            byte[] uft8bom = {(byte) 0xef, (byte) 0xbb, (byte) 0xbf};
            fos = new FileOutputStream(file, true);
            fos.write(uft8bom);
            //设置csv文件表头
            String[] strings = {"path", "name", "code", "sub-code", "price", "member-price", "options",
                    "headline", "caption", "explanation", "relevant-links", "taxable", "point-code",
                    "meta-desc", "template", "sale-limit", "delivery", "astk-code", "condition",
                    "product-category", "display", "sort", "sort_priority", "sp-additional",
                    "lead-time-instock", "lead-time-outstock", "keep-stock", "postage-set",
                    "taxrate-type", "item-tag", "pick-and-delivery-transport-rule-type"};
            writeLine.add(strings);

            //设置個別商品コード尾数初始值
            int i = 11;
            //设置個別商品コード初始变量
            String subCode = "";
            //遍历item集合下载每一件产品信息
            for (Item item : itemList) {
                //开始时间
                long start1 = System.currentTimeMillis();
                //保存每一列的数据使用
                String[] string = new String[31];
                //option1时空值是不下载给itemdeflog赋值0(代表还需编辑)
                if (item.getOption1() == null || "".equals(item.getOption1())) {
                    //数据库中的flog字段赋值0
                    item.setFlog(0);
                    exportItemInfoCsvUtil.itemService.updateItem(item);
                    item.setOption1("修正する必要があります。");
                    //保存数据有问题的item
                    itemList1.add(item);
                    continue;
                }

                //metaDesc
                ItemKeyword itemKeyword = new ItemKeyword();
                itemKeyword.setProductCategory(item.getItemPath());
                List<ItemKeyword> itemKeyword1 = exportItemInfoCsvUtil.itemKeywordService.findGoodItemKeyword(itemKeyword);
                String metaDesc = "";
                for (ItemKeyword keyword : itemKeyword1) {
                    metaDesc += keyword.getKeyword() + " ";
                }
                if (metaDesc != null && metaDesc.contains(" ")) {
                    metaDesc = metaDesc.replaceAll("　", " ");

                    if (metaDesc.length() >= 80) {
                        //把产品名称的长度调整
                        metaDesc = SetDataUtil.setStrLength(metaDesc, 80);
                    }
                } else {
                    metaDesc = item.getItemName();
                }
                //产品名修改
                String itemName = item.getItemName();
                //把产品名字中的全角空格去除
                if (itemName != null && !itemName.contains(" ")) {
                    itemName = metaDesc.replaceAll("　", " ");

                    if (itemName.length() >= 70 && itemName.contains(" ")) {
                        //把产品名称的长度调整
                        itemName = SetDataUtil.setStrLength(itemName, 70);
                    }
                } else if (itemName != null) {
                    itemName = itemName.replaceAll("　", " ");

                    if (itemName.length() > 70 && itemName.contains(" ")) {
                        //把产品名称的长度调整
                        itemName = SetDataUtil.setStrLength(itemName, 70);
                    }
                }
                //headline
                String headline = "";
                if (item.getHeadline() == null || !"".equals(item.getHeadline())) {
                    headline = metaDesc.replaceAll("　", " ");

                    if (headline.length() >= 30 && headline.contains(" ")) {
                        //把产品名称的长度调整
                        headline = SetDataUtil.setStrLength(headline, 30);
                    }
                } else {
                    headline = item.getHeadline();
                }
                //おすすめ商品 relevant-links
                String relevantLinks = SetDataUtil.getRelevantLinks(item.getItemPath());
                item.setRelevantLinks(relevantLinks);
                //個別商品コード(sub-code)
                //value1以" "分割成各个选项参数(颜色/尺码)
//                if(item.getValue1() != null && !"".equals(item.getValue1())){
//                String[] values1 = item.getValue1().split(" ");
////                }
//
//                //value2以" "分割成各个选项参数(颜色/尺码)
//                String values = item.getValue2();
//                if (values != null && !"".equals(values) && item.getOption2() != null && !"".equals(item.getOption2()) && item.getOption2().length() < 6) {
////                    String[] values2 = values.split(" ");
//                    //value1和value2做特定格式拼接
////                    for (String value1 : values1) {
////                        for (String value2 : values2) {
////                            //カラー:ホワイト#サイズ:S=t00106s&カラー:ホワイト#サイズ:M=t00106m&カラー:ホワイト#サイズ:L=t00106l&カラー:グレー#サイズ:S=t001061s&カラー:グレー#サイズ:M=t001061m&カラー:グレー#サイズ:L=t001061l
////                            subCode += item.getOption1() + ":" + value1 + "#" + item.getOption2() + ":" + value2 + "=" + item.getItemCode() + (i <= 500 ? i++ : 0) + "&";
////                        }
////                    }
////                    //把最后的"&"去除
////                    subCode = subCode.substring(0, subCode.lastIndexOf("&"));
//                } else {
//                    //個別商品コード(sub-code) 选项只有一个的时候
//                    for (String value1 : values1) {
//                        subCode += item.getOption1() + ":" + value1 + "=" + item.getItemCode() + (i <= 500 ? i++ : 0) + "&";
//                    }
//                    //把最后的"&"去除
//                    subCode = subCode.substring(0, subCode.lastIndexOf("&"));
//                }
                //オプション(options)
                String options =
                        (!"".equals(item.getOption1()) && item.getOption1() != null ? item.getOption1() + " " : "") + (item.getValue1() != "" && item.getValue1() != null ? item.getValue1() : "") +
                                (!"".equals(item.getOption2()) && item.getOption2() != null ? "\n" + "\n" + item.getOption2() + " " : "") + (item.getValue2() != "" && item.getValue2() != null ? item.getValue2() : "") +
                                (!"".equals(item.getOption3()) && item.getOption3() != null ? "\n" + "\n" + item.getOption3() + " " : "") + (item.getValue3() != "" && item.getValue3() != null ? item.getValue3() : "") +
                                (!"".equals(item.getOption4()) && item.getOption4() != null ? "\n" + "\n" + item.getOption4() + " " : "") + (item.getValue4() != "" && item.getValue4() != null ? item.getValue4() : "") +
                                (!"".equals(item.getOption5()) && item.getOption5() != null ? "\n" + "\n" + item.getOption5() + " " : "") + (item.getValue5() != "" && item.getValue5() != null ? item.getValue5() : "");
                //商品コード code
                itemCode = item.getItemCode();
                //価格
                Integer price = null;
//                if (item.getSalePrice() != null || "".equals(item.getSalePrice())) {
//                    price = item.getSalePrice();
//                } else {
                price = item.getSalePrice();
                if (price == null) {
                    price = 0;
                }
                Double a = new BigDecimal(price * 0.99).setScale(0, BigDecimal.ROUND_UP).doubleValue();
                Integer memberPrice = a.intValue();
//                }
                Integer itemCategoryCode = item.getItemCategoryCode();
                if (itemCategoryCode == null) {
                    itemCategoryCode = 0;
                }
                //csv文件写出
                string[0] = item.getItemPath();//商品ページのストア内カテゴリパス path
                string[1] = itemName;//商品名 name
                string[2] = itemCode; //商品コード code
//                string[3] = subCode;      //個別商品コード sub-code 暂时不用
                string[3] = "";      //個別商品コード sub-code
                string[4] = String.valueOf(price);     //通常販売価格 price
                string[5] = String.valueOf(memberPrice);    //プレミアム価格
                string[6] = options;     //オプション options
                string[7] = headline;      //キャッチコピー headline
//                string[7] = (item.getCaption() != null ? item.getCaption() : "");//商品の説明文 caption
                string[8] = "";
                string[9] = item.getExplanation();     //商品情報 explanation 暂时不用
                string[10] = (item.getRelevantLinks() != null && !"".equals(item.getRelevantLinks()) ? item.getRelevantLinks() : "");      //おすすめ商品 relevant-links
                string[11] = "1";      //課税対象 taxable
                string[12] = "1";     //ポイント倍率 point-code
                string[13] = metaDesc;//meta-desc META descriptionには、商品ページに関連するストアのキーワードや説明文を、全角80文字（半角160文字）以内で入力します。HTMLは使用できません。この項目に入力した文言は、さまざまな検索サイトでの検索結果の表示に使用されます。
                string[14] = "IT02";      //使用中のテンプレート template
                string[15] = "";     //購入数制限 sale-limit
                string[16] = "3";      //送料無料の設定 delivery
                string[17] = "0";      //旧：きょうつく、あすつく astk-code
                string[18] = "0";      //商品の状態 condition
                string[19] = String.valueOf(itemCategoryCode);      //プロダクトカテゴリ product-category
                string[20] = "1";      //ページの公開/非公開 display
                string[21] = "1";      //旧：商品表示順序 sort
                string[22] = "";      //商品表示優先度 sort_priority
//                string[21] = (item.getCaption() != null && !"".equals(item.getCaption()) ? item.getCaption() : "");  //スマートフォン用商品の補足説明を入力。 sp-additional 暂时不用
                string[23] = "";
                string[24] = "1";      //発送日情報 lead-time-instock
                string[25] = "4000";      //発送日情報 lead-time-outstock
                string[26] = "1";      //購入者キャンセル在庫取り扱い keep-stock
                string[27] = "1";      //配送グループ postage-set
                string[28] = "0.1";      //軽減税率コード taxrate-type
                string[29] = "";     //商品タグ item-tag
                string[30] = "0";            //荷扱い情報 pick-and-delivery-transport-rule-type
                //個別商品コード值去除
                subCode = "";
                //個別商品コード尾数初始值归1
                i = 1;
                //输出次数
                writeLine.add(string);
                //结束时间
                long end = System.currentTimeMillis();
                System.out.println(fileName + " 产品CSV, 输出的: 第" + count++ + "行了    耗时：" + (end - start1) + " ms");
            }
            writer.write(writeLine);
            //结束时间
            long end = System.currentTimeMillis();
            System.out.println(fileName + "产品CSV, 总共输出了: " + (count - 1) + "行数据    耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    //auショップ商品のcsvファイルダウンロード
    public static void exportAuItemInfoToCsv(List<Item> itemList, String filePath, String fileName) {

        //properties文件的名字取得
        ResourceBundle bundle = ResourceBundle.getBundle(FILENAME);
        //IMG照片下载地址取得
        String itemCsvPath = bundle.getString("ITEMPHOTO");

        //现在时间作为文件名(线程安全的)
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        now = now.replaceAll("-", "").replaceAll(":", "").replace(" ", "");
        PrintWriter printWriter = null;
        //数次数使用
        int count = 1;
        //商品コード 为了在下面读值
        CsvWriter writer = null;
        try {
            //文件路径
            File file = new File(filePath + File.separator + fileName + now + ".csv");
            //文件不存在时创建
            if (file == null) {
                file.mkdir();
            }
            List<String[]> writeLine = new ArrayList<>();
            //创建csv文件
            writer = CsvUtil.getWriter(file, Charset.forName("Shift-JIS"));
            //设置csv文件表头
            String[] strings = {"ctrlCol", "itemCode", "itemName", "itemPrice", "sellMethodSegment", "taxSegment", "reducedTax",
                    "postageSegment", "postage", "deliveryMethodId1", "deliveryMethodName1", "sellStartDate", "sellEndDate",
                    "countdownTimerConfig", "sellNumberDispConfig", "buyNumLimtConfig", "buyNumMax", "stockRequestConfig",
                    "returnRequestConfig", "deliveryLeadtimeId", "stockSegment", "stockShippingDayId",
                    "displayStockSegment", "stockCount", "displayBackorderMessage", "displayChoicesStockSegment",
                    "description", "descriptionForSP", "descriptionForPC", "detailTitle", "detailDescription",
                    "specTitle", "spec1", "searchKeyword1", "searchKeyword2", "searchKeyword3", "searchTarget",
                    "imageUrl1", "imageUrl2", "imageUrl3", "imageUrl4", "imageUrl5", "imageUrl6", "imageUrl7",
                    "imageUrl8", "imageUrl9", "imageUrl10", "imageUrl11", "imageUrl12", "imageUrl13",
                    "imageUrl14", "imageUrl15", "imageUrl16", "imageUrl17", "imageUrl18", "imageUrl19",
                    "imageUrl20", "categoryId", "tagId", "shopCategory1", "saleStatus", "crossBorderEcLnkConfig",
                    "crossBorderEcLnkSts", "itemOption1", "itemOption2", "itemOption3", "itemOptionCommissionTitle1",
                    "itemOptionCommissionVal1", "itemOptionCommissionNote1", "pointRate"};
            //csv表头
            writeLine.add(strings);

            //遍历item集合下载每一件产品信息
            for (Item item : itemList) {
                //保存每一列的数据使用
                String[] string = new String[70];
                //おすすめ商品 relevant-links
//                item.setRelevantLinks(SetDataUtil.getRelevantLinks(item.getItemPath()));
                //商品コード code
                String itemCode = item.getItemCode();
                //产品名称
                String itemName = item.getItemName();
                //产品价格
                String itemPrice = item.getSalePrice().toString();
                //商品詳細説明(文字说明)
                String explanation = item.getExplanation();
                String explanationBr = "";
                //把按照换行进行分割
                Matcher m = Pattern.compile("(?m)^.*$").matcher(explanation);
                while (m.find()) {
                    //每一行
                    String s = m.group();
                    if (s.length() == 0) {
                        s = s + "<br><br>\n\n";
                    } else {
                        s = s + "<br>\n";
                    }
                    explanationBr += s;
                }
                //产品类别
                String itemPath = item.getItemPath();
                itemPath = itemPath.substring(itemPath.indexOf(":") + 1, itemPath.length());
                itemPath = itemPath.substring(itemPath.indexOf(":") + 1, itemPath.length());
                //option1
                String option1 = item.getOption1();
                //value1
                String value1 = item.getValue1();
                //itemOptin1
                String itemOption1 = "";
                if (option1 != null && !"".equals(option1) && value1 != null && !"".equals(value1)) {
                    itemOption1 = option1 + ":" + value1.replaceAll(" ", ":");
                }
                //option2
                String option2 = item.getOption2();
                //value2
                String value2 = item.getValue2();
                //itemOptin1
                String itemOption2 = "";
                if (option2 != null && !"".equals(option2) && value2 != null && !"".equals(value2)) {
                    itemOption2 = option2 + ":" + value2.replaceAll(" ", ":");
                }
                //option3
                String option3 = item.getOption3();
                //value3
                String value3 = item.getValue3();
                //itemOptin1
                String itemOption3 = "";
                if (option3 != null && !"".equals(option3) && value3 != null && !"".equals(value3)) {
                    itemOption3 = option3 + ":" + value3.replaceAll(" ", ":");
                }
                //照片的数量取得
                List<String> itemPhotoList = new ArrayList<>();
                Integer itemPhotoSize = getItemPhotoSize(itemCode, itemCsvPath);
                if (itemPhotoSize != null) {
                    //总共有的照片数
                    for (int i = 0; i < itemPhotoSize; i++) {
                        String itemPhotoUrl = "";
                        if (i == 0) {
                            itemPhotoUrl = "https://image.wowma.jp/65506022/65506022-1/" + itemCode + "/" + itemCode + ".jpg";
                        } else {
                            itemPhotoUrl = "https://image.wowma.jp/65506022/65506022-1/" + itemCode + "/" + itemCode + "_" + i + ".jpg";
                        }
                        itemPhotoList.add(itemPhotoUrl);
                    }
                    //补全20张
                    for (int i = itemPhotoSize; i < 21; i++) {
                        String itemPhotoUrl = "";
                        itemPhotoList.add(itemPhotoUrl);
                    }
                }else {
                    for (int i = 0; i < 21; i++) {
                        String itemPhotoUrl = "";
                        itemPhotoList.add(itemPhotoUrl);
                    }
                }
                //产品详细说明(照片等)
                String caption = "";
                for (String photoUrl : itemPhotoList) {
                    if (photoUrl.length() != 0) {
                        caption += "<img src='" + photoUrl + "' width='100%'/><br>";
                    }
                }
                //csv文件写出
                string[0] = "N";            //N：新商品、U：更新、D：削除
                string[1] = itemCode;       //商品コード
                string[2] = itemName;       //商品名
                string[3] = itemPrice;      //通常販売価格
                string[4] = "1";            //販売方法区分
                string[5] = "1";            //税区分
                string[6] = "1";            //軽減税率設定
                string[7] = "1";            //送料設定区分
                string[8] = "";             //個別送料
                string[9] = "wizardDlv";    //(新)配送方法ID1～5
                string[10] = "宅配便";       //配送方法名1〜5
                string[11] = "";            //販売開始日時
                string[12] = "";            //販売終了日時
                string[13] = "1";           //販売終了カウントダウン設定
                string[14] = "1";           //期間内販売個数設定
                string[15] = "1";           //期間内購入点数制限設定
                string[16] = "";            //期間内購入上限点数
                string[17] = "1";           //入荷リクエスト設定
                string[18] = "1";           //返品申込み
                string[19] = "";            //お届けリードタイムID
                string[20] = "1";           //在庫区分
                string[21] = "wizardSDI";   //通常在庫発送日ID
                string[22] = "1";           //残在庫表示区分
                string[23] = "999";         //通常在庫数
                string[24] = "まもなく入荷致します。お急ぎは入荷リクエストお願い致します。";      //在庫切れメッセージ
                string[25] = "";           //残選択肢別在庫表示区分
                string[26] = "<洗い方注意><br>\n" +
                        "手洗いまたはドライクリーニングを推奨<br>\n" +
                        "個々のアイテムの洗濯方法について疑問がある場合は、カスタマーサービスにご相談ください / 洗濯機で洗ったり、混ぜたりしないでください<br>\n" +
                        "衣類の風合いを保つため、30℃以上のお湯を長時間使用しないでください。<br>\n" +
                        "濃い色のものは別洗いしてください／カラーブロックの衣類は浸け置きしないでください<br><br>\n\n" +
                        "CARE/アイロン：<br>\n" +
                        "すべての衣類に低温アイロンが推奨されます<br><br>\n" +
                        "※サイズは平置き計測となっておりますので、1〜2の誤差が生じる場合がございます。<br> \n" +
                        "※モニター環境により、実際のものと素材感・色が若干異なって見える場合がありますので、ご了承ください。";
                string[27] = (caption != null ? caption : "");     //SP用商品説明
                string[28] = (caption != null ? caption : "");     //PC用商品説明
                string[29] = "商品詳細情報";              //商品詳細タイトル
                string[30] = explanationBr;     //商品詳細説明
                string[31] = "";      //スペックタイトル
                string[32] = "";     //スペック1～5
                string[33] = "";     //検索キーワード1
                string[34] = "";     //検索キーワード2
                string[35] = "";     //検索キーワード3
                string[36] = "1";     //検索対象
                string[37] = (itemPhotoList.get(0) != null ? itemPhotoList.get(0) : "");     //商品画像URL1
                string[38] = (itemPhotoList.get(1) != null ? itemPhotoList.get(1) : "");     //商品画像URL2
                string[39] = (itemPhotoList.get(2) != null ? itemPhotoList.get(2) : "");     //商品画像URL3
                string[40] = (itemPhotoList.get(3) != null ? itemPhotoList.get(3) : "");     //商品画像URL4
                string[41] = (itemPhotoList.get(4) != null ? itemPhotoList.get(4) : "");     //商品画像URL5
                string[42] = (itemPhotoList.get(5) != null ? itemPhotoList.get(5) : "");     //商品画像URL6
                string[43] = (itemPhotoList.get(6) != null ? itemPhotoList.get(6) : "");     //商品画像URL7
                string[44] = (itemPhotoList.get(7) != null ? itemPhotoList.get(7) : "");     //商品画像URL8
                string[45] = (itemPhotoList.get(8) != null ? itemPhotoList.get(8) : "");     //商品画像URL9
                string[46] = (itemPhotoList.get(9) != null ? itemPhotoList.get(9) : "");     //商品画像URL10
                string[47] = (itemPhotoList.get(10) != null ? itemPhotoList.get(10) : "");     //商品画像URL11
                string[48] = (itemPhotoList.get(11) != null ? itemPhotoList.get(11) : "");     //商品画像URL12
                string[49] = (itemPhotoList.get(12) != null ? itemPhotoList.get(12) : "");     //商品画像URL13
                string[50] = (itemPhotoList.get(13) != null ? itemPhotoList.get(13) : "");     //商品画像URL14
                string[51] = (itemPhotoList.get(14) != null ? itemPhotoList.get(14) : "");     //商品画像URL15
                string[52] = (itemPhotoList.get(15) != null ? itemPhotoList.get(15) : "");     //商品画像URL16
                string[53] = (itemPhotoList.get(16) != null ? itemPhotoList.get(16) : "");     //商品画像URL17
                string[54] = (itemPhotoList.get(17) != null ? itemPhotoList.get(17) : "");     //商品画像URL18
                string[55] = (itemPhotoList.get(18) != null ? itemPhotoList.get(18) : "");     //商品画像URL19
                string[56] = (itemPhotoList.get(19) != null ? itemPhotoList.get(19) : "");     //商品画像URL20
                string[57] = "検索して記入すること";     //カテゴリID
                string[58] = "";     //検索タグID
                string[59] = itemPath;     //店舗内カテゴリ1～10
                string[60] = "1";     //販売ステータス
                string[61] = "2";     //越境EC連携区分
                string[62] = "2";     //越境ECステータス
                string[63] = itemOption1;     //購入オプション1～20 サイズ:S:M:L:XL
                string[64] = itemOption2;     //購入オプション1～20 カラー:ホワイト
                string[65] = itemOption3;     //購入オプション1～20 itemOption3
                string[66] = "オプション";     //購入オプション(手数料型)タイトル1～20
                string[67] = "";     //itemOptionCommissionVal1
                string[68] = "";     //itemOptionCommissionNote1
                string[69] = "1";     //pointRate
                //個別商品コード尾数初始值归1
                writeLine.add(string);
                //输出次数
                System.out.println("这是输出的: 第" + count++ + "件了");
            }
            writer.write(writeLine);
            System.out.println("总共输出了: " + (count - 1));
        } catch (NullPointerException e) {
            System.out.println("这产品番号为:  " + " 的没输出成功,有问题!");
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }


}



























