package con.chin.controller;

import con.chin.pojo.Item;
import con.chin.pojo.ItemCategory;
import con.chin.pojo.OrderInfo;
import con.chin.pojo.OrderItemInfo;
import con.chin.service.ItemCategoryService;
import con.chin.service.ItemService;
import con.chin.service.impl.FileImportServiceImpl;
import con.chin.util.ImportCsvUtil;
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
    private FileImportServiceImpl fileImportService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemCategoryService categoryService;

    //订单信息csv上传
    @PostMapping("/orderCsvImport")
    public String orderInfoCsvImport(@RequestParam("import-ordercsv") MultipartFile file, RedirectAttributes redirectAttributes) {

        //上传文件的路径
        File csvFile = ImportCsvUtil.uploadFile(file);
        //上传成功次数
        int saveSuccessCount = 0;
        //更新成功次数
        int updateSuccessCount = 0;
        //上传文件是orderdate.csv
        if ("orderdate.csv".equals(file.getOriginalFilename())) {
            //传入path
            List<OrderInfo> orderInfoList = ImportCsvUtil.readOrderInfoCSV(csvFile.getPath());

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
            List<OrderItemInfo> orderItemInfoList = ImportCsvUtil.readOrderItemInfoCSV(csvFile.getPath());

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
        File csvFile = ImportCsvUtil.uploadFile(file);
        //上传成功次数
        int saveSuccessCount = 0;
        //更新成功次数
        int updateSuccessCount = 0;

        int counts = 0;

        List<Item> itemList = ImportCsvUtil.readItemInfoCSV(csvFile.getPath());
        //开始时间
        long start2 = System.currentTimeMillis();
        int count = itemService.updateItemByCsv(itemList);
        long end = System.currentTimeMillis();
        if (count > 0) {
            System.out.println("从CSV文件中保存了产品  ID为:       " + counts + "件完成     耗时：" + (end - start2) + " ms");
            redirectAttributes.addFlashAttribute("message", itemList.size() + "件データアップロード," + Math.abs(updateSuccessCount) + "件アップデート成功しました。");
        }
        return "redirect:/iteminfo";
    }


    //productCategory上传
    @PostMapping("/productCategory")
    public String productCategory(@RequestParam("productCategory") MultipartFile file,
                                    RedirectAttributes redirectAttributes
    ) {

        //上传文件的路径
        File csvFile = ImportCsvUtil.uploadFile(file);
        //从csv中读取
        List<ItemCategory> itemCategoryList = ImportCsvUtil.readProductCategoryInfoCSV(csvFile.getPath());

        categoryService.updateItemCategory(itemCategoryList);

        return "redirect:/config";
    }



}




























