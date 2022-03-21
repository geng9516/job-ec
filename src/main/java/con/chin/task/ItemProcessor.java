package con.chin.task;

import con.chin.util.YahooItemInfoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

@Component
public class ItemProcessor implements PageProcessor {

    @Override
    public void process(Page page) {

        //获取页面数据
        List<Selectable> list = page.getHtml().css("div.mdSideCategoryMenu").nodes();

        if (list.size() > 0){
            //商品一览URL
            String itemInfoUrl = page.getHtml().css("div.mdSideCategoryMenu div#sdnv a").nodes().get(0).links().toString();
            //添加到任务
            page.addTargetRequest(itemInfoUrl);
        }else {
            //获取商品一览URL
            List<String> listUrl = page.getHtml().css("div#itmlst").links().all();
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
                YahooItemInfoUtil.saveItemInfo(page);
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
