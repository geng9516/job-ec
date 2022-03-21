package con.chin.util;

import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class PhotoDownloadUtil {

    //照片下载
    public static void download(List<String> urlList, String itemCode, String path) {

        imageInput(urlList.get(0),itemCode);

        path = path.replace(":", "/");


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

                File file = new File("/Users/geng9516/Documents/EC関連/99_クローラー/写真保存" + path + File.separator + itemCode);

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


        String staticPath = "/Users/geng9516/Documents/ideaworkspace/job-ec/src/main/resources/static/images/itemphoto";

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            URL url = new URL(photoUrl);

            URLConnection connection = url.openConnection();

            connection.setConnectTimeout(5000);

            inputStream = connection.getInputStream();

            byte[] bytes = new byte[2048];

            int len;

            File file = new File(staticPath);

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
