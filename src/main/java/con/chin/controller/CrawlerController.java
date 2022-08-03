package con.chin.controller;

import con.chin.pojo.EcSiteShop;
import con.chin.pojo.Item;
import con.chin.pojo.ItemCategory;
import con.chin.service.EcSiteShopAndItemService;
import con.chin.service.EcSiteShopService;
import con.chin.service.ItemCategoryService;
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
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class CrawlerController {
    @Autowired
    private ItemPipeline itemPipeline;

    @Autowired
    ItemService itemService;

    @Autowired
    ItemCategoryService itemCategoryService;

    @Autowired
    private EcSiteShopService ecSiteShopService;

    @GetMapping("/")
    public String index(Model model, HttpSession httpSession) {
        //把查询条件清空
        httpSession.removeAttribute("siteShop");
        httpSession.removeAttribute("searchConditions");
        String ecSite = (String) httpSession.getAttribute("ecSite");
        //全局变量中ecsite为空时设定初始值[yahoo]
        if (ecSite == null || "".equals(ecSite)) {
            httpSession.setAttribute("ecSite", "yahoo");
            model.addAttribute("ecSite", "yahoo");
        }
        //ecsiteshop一览
        List<EcSiteShop> ecSiteShopList = ecSiteShopService.findAllEcSiteShop();
        model.addAttribute("ecSiteShopList", ecSiteShopList);
        model.addAttribute("ecSite", ecSite);
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
//                for (Item item : itemList) {
//                    if (item.getFlog() == 0) {
//                        item.setFlog(1);
//                        itemList1.add(item);
//                    }
//                }
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
//                        DataExportUtil.exportItemStockCsv(stringList, itemCsvPath, "quantity");
//                        //导出optionCSV文件
//                        DataExportUtil.exportItemOptionCsv(itemList, itemCsvPath, "option_add");
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
//                    DataExportUtil.exportItemStockCsv(stringList, itemCsvPath, "quantity");
//                    //导出optionCSV文件
//                    DataExportUtil.exportItemOptionCsv(itemList, itemCsvPath, "option_add");
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
        } else if ("6".equals(frequency)) {
            //开始时间
            long start = System.currentTimeMillis();
            List<Item> itemList = new ArrayList<>();
            List<Item> itemList1 = new ArrayList<>();
            if (itemCodes != null && itemCodes != "") {
                itemList = itemService.findItemByItemCodeAll(stringList);
            } else {
                itemList = itemService.findAll();
            }
            for (Item item : itemList) {
                //更新时间
                String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                //flog为CSVダウンロード待ち时修改
                if (item.getFlog() == 0) {
                    item.setFlog(1);
                    item.setUpdatetime(now);
                    itemList1.add(item);
                }
                if (item.getFlog() == 2) {
                    item.setFlog(1);
                    item.setUpdatetime(now);
                    itemList1.add(item);
                }
            }
            //存在值时
            if (itemList1.size() > 0) {
                itemService.setItemFlogs(itemList1);
            }
            //结束时间
            long end = System.currentTimeMillis();
            System.out.println("更改产品状态为编辑済!  更改总件数为" + itemList.size() + "   总耗时：" + (end - start) / 1000 + " 秒");
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

    @Value("${ZIPPHNTOFLEPATH}")
    private String itemCodeCsvPath;

    @Value("${AUPHPTOFILE}")
    private String auItemCodeCsvPath;

    //処理後の写真をフォルダーごとにまとめる
    @GetMapping("/photoToFolde")
    public String photoToFolde(Model model, HttpSession httpSession) {

        String ecSite = (String) httpSession.getAttribute("ecSite");
        //开始时间
        long start = System.currentTimeMillis();
        //雅虎时
        if (ecSite.equals("yahoo")) {
            //调用创建写真フォルダー方法
            ItemPhotoCopyUtil.read4(itemCodeCsvPath);
            //au时
        } else if (ecSite.equals("au")) {
            //调用创建写真フォルダー方法
            ItemPhotoCopyUtil.read4(auItemCodeCsvPath);
        }
        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("処理後の写真をフォルダーに!    总耗时：" + (end - start) + " ms");
        model.addAttribute("message", "処理後の写真をフォルダーごとにまとめました");
        return "index";
    }

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
    @PostMapping("/setDate")
    public String setDate(@RequestParam("itemCodes") String itemCodes) {
        //开始时间
        long start = System.currentTimeMillis();
        List<String> stringList = new ArrayList<>();
        //把按照换行进行分割
        if (itemCodes != null || !"".equals(itemCodes)) {
            Matcher m = Pattern.compile("(?m)^.*$").matcher(itemCodes);
            while (m.find()) {
                stringList.add(m.group());
            }
        }
        read(stringList);

        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("更新アイテム!    总耗时：" + (end - start) + " ms");


        return "index";
    }

    @Value("${ITEMPHOTOCOPY}")
    private String itemPath;

    //拷贝照片
    public void read(List<String> itemCodeList) {

        File file = new File(itemPath);
        findCopyPhoto(file, itemCodeList);
    }

    //读取photo文件
    private void findCopyPhoto(File itemphotoPath, List<String> itemCodeList) {

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

    @Value("${ITEMPHOTOCOPY1}")
    private String itemPath1;

    //照片拷贝方法
    public void copyItemPhoto(String filePath, String folderName) {

        //创建输入流
        FileInputStream fileInputStream = null;
        //创建输出流
        FileOutputStream fileOutputStream = null;
        //照片拷贝地址
        File file = new File(filePath);
        //拷贝后的地址加文件名
//        File file2 = new File(newPath + File.separator + folderName);
        File file2 = new File(itemPath1);
        //拷贝源中的所以文件
        File[] files = file.listFiles();
        try {
            //循环拷贝源中的所以文件
            for (File file1 : files) {
                if (file1.isFile() && file1.getName().replaceAll(".jpg", "").equals(folderName)) {
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

    @Autowired
    private EcSiteShopAndItemService ecSiteShopAndItemService;

    //ショップアイテム作成
    //数据错误时做更新使用
    @PostMapping("/importItemToEcsiteShops")
    public String importItemToEcsiteShops(@RequestParam("itemCodes") String itemCodes, @RequestParam("ecSiteShop") String ecSiteShop,Model model) {

        //开始时间
        long start = System.currentTimeMillis();
        //ecsiteshop一览
        List<EcSiteShop> ecSiteShopList = ecSiteShopService.findAllEcSiteShop();
        model.addAttribute("ecSiteShopList", ecSiteShopList);
        List<String> stringList = new ArrayList<>();
        if("".equals(itemCodes)){
            //成功信息
            model.addAttribute("message", "アイテムコードを入力してください。");
            return "index";
        }
        //把按照换行进行分割
        if (itemCodes != null || !"".equals(itemCodes)) {
            Matcher m = Pattern.compile("(?m)^.*$").matcher(itemCodes);
            while (m.find()) {
                stringList.add(m.group());
            }
        }

        ecSiteShopAndItemService.importItemToEcsiteShop(stringList, ecSiteShop);

        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("更新アイテム!    总耗时：" + (end - start) + " ms");

        //成功信息
        model.addAttribute("message", "ショップアイテム作成完了。");

        return "index";
    }

}













