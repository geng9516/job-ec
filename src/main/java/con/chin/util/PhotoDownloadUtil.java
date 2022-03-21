package con.chin.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class PhotoDownloadUtil {


    private static String fileName;

    @Value("${FILENAME}")
    public void setFileName(String FILENAME){
        this.fileName = FILENAME;
    }

    //照片下载
    public static void download(List<String> urlList, String itemCode, String path) {

        imageInput(urlList.get(0),itemCode);

        path = path.replace(":", "/");

        //照片下载地址取得
        ResourceBundle bundle = ResourceBundle.getBundle(fileName);
        String itemphoto = bundle.getString("ITEMPHOTO");

        InputStream inputStream = null;
        OutputStream outputStream = null;

        for (int i = 0; i < urlList.size(); i++) {
            try {
                URL url = new URL(urlList.get(i));

                URLConnection connection = url.openConnection();

                connection.setConnectTimeout(5000);

                inputStream = connection.getInputStream();

                byte[] bytes = new byte[2048];

                int len;

                File file = new File(itemphoto + path + File.separator + itemCode);

                if (!file.exists()) {
                    file.mkdirs();
                }

                outputStream = new FileOutputStream(file.getPath() + File.separator + itemCode + (i==0?"":"_"+i) + ".jpg", true);

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
    public static void imageInput(String photoUrl, String fileName) {

        //照片下载地址取得
        ResourceBundle bundle = ResourceBundle.getBundle(fileName);
        String itemphoto = bundle.getString("FILENAME");

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            URL url = new URL(photoUrl);

            URLConnection connection = url.openConnection();

            connection.setConnectTimeout(5000);

            inputStream = connection.getInputStream();

            byte[] bytes = new byte[2048];

            int len;

            File file = new File(itemphoto);

            if (!file.exists()) {
                file.mkdirs();
            }

            outputStream = new FileOutputStream(file.getPath() + File.separator + fileName + ".jpg", true);

            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }

        } catch (MalformedURLException e){
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
