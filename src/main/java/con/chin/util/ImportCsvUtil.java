package con.chin.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import con.chin.pojo.Item;
import con.chin.pojo.OrderInfo;
import con.chin.pojo.OrderItemInfo;
import org.apache.commons.collections.map.HashedMap;
import org.checkerframework.checker.units.qual.C;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ImportCsvUtil {


    //上传文件的路径
    private final static URL PATH = Thread.currentThread().getContextClassLoader().getResource("");

    private static String FILENAME;

    @Value("${FILENAME}")
    public void setItemphoto(String FILENAME) {
        this.FILENAME = FILENAME;
    }

    /**
     * @return File  一般文件类型
     * @Description 上传文件的文件类型
     * @Param multipartFile
     **/
    public static File uploadFile(MultipartFile multipartFile) {
        // 获 取上传 路径
        String path = PATH.getPath() + multipartFile.getOriginalFilename();
        try {
            // 通过将给定的路径名字符串转换为抽象路径名来创建新的 File实例
            File file = new File(path);
            // 此抽象路径名表示的文件或目录是否存在
            if (!file.getParentFile().exists()) {
                // 创建由此抽象路径名命名的目录，包括任何必需但不存在的父目录
                file.getParentFile().mkdirs();
            }
            // 转换为一般file 文件
            multipartFile.transferTo(file);

            return file;
        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }

    }


    /**
     * @return List<List < String>>
     * @Description 读取CSV文件的内容（不含表头）
     * @Param filePath 文件存储路径，colNum 列数
     **/
    public static List<OrderInfo> readOrderInfoCSV(String filePath) {
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        FileInputStream fileInputStream = null;

        try {

            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "Shift-JIS"));
            List<OrderInfo> orderInfoList = new ArrayList<>();
            String line = null;
            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                //订单对象
                OrderInfo orderInfo = new OrderInfo();
                String[] orderdata = line.replaceAll("\"", "").split(",");

                //オーダーID
                orderInfo.setOrderId(orderdata[0]);
                //ショップ名付きオーダーID
                orderInfo.setShopNameAndOrderId(orderdata[1]);
                //注文日時
                orderInfo.setOrderTime(orderdata[2]);
                //お届け先氏名カナ
                orderInfo.setShipNameKana(orderdata[3]);
                //お届け先氏名
                orderInfo.setShipName(orderdata[4]);
                //お届け先郵便番号
                orderInfo.setShipZipCode(orderdata[5]);
                //お届け先フル住所
                orderInfo.setShipAddressFull(orderdata[6]);
                //都道府県
                orderInfo.setShipPrefecture(orderdata[7]);
                //市区町村
                orderInfo.setShipCity(orderdata[8]);
                //お届け先住所１行目
                orderInfo.setShipAddress1(orderdata[9]);
                //お届け先住所２行目
                orderInfo.setShipAddress2(orderdata[10]);
                //お届け先電話番号
                orderInfo.setShipPhoneNumber(orderdata[11]);
                //お届け方法名称
                orderInfo.setShipMethodName(orderdata[12]);
                //配送会社code
                orderInfo.setShipCompanyCode(orderdata[13]);
                //問い合わせ番号１
                orderInfo.setShipInvoiceNumber1(orderdata[14]);
                //問い合わせ番号２
                orderInfo.setShipInvoiceNumber2(orderdata[15]);
                //出荷日
                orderInfo.setShipDate(orderdata[16]);
                //請求先メールアドレス
                orderInfo.setBillMailAddress(orderdata[17]);
                //送料
                orderInfo.setShipCharge(Integer.parseInt(orderdata[18]));
                //請求金額合計（ポイント分を除いた）
                orderInfo.setTotal(Integer.parseInt(orderdata[19]));
                //参照元URL
                orderInfo.setReferer(orderdata[20]);
                //お支払い方法名称
                orderInfo.setPayMethodName(orderdata[21]);
                //併用お支払い方法名称
                orderInfo.setCombinedPayMethodName(orderdata[22]);
                //出荷ステータス
                orderInfo.setShipStatus(orderdata[23]);
                //入金ステータス
                orderInfo.setPayStatus(orderdata[24]);
                //注文媒体
                orderInfo.setDeviceType(orderdata[25]);
                //List追加
                orderInfoList.add(orderInfo);
            }
            //返回list
            return orderInfoList;

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            //关闭流
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    //orderiteminfocsv
    public static List<OrderItemInfo> readOrderItemInfoCSV(String filePath) {
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        FileInputStream fileInputStream = null;

        try {

            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "Shift-JIS"));
            List<OrderItemInfo> orderItemInfoList = new ArrayList<>();

            String line = null;
            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                //订单对象
                OrderItemInfo orderItemInfo = new OrderItemInfo();
                String[] orderItemData = line.replaceAll("\"", "").split(",");
                //注文ID
                orderItemInfo.setOrderId(orderItemData[0]);
                //注文数量
                orderItemInfo.setQuantity(Integer.parseInt(orderItemData[1]));
                //商品コード
                orderItemInfo.setItemId(orderItemData[2]);
                //商品サブコード
                orderItemInfo.setSubCode(orderItemData[3]);
                //商品タイトル
                orderItemInfo.setTitle(orderItemData[4]);
                //商品オプション名、複数の場合は：で区切り
                orderItemInfo.setItemOptionName(orderItemData[5]);
                //商品オプション内容、複数の場合は：で区切り
                orderItemInfo.setItemOptionValue(orderItemData[6]);
                //商品オプション価格、複数の場合は：で区切り
                orderItemInfo.setItemOptionPrice(orderItemData[7]);
                //商品サブコードオプション内容文字列
                orderItemInfo.setSubCodeOption(orderItemData[8]);
                //インスクリプション番号、複数の場合は：で区切り
                orderItemInfo.setInscriptionName(orderItemData[9]);
                //インスクリプション内容、複数の場合は：で区切り
                orderItemInfo.setInscriptionValue(orderItemData[10]);
                //商品の通常販売価格または、特別販売価格または、会員価格
                orderItemInfo.setUnitPrice(orderItemData[11].isEmpty() ? 0 : Integer.parseInt(orderItemData[11]));
                //商品の販売価格種別
                orderItemInfo.setPriceType(orderItemData[12]);
                //価格対する注文によるポイント
                orderItemInfo.setUnitGetPoint(orderItemData[13].isEmpty() ? 0 : Integer.parseInt(orderItemData[13]));
                //Lineごと小計
                orderItemInfo.setLineSubTotal(orderItemData[14].isEmpty() ? 0 : Integer.parseInt(orderItemData[14]));
                //Lineごとの付与ポイント小計
                orderItemInfo.setLineGetPoint(orderItemData[15].isEmpty() ? 0 : Integer.parseInt(orderItemData[15]));
                //商品に付与したポイントコード
                orderItemInfo.setPointFspCode(orderItemData[16]);
                //クーポンID
                orderItemInfo.setCouponId(orderItemData[17]);
                //クーポン利用値引額
                orderItemInfo.setCouponDiscount(orderItemData[18].isEmpty() ? 0 : Integer.parseInt(orderItemData[18]));
                //値引き前の単価
                orderItemInfo.setOriginalPrice(orderItemData[19].isEmpty() ? 0 : Integer.parseInt(orderItemData[19]));
                //ポイント確定状態
                orderItemInfo.setIsGetPointFix(orderItemData[20]);
                //付与ポイント種別
                orderItemInfo.setGetPointType(orderItemData[21]);
                //商品ごとのストア負担ポイント
                orderItemInfo.setLineGetPointChargedToStore(Integer.parseInt(orderItemData[22]));
                //発送日スタート
                orderItemInfo.setLeadTimeStart(orderItemData[23].isEmpty() ? "" : orderItemData[23]);
                //発送日エンド
                orderItemInfo.setLeadTimeEnd(orderItemData[24].isEmpty() ? "" : orderItemData[24]);
                //発送日テキスト
                orderItemInfo.setLeadTimeText(orderItemData[25].isEmpty() ? "" : orderItemData[25]);
                //ポイント確定予定日
                orderItemInfo.setGetPointFixDate(orderItemData[26]);
                //注文商品別
                orderItemInfo.setLineId(orderItemData[27]);
                //List追加
                orderItemInfoList.add(orderItemInfo);
            }
            //返回list
            return orderItemInfoList;
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            //关闭流
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    //把产品信息加到item对象中
    public static List<Item> readItemInfoCSV(String filePath) {

        CsvReader reader = CsvUtil.getReader();
        CsvData readData = reader.read(FileUtil.file(filePath), Charset.forName("Shift-JIS"));
        List<CsvRow> rows = readData.getRows();

        List<String> headsplit = rows.get(0).getRawList();

        Map<String, Integer> stringMap = new HashedMap();
        for (int i = 0; i < headsplit.size(); i++) {
            switch (headsplit.get(i)) {
                case "path":
                    stringMap.put("path", i);
                    break;
                case "name":
                    stringMap.put("name", i);
                    break;
                case "code":
                    stringMap.put("code", i);
                    break;
                case "price":
                    stringMap.put("price", i);
                    break;
                case "options":
                    stringMap.put("options", i);
                    break;
                case "headline":
                    stringMap.put("headline", i);
                    break;
                case "caption":
                    stringMap.put("caption", i);
                    break;
                case "explanation":
                    stringMap.put("explanation", i);
                    break;
                case "relevant-links":
                    stringMap.put("relevant-links", i);
                    break;
                case "product-category":
                    stringMap.put("product-category", i);
                    break;
                default:
                    break;
            }
        }

        List<Item> itemList = new ArrayList<>();
        List<Item> itemList1 = new ArrayList<>();
        List<String> itemCodoList = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {

            Item item = new Item();

            List<String> rawList = rows.get(i).getRawList();
            for (String key : stringMap.keySet()) {
                switch (key) {
                    case "path":
                        item.setItemPath(rawList.get(stringMap.get(key)));
                        break;
                    case "name":
                        String itemName = rawList.get(stringMap.get(key));
                        itemName = itemName.replaceAll("\"", "").replaceAll("”", "");
                        item.setItemName(itemName);
                        break;
                    case "code":
                        item.setItemCode(rawList.get(stringMap.get(key)));
                        //itemcode放入集合(为了下载itemcodeCSV)
                        itemCodoList.add(rawList.get(stringMap.get(key)));
                        break;
                    case "price":
                        String price = rawList.get(stringMap.get(key));
                        //是不是数字判断,不是为0
                        if (price == null || !FlogUtil.isInteger(price)) {
                            price = "0";
                        }
                        item.setPrice(Integer.parseInt(price));
                        item.setSalePrice(Integer.parseInt(price));
                        break;
                    case "options":
                        //把原有的删除
                        List<String> stringList = new ArrayList<>();
                        String options = rawList.get(stringMap.get(key));
                        //把按照换行进行分割
                        Matcher m = Pattern.compile("(?m)^.*$").matcher(options);
                        while (m.find()) {
                            String s = m.group();
                            s = s.replaceAll(" ", "").replaceAll("　", "");
                            if (s.length() == 0) {
                                continue;
                            }
                            //把每个opton值的空格统一
                            String option = m.group().replaceAll("　", " ").replaceAll("  ", " ");
                            stringList.add(option);
                        }
                        for (int t = 0; t < stringList.size(); t++) {
                            String optionAndValue = stringList.get(t);
                            String option = optionAndValue.substring(0, optionAndValue.indexOf(" "));
                            option = option.replaceAll(" ", "").replaceAll("　", "");
                            String value = optionAndValue.substring(optionAndValue.indexOf(" ") + 1).trim();
                            if (value == null || "".equals(value)) {
                                continue;
                            }
                            //value值进行去重
                            String[] values = SetDataUtil.distinctByArr(value.split(" "));
                            String value2 = "";
                            for (String s : values) {
                                value2 += s + " ";
                            }
                            value2 = value2.substring(0, value2.lastIndexOf(" "));
                            switch (t) {
                                case 0:
                                    if (option != null || !"".equals(option) && value2 != null || !"".equals(value2)) {
                                        item.setOption1(option);
                                        item.setValue1(value2);
                                    }
                                    break;
                                case 1:
                                    if (option != null || !"".equals(option) && value2 != null || !"".equals(value2)) {

                                        item.setOption2(option);
                                        item.setValue2(value2);
                                    }
                                    break;
                                case 2:
                                    if (option != null || !"".equals(option) && value2 != null || !"".equals(value2)) {

                                        item.setOption3(option);
                                        item.setValue3(value2);
                                    }
                                    break;
                                case 3:
                                    if (option != null || !"".equals(option) && value2 != null || !"".equals(value2)) {

                                        item.setOption4(option);
                                        item.setValue4(value);
                                    }
                                    break;
                                case 4:
                                    if (option != null || !"".equals(option) && value2 != null || !"".equals(value2)) {

                                        item.setOption5(option);
                                        item.setValue5(value2);
                                    }
                                    break;
                            }
                        }
                        break;
                    case "headline":
                        String headline = rawList.get(stringMap.get(key));
                        //把「"」去除
                        headline = headline.replaceAll("\"", "").replaceAll("”", "");
                        item.setHeadline(headline);
                        break;
                    case "caption":
                        item.setCaption(rawList.get(stringMap.get(key)));
                        break;
                    case "explanation":
                        String explanation = rawList.get(stringMap.get(key));
                        //把「"」去除
                        explanation = explanation.replaceAll("\"", "").replaceAll("”", "");
                        item.setExplanation(explanation);
                        break;
                    case "relevant-links":
                        item.setRelevantLinks(rawList.get(stringMap.get(key)));
                        break;
                    case "product-category":
                        item.setItemCategoryCode(Integer.parseInt(rawList.get(stringMap.get(key))));
                        break;
                }
            }
            //商品说明的长度过长
            if (item.getExplanation().length() > 500) {
                item.setExplanation("字数 " + item.getExplanation().length() + " 商品説明の長さが500を超えています。\n\n" + item.getExplanation());
                itemList1.add(item);
            } else {
                itemList.add(item);
            }
        }
        //properties文件的名字取得
        ResourceBundle bundle = ResourceBundle.getBundle(FILENAME);
        //IMG照片下载地址取得
        String itemCsvPath = bundle.getString("EDITCSVPATH");
        //调用itemcodecsv下载
        ItemInfoCsvExportUtil.exportYahooItemInfoToCsv(itemList1, itemCsvPath, "edit");
        //返回list
        return itemList;

    }
}















