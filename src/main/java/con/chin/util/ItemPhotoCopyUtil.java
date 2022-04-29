package con.chin.util;

import con.chin.pojo.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class ItemPhotoCopyUtil {

    private static String FILENAME;

    @Value("${FILENAME}")
    public void setFileName(String FILENAME) {
        this.FILENAME = FILENAME;
    }

    //拷贝照片
    public static void read(List<Item> itemCodeList) {

        ResourceBundle bundle = ResourceBundle.getBundle(FILENAME);
        //从propertied文件中照片读取地址取得
        String itemphotoPath = bundle.getString("ITEMPHOTOCOPY");
        File file = new File(itemphotoPath);
        //循环要要拷贝的照片名文件夹
        for (Item item : itemCodeList) {
            checkFileExitst(file.getAbsolutePath(), item.getItemCode());
        }
    }

    //判断itemCode的文件价存在
    public static void checkFileExitst(String filePath, String fileName) {
        //把文件路径的文件价抽象化
        File file = new File(filePath);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files.length == 0) {
//                System.out.println("ファイルが存在しません。");
            } else {
                //文件夹下存在文件时
                for (File file1 : files) {
                    //是一个文件夹
                    if (file1.isDirectory()) {
                        //文件夹名叫 .DS_Store 时跳过
                        if (".DS_Store".equals(file1.getName())) {
                            continue;
                            //文件名和itemCode一致时
                        } else if (fileName.equals(file1.getName())) {
                            //调用拷贝方法 传入filepath
                            copyItemPhoto(file1.getAbsolutePath(), file1.getName());
                            //不一致时递归
                        } else {
                            checkFileExitst(file1.getAbsolutePath(), fileName);
                        }
                    }
                }
            }
        } else {
            System.out.println("文件路径不存在!");
        }
    }

    //照片拷贝方法
    public static void copyItemPhoto(String filePath, String folderName) {
        //开始时间
        long start = System.currentTimeMillis();
        //创建输入流
        FileInputStream fileInputStream = null;
        //创建输出流
        FileOutputStream fileOutputStream = null;
        //读取properties
        ResourceBundle bundle = ResourceBundle.getBundle(FILENAME);
        //从propertied文件中照片拷贝地址取得
        String newPath = bundle.getString("ITEMPHOTOCOPY1");
        //照片拷贝地址
        File file = new File(filePath);
        //拷贝后的地址加文件名
        File file2 = new File(newPath + File.separator + folderName);
        //拷贝源中的所以文件
        File[] files = file.listFiles();
        try {
            //循环拷贝源中的所以文件
            for (File file1 : files) {
                if (file1.isFile()) {
                    //拷贝源输入流
                    fileInputStream = new FileInputStream(file1.getPath());
                    //判断是否有那个itemCode的文件夹,没有创建
                    if (!file2.exists()) {
                        file2.mkdir();
                    }
                    //拷贝后的地址输出流
                    fileOutputStream = new FileOutputStream(file2.getPath() + File.separator + file1.getName());
                    // 一次复制1MB 设定
                    byte[] bytes = new byte[1024 * 1024];
                    int readCount = 0;
                    while ((readCount = fileInputStream.read(bytes)) != -1) {
                        fileOutputStream.write(bytes, 0, readCount);
                    }
                    //清空流的管道
                    fileOutputStream.flush();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭流
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
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
        long end = System.currentTimeMillis();
        System.out.println("照片拷贝完成!  照片文件价名为 -->  " + folderName + "    耗时：" + (end - start) + " ms");
        return;
    }

    //取得所以照片保存变量
    private static final List<String> stringList2 = new ArrayList<>();

    //取得所以照片名
    public static List<String> read2() {

        ResourceBundle bundle = ResourceBundle.getBundle(FILENAME);
        //从propertied文件中照片读取地址取得
        String itemphotoPath = bundle.getString("ITEMPHOTOCOPY");
        File file = new File(itemphotoPath);
        List<String> stringList = new ArrayList<>();
        //循环要要拷贝的照片名文件夹
        stringList = checkFileExitst2(file.getAbsolutePath());
        return stringList2;
    }

    //判断itemCode的文件价存在
    public static List<String> checkFileExitst2(String filePath) {
        List<String> stringList = new ArrayList<>();
        //把文件路径的文件价抽象化
        File file = new File(filePath);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files.length == 0) {
//                System.out.println("ファイルが存在しません。");
            } else {
                //文件夹下存在文件时
                for (File file1 : files) {
                    //是一个文件夹
                    if (file1.isDirectory()) {
                        checkFileExitst2(file1.getAbsolutePath());
                    } else {
                        if (file1.getName().contains("_")) {
                            continue;
                        } else {
                            stringList2.add(file1.getName().replaceAll(".jpg", ""));
                        }
                    }
                }
            }
        } else {
            System.out.println("文件路径不存在!");
        }
        return stringList;
    }

    //产品照片删除
    public static void read3(List<String> itemCodeList) {

        ResourceBundle bundle = ResourceBundle.getBundle(FILENAME);
        //从propertied文件中照片读取地址取得
        String itemphotoPath = bundle.getString("ITEMPHOTO");
        File file = new File(itemphotoPath);
        //IMG照片下载地址取得
        String itemIMG = bundle.getString("ITEM-IMG");
        File file1 = new File(itemIMG);
        //循环要要拷贝的照片名文件夹
        for (String itemCode : itemCodeList) {
            //开始时间
            long start = System.currentTimeMillis();
            //删除Itemphpto
            deleteItemPhotos(file.getAbsolutePath(), itemCode);
            //删除itemIMG
            deleteItemImage(file1.getAbsolutePath(), itemCode);
            //结束时间
            long end = System.currentTimeMillis();
            System.out.println("产品code为: " + itemCode + " 的产品删除成功    耗时：" + (end - start) + " ms");
        }
    }

    //照片删除
    public static void deleteItemPhotos(String filePath, String fileName) {
        //开始时间
        long start = System.currentTimeMillis();
        //把文件路径的文件价抽象化
        File file = new File(filePath);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files.length == 0) {
//                System.out.println("ファイルが存在しません。");
            } else {
                //文件夹下存在文件时
                for (File file1 : files) {
                    //是一个文件夹
                    if (file1.isDirectory()) {
                        //文件夹名叫 .DS_Store 时跳过
                        if (".DS_Store".equals(file1.getName())) {
                            continue;
                            //文件名和itemCode一致时
                        } else if (fileName.equals(file1.getName())) {
                            //调用拷贝方法 传入filepath
                            for (File listFile : file1.listFiles()) {
                                if (listFile.isFile()) {
                                    listFile.delete();
                                }
                            }
                            file1.delete();
                            //结束时间
                            long end = System.currentTimeMillis();
                            System.out.println("照片删除完成!  照片文件价名为 -->  " + fileName + "    耗时：" + (end - start) + " ms");
                            //不一致时递归
                        } else {
                            deleteItemPhotos(file1.getAbsolutePath(), fileName);
                        }
                    }
                }
            }
        } else {
            System.out.println("文件路径不存在!");
        }
    }

    //itemIMG删除
    public static void deleteItemImage(String filePath, String fileName) {
        //开始时间
        long start = System.currentTimeMillis();
        //把文件路径的文件价抽象化
        File file = new File(filePath);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files.length == 0) {
//                System.out.println("ファイルが存在しません。");
            } else {
                //文件夹下存在文件时
                for (File file1 : files) {
                    String imgName = file1.getName();
                    imgName = imgName.replaceAll(".jpg", "");
                    if (fileName.equals(imgName)) {
                        file1.delete();
                        long end = System.currentTimeMillis();
                        System.out.println("IMG照片删除完成!  照片文件价名为 -->  " + fileName + "    耗时：" + (end - start) + " ms");
                    } else {
                        return;
                    }
                }
            }
        }
    }

}



















