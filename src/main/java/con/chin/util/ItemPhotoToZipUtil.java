package con.chin.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.image.AffineTransformOp;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class ItemPhotoToZipUtil {

    private static String fileName;

    @Value("${FILENAME}")
    public void setFileName(String FILENAME) {
        this.fileName = FILENAME;
    }

    //public List<String> fileZipSave(List<String> newVo, String filePath, String zipPath)  throws Exception{
    public static void fileZipSave() {

        ResourceBundle bundle = ResourceBundle.getBundle(fileName);
        //从propertied文件中照片读取地址取得
        String itemphotoPath = bundle.getString("ZIPPHNTOFLEPATH");
        String itemphotoPath1 = bundle.getString("ITEMPHOTOCOPY1");
        //zip文件保存地址
        String zipPath = bundle.getString("ZIPPHNTOFLEPATHTO");
        //产品照片保存的文件对象获取
        File file = new File(itemphotoPath);
        File file1 = new File(itemphotoPath1);
        //产品照片保存的文件所有文件
        File[] photoFiles = file.listFiles();
        File[] photoFiles1 = file1.listFiles();
        if (photoFiles.length > 1) {
            //每23mb大小的产品照片保存用的集合
            List<File> fileList = new ArrayList<>();

            findPhoto(photoFiles, fileList, zipPath);
        } else if (photoFiles1.length > 1) {
            //每23mb大小的产品照片保存用的集合
            List<File> fileList = new ArrayList<>();

            findPhoto(photoFiles1, fileList, zipPath);
        }

    }

    private static void findPhoto(File[] photoFiles, List<File> fileList, String filePath) {
        //创建输入流
        FileInputStream fileInputStream = null;
        //创建输出流
        FileOutputStream fileOutputStream = null;
        //判断是否到达24mb的
        long picLength = 0;
        //计数
        int count = 1;
        try {
            //循环产品照片
            for (File photoFile : photoFiles) {
                //不是文件夹时
                if (!photoFile.isDirectory()) {
                    //输入流
                    fileInputStream = new FileInputStream(photoFile);
                    //获取每个照片的大小
                    byte[] photoByte = readInputStream(fileInputStream);
                    //照片大小叠加
                    picLength += photoByte.length;
                    //把照片放入集合(这行不在这里会发生数据丢失)
                    fileList.add(photoFile);
                    //每24mb输出zip文件
                    if (picLength > 47 * 1024 * 1024) {
                        //现在时间作为文件名(线程安全的)
                        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        //zip文件夹名
                        now = now.replaceAll("-", "").replaceAll(":", "").replace(" ", "");
                        //创建zip文件夹
                        File file1 = new File(filePath + File.separator + "img" + now + ".zip");
                        //输出流
                        fileOutputStream = new FileOutputStream(file1);
                        //开始时间
                        long start = System.currentTimeMillis();
                        //输出zip文件
                        toZip(fileList, fileOutputStream);
                        //结束时间
                        long end = System.currentTimeMillis();
                        System.out.println("输出产品照片ZIP文件" + count++ + "件    耗时：" + (end - start) / 1000 + " 秒");
                        //集合清空
                        fileList.clear();
                        //照片大小清空
                        picLength = 0;
                    }
                    //如果是文件夹略过
                } else {
                    continue;
                }
            }
            //剩下的不超过24mb的产品照片输出zip
            //现在时间作为文件名(线程安全的)
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            //zip文件夹名
            now = now.replaceAll("-", "").replaceAll(":", "").replace(" ", "");
            //创建zip文件夹
            File file1 = new File(filePath + File.separator + "img" + now + ".zip");
            //输出流
            fileOutputStream = new FileOutputStream(file1);
            //开始时间
            long start = System.currentTimeMillis();
            //输出zip文件
            toZip(fileList, fileOutputStream);
            //结束时间
            long end = System.currentTimeMillis();
            System.out.println("输出产品照片ZIP文件" + count++ + "件    耗时：" + (end - start) / 1000 + " 秒");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 得到图片的二进制数据，以二进制封装得到数据，具有通用性
     *
     * @param inStream
     * @return
     * @throws Exception
     */
    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while ((len = inStream.read(buffer)) != -1) {
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }


    /**
     * 压缩成ZIP 方法2
     *
     * @param srcFiles 需要压缩的文件列表
     * @param out      压缩文件输出流
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(List<File> srcFiles, OutputStream out) throws RuntimeException {

        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            for (File srcFile : srcFiles) {
                byte[] buf = new byte[2 * 1024];
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
