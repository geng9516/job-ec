package con.chin.task;

import con.chin.util.FlogUtil;
import con.chin.util.AddItemInfoUtil;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class ItemProcessor implements PageProcessor {

    @Override
    public void process(Page page) {

        String url = page.getUrl().nodes().get(0).toString();

        Integer flog = FlogUtil.getUri(url);
        List<Selectable> list = new ArrayList<>();
        String company = null;

        //yahoo
        if (flog == 1) {
            //获取页面数据
            list = page.getHtml().css("div.mdSideCategoryMenu ul.elListItems").nodes();
            //会社名
            company = page.getHtml().css("div.mdInformationTable p.elHeaderTitle", "text").toString();
            //商品一览
            if (list.size() > 0 && "会社概要".equals(company)) {
                //商品一览URL
                String itemInfoUrl = page.getHtml().css("div.mdSideCategoryMenu div#sdnv a").nodes().get(0).links().toString();
                //添加到任务
                page.addTargetRequest(itemInfoUrl);
            } else {
                //获取商品一览URL
                List<String> listUrl = page.getHtml().css("div#itmlst div.elImage a").links().all();
                //如果是大于0的话就是商品一览
                if (listUrl.size() > 0) {
                    for (String url1 : listUrl) {
                        //把商品详情页加入任务
                        page.addTargetRequest(url1);
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
//                    find_result = m.group(1);  index-root-3VyZeKiLpbFzvshaPtcspE  index-root-3VyZeKiLpbFzvshaPtcspE
//                }
//                String elReviewValue = html.css(".mdReviewSummary a.elReviewLink span.elReviewCount").nodes().get(0).css("span","text").toString();
//                Integer elReviewCount = Integer.parseInt(find_result);
//                if(elReviewCount > 0 && elReviewCount != null){
                    //yahoo
                    AddItemInfoUtil.saveYahooItemInfo(page);
//                }
                }
            }
            //搜款网
        } else if (flog == 2) {
            //获取商品一览URL
            List<String> listUrl = page.getHtml().css("div.goods-list ul.clearfix div.desc a").links().all();
            //如果是大于0的话就是商品一览
            if (listUrl.size() > 0) {
                for (String url1 : listUrl) {
                    //把商品详情页加入任务
                    page.addTargetRequest(url1);
//                    return;
                }
                //下页
                Html html = page.getHtml();
                List<Selectable> nodes = html.css("div.pagination pagination_all a").nodes();
                //现在页的页码
                String s = url.substring(url.indexOf("="), url.indexOf("=") + 1);
                //判断页码
                if (Integer.parseInt(s) == nodes.size()) {
                    //如果页码数和总页码相同的就是最后一页
                    url = url.substring(0, url.indexOf("=") + 1) + nodes.size();
                } else {
                    //如果不同就下一页
                    url = url.substring(0, url.indexOf("=") + 1) + (Integer.parseInt(s) + 1);
                }
                //添加任务
                page.addTargetRequest(url);
                //商品详情页
            } else {
                //搜款网
                AddItemInfoUtil.saveVVICItemInfo(page);
            }

        } else if (flog == 3) {

            //获取商品一览URL
            List<String> listUrl = page.getHtml().css("div.index-root-DKlVY8VJwC33q3o3t_8eM a").links().all();
            //如果是大于0的话就是商品一览
            if (listUrl.size() > 0) {
                for (String url1 : listUrl) {
                    //把商品详情页加入任务
                    page.addTargetRequest(url1);
//                    return;
                }
//                //下页
//                Html html = page.getHtml();
//                String totolItems = html.css("div.index-tip-RPRxwO5K9IL8IArkh7mGZ span", "text").toString();
//                Integer pageNanber = null;
//                //现在页的页码
//                String url1 = "";
//                Double s = Double.parseDouble(totolItems);
//                Double a = new BigDecimal(s / 80).setScale(0, BigDecimal.ROUND_UP).doubleValue();
//                if (url.contains("page")) {
//                    pageNanber = Integer.parseInt(url.substring(url.indexOf("=") + 1, url.indexOf("=") + 2)) + 1;
//                    if (pageNanber == a.intValue()) {
//                        return;
//                    }
//                    url1 = url.substring(0, url.indexOf("=") + 1) + pageNanber;
//
//                } else {
//                    pageNanber = 2;
//                    url1 = url + "?page=" + pageNanber;
//                }
                //判断页码
//                for (int i = pageNanber; i <= ; i++) {
//                    url1 = url1 + i;
//
//
//                }
//                url1 = url1.substring(0, url.indexOf("=") + 1);
                //添加任务
//                page.addTargetRequest(url1);
                //商品详情页
            } else {
                //搜款网
                AddItemInfoUtil.save17zwdItemInfo(page);
            }
        } else {
            return;
        }
    }


    //设定参数
    private Site site = Site.me()
            .setCharset("utf8")//设置编码
            .setTimeOut(10 * 1000)//设置超时时间
            .setRetrySleepTime(3000)//重试的间隔时间
            .setRetryTimes(3);//设置重试的次数

    @Override
    public Site getSite() {
        return site;
    }


}
