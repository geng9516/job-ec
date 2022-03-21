package con.chin.util;

import con.chin.pojo.OrderInfo;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class CsvImportUtil {


    //上传文件的路径
    private final static URL PATH = Thread.currentThread().getContextClassLoader().getResource("");


    /**
     * @return File  一般文件类型
     * @Description 上传文件的文件类型
     * @Param multipartFile
     **/
    public static File uploadFile(MultipartFile multipartFile) {
        // 获 取上传 路径
        String path = PATH.getPath() + multipartFile.getOriginalFilename();
        try {
            // 通过将给定的路径名字符串转换为抽象路径名来创建新的 File实例
            File file = new File(path);
            // 此抽象路径名表示的文件或目录是否存在
            if (!file.getParentFile().exists()) {
                // 创建由此抽象路径名命名的目录，包括任何必需但不存在的父目录
                file.getParentFile().mkdirs();
            }
            // 转换为一般file 文件
            multipartFile.transferTo(file);

            return file;
        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }

    }


    /**
     * @return List<List < String>>
     * @Description 读取CSV文件的内容（不含表头）
     * @Param filePath 文件存储路径，colNum 列数
     **/
    public static List<OrderInfo> readCSV(String filePath) {
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        FileInputStream fileInputStream = null;

        try {

            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "Shift-JIS"));

            //  表内容集合，外层 List为行的集合，内层 List为字段集合
            List<OrderInfo> orderInfoList = new ArrayList<>();

            String line = null;
            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                //订单对象
                OrderInfo orderInfo = new OrderInfo();
                String[] orderdata = line.replaceAll("\"", "").split(",");
                for (int i = 0; i < orderdata.length; i++) {
                    switch (i) {
                        case 0:
                            orderInfo.setOrderId(orderdata[i]);
                            break;
                        case 1:
                            orderInfo.setShopNameAndOrderId(orderdata[i]);
                            break;
                        case 2:
                            orderInfo.setOrderTime(orderdata[i]);
                            break;
                        case 3:
                            orderInfo.setShipNameKana(orderdata[i]);
                            break;
                        case 4:
                            orderInfo.setShipName(orderdata[i]);
                            break;
                        case 5:
                            orderInfo.setShipZipCode(orderdata[i]);
                            break;
                        case 6:
                            orderInfo.setShipAddressFull(orderdata[i]);
                            break;
                        case 7:
                            orderInfo.setShipPrefecture(orderdata[i]);
                            break;
                        case 8:
                            orderInfo.setShipCity(orderdata[i]);
                            break;
                        case 9:
                            orderInfo.setShipAddress1(orderdata[i]);
                            break;
                        case 10:
                            orderInfo.setShipAddress2(orderdata[i]);
                            break;
                        case 11:
                            orderInfo.setShipPhoneNumber(orderdata[i]);
                            break;
                        case 12:
                            orderInfo.setShipMethodName(orderdata[i]);
                            break;
                        case 13:
                            orderInfo.setShipCompanyCode(orderdata[i]);
                            break;
                        case 14:
                            orderInfo.setShipInvoiceNumber1(orderdata[i]);
                            break;
                        case 15:
                            orderInfo.setShipInvoiceNumber2(orderdata[i]);
                            break;
                        case 16:
                            orderInfo.setShipDate(orderdata[i]);
                            break;
                        case 17:
                            orderInfo.setBillMailAddress(orderdata[i]);
                            break;
                        case 18:
                            orderInfo.setShipCharge(Integer.parseInt(orderdata[i]));
                            break;
                        case 19:
                            orderInfo.setTotal(Integer.parseInt(orderdata[i]));
                            break;
                        case 20:
                            orderInfo.setReferer(orderdata[i]);
                            break;
                        case 21:
                            orderInfo.setPayMethodName(orderdata[i]);
                            break;
                        case 22:
                            orderInfo.setCombinedPayMethodName(orderdata[i]);
                            break;
                        case 23:
                            orderInfo.setShipStatus(orderdata[i]);
                            break;
                        case 24:
                            orderInfo.setPayStatus(orderdata[i]);
                            break;
                        case 25:
                            orderInfo.setDeviceType(orderdata[i]);
                            break;
                    }
                }
                orderInfoList.add(orderInfo);

            }


            return orderInfoList;
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            //关闭流
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
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
        return null;

    }


}
