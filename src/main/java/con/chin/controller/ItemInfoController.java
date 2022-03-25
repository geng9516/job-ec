package con.chin.controller;

import com.github.pagehelper.PageInfo;
import con.chin.pojo.Item;
import con.chin.pojo.SiteShop;
import con.chin.pojo.query.ItemQuery;
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

    //产品一览
    @GetMapping("/iteminfo")
    public String iteminfo(Model model, ItemQuery itemQuery, HttpSession httpSession) {
        httpSession.removeAttribute("siteShop");
        //siteshop一覧
        List<SiteShop> siteShopList = siteShopService.findSiteShop(new SiteShop());
        model.addAttribute("siteShopList", siteShopList);
        PageInfo<Item> itemList = itemService.findItemBySearchConditions(itemQuery);
        model.addAttribute("page", itemList);
        return "iteminfo";
    }

    //查询
    @PostMapping("/iteminfo")
    public String findIteminfo(Model model, ItemQuery itemQuery, HttpSession httpSession) {
        System.out.println(itemQuery.getSearchConditions());
        if (itemQuery.getSearchConditions() == null || itemQuery.getSearchConditions() == "") {
            httpSession.removeAttribute("siteShop");
        }
        String siteShop = (String) httpSession.getAttribute("siteShop");
        itemQuery.setShopName(siteShop);
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
        //siteshop一覧
        List<SiteShop> siteShopList = siteShopService.findSiteShop(new SiteShop());
        model.addAttribute("siteShopList", siteShopList);
        PageInfo<Item> itemList = itemService.findItemBySiteShop(itemQuery);
        model.addAttribute("page", itemList);
        return "iteminfo";
    }

    //一括操作
    @PostMapping("/bulkOperation")
    public String bulkOperation(@RequestParam("listString[]") List<String> strings, RedirectAttributes redirectAttributes) {

        for (String string : strings) {
            System.out.println(string);

        }
        System.out.println("@@@@@111111");

        redirectAttributes.addAttribute("aaa", "123456");
        return "redirect:/iteminfo";
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

    @PostMapping("/setPrice")
    public String setPrice(Model model,
                           @RequestParam("salePrice") String salePrice,
                           @RequestParam("delivery") String delivery,
                           @RequestParam("purchase-price") String purchasePrice,
                           @RequestParam("itemCode") String intemCode,
                           @RequestParam("url1") String url1,
                           @RequestParam("url2") String url2,
                           @RequestParam("url3") String url3) {

        Item item = new Item();
        item.setItemCode(intemCode);
        item.setPurchasePrice(purchasePrice == "" ? null : Integer.parseInt(purchasePrice));
        item.setDelivery(delivery == "" ? null : Integer.parseInt(delivery));
        item.setSalePrice(salePrice == "" ? null : Integer.parseInt(salePrice));
        item.setUrl1(url1 == "" ? "" : url1);
        item.setUrl1(url2 == "" ? "" : url2);
        item.setUrl1(url3 == "" ? "" : url3);

        itemService.setItemSalePrice(item);

        return "redirect:/iteminfo";
    }

}


















