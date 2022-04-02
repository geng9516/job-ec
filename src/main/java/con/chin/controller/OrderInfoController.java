package con.chin.controller;

import com.github.pagehelper.PageInfo;
import con.chin.pojo.OrderInfo;
import con.chin.pojo.OrderItemInfo;
import con.chin.pojo.query.OrderInfoQuery;
import con.chin.pojo.result.Order;
import con.chin.service.OrderInfoService;
import con.chin.service.OrderItemInfoService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class OrderInfoController {

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private OrderItemInfoService orderItemInfoService;

    //界面显示
    @GetMapping("/orderinfo")
    public String meru(Model model, OrderInfoQuery orderInfoQuery) {

        PageInfo<Order> orderInfoList = orderInfoService.findAllOrderInfo(orderInfoQuery);
        model.addAttribute("page", orderInfoList);

//        List<Order> list = orderInfoList.getList();

        return "orderinfo";
    }


}
