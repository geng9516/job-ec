package con.chin.util;

import con.chin.pojo.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
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
    public static void read(List<String> itemCodeList) {

        ResourceBundle bundle = ResourceBundle.getBundle(FILENAME);
        //从propertied文件中照片读取地址取得
        String itemphotoPath = bundle.getString("ITEMPHOTOCOPY");
        File file = new File(itemphotoPath);
        findCopyPhoto(file, itemCodeList);
    }

    //读取photo文件
    private static void findCopyPhoto(File itemphotoPath, List<String> itemCodeList) {

        File[] fileItemPhotos = itemphotoPath.listFiles();
        int count = 1;
        //循环要要拷贝的照片名文件夹
        for (String itemCode : itemCodeList) {
            //判断是否有照片文件
            Integer flog = 0;
            //开始时间
            long start = System.currentTimeMillis();
            for (File fileItemPhoto : fileItemPhotos) {

                if (itemCode.equals(fileItemPhoto.getName())) {
                    //调用拷贝方法
                    copyItemPhoto(fileItemPhoto.getPath(), itemCode);
                    long end = System.currentTimeMillis();
                    System.out.println(count++ + " 件产品照片拷贝完成!  照片文件夹名为 -->  " + itemCode + "    耗时：" + (end - start) + " ms");
                    flog = 1;
                }
            }
            if (flog == 0) {
                System.out.println("********* 第" + count++ + " 件产品照片不存在!  照片文件夹名为 -->  " + itemCode + "  *********");
            }

        }
        System.out.println("总共 " + (count - 1) + " 件产品照片拷贝完成!");
    }

    //照片拷贝方法
    public static void copyItemPhoto(String filePath, String folderName) {

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
//        File file2 = new File(newPath + File.separator + folderName);
        File file2 = new File(newPath);
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
        checkFileExitst2(file.getAbsolutePath());
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
                System.out.println("ファイルが存在しません。");
            } else {
                //文件夹下存在文件时
                for (File file1 : files) {
                    //是一个文件夹
                    if (file1.isDirectory()) {
//                        checkFileExitst2(file1.getAbsolutePath());
                        stringList2.add(file1.getName().replaceAll(".jpg", ""));
                    } else {
                        if (file1.getName().contains("_")) {
                            continue;
                        } else {
//                            stringList2.add(file1.getName().replaceAll(".jpg", ""));
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
    public static void read3(List<String> itemCodeList, String filePath) {

        ResourceBundle bundle = ResourceBundle.getBundle(FILENAME);
        //从propertied文件中照片读取地址取得
//        String itemphotoPath = bundle.getString("ITEMPHOTO");
        File file = new File(filePath);
        //IMG照片下载地址取得
        String itemIMGPath = bundle.getString("ITEM-IMG");
        File file1 = new File(itemIMGPath);
        findPhotoFile(file, file1, itemCodeList);
    }

    public static void findPhotoFile(File itemphotoPath, File itemIMGPath, List<String> itemCodeList) {

        //编辑完成的产品照片
        File[] fileItemPhotos = itemphotoPath.listFiles();

        //产品IMG
        File[] fileItemIMGs = itemIMGPath.listFiles();

        //循环要要拷贝的照片名文件夹
        for (String itemCode : itemCodeList) {
            for (File fileItemPhoto : fileItemPhotos) {

                if (itemCode.equals(fileItemPhoto.getName())) {
                    //删除Itemphpto
                    deleteItemPhotos(fileItemPhoto.getPath(), itemCode);
                }
            }
            if (fileItemIMGs == null || fileItemIMGs.length < 0) {
                continue;
            } else {
                for (File fileItemIMG : fileItemIMGs) {
                    String imgName = fileItemIMG.getName();
                    imgName = imgName.replaceAll(".jpg", "");
                    if (itemCode.equals(imgName)) {
                        //删除itemIMG
                        fileItemIMG.delete();
                    }
                }
            }
        }
    }

    //照片删除
    public static void deleteItemPhotos(String filePath, String fileName) {
        //开始时间
        long start = System.currentTimeMillis();
        //把文件路径的文件价抽象化
        File file = new File(filePath);
        //调用拷贝方法 传入filepath
        File[] files = file.listFiles();
        if (files == null) {
            System.out.println("产品ID为  " + file.getName() + "  没有照片");
        } else {
            for (File listFile : file.listFiles()) {
                if (listFile.isFile()) {
                    listFile.delete();
                }
            }
        }

        file.delete();
        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("照片删除完成!  照片文件价名为 -->  " + fileName + "    耗时：" + (end - start) + " ms");
    }

    //获取照片的枚数
    public static Integer getItemPhotoSize(String itemCode, String itemphotoPath) {
        File file = new File(itemphotoPath);
        if (file == null) {
            return null;
        }
        File[] fileItemPhotos = file.listFiles();
        //循环要要拷贝的照片名文件夹
        for (File fileItemPhoto : fileItemPhotos) {

            if (itemCode.equals(fileItemPhoto.getName())) {
                //调用拷贝方法
                File file1 = new File(fileItemPhoto.getPath());
                File[] files = file1.listFiles();
                return files.length;
            }
        }
        return null;
    }


    //把照片按照itemcode进行分组
    public static void read4(String path) {

        List<String> itemCodeList = new ArrayList<>();
//        //读取properties
//        ResourceBundle bundle = ResourceBundle.getBundle(FILENAME);
//        //从propertied文件中照片读取地址取得
//        String itemphotoPath = bundle.getString("ZIPPHNTOFLEPATH");
        File file = new File(path);
        File[] files = file.listFiles();
        for (File listFile : files) {
            if (!listFile.getName().contains("_")) {
                itemCodeList.add(listFile.getName().replaceAll(".jpg", ""));
            }
        }

        filePath(files, itemCodeList, path);

    }

    //把照片按照itemcode进行分组
    private static void filePath(File[] files, List<String> itemCodeList, String filePath) {

        //循环itemcode 并且每一个itemcode和全部照片中文件做对比,包含的发到一个List里
        for (String itemCode : itemCodeList) {
            //存放filePathList
            List<String> filePathList = new ArrayList<>();
            for (File file : files) {
                String fileName = file.getName();
                if (fileName.contains(itemCode)) {
                    filePathList.add(file.getPath());
                }
            }
            creadPhotoFolder(filePathList, filePath, itemCode);
        }

    }

    //进行拷贝
    private static void creadPhotoFolder(List<String> filePathList, String filePath, String folderName) {

        //创建输入流
        FileInputStream fileInputStream = null;
        //创建输出流
        FileOutputStream fileOutputStream = null;
        //拷贝后的地址加文件名
        File file = new File(filePath + File.separator + "コピー後" + File.separator + folderName);
        try {
            //循环拷贝源中的所以文件
            for (String file1 : filePathList) {
                File file2 = new File(file1);
                if (file2.isFile()) {
                    //拷贝源输入流
                    fileInputStream = new FileInputStream(file2.getPath());
                    //判断是否有那个itemCode的文件夹,没有创建
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    //拷贝后的地址输出流
                    fileOutputStream = new FileOutputStream(file.getPath() + File.separator + file2.getName());
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
        return;

    }


    //检查产品表示照片
    public static void read5(List<Item> itemList, File[] files, File[] files1, File file) {

        List<String> itemImgPath = new ArrayList<>();
        List<String> itemImgPath2 = new ArrayList<>();

        for (Item item : itemList) {
            int flog = 0;
            for (File file1 : files) {
                //如果itemcode和照片文件夹名一致
                if (item.getItemCode().equals(file1.getName().replaceAll(".jpg", ""))) {
                    //从文件夹中把把照片path取得
                    String ImgPath = getItemImg(file1, item.getItemCode());
                    if (ImgPath != null) {
                        itemImgPath.add(ImgPath);
                    }
                    flog = 1;
                }
            }
            //如果在第一个文件中为找到itemIGMG文件
            if (flog == 0) {
                for (File file1 : files1) {
                    //如果itemcode和照片文件夹名一致
                    if (item.getItemCode().equals(file1.getName().replaceAll(".jpg", ""))) {
                        //从文件夹中把把照片path取得
                        String ImgPath = getItemImg(file1, item.getItemCode());
                        if (ImgPath != null) {
                            itemImgPath.add(ImgPath);
                        }
                        flog = 1;
                    }
                }
            }
            if (flog == 0) {
                System.out.println(item.getItemCode() + " 这件产品没找到照片");
                itemImgPath2.add(item.getItemCode());

                continue;
            }
            System.out.println("------------------------------" + item.getItemCode() + " 这件产品已找到照片------------------------------");
        }
        DataExportUtil.exportItemCodeCsv(itemImgPath2, "没有照片的产品");
        System.out.println("照片地址已加载完成,准备下载!!!  文件数为" + itemImgPath.size() + " 件");
        setItemImg(itemImgPath, file);

    }

    //把照片取得
    private static String getItemImg(File file1, String itemCode) {

        File[] files = file1.listFiles();
        for (File file : files) {
            if (itemCode.equals(file.getName().replaceAll(".jpg", ""))) {
                return file.getPath();
            }
        }
        return null;

    }

    //把照片下载到itemIMG文件夹中
    private static void setItemImg(List<String> itemImgPath, File file) {

        //创建输入流
        FileInputStream fileInputStream = null;
        //创建输出流
        FileOutputStream fileOutputStream = null;
        //拷贝后的地址加文件名
//        File file = new File(file4);
        try {
            //循环拷贝源中的所以文件
            for (String file1 : itemImgPath) {
                File file2 = new File(file1);
                if (file2.isFile()) {
                    //拷贝源输入流
                    fileInputStream = new FileInputStream(file2.getPath());
                    //判断是否有那个itemCode的文件夹,没有创建
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    //拷贝后的地址输出流
                    fileOutputStream = new FileOutputStream(file.getPath() + File.separator + file2.getName());
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
        return;


    }


}



















