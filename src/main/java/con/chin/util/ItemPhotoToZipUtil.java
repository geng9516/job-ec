package con.chin.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
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

        //创建输入流
        FileInputStream fileInputStream = null;
        //创建输出流
        FileOutputStream fileOutputStream = null;

        ZipOutputStream zipOutputStream = null;

        ResourceBundle bundle = ResourceBundle.getBundle(fileName);
        //从propertied文件中照片读取地址取得
        String itemphotoPath = bundle.getString("ZIPPHNTOFLEPATH");

        //zip文件保存地址
        String zipPath = bundle.getString("ZIPPHNTOFLEPATHTO");

        //String fastZipPath = zipPath;

        File file = new File(itemphotoPath);

        File[] photoFiles = file.listFiles();

        List<String> fileList = new ArrayList<>();
        fileList.add(zipPath);

//        long picLength = 0;
//        try {
//            for (File photoFile : photoFiles) {
//                if (photoFile.isFile()) {
//                //fileInputStream = new FileInputStream(photoFile.getPath());
//                }
//
//                //得到图片的二进制数据，以二进制封装得到数据，具有通用性
////                byte[] data = readInputStream(inStream);
//                picLength += data.length;
//                // 如果图片大于10M,就再创建一个文件夹
//                if (picLength > 10 * 1024 * 1024) {
//                    picLength = data.length;
//                    zipPath = zipPath + i;
//                    File files = new File(zipPath);
//                    //如果文件夹存在
//                    if (!files.exists()) {
//
//                        files.mkdirs();
//                    }
//                    fileList.add(zipPath);
//                }
//
//                //new一个文件对象用来保存图片，默认保存当前工程根目录
//
//                String picName = zipPath + "/" + "图片名称"+".png";
//                //logger.info("成交确认单写入文件的图片名称：=======>>>>  " + picName);
//                File imageFile = new File(picName);
//                //创建输出流
//                FileOutputStream outStream = new FileOutputStream(imageFile);
//                //写入数据
//                outStream.write(data);
//                //关闭输出流
//                outStream.close();
//            }
            // 压缩法
//            for (int i = 0; i < fileList.size(); i++) {
//                zipPath = zipPath + i;
//                FileOutputStream fos1 = new FileOutputStream(new File(zipPath + ".zip"));
//                toZip1(fileList.get(i), fos1, false);
//
//                File zipFile = new File(zipPath + ".zip");
//                //logger.info("成交确认单压缩文件名称=======>>>>  " + zipFile.getName() + ": " + zipFile.getPath());
//                FileInputStream fileInputStream = new FileInputStream(zipFile);
//                // 注意：这个是我保存到自己服务器代码
//                String savePath = mediaStorageService.storageMedia(fileInputStream, "zip");
//                if (org.apache.commons.lang.StringUtils.isNotBlank(savePath)) {
//                    savePathList.add(savePath);
//                }
//                if (org.apache.commons.lang.StringUtils.isNotEmpty(savePath)) {
//                    // 删除文件和压缩文件
//                    FileUtil.delFolder(fileList.get(i));
//                    FileUtil.delFolder(zipPath + ".zip");
//                }
//            }


//            ---------------------------------------------------------------

////现在时间作为文件名(线程安全的)
//                String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//                now = now.replaceAll("-", "").replaceAll(":", "").replace(" ", "");
//
//                if (photoFile.isFile()) {
//
//                    fileInputStream = new FileInputStream(photoFile.getPath());
//
//                    data = readInputStream(fileInputStream);
//                    picLength += data.length;
//                    // 如果图片大于10M,就再创建一个文件夹
//                    if (picLength > 24 * 1024 * 1024) {
//
////                        picLength = data.length;
////                        zipPath = zipPath + File.separator + now;
//                        File files = new File(zipPath);
//                        //如果文件夹存在
//                        if (!files.exists()) {
//                            files.mkdirs();
//                        }
//
////                        File file1 = new File(zipPath);
////                        if (!file1.exists()) {
////                            file1.mkdirs();
////                        }
////                        fileOutputStream = new FileOutputStream(file1.getPath() + File.separator + now + ".zip");
////                        zipOutputStream = new ZipOutputStream(fileOutputStream);
////                        ZipEntry zipEntry = new ZipEntry(photoFile.getName() + ".jpg");
////                        zipOutputStream.putNextEntry(zipEntry);
//                        picLength = 0;
//                        fastZipPath = "";
////                        if(picLength > 48 * 1024 * 1024){
////                            data = null;
////                        }
//                    }
//
//                    if (fastZipPath != null && fastZipPath != "") {
//                        File file1 = new File(fastZipPath);
//                        if (!file1.exists()) {
//                            file1.mkdirs();
//                        }
//                        fileOutputStream = new FileOutputStream(file1.getPath() + File.separator + now + ".zip");
//                        zipOutputStream = new ZipOutputStream(fileOutputStream);
//                        ZipEntry zipEntry = new ZipEntry(photoFile.getName());
//                        zipOutputStream.putNextEntry(zipEntry);
//                    }else {
//                        File file1 = new File(zipPath);
//                        if (!file1.exists()) {
//                            file1.mkdirs();
//                        }
//                        fileOutputStream = new FileOutputStream(file1.getPath()+ File.separator + now + ".zip");
//                        zipOutputStream = new ZipOutputStream(fileOutputStream);
//                        ZipEntry zipEntry = new ZipEntry(photoFile.getName());
//                        zipOutputStream.putNextEntry(zipEntry);
//                        zipOutputStream.write(data);
//                    }
//                }
//                data = null;
//            }
//            zipOutputStream.flush();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (zipOutputStream != null) {
//                try {
//                    zipOutputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (fileOutputStream != null) {
//                try {
//                    fileOutputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (fileInputStream != null) {
//                try {
//                    fileInputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
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
     * 压缩成ZIP 方法1
     *
     * @param srcDir           压缩文件夹路径
     * @param out              压缩文件输出流
     * @param KeepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip1(String srcDir, OutputStream out, boolean KeepDirStructure)
            throws RuntimeException {
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            compress(sourceFile, zos, sourceFile.getName(), KeepDirStructure);
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) + " ms");
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

    /**
     * 压缩成ZIP 方法2
     *
     * @param srcFiles 需要压缩的文件列表
     * @param out      压缩文件输出流
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(List<File> srcFiles, OutputStream out) throws RuntimeException {
        long start = System.currentTimeMillis();
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
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) + " ms");
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

    /**
     * 递归压缩方法
     *
     * @param sourceFile       源文件
     * @param zos              zip输出流
     * @param name             压缩后的名称
     * @param KeepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws Exception
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name,
                                 boolean KeepDirStructure) throws Exception {
        byte[] buf = new byte[2 * 1024];
        if (sourceFile.isFile()) {
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if (KeepDirStructure) {
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }
            } else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (KeepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(), KeepDirStructure);
                    } else {
                        compress(file, zos, file.getName(), KeepDirStructure);
                    }
                }
            }
        }
    }
}
