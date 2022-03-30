package con.chin.controller;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import con.chin.pojo.Config;
import con.chin.pojo.Item;
import con.chin.pojo.SiteShop;
import con.chin.pojo.query.ItemQuery;
import con.chin.service.ConfigService;
import con.chin.service.ItemService;
import con.chin.service.SiteShopService;
import con.chin.util.CopyItemPhoto;
import con.chin.util.PutItemInfoUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
    public String iteminfo(Model model, @Param("id") String id, ItemQuery itemQuery, HttpSession httpSession) {

        //全商品表示
        if(id != null && id != ""){
            httpSession.removeAttribute("siteShop");
            httpSession.removeAttribute("searchConditions");
            httpSession.removeAttribute("pageSize");
            httpSession.removeAttribute("flog");
        }
        //在店铺查询下在点击下一页时
        String siteShop = (String) httpSession.getAttribute("siteShop");
        itemQuery.setShopName(siteShop);
        //如果是店铺查询后的模糊查询,并且分页
        String searchConditions =  (String) httpSession.getAttribute("searchConditions");
        itemQuery.setSearchConditions(searchConditions);
        //如果表示页数有修改的话,进行设定
        String pageSize = (String) httpSession.getAttribute("pageSize");
        if(pageSize != null && !"".equals(pageSize)){
            itemQuery.setPageSize(Integer.parseInt(pageSize));
        }
        //编辑状态
        String flog = (String) httpSession.getAttribute("flog");
        if(flog != null && !"".equals(flog)){
            itemQuery.setFlog(Integer.parseInt(flog));
        }
        httpSession.setAttribute("flog",String.valueOf(0));
        itemQuery.setFlog(0);
        //取得送料设定值
        List<Config> configList = configService.findDeliveryConfig();
        model.addAttribute("configList", configList);
        //siteshop一覧
        List<SiteShop> siteShopList = siteShopService.findAllSiteShop(new SiteShop());
        model.addAttribute("siteShopList", siteShopList);
        PageInfo<Item> itemList = itemService.findItemBySearchConditions(itemQuery);
        model.addAttribute("page", itemList);
        return "iteminfo";
    }

    //模糊查询
    @PostMapping("/iteminfo")
    public String findIteminfo(Model model, ItemQuery itemQuery, HttpSession httpSession) {

        //没有输入查询条件时把session中的值删掉
        if (itemQuery.getSearchConditions() == null || itemQuery.getSearchConditions() == "") {
            httpSession.removeAttribute("siteShop");
            httpSession.removeAttribute("pageSize");
        }
        //为了条件查询后的分页
        httpSession.setAttribute("searchConditions",itemQuery.getSearchConditions());
        //如果是店铺查询后的模糊查询,并且分页
        String siteShop = (String) httpSession.getAttribute("siteShop");
        itemQuery.setShopName(siteShop);
        //如果表示页数有修改的话,进行设定
        String pageSize = (String) httpSession.getAttribute("pageSize");
        if(pageSize != null && !"".equals(pageSize)){
            itemQuery.setPageSize(Integer.parseInt(pageSize));
        }
        //编辑状态
        String flog = (String) httpSession.getAttribute("flog");
        if(flog != null && !"".equals(flog)){
            itemQuery.setFlog(Integer.parseInt(flog));
        }
        //取得送料设定值
        List<Config> configList = configService.findDeliveryConfig();
        model.addAttribute("configList", configList);
        //siteshop一覧
        List<SiteShop> siteShopList = siteShopService.findAllSiteShop(new SiteShop());
        model.addAttribute("siteShopList", siteShopList);
        PageInfo<Item> itemList = itemService.findItemBySearchConditions(itemQuery);
        model.addAttribute("page", itemList);
        return "iteminfo";
    }

    //店铺区分查询
    @GetMapping("/findIteminfoBySiteShop")
    public String findIteminfoBySiteShop(Model model, ItemQuery itemQuery, HttpSession httpSession) {

        //把siteShop值放到全局变量中
        httpSession.setAttribute("siteShop", itemQuery.getSearchConditions());
        //如果表示页数有修改的话,进行设定
        String pageSize = (String) httpSession.getAttribute("pageSize");
        if(pageSize != null && !"".equals(pageSize)){
            itemQuery.setPageSize(Integer.parseInt(pageSize));
        }
        //编辑状态
        String flog = (String) httpSession.getAttribute("flog");
        if(flog != null && !"".equals(flog)){
            itemQuery.setFlog(Integer.parseInt(flog));
        }
        //siteshop一覧
        List<SiteShop> siteShopList = siteShopService.findAllSiteShop(new SiteShop());
        model.addAttribute("siteShopList", siteShopList);
        PageInfo<Item> itemList = itemService.findItemBySiteShop(itemQuery);
        model.addAttribute("page", itemList);
        return "iteminfo";
    }

    //一括操作
    @ResponseBody
    @PostMapping("/bulkOperation")
    public String bulkOperation(@RequestParam("listString[]") List<String> itemCodeList) {

        //检索下载iteminfo
        List<Item> itemList = itemService.findItemByItemCodes(itemCodeList);
        //导出CSV文件
        PutItemInfoUtil.putItemInfoToCsv(itemList, null);
        //产品照片拷贝
        System.out.println("照片拷贝执行开始");
        CopyItemPhoto.read(itemCodeList);
        System.out.println("照片拷贝执行结束");
        Gson gson = new Gson();
        return gson.toJson("アイテムCSV情報出力完了しました。");
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
        if (flog > 0) {
            res = itemService.setItemSalePrice(item);
        }

        if (res == 1) {
            redirectAttributes.addFlashAttribute("message", "アイテム情報更新できました。");
        }
        return "redirect:/iteminfo";
    }

    //一页显示数设定
    @PostMapping("/setPageSize")
    public String setPageNum(Model model, HttpSession httpSession,  ItemQuery itemQuery) {

        //如果siteShop不为空的话的设定查询条件
        String siteShop = (String) httpSession.getAttribute("siteShop");
        if(siteShop != null && !"".equals(siteShop)){
            itemQuery.setShopName(siteShop);
        }
        //如果searchConditions不为空的话的设定查询条件
        String searchConditions = (String) httpSession.getAttribute("searchConditions");
        if(searchConditions != null && !"".equals(searchConditions)){
            itemQuery.setSearchConditions(searchConditions);
        }
        //编辑状态
        String flog = (String) httpSession.getAttribute("flog");
        if(flog != null && !"".equals(flog)){
            itemQuery.setFlog(Integer.parseInt(flog));
        }
        //一页表示数发到全局变量中
//        if(itemQuery.getPageSize())
        httpSession.setAttribute("pageSize", String.valueOf(itemQuery.getPageSize()));
        //siteshop一覧
        List<SiteShop> siteShopList = siteShopService.findAllSiteShop(new SiteShop());
        model.addAttribute("siteShopList", siteShopList);
        //一页显示数设定
        PageInfo<Item> itemList = itemService.findItemBySearchConditions(itemQuery);
        model.addAttribute("page", itemList);
        return "iteminfo";
    }

    //编辑与否设定
    @PostMapping("/findEditedIteminfo")
    public String findEditedIteminfo(Model model, HttpSession httpSession,  ItemQuery itemQuery) {

        //如果siteShop不为空的话的设定查询条件
        String siteShop = (String) httpSession.getAttribute("siteShop");
        if(siteShop != null && !"".equals(siteShop)){
            itemQuery.setShopName(siteShop);
        }
        //如果searchConditions不为空的话的设定查询条件
        String searchConditions = (String) httpSession.getAttribute("searchConditions");
        if(searchConditions != null && !"".equals(searchConditions)){
            itemQuery.setSearchConditions(searchConditions);
        }
        //如果表示页数有修改的话,进行设定
        String pageSize = (String) httpSession.getAttribute("pageSize");
        if(pageSize != null && !"".equals(pageSize)){
            itemQuery.setPageSize(Integer.parseInt(pageSize));
        }
        //编辑状态放到全局变量中
        httpSession.setAttribute("flog", String.valueOf(itemQuery.getFlog()));
        //siteshop一覧
        List<SiteShop> siteShopList = siteShopService.findAllSiteShop(new SiteShop());
        model.addAttribute("siteShopList", siteShopList);
        //一页显示数设定
        PageInfo<Item> itemList = itemService.findItemBySearchConditions(itemQuery);
        model.addAttribute("page", itemList);
        return "iteminfo";
    }
}


















