package con.chin.controller;

import con.chin.pojo.OrderInfo;
import con.chin.service.impl.FileImportServiceImpl;
import con.chin.util.CsvImportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Controller
public class FileImportController {

    @Autowired
    FileImportServiceImpl fileImportService;


    @PostMapping( "/csvImport")
    public String csvImport(@RequestParam("import-csv") MultipartFile file) {

        // 使用CSV工具类，生成file文件
        File csvFile = CsvImportUtil.uploadFile(file);
        // 将文件内容解析，存入List容器，List<String>为每一行内容的集合，6为CSV文件每行的总列数
        List<OrderInfo> orderInfoList = CsvImportUtil.readCSV(csvFile.getPath());

        for (OrderInfo orderInfo : orderInfoList) {
            fileImportService.savaOrderInfo(orderInfo);
        }


        return "index";

    }


}
