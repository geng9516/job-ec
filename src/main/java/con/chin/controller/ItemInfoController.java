package con.chin.controller;

import com.github.pagehelper.PageInfo;
import con.chin.pojo.Config;
import con.chin.pojo.Item;
import con.chin.pojo.SiteShop;
import con.chin.pojo.query.ItemQuery;
import con.chin.service.ConfigService;
import con.chin.service.ItemService;
import con.chin.service.SiteShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ItemInfoController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private SiteShopService siteShopService;

    @Autowired
    private ConfigService configService;

    //产品一览
    @GetMapping("/iteminfo")
    public String iteminfo(Model model, ItemQuery itemQuery, HttpSession httpSession) {
        //把session中的siteshop值删除
        httpSession.removeAttribute("siteShop");
        //取得送料设定值
        List<Config> configList = configService.findDeliveryConfig();
        model.addAttribute("configList", configList);
        //siteshop一覧
        List<SiteShop> siteShopList = siteShopService.findSiteShop(new SiteShop());
        model.addAttribute("siteShopList", siteShopList);
        PageInfo<Item> itemList = itemService.findItemBySearchConditions(itemQuery);
        model.addAttribute("page", itemList);
        return "iteminfo";
    }

    //模糊查询
    @PostMapping("/iteminfo")
    public String findIteminfo(Model model, ItemQuery itemQuery, HttpSession httpSession) {
        //没有输入查询条件时把session中的siteshop值删掉
        if (itemQuery.getSearchConditions() == null || itemQuery.getSearchConditions() == "") {
            httpSession.removeAttribute("siteShop");
        }

        String siteShop = (String) httpSession.getAttribute("siteShop");
        itemQuery.setShopName(siteShop);
        //一页显示数设定 判断是否有pageSize
//        if(httpSession.getAttribute("pageSize") != "" || httpSession.getAttribute("pageSize") != null){
//            Integer pageSize = Integer.parseInt(httpSession.getAttribute("pageSize").toString());
//            //20:初始值必须设定,没有的话报错
//            itemQuery.setPageSize(pageSize);
//        }
        //取得送料设定值
        List<Config> configList = configService.findDeliveryConfig();
        model.addAttribute("configList", configList);
        //siteshop一覧
        List<SiteShop> siteShopList = siteShopService.findSiteShop(new SiteShop());
        model.addAttribute("siteShopList", siteShopList);
        PageInfo<Item> itemList = itemService.findItemBySearchConditions(itemQuery);
        model.addAttribute("page", itemList);
        return "iteminfo";
    }

    //店铺区分查询
    @GetMapping("/findIteminfoBySiteShop")
    public String findIteminfoBySiteShop(Model model, ItemQuery itemQuery, HttpSession httpSession) {
        httpSession.setAttribute("siteShop", itemQuery.getSearchConditions());
//        Integer pageSize = (Integer) httpSession.getAttribute("pageSize");
//        itemQuery.setPageSize(pageSize);
        //siteshop一覧
        List<SiteShop> siteShopList = siteShopService.findSiteShop(new SiteShop());
        model.addAttribute("siteShopList", siteShopList);
        PageInfo<Item> itemList = itemService.findItemBySiteShop(itemQuery);
        model.addAttribute("page", itemList);
        return "iteminfo";
    }

    //一括操作
    @PostMapping("/bulkOperation")
    public String bulkOperation(@RequestParam("checkFlog") Integer checkFlog, RedirectAttributes redirectAttributes) {

        //操作方式判断
        switch (checkFlog) {
            //编辑
            case 0:
                System.out.println("待完成");
                break;
            //删除
            case 1:
                //调用删除方法
//                itemService.deleteItems(itemCodes);
                break;
            //csv下载
            case 3:

                break;
        }
        //
        redirectAttributes.addAttribute("aaa", "123456");
        return "iteminfo";
    }

    //削除
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("itemCode") String itemCode, RedirectAttributes redirectAttributes) {
        Item item = new Item();
        item.setItemCode(itemCode);
        int res = itemService.deleteItem(item);
        if (res == 1) {
            redirectAttributes.addFlashAttribute("message", "削除しました。");
        } else {
            redirectAttributes.addFlashAttribute("message", "削除できなかった。");
        }
        return "redirect:/iteminfo";
    }

    //修改值
    @PostMapping("/setItemInfo")
    public String setItemInfo(
            @RequestParam("salePrice") String salePrice,
            @RequestParam("delivery") Long deliveryId,
            @RequestParam("purchase-price") String purchasePrice,
            @RequestParam("itemCode") String intemCode,
            @RequestParam("url1") String url1,
            @RequestParam("url2") String url2,
            @RequestParam("url3") String url3,
            RedirectAttributes redirectAttributes) {

        //保存iteminfo更新值
        Item item = new Item();
        item.setItemCode(intemCode);
        int flog = 0;
        if (purchasePrice != "") {
            item.setPurchasePrice(Integer.parseInt(purchasePrice));
            flog++;
        }
        //条件设置
        Config config = new Config();
        //调用查询送料方法
        if (deliveryId != null) {
            //保存结果
            Config resConfig = new Config();
            config.setId(deliveryId);
            resConfig = configService.findDeliveryValue(config);
            item.setDelivery(Integer.parseInt(resConfig.getValue2()));
            flog++;
        }
        if (salePrice != "") {
            item.setSalePrice(Integer.parseInt(salePrice));
            flog++;
        }
        if (url1 != "") {
            item.setUrl1(url1);
            flog++;
        }
        if (url2 != "") {
            item.setUrl2(url2);
            flog++;
        }
        if (url3 != "") {
            item.setUrl3(url3);
            flog++;
        }
        int res = 0;
        if(flog > 0){
            res = itemService.setItemSalePrice(item);
        }

        if (res == 1) {
            redirectAttributes.addFlashAttribute("message", "アイテム情報更新できました。");
        }
        return "redirect:/iteminfo";
    }

    //一页显示数设定
    @PostMapping("/setPageSize")
    public String setPageNum(Model model, HttpSession httpSession, @RequestParam("pageSize") String pageSize) {
        httpSession.setAttribute("pageSize", pageSize);
        //siteshop一覧
        List<SiteShop> siteShopList = siteShopService.findSiteShop(new SiteShop());
        model.addAttribute("siteShopList", siteShopList);
        //一页显示数设定
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.setPageSize(Integer.parseInt(pageSize));
        PageInfo<Item> itemList = itemService.findItemBySearchConditions(itemQuery);
        model.addAttribute("page", itemList);
        return "iteminfo";
    }

}


















