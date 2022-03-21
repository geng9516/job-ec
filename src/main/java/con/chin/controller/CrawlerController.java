package con.chin.controller;

import con.chin.pojo.Item;
import con.chin.service.ItemService;
import con.chin.task.ItemPipeline;
import con.chin.task.ItemProcessor;
import con.chin.util.PutItemInfoUtil;
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

import java.util.List;

@Controller
public class CrawlerController {
    @Autowired
    private ItemPipeline itemPipeline;

    @Autowired
    ItemService itemService;

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    //爬取url
    @GetMapping("/getUrl")
    public String index(@RequestParam(name = "url") String url, RedirectAttributes redirectAttributes) {

        String[] urls = url.split("/[\n]/");
        //没有输入url时返回提示
        if (urls == null) {
            redirectAttributes.addFlashAttribute("message", "****请输入爬取页url****");
            return "redirect:/";
            //输入url1条时
        } else if (urls.length > 1) {
            Spider.create(new ItemProcessor())
                    .addUrl(urls[0])
                    .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(100000)))
                    //数据输出设定(可添加多个)
                    .addPipeline(this.itemPipeline)
                    .run();
            //输入多条url时
        } else {
            for (String s : urls) {
                Spider.create(new ItemProcessor())
                        .addUrl(s)
                        .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(100000)))
                        //数据输出设定(可添加多个)
                        .addPipeline(this.itemPipeline)
                        .run();
            }
        }
        //成功信息
        redirectAttributes.addFlashAttribute("message", "成功");
        //刷新主页
        return "redirect:/";
    }

    @Value("${ITEMCSVPATH}")
    private String itemCsvPath;

    //所以产品资料下载csv
    @GetMapping("/putItemInfo")
    public String putItemInfo(RedirectAttributes redirectAttributes) {
        //取得所以的item信息
        List<Item> itemList = itemService.findAllItem();
        //item信息不为空时
        if (itemList != null) {
            //调用下载方法
            PutItemInfoUtil.putItemInfoToCsv(itemList, itemCsvPath);
            //完成输出信息
            redirectAttributes.addFlashAttribute("message", "数据出完成");
        } else {
            //失败信息
            redirectAttributes.addFlashAttribute("message", "***没有数据可输出***");
        }
        //刷新主页
        return "redirect:/";
    }


}
