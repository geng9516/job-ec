package con.chin.controller;


import con.chin.service.SiteShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class SiteShopController {

    @Autowired
    private SiteShopService siteShopService;

    public String getAllSiteShop(Model model){return  null;}

}
