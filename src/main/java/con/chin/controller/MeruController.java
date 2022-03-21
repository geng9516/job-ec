package con.chin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MeruController {

    @GetMapping("/meru")
    public String meru(){

        return "meru";
    }

    @GetMapping("/home")
    public String home(){

        return "home";
    }








}
