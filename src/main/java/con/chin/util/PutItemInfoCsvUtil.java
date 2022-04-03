package con.chin.util;

import con.chin.pojo.Item;
import con.chin.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class PutItemInfoCsvUtil {

    private static PutItemInfoCsvUtil putItemInfoCsvUtil;
    //??
    @PostConstruct
    public void init() {
        putItemInfoCsvUtil = this;
    }

    @Autowired
    private ItemService itemService;

    private static String FILENAME;

    @Value("${FILENAME}")
    public void setItemphoto(String FILENAME){
        this.FILENAME = FILENAME;
    }

    public static void putItemInfoToCsv(List<Item> itemList, String filePath) {

        //现在时间作为文件名(线程安全的)
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        now = now.replaceAll("-", "").replaceAll(":", "").replace(" ", "");
        PrintWriter printWriter = null;
        //数次数使用
        int count = 1;
        //商品コード 为了在下面读值
        String itemCode = null;
        try {
            //文件路径
            File file = new File("/Users/geng9516/Documents/EC関連/22_商品情報ファイル/アップロードファイル/" + now + "_yahooiteminfo" + ".csv");
            //文件不存在时创建
            if (file == null) {
                file.mkdir();
            }
            //创建csv文件
            printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getPath()), "Shift-JIS")));

            //设置csv文件表头
            printWriter.write("path,name,code,sub-code,price,options,headline,caption,explanation,relevant-links,taxable,point-code,template,sale-limit,delivery,astk-code,condition,product-category,display,sort,sort_priority,sp-additional,lead-time-instock,lead-time-outstock,keep-stock,postage-set,taxrate-type,item-tag,pick-and-delivery-transport-rule-type\n");
            //设置個別商品コード尾数初始值
            int i = 11;
            //设置個別商品コード初始变量
            String subCode = "";
            //遍历item集合下载每一件产品信息
            for (Item item : itemList) {
                //option1时空值是不下载给itemdeflog赋值0(代表还需编辑)
                if(item.getOption1() == null){
                    //数据库中的flog字段赋值0
                    item.setFlog(0);
                    putItemInfoCsvUtil.itemService.updateItem(item);
                    continue;
                }
                //おすすめ商品 relevant-links
                item.setRelevantLinks(RandomGetRelevantLinksUtil.getRelevantLinks(item.getItemPath()));
                //個別商品コード(sub-code)
                //value1以" "分割成各个选项参数(颜色/尺码)
                String[] values1 = item.getValue1().split(" ");
                //value2以" "分割成各个选项参数(颜色/尺码)
                String values = item.getValue2();
                if (values != null) {
                    String[] values2 = values.split(" ");
                    //value1和value2做特定格式拼接
                    for (String value1 : values1) {
                        for (String value2 : values2) {
                            //カラー:ホワイト#サイズ:S=t00106s&カラー:ホワイト#サイズ:M=t00106m&カラー:ホワイト#サイズ:L=t00106l&カラー:グレー#サイズ:S=t001061s&カラー:グレー#サイズ:M=t001061m&カラー:グレー#サイズ:L=t001061l
                            subCode += item.getOption1() + ":" + value1 + "#" + item.getOption2() + ":" + value2 + "=" + item.getItemCode() + (i <= 500 ? i++ : 0) + "&";
                        }
                    }
                    //把最后的"&"去除
                    subCode = subCode.substring(0, subCode.lastIndexOf("&"));
                } else {
                    //個別商品コード(sub-code) 选项只有一个的时候
                    for (String value1 : values1) {
                        subCode += item.getOption1() + ":" + value1 + "=" + item.getItemCode() + i;
                    }
                }
                //オプション(options)
                String options = (item.getOption1() != "" && item.getOption1() != null ? item.getOption1() : "") + " " + (item.getValue1() != "" && item.getValue1() != null ? item.getValue1() : "") +
                        (item.getOption2() != "" && item.getOption2() != null ? "\n" + "\n" + item.getOption2() : "") + " " + (item.getValue2() != "" && item.getValue2() != null ? item.getValue2() : "") +
                        (item.getOption3() != "" && item.getOption3() != null ? "\n" + "\n" + item.getOption3() : "") + " " + (item.getValue3() != "" && item.getValue3() != null ? item.getValue3() : "") +
                        (item.getOption4() != "" && item.getOption4() != null ? "\n" + "\n" + item.getOption4() : "") + " " + (item.getValue4() != "" && item.getValue4() != null ? item.getValue4() : "") +
                        (item.getOption5() != "" && item.getOption5() != null ? "\n" + "\n" + item.getOption5() : "") + " " + (item.getValue5() != "" && item.getValue5() != null ? item.getValue5() : "");
                //商品コード code
                itemCode = item.getItemCode();
                //csv文件写出
                printWriter.print("\"" + item.getItemPath() + "\"");//商品ページのストア内カテゴリパス path
                printWriter.print(",");
                printWriter.print("\"" + item.getItemName() + "\"");//商品名 name
                printWriter.print(",");
                printWriter.print("\"" + itemCode + "\""); //商品コード code
                printWriter.print(",");
                printWriter.print("\"" + subCode + "\"");      //個別商品コード sub-code
                printWriter.print(",");
                printWriter.print("\"" + item.getSalePrice().toString() + "\"");     //通常販売価格 price
                printWriter.print(",");
                printWriter.print("\"" + options + "\"");     //オプション options
                printWriter.print(",");
                printWriter.print("\"" + item.getHeadline() + "\"");      //キャッチコピー headline
                printWriter.print(",");
                printWriter.print("\"" + (item.getCaption() != null ? item.getCaption() : "") + "\"");//商品の説明文 caption
                printWriter.print(",");
                printWriter.print("\"" + item.getExplanation() + "\"");     //商品情報 explanation
                printWriter.print(",");
                printWriter.print("\"" + (item.getRelevantLinks() != null ? item.getRelevantLinks() : "") + "\"");      //おすすめ商品 relevant-links
                printWriter.print(",");
                printWriter.print("\"" + "1" + "\"");      //課税対象 taxable
                printWriter.print(",");
                printWriter.print("\"" + "1" + "\"");     //ポイント倍率 point-code
                printWriter.print(",");
                printWriter.print("\"" + "IT01" + "\"");      //使用中のテンプレート template
                printWriter.print(",");
                printWriter.print("\"" + "" + "\"");     //購入数制限 sale-limit
                printWriter.print(",");
                printWriter.print("\"" + "0" + "\"");      //送料無料の設定 delivery
                printWriter.print(",");
                printWriter.print("\"" + "0" + "\"");      //旧：きょうつく、あすつく astk-code
                printWriter.print(",");
                printWriter.print("\"" + "0" + "\"");      //商品の状態 condition
                printWriter.print(",");
                printWriter.print("\"" + item.getItemCategoryCode() + "\"");      //プロダクトカテゴリ product-category
                printWriter.print(",");
                printWriter.print("\"" + "1" + "\"");      //ページの公開/非公開 display
                printWriter.print(",");
                printWriter.print("\"" + "1" + "\"");      //旧：商品表示順序 sort
                printWriter.print(",");
                printWriter.print("\"" + "" + "\"");      //商品表示優先度 sort_priority
                printWriter.print(",");
                printWriter.print("\"" + (item.getCaption() != null ? item.getCaption() : "") + "\"");      //スマートフォン用商品の補足説明を入力。 sp-additional
                printWriter.print(",");
                printWriter.print("\"" + "1" + "\"");      //発送日情報 lead-time-instock
                printWriter.print(",");
                printWriter.print("\"" + "4000" + "\"");      //発送日情報 lead-time-outstock
                printWriter.print(",");
                printWriter.print("\"" + "1" + "\"");      //購入者キャンセル在庫取り扱い keep-stock
                printWriter.print(",");
                printWriter.print("\"" + "1" + "\"");      //配送グループ postage-set
                printWriter.print(",");
                printWriter.print("\"" + "0.1" + "\"");      //軽減税率コード taxrate-type
                printWriter.print(",");
                printWriter.print("\"" + "" + "\"");     //商品タグ item-tag
                printWriter.print(",");
                printWriter.print("\"" + "0" + "\"");            //荷扱い情報 pick-and-delivery-transport-rule-type
                printWriter.println();
                //個別商品コード值去除
                subCode = "";
                //個別商品コード尾数初始值归1
                i = 1;
                //输出次数
                System.out.println("这是输出的: 第" + count++ + "件了");
//                try {
//                    Thread.sleep(50); //1000 毫秒，也就是1秒.
//                } catch (InterruptedException ex) {
//                    Thread.currentThread().interrupt();
//                }
            }
            System.out.println("总共输出了: " + (count - 1));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("这产品番号为:  " + itemCode + " 的没输出成功,有问题!");
        } finally {
            //文件流关闭
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }

    //制作数据格式yahoo
    private static List<List<Object>> getNovel(List<Item> itemList) {
        List<List<Object>> dataList = new ArrayList<>();
        List<Object> rowList = null;
        int in = 1;
        //设置個別商品コード初始变量
        String subCode = "";
        //遍历item
        int count = 1;
        for (int i = 0; i < itemList.size(); i++) {

            rowList = new ArrayList<Object>();
            Object[] row = new Object[29];

            //個別商品コード(sub-code)
            //value1以" "分割成各个选项参数(颜色/尺码)
            String[] values1 = itemList.get(i).getValue1().split(" ");
            //value2以" "分割成各个选项参数(颜色/尺码)
            String[] values2 = itemList.get(i).getValue2().split(" ");
            //value1和value2做特定格式拼接
            for (String value1 : values1) {
                for (String value2 : values2) {
                    //カラー:ホワイト#サイズ:S=t00106s&カラー:ホワイト#サイズ:M=t00106m&カラー:ホワイト#サイズ:L=t00106l&カラー:グレー#サイズ:S=t001061s&カラー:グレー#サイズ:M=t001061m&カラー:グレー#サイズ:L=t001061l
                    subCode += itemList.get(i).getOption1() + ":" + value1 + "#" + itemList.get(i).getOption2() + ":" + value2 + "=" + itemList.get(i).getItemCode() + (in <= 1000 ? in++ : 0) + "&";
                }
                in = 1;
            }
            //把最后的"&"去除
            subCode = subCode.substring(0, subCode.lastIndexOf("&"));
            //オプション(options)
            String options = (itemList.get(i).getOption3() != "" && itemList.get(i).getOption3() != null ? itemList.get(i).getOption3() : "") + " " + (itemList.get(i).getValue3() != "" && itemList.get(i).getValue3() != null ? itemList.get(i).getValue3() : "") +
                    (itemList.get(i).getOption4() != "" && itemList.get(i).getOption4() != null ? "\n" + "\n" + itemList.get(i).getOption4() : "") + " " + (itemList.get(i).getValue4() != "" && itemList.get(i).getValue4() != null ? itemList.get(i).getValue4() : "") +
                    (itemList.get(i).getOption5() != "" && itemList.get(i).getOption5() != null ? "\n" + "\n" + itemList.get(i).getOption5() : "") + " " + (itemList.get(i).getValue5() != "" && itemList.get(i).getValue5() != null ? itemList.get(i).getValue5() : "");
            //csv文件写出
            row[0] = itemList.get(i).getItemPath();//商品ページのストア内カテゴリパス path
            row[1] = itemList.get(i).getItemName();//商品名 name
            row[2] = itemList.get(i).getItemCode(); //商品コード code
            row[3] = subCode;      //個別商品コード sub-code
            row[4] = itemList.get(i).getPrice();     //通常販売価格 price
            row[5] = options;     //オプション options
            row[6] = itemList.get(i).getHeadline();      //キャッチコピー headline
            row[7] = (itemList.get(i).getCaption() != null ? itemList.get(i).getCaption() : "");//商品の説明文 caption
            row[8] = itemList.get(i).getExplanation();     //商品情報 explanation
            row[9] = (itemList.get(i).getRelevantLinks() != null ? itemList.get(i).getRelevantLinks() : "");      //おすすめ商品 relevant-links
            row[10] = 1;      //課税対象 taxable
            row[11] = 1;     //ポイント倍率 point-code
            row[12] = "IT01";      //使用中のテンプレート template
            row[13] = "";     //購入数制限 sale-limit
            row[14] = 0;      //送料無料の設定 delivery
            row[15] = 0;      //旧：きょうつく、あすつく astk-code
            row[16] = 0;      //商品の状態 condition
            row[17] = itemList.get(i).getItemCategoryCode();      //プロダクトカテゴリ product-category
            row[18] = 1;      //ページの公開/非公開 display
            row[19] = 1;      //旧：商品表示順序 sort
            row[20] = "";      //商品表示優先度 sort_priority
            row[21] = (itemList.get(i).getCaption() != null ? itemList.get(i).getCaption() : "");      //スマートフォン用商品の補足説明を入力。 sp-additional
            row[22] = 1;      //発送日情報 lead-time-instock
            row[23] = 4000;      //発送日情報 lead-time-outstock
            row[24] = 1;      //購入者キャンセル在庫取り扱い keep-stock
            row[25] = 1;      //配送グループ postage-set
            row[26] = 0.1;      //軽減税率コード taxrate-type
            row[27] = "";     //商品タグ item-tag
            row[28] = 0;            //荷扱い情報 pick-and-delivery-transport-rule-type
            for (int j = 0; j < row.length; j++) {
                rowList.add(row[j]);
            }
            dataList.add(rowList);

            subCode = "";

        }
        return dataList;
    }


    //初始化表格
    public static void exportCsv(List<Item> itemList, String filePath) {
        long startTime = System.currentTimeMillis();


        // 设置表格头
        String[] head = {"path", "name", "code", "sub-code", "price", "options",
                "headline", "caption", "explanation", "relevant-links", "taxable",
                "point-code", "template", "sale-limit", "delivery", "astk-code",
                "condition", "product-category", "display", "sort", "sort_priority",
                "sp-additional", "lead-time-instock", "lead-time-outstock", "keep-stock",
                "postage-set", "taxrate-type", "item-tag", "pick-and-delivery-transport-rule-type"};
        List<Object> headList = Arrays.asList(head);
        List<List<Object>> dataList = getNovel(itemList);
        // 导出文件路径
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        now = now.replaceAll("-", "").replaceAll(":", "").replace(" ", "");

        String downloadFilePath = (filePath != null ? filePath : "/Users/geng9516/Documents/EC関連/22_商品情報ファイル/アップロードファイル/");
        // 导出文件名称
        String fileName = now + "_yahooiteminfo";
        // 导出CSV文件
        File csvFile = PutItemInfoCsvUtil.createCSVFile(headList, dataList, downloadFilePath, fileName);
        String fileName2 = csvFile.getName();
        System.out.println(fileName2);


        long endTime = System.currentTimeMillis();
        System.out.println("整个CSV导出" + (endTime - startTime) + "毫秒");
    }

    /**
     * CSV文件生成方法
     *
     * @param head
     * @param dataList
     * @param outPutPath
     * @param filename
     * @return
     */
    public static File createCSVFile(List<Object> head, List<List<Object>> dataList, String outPutPath, String filename) {
        File csvFile = null;
        BufferedWriter csvWtriter = null;
        try {
            csvFile = new File(outPutPath + File.separator + filename + ".csv");
            File parent = csvFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            csvFile.createNewFile();

            // Shift-JIS使正确读取分隔符","
            csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "Shift-JIS"), 1024);
            // 写入文件头部
            writeRow(head, csvWtriter);

            // 写入文件内容
            for (List<Object> row : dataList) {
                writeRow(row, csvWtriter);
            }
            csvWtriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                csvWtriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return csvFile;
    }


    /**
     * 写一行数据方法
     *
     * @param
     * @param csvWriter
     * @throws IOException
     */
    private static void writeRow(List<Object> itemList, BufferedWriter csvWriter) throws IOException {
        // 写入文件头部

        for (Object data : itemList) {
            StringBuffer buf = new StringBuffer();
            String rowStr = buf.append("\"").append(data).append("\t\",").toString();
            csvWriter.write(rowStr);
        }
        csvWriter.newLine();
    }


}



























