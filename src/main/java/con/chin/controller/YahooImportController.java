package con.chin.controller;

import jp.co.yahoo.yconnect.YConnectExplicit;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class YahooImportController {

    @GetMapping("/showYahoo")
    public String show(){

        return "yahooimport";
    }




}
