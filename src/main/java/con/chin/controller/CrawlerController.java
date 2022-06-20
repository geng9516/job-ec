package con.chin.controller;

import con.chin.pojo.Item;
import con.chin.service.ItemService;
import con.chin.task.ItemPipeline;
import con.chin.task.ItemProcessor;
import con.chin.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;

import javax.servlet.http.HttpSession;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    @PostMapping("/getUrl")
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

    //csv保存パス
    @Value("${ITEMCSVPATH}")
    private String itemCsvPath;

    //写真保存パス
    @Value("${ITEMPHOTO}")
    private String itemPhoto;

    //中国語写真保存パス
    @Value("${ITEMPHOTO2}")
    private String itemPhoto2;

    //根据产品ID下载csv 和删除
    @PostMapping("/putItemInfo")
    public String putItemInfo(RedirectAttributes redirectAttributes,
                              @RequestParam("itemCodes") String itemCodes,
                              @RequestParam("frequency") String frequency,
                              HttpSession httpSession) {

        //ecsite
        String ecSite = (String) httpSession.getAttribute("ecSite");
        List<String> stringList = new ArrayList<>();
        //把按照换行进行分割
        if (itemCodes != null || !"".equals(itemCodes)) {
            Matcher m = Pattern.compile("(?m)^.*$").matcher(itemCodes);
            while (m.find()) {
                stringList.add(m.group());
            }
        }
        //下载产品资料
        if ("0".equals(frequency)) {
            List<Item> itemList = new ArrayList<>();
            //取得所有已编辑并且没有失效的产品
            if (itemCodes != null && !"".equals(itemCodes)) {
                itemList = itemService.findItemByItemCodeAll(stringList);
            } else {
                itemList = itemService.findAll();
            }
            //item信息不为空时
            if (itemList.size() > 0) {
                List<Item> itemList1 = new ArrayList<>();
                for (Item item : itemList) {
                    if (item.getFlog() == 0) {
                        item.setFlog(1);
                        itemList1.add(item);
                    }
                }
                //存在值时
                if (itemList1.size() > 0) {
                    itemService.setItemFlog(itemList1);
                }
                //开始时间
                long start = System.currentTimeMillis();
                //ecSite已经在全局变量中存在时
                if (ecSite != null && !"".equals(ecSite)) {
                    if (ecSite.equals("yahoo")) {
                        //导出yahooCSV文件
                        ItemInfoCsvExportUtil.exportYahooItemInfoToCsv(itemList, itemCsvPath, "data_spy");
                        //导出库存CSV文件
                        DataExportUtil.exportItemStockCsv(stringList, itemCsvPath, "quantity");
                        //导出optionCSV文件
                        DataExportUtil.exportItemOptionCsv(itemList, itemCsvPath, "option_add");
                    } else if (ecSite.equals("au")) {
                        //导出auCSV文件
                        ItemInfoCsvExportUtil.exportAuItemInfoToCsv(itemList, itemCsvPath, "item");
                        //导出optionCSV文件
//                        DataExportUtil.exportAuItemOptionCsv(itemList, itemCsvPath, "stock");
                    }
                    //不存在值时默认
                } else {
                    //导出CSV文件
                    ItemInfoCsvExportUtil.exportYahooItemInfoToCsv(itemList, itemCsvPath, "data_spy");
                    //导出库存CSV文件
                    DataExportUtil.exportItemStockCsv(stringList, itemCsvPath, "quantity");
                    //导出optionCSV文件
                    DataExportUtil.exportItemOptionCsv(itemList, itemCsvPath, "option_add");
                }
                long end = System.currentTimeMillis();
                //完成输出信息
                redirectAttributes.addFlashAttribute("message", "アイテム情報が " + itemList.size() + " 件出力されました。");
            } else {
                //失败信息
                redirectAttributes.addFlashAttribute("message", "***入力されたアイテムコード関連のデータがありません***");
            }
            //删除产品资料
        } else if ("1".equals(frequency)) {
            if (itemCodes != null && !"".equals(itemCodes)) {
                //开始时间
                long start = System.currentTimeMillis();
                itemService.deleteItems(stringList);
                long end = System.currentTimeMillis();
                System.out.println("产品删除完成!    总耗时：" + (end - start) + " ms");
                redirectAttributes.addFlashAttribute("message", "アイテム削除完了しました");
            } else {
                redirectAttributes.addFlashAttribute("message", "削除対象アイテムコードを入力してください！");
            }
            //产品照片拷贝
        } else if ("2".equals(frequency)) {

            if (itemCodes != null && !"".equals(itemCodes)) {
                //开始时间
                long start = System.currentTimeMillis();
                //产品照片拷贝
                System.out.println("照片拷贝执行开始");
                ItemPhotoCopyUtil.read(stringList);
                //结束时间
                long end = System.currentTimeMillis();
                System.out.println("照片拷贝完成!    总耗时：" + (end - start) / 1000 + " 秒");
                redirectAttributes.addFlashAttribute("message", stringList.size() + " 件のアイテム写真のダウンロードが完了しました。");
            } else {
                redirectAttributes.addFlashAttribute("message", "ダウンロード対象のアイテムコードを入力してください！");
            }
            //照片删除
        } else if ("3".equals(frequency)) {

            if (itemCodes != null && !"".equals(itemCodes)) {
                //开始时间
                long start = System.currentTimeMillis();
                //产品照片拷贝
                System.out.println("照片删除执行开始");
                ItemPhotoCopyUtil.read3(stringList, itemPhoto);
                System.out.println("照片删除执行结束");
                //结束时间
                long end = System.currentTimeMillis();
                System.out.println("照片删除完成!    总耗时：" + (end - start) / 1000 + " 秒");
                redirectAttributes.addFlashAttribute("message", "アイテム写真の删除が完了しました。");
            } else {
                redirectAttributes.addFlashAttribute("message", "ダウンロード対象のアイテムコードを入力してください！");
            }
            //オプションCSVダウンロード
        } else if ("4".equals(frequency)) {
            //开始时间
            long start = System.currentTimeMillis();
            List<Item> itemList = new ArrayList<>();
            //取得所有已编辑并且没有失效的产品
            if (itemCodes != null && itemCodes != "") {
                itemList = itemService.findItemByItemCodeAll(stringList);
            } else {
                itemList = itemService.findAll();
            }
            //导出optionCSV文件
            DataExportUtil.exportItemOptionCsv(itemList, itemCsvPath, "option_add");
            //结束时间
            long end = System.currentTimeMillis();
            System.out.println("导出optionCSV文件完成!  导出总件数为" + itemList.size() + "   总耗时：" + (end - start) / 1000 + " 秒");
            //在庫CSVダウンロード
        } else if ("5".equals(frequency)) {
            //开始时间
            long start = System.currentTimeMillis();
            List<Item> itemList = new ArrayList<>();
            //取得所有已编辑并且没有失效的产品
            if (itemCodes != null && itemCodes != "") {
                itemList = itemService.findItemByItemCodeAll(stringList);
            } else {
                itemList = itemService.findAll();
            }
            //把itemcode单独取出
            List<String> itemCodeList = new ArrayList<>();
            //把itemcode取出
            for (Item item : itemList) {
                itemCodeList.add(item.getItemCode());
            }
            //导出库存CSV文件
            DataExportUtil.exportItemStockCsv(itemCodeList, itemCsvPath, "quantity");
            //结束时间
            long end = System.currentTimeMillis();
            System.out.println("导出库存CSV文件完成!  导出总件数为" + itemList.size() + "   总耗时：" + (end - start) / 1000 + " 秒");
        }
        //刷新主页
        return "redirect:/";
    }

    //创建ZIP文件
    @GetMapping("/photoToZip")
    public String photoToZip(Model model) {
        //开始时间
        long start = System.currentTimeMillis();
        //调用创建ZIP文件方法
        ItemPhotoToZipUtil.fileZipSave();
        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("创建ZIP文件!    总耗时：" + (end - start) + " ms");
        model.addAttribute("message", "写真を圧縮完了しました。");
        return "index";
    }

    //処理後の写真をフォルダーごとにまとめる
    @GetMapping("/photoToFolde")
    public String photoToFolde(Model model) {
        //开始时间
        long start = System.currentTimeMillis();
        //调用创建写真フォルダー方法
        ItemPhotoCopyUtil.read4();
        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("処理後の写真をフォルダーに!    总耗时：" + (end - start) + " ms");
        model.addAttribute("message", "処理後の写真をフォルダーごとにまとめました");
        return "index";
    }

    @Value("${ZIPPHNTOFLEPATH}")
    private String itemCodeCsvPath;

    //把制作好的照片的产品ID取得
    @PostMapping("/filePath")
    public String filePath(Model model, HttpSession httpSession) {

        List<String> itemCodeList = new ArrayList<>();

        itemCodeList = ItemPhotoCopyUtil.read2();
        //现在时间作为文件名(线程安全的)
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        now = now.replaceAll("-", "").replaceAll(":", "").replace(" ", "");
        System.out.println("写入itemCodeCSV开始");
        DataExportUtil.exportItemCodeCsv(itemCodeList, now);
        System.out.println("写入itemCodeCSV结束");
        return "index";
    }

    //---------------------------------------------------------------------------------------------------------

    @Value("${ITEMPHOTO}")
    private String itemPhotoPath1;

    @Value("${ITEMPHOTO2}")
    private String itemPhotoPath2;

    @Value("${ITEM-IMG}")
    private String itemImg;


    //数据错误时做更新使用
    @GetMapping("/setDate")
    public String setDate() {
        //开始时间
//        long start = System.currentTimeMillis();
//        List<Item> itemList1 = new ArrayList<>();
//        List<Item> itemList = new ArrayList<>();
//
//        itemList = itemService.findAll();
//        System.out.println(itemList.size() + " 件产品加载完成");
//
//        File file = new File(itemPhotoPath1);
//        File[] files = file.listFiles();
//        System.out.println("照片 1 文件夹准备完成");
//        File file1 = new File(itemPhotoPath2);
//        File[] files1 = file1.listFiles();
//        System.out.println("照片 2 文件夹准备完成");
//        File file2 = new File(itemImg);
//
//        ItemPhotoCopyUtil.read5(itemList,files,files1,file2);
//
//
//
//        //结束时间
//        long end = System.currentTimeMillis();
//        System.out.println("更新アイテム!    总耗时：" + (end - start) + " ms");

        File desktopDir = FileSystemView.getFileSystemView() .getDefaultDirectory();
        System.out.println(desktopDir);

        return "index";
    }

}













