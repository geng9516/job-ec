package con.chin.controller;

import con.chin.pojo.Item;
import con.chin.service.ItemService;
import con.chin.task.ItemPipeline;
import con.chin.task.ItemProcessor;
import con.chin.util.ItemPhotoToZipUtil;
import con.chin.util.ExportItemInfoCsvUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
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
    @GetMapping("/getUrl")
    public String index(@RequestParam(name = "url") String url, RedirectAttributes redirectAttributes) {

        String[] urls = url.split(System.lineSeparator());
        List<String> stringList = new ArrayList<>();
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

    //所以产品资料下载csv
    @GetMapping("/putItemInfo")
    public String putItemInfo(RedirectAttributes redirectAttributes) {
        //取得所有已编辑并且没有失效的产品
        List<Item> itemList = itemService.findAllValidItem();
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
        //刷新主页
        return "redirect:/";
    }

    //数据错误时做更新使用
    @GetMapping("/photoToZip")
    public String photoToZip() {

        ItemPhotoToZipUtil.fileZipSave();
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
