package con.chin.util;

import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URLEncoder;
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
        String itemCsvPath = bundle.getString("ITEMCSVPATH");

        CsvWriter writer = null;
        try {
            writer = CsvUtil.getWriter(itemCsvPath  + File.separator + fileName + "_itemcode" + ".txt", Charset.forName("Shift-JIS"));
            writer.write(itemCodeList);


        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }


    }

    //yahooショップ商品のcsvファイルダウンロード
    public static void exportOptionCsv(List<String> itemCodeList) {

        //现在时间作为文件名(线程安全的)
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        now = now.replaceAll("-", "").replaceAll(":", "").replace(" ", "");
        //properties文件的名字取得
        ResourceBundle bundle = ResourceBundle.getBundle(FILENAME);
        //IMG照片下载地址取得
        String itemCsvPath = bundle.getString("ITEMCSVPATH");
        //创建输出流


    }
}
