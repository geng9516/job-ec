package con.chin.util;

import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import con.chin.pojo.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class DataExportUtil {

    private static String FILENAME;

    @Value("${FILENAME}")
    public void setItemphoto(String FILENAME) {
        this.FILENAME = FILENAME;
    }

    //产品ID导出
    public static void exportItemCodeCsv(List<String> itemCodeList, String fileName) {

        //properties文件的名字取得
        ResourceBundle bundle = ResourceBundle.getBundle(FILENAME);
        //IMG照片下载地址取得
        String itemCsvPath = bundle.getString("URL");

        CsvWriter writer = null;
        try {
            writer = CsvUtil.getWriter(itemCsvPath + File.separator + fileName + ".txt", Charset.forName("Shift-JIS"), true);
            writer.write(itemCodeList);
            writer.flush();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    //库存csv导出
    public static void exportItemStockCsv(List<String> itemCodeList, String filePath, String fileName) {
        //开始时间
        long start = System.currentTimeMillis();
        //现在时间作为文件名(线程安全的)
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        now = now.replaceAll("-", "").replaceAll(":", "").replace(" ", "");

        CsvWriter writer = null;
        try {
            writer = CsvUtil.getWriter(filePath + File.separator + fileName + now + ".csv", Charset.forName("Shift-JIS"));
            //csv内容保存
            List<String[]> writeLine = new ArrayList<>();
            //设置csv文件表头
            String[] strings = {"code", "sub-code", "quantity", "allow-overdraft", "stock-close"};
            writeLine.add(strings);
            for (String itemCode : itemCodeList) {
                //保存每一列的数据使用
                String[] column = new String[5];
                column[0] = itemCode;
                column[1] = "";
                column[2] = "999";
                column[3] = "1";
                column[4] = "";
                writeLine.add(column);
            }
            writer.write(writeLine);
            writer.flush();
            //结束时间
            long end = System.currentTimeMillis();
            System.out.println(fileName + "库存CSV, 总共输出了: " + itemCodeList.size() + " 行数据    耗时：" + (end - start) + " ms");

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    //option csv导出
    public static void exportItemOptionCsv(List<Item> itemList, String filePath, String fileName) {
        //开始时间
        long start = System.currentTimeMillis();
        //现在时间作为文件名(线程安全的)
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        now = now.replaceAll("-", "").replaceAll(":", "").replace(" ", "");

        CsvWriter writer = null;
        try {
            writer = CsvUtil.getWriter(filePath + File.separator + fileName + now + ".csv", Charset.forName("Shift-JIS"));
            //csv内容保存
            List<String[]> writeLine = new ArrayList<>();
            //设置csv文件表头
            String[] strings = {"code", "sub-code", "name", "option-name-1", "option-value-1", "unselectable-1", "spec-id-1", "spec-value-id-1", "option-charge-1", "option-name-2", "option-value-2", "spec-id-2", "spec-value-id-2", "etc-options", "lead-time-instock", "lead-time-outstock", "sub-code-img1", "main-flag", "exist-flag", "pick-and-delivery-code", "yamato-ff-flag"};
            writeLine.add(strings);
            for (Item item : itemList) {

                try {


                    //option1不为空时
                    if (item.getOption1() != null && !"".equals(item.getOption1())) {
                        String[] value1 = item.getValue1().split(" ");
                        for (String optionValue : value1) {
                            //追加金保存用
                            String optionCharge = null;
                            //追加金有的话
                            if (optionValue.contains("（+")) {
                                optionCharge = optionValue.substring(optionValue.lastIndexOf("+") + 1, optionValue.length() - 2);
                                optionValue = optionValue.substring(0, optionValue.indexOf("（"));
                            }
                            //保存每一列的数据使用
                            String[] column = new String[21];
                            column[0] = item.getItemCode(); //itemcode
                            column[1] = ""; //sub-code
                            column[2] = item.getItemName(); //name
                            column[3] = item.getOption1();  //option1名
                            column[4] = optionValue; //option-value-1
                            column[5] = "0"; //unselectable-1
                            column[6] = ""; //spec-id-1
                            column[7] = ""; //spec-value-id-1
                            column[8] = optionCharge == null ? "" : optionCharge.replaceAll(",", ""); //option-charge-1
                            column[9] = ""; //option-name-2
                            column[10] = ""; //option-value-2
                            column[11] = ""; //spec-id-2
                            column[12] = ""; //spec-value-id-2
                            column[13] = ""; //etc-options
                            column[14] = ""; //lead-time-instock
                            column[15] = ""; //lead-time-outstock
                            column[16] = ""; //sub-code-img1
                            column[17] = ""; //main-flag
                            column[18] = ""; //exist-flag
                            column[19] = ""; //pick-and-delivery-code
                            column[20] = ""; //yamato-ff-flag
                            //一行数据
                            writeLine.add(column);
                        }
                    }
                    //option2不为空时
                    if (item.getOption2() != null && !"".equals(item.getOption2())) {
                        String[] value2 = item.getValue2().split(" ");
                        for (String optionValue : value2) {
                            //追加金保存用
                            String optionCharge = null;
                            //追加金有的话
                            if (optionValue.contains("（+")) {
                                optionCharge = optionValue.substring(optionValue.lastIndexOf("+") + 1, optionValue.length() - 2);
                                optionValue = optionValue.substring(0, optionValue.indexOf("（"));
                            }
                            //保存每一列的数据使用
                            String[] column = new String[21];
                            column[0] = item.getItemCode(); //itemcode
                            column[1] = ""; //sub-code
                            column[2] = item.getItemName(); //name
                            column[3] = item.getOption2();  //option1名
                            column[4] = optionValue; //option-value-1
                            column[5] = "0"; //unselectable-1
                            column[6] = ""; //spec-id-1
                            column[7] = ""; //spec-value-id-1
                            column[8] = optionCharge == null ? "" : optionCharge.replaceAll(",", ""); //option-charge-1
                            column[9] = ""; //option-name-2
                            column[10] = ""; //option-value-2
                            column[11] = ""; //spec-id-2
                            column[12] = ""; //spec-value-id-2
                            column[13] = ""; //etc-options
                            column[14] = ""; //lead-time-instock
                            column[15] = ""; //lead-time-outstock
                            column[16] = ""; //sub-code-img1
                            column[17] = ""; //main-flag
                            column[18] = ""; //exist-flag
                            column[19] = ""; //pick-and-delivery-code
                            column[20] = ""; //yamato-ff-flag
                            //一行数据
                            writeLine.add(column);
                        }
                    }
                    //option3不为空时
                    if (item.getOption3() != null && !"".equals(item.getOption3())) {
                        String[] value3 = item.getValue3().split(" ");
                        for (String optionValue : value3) {
                            //追加金保存用
                            String optionCharge = null;
                            //追加金有的话
                            if (optionValue.contains("（+")) {
                                optionCharge = optionValue.substring(optionValue.lastIndexOf("+") + 1, optionValue.length() - 2);
                                optionValue = optionValue.substring(0, optionValue.indexOf("（"));
                            }
                            //保存每一列的数据使用
                            String[] column = new String[21];
                            column[0] = item.getItemCode(); //itemcode
                            column[1] = ""; //sub-code
                            column[2] = item.getItemName(); //name
                            column[3] = item.getOption3();  //option1名
                            column[4] = optionValue; //option-value-1
                            column[5] = "0"; //unselectable-1
                            column[6] = ""; //spec-id-1
                            column[7] = ""; //spec-value-id-1
                            column[8] = optionCharge == null ? "" : optionCharge.replaceAll(",", ""); //option-charge-1
                            column[9] = ""; //option-name-2
                            column[10] = ""; //option-value-2
                            column[11] = ""; //spec-id-2
                            column[12] = ""; //spec-value-id-2
                            column[13] = ""; //etc-options
                            column[14] = ""; //lead-time-instock
                            column[15] = ""; //lead-time-outstock
                            column[16] = ""; //sub-code-img1
                            column[17] = ""; //main-flag
                            column[18] = ""; //exist-flag
                            column[19] = ""; //pick-and-delivery-code
                            column[20] = ""; //yamato-ff-flag
                            //一行数据
                            writeLine.add(column);
                        }
                    }
                    //option4不为空时
                    if (item.getOption4() != null && !"".equals(item.getOption4())) {
                        String[] value4 = item.getValue4().split(" ");
                        for (String optionValue : value4) {
                            //追加金保存用
                            String optionCharge = null;
                            //追加金有的话
                            if (optionValue.contains("（+")) {
                                optionCharge = optionValue.substring(optionValue.lastIndexOf("+") + 1, optionValue.length() - 2);
                                optionValue = optionValue.substring(0, optionValue.indexOf("（"));
                            }
                            //保存每一列的数据使用
                            String[] column = new String[21];
                            column[0] = item.getItemCode(); //itemcode
                            column[1] = ""; //sub-code
                            column[2] = item.getItemName(); //name
                            column[3] = item.getOption4();  //option1名
                            column[4] = optionValue; //option-value-1
                            column[5] = "0"; //unselectable-1
                            column[6] = ""; //spec-id-1
                            column[7] = ""; //spec-value-id-1
                            column[8] = optionCharge == null ? "" : optionCharge.replaceAll(",", ""); //option-charge-1
                            column[9] = ""; //option-name-2
                            column[10] = ""; //option-value-2
                            column[11] = ""; //spec-id-2
                            column[12] = ""; //spec-value-id-2
                            column[13] = ""; //etc-options
                            column[14] = ""; //lead-time-instock
                            column[15] = ""; //lead-time-outstock
                            column[16] = ""; //sub-code-img1
                            column[17] = ""; //main-flag
                            column[18] = ""; //exist-flag
                            column[19] = ""; //pick-and-delivery-code
                            column[20] = ""; //yamato-ff-flag
                            //一行数据
                            writeLine.add(column);
                        }
                    }
                    //option4不为空时
                    if (item.getOption5() != null && !"".equals(item.getOption5())) {
                        String[] value5 = item.getValue5().split(" ");
                        for (String optionValue : value5) {
                            //追加金保存用
                            String optionCharge = null;
                            //追加金有的话
                            if (optionValue.contains("（+")) {
                                optionCharge = optionValue.substring(optionValue.lastIndexOf("+") + 1, optionValue.length() - 2);
                                optionValue = optionValue.substring(0, optionValue.indexOf("（"));
                            }
                            //保存每一列的数据使用
                            String[] column = new String[21];
                            column[0] = item.getItemCode(); //itemcode
                            column[1] = ""; //sub-code
                            column[2] = item.getItemName(); //name
                            column[3] = item.getOption5();  //option1名
                            column[4] = optionValue; //option-value-1
                            column[5] = "0"; //unselectable-1
                            column[6] = ""; //spec-id-1
                            column[7] = ""; //spec-value-id-1
                            column[8] = optionCharge == null ? "" : optionCharge.replaceAll(",", ""); //option-charge-1
                            column[9] = ""; //option-name-2
                            column[10] = ""; //option-value-2
                            column[11] = ""; //spec-id-2
                            column[12] = ""; //spec-value-id-2
                            column[13] = ""; //etc-options
                            column[14] = ""; //lead-time-instock
                            column[15] = ""; //lead-time-outstock
                            column[16] = ""; //sub-code-img1
                            column[17] = ""; //main-flag
                            column[18] = ""; //exist-flag
                            column[19] = ""; //pick-and-delivery-code
                            column[20] = ""; //yamato-ff-flag
                            //一行数据
                            writeLine.add(column);
                        }
                    }
                } catch (Exception e) {
                    List<String> stringList = new ArrayList<>();
                    stringList.add(item.getItemCode());
                    exportItemCodeCsv(stringList,"optionsEorr");

                }
            }
            writer.write(writeLine);
            writer.flush();
            //结束时间
            long end = System.currentTimeMillis();
            System.out.println(fileName + "option CSV, 总共输出了: " + itemList.size() + " 行数据    耗时：" + (end - start) + " ms");

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    //AuStock csv导出
    public static void exportAuItemOptionCsv(List<Item> itemList, String filePath, String fileName) {
        //开始时间
        long start = System.currentTimeMillis();
        //现在时间作为文件名(线程安全的)
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        now = now.replaceAll("-", "").replaceAll(":", "").replace(" ", "");

        CsvWriter writer = null;
        try {
            writer = CsvUtil.getWriter(filePath + File.separator + fileName + now + ".csv", Charset.forName("Shift-JIS"));
            //csv内容保存
            List<String[]> writeLine = new ArrayList<>();
            //设置csv文件表头
            String[] strings = {"ctrlCol", "lotNumber", "itemCode", "stockSegment", "stockCount",
                    "choicesStockHorizontalName", "choicesStockHorizontalCode", "choicesStockHorizontalSeq",
                    "choicesStockVerticalName", "choicesStockVerticalCode", "choicesStockVerticalSeq",
                    "choicesStockCount", "choicesStockShippingDayId", "choicesStockShippingDayDispTxt",
                    "choicesStockImageUrl", "choicesStockColorSegment"};
            writeLine.add(strings);
            for (Item item : itemList) {

                //option1不为空时
                if (item.getOption1() != null && !"".equals(item.getOption1())) {
                    //把value1值以空格分割出来
                    String[] value1 = item.getValue1().split(" ");
                    //option2不为空时
                    if (item.getOption2() != null && !"".equals(item.getOption2())) {
                        //把value2值以空格分割出来
                        String[] value2 = item.getValue2().split(" ");
                        //
                        for (int i = 0; i < value1.length; i++) {
                            //
                            for (int j = 0; j < value2.length; j++) {

                                //保存每一列的数据使用
                                String[] column = new String[16];
                                column[0] = "N"; //
                                column[1] = ""; //
                                column[2] = item.getItemCode(); //
                                column[3] = "2";  //
                                column[4] = ""; //
                                column[5] = value1[i]; //
                                column[6] = value1[i]; //
                                column[7] = String.valueOf(i); //
                                column[8] = value2[j]; //
                                column[9] = "A" + j; //
                                column[10] = String.valueOf(j); //
                                column[11] = String.valueOf(999); //
                                column[12] = ""; //
                                column[13] = ""; //
                                column[14] = ""; //
                                column[15] = ""; //
                                //一行数据
                                writeLine.add(column);
                            }

                        }
                    } else {
                        //
                        for (int i = 0; i < value1.length; i++) {

                            //保存每一列的数据使用
                            String[] column = new String[16];
                            column[0] = "N"; //
                            column[1] = ""; //
                            column[2] = item.getItemCode(); //
                            column[3] = "2";  //
                            column[4] = ""; //
                            column[5] = value1[i]; //
                            column[6] = value1[i]; //
                            column[7] = String.valueOf(i); //
                            column[8] = ""; //
                            column[9] = ""; //
                            column[10] = ""; //
                            column[11] = String.valueOf(999); //
                            column[12] = ""; //
                            column[13] = ""; //
                            column[14] = ""; //
                            column[15] = ""; //
                            //一行数据
                            writeLine.add(column);
                        }
                    }
                }
            }
            writer.write(writeLine);
            writer.flush();
            //结束时间
            long end = System.currentTimeMillis();
            System.out.println(fileName + "option CSV, 总共输出了: " + itemList.size() + " 行数据    耗时：" + (end - start) + " ms");

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
