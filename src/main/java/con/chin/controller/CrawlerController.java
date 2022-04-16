package con.chin.controller;

import con.chin.pojo.Item;
import con.chin.service.ItemService;
import con.chin.task.ItemPipeline;
import con.chin.task.ItemProcessor;
import con.chin.util.CopyItemPhotoUtil;
import con.chin.util.CsvImportUtil;
import con.chin.util.ItemPhotoToZipUtil;
import con.chin.util.ExportItemInfoCsvUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static con.chin.util.CopyItemPhotoUtil.copyItemPhoto;

@Controller
public class CrawlerController {
    @Autowired
    private ItemPipeline itemPipeline;

    @Autowired
    ItemService itemService;

    @GetMapping("/")
    public String index(Model model, HttpSession httpSession) {
        //把查询条件清空
        httpSession.removeAttribute("siteShop");
        httpSession.removeAttribute("searchConditions");
        return "index";
    }

    //爬取url
    @GetMapping("/getUrl")
    public String index(@RequestParam(name = "url") String url, RedirectAttributes redirectAttributes) {

        String[] urls = url.split(System.lineSeparator());
        List<String> stringList = new ArrayList<>();
        //把按照换行进行分割
        Matcher m = Pattern.compile("(?m)^.*$").matcher(url);
        while (m.find()) {
            stringList.add(m.group());
        }
        //没有输入url时返回提示
        if (urls.length == 1 && urls[0] == "") {
            redirectAttributes.addFlashAttribute("message", "****请输入爬取页url****");
            return "redirect:/";
            //输入url1条时
        } else if (urls.length == 1) {
            Spider.create(new ItemProcessor())
                    .addUrl(url)
                    .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(100000)))
                    //数据输出设定(可添加多个)
                    .addPipeline(this.itemPipeline)
                    .run();
            //输入多条url时
        } else {
            for (String s : stringList) {
                Spider.create(new ItemProcessor())
                        .addUrl(s)
                        .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(100000)))
                        //数据输出设定(可添加多个)
                        .addPipeline(this.itemPipeline)
//                        .thread(2)
                        .run();
            }
        }
        //成功信息
        redirectAttributes.addFlashAttribute("message", "全部抓取完成");
        System.out.println("全部抓取完成");
        //刷新主页
        return "redirect:/";
    }

    @Value("${ITEMCSVPATH}")
    private String itemCsvPath;

    //根据产品ID下载csv 和删除
    @PostMapping("/putItemInfo")
    public String putItemInfo(RedirectAttributes redirectAttributes,
                              @RequestParam("itemCodes") String itemCodes,
                              @RequestParam("frequency") String frequency

    ) {
        List<String> stringList = new ArrayList<>();
        //把按照换行进行分割
        if(itemCodes != null && itemCodes != ""){
            Matcher m = Pattern.compile("(?m)^.*$").matcher(itemCodes);
            while (m.find()) {
                stringList.add(m.group());
            }
        }
        //下载产品资料
        if("0".equals(frequency)){
            List<Item> itemList = new ArrayList<>();
            //取得所有已编辑并且没有失效的产品
            if(itemCodes != null && itemCodes != ""){
                itemList = itemService.findItemByItemCodeAll(stringList);
            }else {
                itemList = itemService.findAll();
            }
            //item信息不为空时
            if (itemList != null) {
                //调用下载方法
                ExportItemInfoCsvUtil.exportYahooItemInfoToCsv(itemList, itemCsvPath);
                //完成输出信息
                redirectAttributes.addFlashAttribute("message", "数据出完成");
            } else {
                //失败信息
                redirectAttributes.addFlashAttribute("message", "***没有数据可输出***");
            }
            //删除产品资料
        }else if ("1".equals(frequency)){
            itemService.deleteItems(stringList);
        }else if("2".equals(frequency)){
            List<Item> itemList = new ArrayList<>();
            itemList = itemService.findItemByItemCodeAll(stringList);
            //产品照片拷贝
            System.out.println("照片拷贝执行开始");
            CopyItemPhotoUtil.read(itemList);
            System.out.println("照片拷贝执行结束");
        }

        //刷新主页
        return "redirect:/";
    }

    //创建ZIP文件
    @GetMapping("/photoToZip")
    public String photoToZip() {
        //调用创建ZIP文件方法
        ItemPhotoToZipUtil.fileZipSave();
        return "index";
    }

    //把制作好的照片的产品ID取得
    @PostMapping("/filePath")
    public String filePath() {
        //上传文件的路径
        File csvFile = new File("/Users/geng9516/Documents/商品画像編集/200_実行後");
        //判断itemCode的文件价存在

        //把文件路径的文件价抽象化

        if (csvFile.exists()) {
            File[] files = csvFile.listFiles();
            if (files.length == 0) {
//                System.out.println("ファイルが存在しません。");
            } else {
                //文件夹下存在文件时
                for (File file1 : files) {
                    //是一个文件夹
                    if (!file1.isDirectory()) {
                        if(file1.isFile()){
                            String fileName = file1.getName();
                            fileName= fileName.replaceAll(".jpg","");
                            if(fileName.contains("_")){
                                continue;
                            }else {
                                System.out.println(fileName);
                            }
                        }
                    }else {
                        return "index";
                    }
                }
            }
        } else {
            System.out.println("文件路径不存在!");
        }

        return "index";
    }

    //---------------------------------------------------------------------------------------------------------

    //数据错误时做更新使用
    @GetMapping("/setDate")
    public String setDate() {
        itemService.setdate();
        return "index";
    }





}
