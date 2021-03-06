package con.chin.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class PhotoDownloadUtil {


    private static String FILENAME;

    @Value("${FILENAME}")
    public void setFileName(String FILENAME) {
        this.FILENAME = FILENAME;
    }

    //照片下载
    public static void download(List<String> urlList, String itemCode, String path) {

        //产品表示照片下载
        String imgUrl = urlList.get(0);
        if (!imgUrl.contains("https:")) {
            imgUrl = "https:" + imgUrl;
        }
        imageInput(imgUrl, itemCode);
        //创建流
        InputStream inputStream = null;
        OutputStream outputStream = null;
        Integer urls = urlList.size();
//        urls = urls > 21 ? 21 : urls;
        //遍历照片集合
        for (int i = 0; i < urls; i++) {
            try {
                String urlString = urlList.get(i);
                if (!urlString.contains("https:")) {
                    urlString = "https:" + urlString;
                }
                URL url = new URL(urlString);

                URLConnection connection = url.openConnection();

                connection.setConnectTimeout(50000);

                inputStream = connection.getInputStream();

                byte[] bytes = new byte[2048];

                int len;

                File file = new File(path + File.separator + itemCode);

                if (!file.exists()) {
                    file.mkdirs();
                }

                outputStream = new FileOutputStream(file.getPath() + File.separator + itemCode + (i == 0 ? "" : "_" + i) + ".jpg", true);

                while ((len = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, len);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //Itemimage
    public static void imageInput(String photoUrl, String imgName) {

        //properties文件的名字取得
        ResourceBundle bundle = ResourceBundle.getBundle(FILENAME);
        //IMG照片下载地址取得
        String itemIMG = bundle.getString("ITEM-IMG");
        if ("".equals(itemIMG)) {
            return;
        }

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            URL url = new URL(photoUrl);

            URLConnection connection = url.openConnection();

            connection.setConnectTimeout(5000);

            inputStream = connection.getInputStream();

            byte[] bytes = new byte[2048];

            int len;

            File file = new File(itemIMG);

            if (!file.exists()) {
                file.mkdirs();
            }

            outputStream = new FileOutputStream(file.getPath() + File.separator + imgName + ".jpg", true);

            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
            outputStream.flush();
            //当前时间
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            System.out.println("产品主图番号为:  " + imgName + "   的下载完成 :  " + now);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
