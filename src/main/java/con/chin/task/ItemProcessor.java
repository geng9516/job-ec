package con.chin.task;

import con.chin.mapper.ItemMapper;
import con.chin.util.YahooItemInfoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ItemProcessor implements PageProcessor {

    @Autowired
    private ItemMapper itemMapper;

    @Override
    public void process(Page page) {

        //获取页面数据
        List<Selectable> list = page.getHtml().css("div.mdSideCategoryMenu ul.elListItems").nodes();
        //会社名
        String company = page.getHtml().css("div.mdInformationTable p.elHeaderTitle","text").toString();
        //商品一览
        if (list.size() > 0 && "会社概要".equals(company)){
            //商品一览URL
            String itemInfoUrl = page.getHtml().css("div.mdSideCategoryMenu div#sdnv a").nodes().get(0).links().toString();
            //添加到任务
            page.addTargetRequest(itemInfoUrl);
        }else {
            //获取商品一览URL
            List<String> listUrl = page.getHtml().css("div#itmlst div.elImage a").links().all();
            //如果是大于0的话就是商品一览
            if (listUrl.size() > 0) {
                for (String url : listUrl) {
                    //把商品详情页加入任务
                    page.addTargetRequest(url);
//                    return;
                }
                //下页
                String elNext = page.getHtml().css("div#pglist").css("li.elNext").links().toString();
                //添加任务
                page.addTargetRequest(elNext);
                //商品详情页
            } else {
//                Html html = page.getHtml();
//                String ss = html.css(".mdReviewSummary a.elReviewLink span.elReviewCount").nodes().get(0).css("span","text").toString();
//
//                Pattern p = Pattern.compile("[^\\d]+([\\d]+)[^\\d]+.*");
//                Matcher m = p.matcher(ss);
//                boolean result = m.find();
//                String find_result = null;
//                if (result) {
//                    find_result = m.group(1);
//                }
//                String elReviewValue = html.css(".mdReviewSummary a.elReviewLink span.elReviewCount").nodes().get(0).css("span","text").toString();
//                Integer elReviewCount = Integer.parseInt(find_result);
//                if(elReviewCount > 0 && elReviewCount != null){
                    YahooItemInfoUtil.saveItemInfo(page);
//                }
            }
        }
    }

    //设定参数
    private Site site = Site.me()
            .setCharset("utf8")//设置编码
            .setTimeOut(10*1000)//设置超时时间
            .setRetrySleepTime(3000)//重试的间隔时间
            .setRetryTimes(3);//设置重试的次数

    @Override
    public Site getSite() {
        return site;
    }


}
