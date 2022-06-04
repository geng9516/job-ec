package con.chin.task;

import con.chin.mapper.SiteShopMapper;
import con.chin.pojo.Item;
import con.chin.pojo.ItemKeyword;
import con.chin.pojo.PurchasingItem;
import con.chin.pojo.SiteShop;
import con.chin.service.ItemKeywordService;
import con.chin.service.ItemService;
import con.chin.service.PurchasingItemService;
import con.chin.service.SiteShopService;
import con.chin.util.PhotoDownloadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

//产品数据导出方式改为数据库
@Component
@Transactional
public class ItemPipeline implements Pipeline {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemKeywordService itemKeywordService;

    @Autowired
    private SiteShopService siteShopService;

    @Autowired
    private PurchasingItemService purchasingItemService;

    private static String FILENAME;

    @Value("${FILENAME}")
    public void setFileName(String FILENAME) {
        this.FILENAME = FILENAME;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        //properties文件的名字取得
        ResourceBundle bundle = ResourceBundle.getBundle(FILENAME);
        //照片下载地址取得
        String itemphoto = bundle.getString("ITEMPHOTO");
        String itemphoto2 = bundle.getString("ITEMPHOTO2");

        //开始时间
        long start = System.currentTimeMillis();
        int res = -1;
        //resultItems 页面保存对象
        //iteminfo保存数据库
        Item item = resultItems.get("item");
        if (item != null) {
            res = itemService.saveItem(item);
        }
        //关键词保存数据库
        List<ItemKeyword> itemKeywordList = resultItems.get("itemKeywordList");
        if (item.getItemPath() != null) {
            itemKeywordService.save(itemKeywordList);
        }
        //照片下载本地
        Map<String, Object> map = resultItems.get("photoDownload");
        if (map != null && res != -1) {
            PhotoDownloadUtil.download((List) map.get("photoAll"), (String) map.get("itemCode"), itemphoto);
            long end = System.currentTimeMillis();
            System.out.println("itemcode为:  " + item.getItemCode() + " 的产品照片下载完成 :    " + "耗时：" + (end - start) + " ms");
        }
        //siteshopinfo保存数据库
        SiteShop siteShop = resultItems.get("siteShop");
        if (siteShop != null) {
            siteShopService.saveSiteShopInfo(siteShop);
        }

        //PurchasingItem保存数据库
        PurchasingItem purchasingItem = resultItems.get("purchasingItem");
        if (purchasingItem != null) {
            res = purchasingItemService.savePurchasingItem(purchasingItem);
        }

        //照片下载本地
        Map<String, Object> map2 = resultItems.get("purchasingItemhotPDownload");
        if (map2 != null && res != -1) {
            PhotoDownloadUtil.download((List) map2.get("photoAll"), (String) map2.get("itemCode"), itemphoto2);
            long end = System.currentTimeMillis();
            System.out.println("itemcode为:  " + map2.get("itemCode") + " 的产品照片下载完成 :    " + "耗时：" + (end - start) + " ms");
        }


    }
}
