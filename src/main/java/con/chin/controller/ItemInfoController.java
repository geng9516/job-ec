package con.chin.controller;

import com.github.pagehelper.PageInfo;
import con.chin.pojo.Item;
import con.chin.pojo.query.ItemQuery;
import con.chin.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ItemInfoController {

    @Autowired
    private ItemService itemService;

    //产品一览
    @GetMapping("/iteminfo")
    public String iteminfo(Model model, ItemQuery itemQuery) {
        PageInfo<Item> itemList = itemService.findItemByItemCode(itemQuery);
        model.addAttribute("page", itemList);
        return "iteminfo";
    }


    @PostMapping("/iteminfo")
    public String findIteminfo(Model model, ItemQuery itemQuery) {
        PageInfo<Item> itemList = itemService.findItemByItemCode(itemQuery);
        model.addAttribute("page", itemList);
        return "iteminfo";
    }


}
