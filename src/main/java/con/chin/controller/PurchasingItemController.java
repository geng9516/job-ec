package con.chin.controller;

import con.chin.pojo.Item;
import con.chin.pojo.PurchasingItem;
import con.chin.service.PurchasingItemService;
import con.chin.util.ItemInfoCsvExportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PurchasingItemController {

    @Autowired
    private PurchasingItemService purchasingItemService;

    @GetMapping("/showPurchasingItem")
    public String showPurchasingItem() {

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
            item.setOption1(purchasingItem.getOption2());
            item.setValue1(purchasingItem.getValue2());
            item.setOption1(purchasingItem.getOption3());
            item.setValue1(purchasingItem.getValue3());
            item.setOption1(purchasingItem.getOption4());
            item.setValue1(purchasingItem.getValue4());
            item.setOption1(purchasingItem.getOption5());
            item.setValue1(purchasingItem.getValue5());
            item.setExplanation(purchasingItem.getExplanation());
            item.setPurchasePrice(purchasingItem.getPurchasePrice1());
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
        return "iteminfo";
    }
}
