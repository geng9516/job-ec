package con.chin.controller;

import com.github.pagehelper.PageInfo;
import con.chin.pojo.Item;
import con.chin.pojo.PurchasingItem;
import con.chin.pojo.query.PurchasingItemQuery;
import con.chin.service.PurchasingItemService;
import con.chin.util.ItemInfoCsvExportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PurchasingItemController {

    @Autowired
    private PurchasingItemService purchasingItemService;

    @GetMapping("/showPurchasingItem")
    public String showPurchasingItem(Model model, PurchasingItemQuery purchasingItemQuery) {
        PageInfo<PurchasingItem> purchasingItemQueryBySearchConditions = purchasingItemService.findPurchasingItemQueryBySearchConditions(purchasingItemQuery);
        model.addAttribute("page",purchasingItemQueryBySearchConditions);
        return "purchasingItemInfo";
    }

    @Value("${CHINESEITEMINFO}")
    private String itemCsvPath;

    @GetMapping("/putPurchasingItemInfo")
    public String putPurchasingItemInfo() {
        List<Item> itemList = new ArrayList<>();
        List<PurchasingItem> purchasingItemByFlog0List = purchasingItemService.findPurchasingItemByFlog0();
        for (PurchasingItem purchasingItem : purchasingItemByFlog0List) {
            Item item = new Item();
            item.setItemCode(purchasingItem.getItemCode());
            item.setItemName(purchasingItem.getItemName());
            item.setOption1(purchasingItem.getOption1());
            item.setValue1(purchasingItem.getValue1());
            item.setOption2(purchasingItem.getOption2());
            item.setValue2(purchasingItem.getValue2());
            item.setOption3(purchasingItem.getOption3());
            item.setValue3(purchasingItem.getValue3());
            item.setOption4(purchasingItem.getOption4());
            item.setValue4(purchasingItem.getValue4());
            item.setOption5(purchasingItem.getOption5());
            item.setValue5(purchasingItem.getValue5());
            item.setExplanation(purchasingItem.getExplanation());
            item.setSalePrice(purchasingItem.getPurchasePrice1());
            item.setHeadline(String.valueOf(purchasingItem.getPurchasePrice2()));
            itemList.add(item);
        }
        //开始时间
        long start = System.currentTimeMillis();
        //导出CSV文件
        ItemInfoCsvExportUtil.exportYahooItemInfoToCsv(itemList, itemCsvPath, "chineseEdit");
        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("中文产品导出!    总耗时：" + (end - start) + " ms");
        return "index";
    }
}
