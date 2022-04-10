package con.chin.controller;

import con.chin.pojo.Item;
import con.chin.pojo.OrderInfo;
import con.chin.pojo.OrderItemInfo;
import con.chin.service.impl.FileImportServiceImpl;
import con.chin.util.CsvImportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.util.List;

@Controller
public class CsvFileImportController {

    @Autowired
    FileImportServiceImpl fileImportService;

    //订单信息csv上传
    @PostMapping("/orderCsvImport")
    public String orderInfoCsvImport(@RequestParam("import-ordercsv") MultipartFile file, RedirectAttributes redirectAttributes) {

        //上传文件的路径
        File csvFile = CsvImportUtil.uploadFile(file);
        //上传成功次数
        int saveSuccessCount = 0;
        //更新成功次数
        int updateSuccessCount = 0;
        //上传文件是orderdate.csv
        if ("orderdate.csv".equals(file.getOriginalFilename())) {
            //传入path
            List<OrderInfo> orderInfoList = CsvImportUtil.readOrderInfoCSV(csvFile.getPath());

            int count = 0;
            for (OrderInfo orderInfo : orderInfoList) {
                count = fileImportService.savaOrderInfo(orderInfo);
                if (count > 0) {
                    saveSuccessCount += count;
                } else {
                    updateSuccessCount += count;
                }
            }
        }
        //上传文件是itemdate.csv
        if ("itemdate.csv".equals(file.getOriginalFilename())) {
            //传入path
            List<OrderItemInfo> orderItemInfoList = CsvImportUtil.readOrderItemInfoCSV(csvFile.getPath());

            int count = 0;
            for (OrderItemInfo orderItemInfo : orderItemInfoList) {
                count = fileImportService.savaOrderItemInfo(orderItemInfo);
                if (count > 0) {
                    saveSuccessCount += count;
                } else {
                    updateSuccessCount += count;
                }
            }
        }
        //前端传消息
        redirectAttributes.addFlashAttribute("message", saveSuccessCount + "件データアップロード," + Math.abs(updateSuccessCount) + "件アップデート成功しました。");
        //刷新order数据界面
        return "redirect:/orderinfo";
    }

    //产品信息csv上传
    @PostMapping("/itemCsvImport")
    public String itemInfoCsvImport(@RequestParam("import-iteminfocsv") MultipartFile file,
                                    RedirectAttributes redirectAttributes
    ) {

        //上传文件的路径
        File csvFile = CsvImportUtil.uploadFile(file);
        //上传成功次数
        int saveSuccessCount = 0;
        //更新成功次数
        int updateSuccessCount = 0;
        //上传文件是orderdate.csv
//        if ("orderdate.csv".equals(file.getOriginalFilename())) {
            //传入path
            List<Item> itemList = CsvImportUtil.readItemInfoCSV(csvFile.getPath());

            int count = 0;
            for (Item item : itemList) {
                count = fileImportService.savaItem(item);
                if (count > 0) {
                    saveSuccessCount += count;
                } else {
                    updateSuccessCount += count;
                }
            }
//        }

        //前端传消息
        redirectAttributes.addFlashAttribute("message", saveSuccessCount + "件データアップロード," + Math.abs(updateSuccessCount) + "件アップデート成功しました。");
        //刷新order数据界面
        return "redirect:/iteminfo";
    }


}




























