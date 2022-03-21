package con.chin.controller;

import con.chin.pojo.Item;
import con.chin.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ItemInfoController {

    @Autowired
    private ItemService itemService;

    //产品一览
    @GetMapping("/iteminfo")
    public String iteminfo(Model model) {
        List<Item> itemList = itemService.findAllItem();
        model.addAttribute("itemList", itemList);
        return "iteminfo";
    }


}
