package con.chin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PurchasingItemController {

    @GetMapping("/showPurchasingItem")
    public String showPurchasingItem(){
        return "purchasingItemInfo";
    }
}
