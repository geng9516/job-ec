package con.chin.controller;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import con.chin.pojo.Config;
import con.chin.pojo.Item;
import con.chin.pojo.SiteShop;
import con.chin.pojo.query.ItemInfoQuery;
import con.chin.service.ConfigService;
import con.chin.service.ItemService;
import con.chin.service.SiteShopService;
import con.chin.util.CopyItemPhotoUtil;
import con.chin.util.ExportItemInfoCsvUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ItemInfoController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private SiteShopService siteShopService;

    @Autowired
    private ConfigService configService;

    //产品一览
    @GetMapping("/iteminfo")
    public String iteminfo(Model model, @Param("id") String id, ItemInfoQuery itemInfoQuery, HttpSession httpSession) {

        //全商品表示
        if (id != null && id != "") {
            httpSession.removeAttribute("siteShop");
            httpSession.removeAttribute("searchConditions");
            httpSession.removeAttribute("pageSize");
            httpSession.removeAttribute("flog");
        }
        //跳转页码的时候把页码放到session中,方便刷新页面时停留在当前页
        if (itemInfoQuery.getPageNum() != null) {
            httpSession.setAttribute("pageNum", String.valueOf(itemInfoQuery.getPageNum()));
        }
        //在店铺查询下在点击下一页时
        String siteShop = (String) httpSession.getAttribute("siteShop");
        itemInfoQuery.setShopName(siteShop);
        //如果是店铺查询后的模糊查询,并且分页
        String searchConditions = (String) httpSession.getAttribute("searchConditions");
        itemInfoQuery.setSearchConditions(searchConditions);
        //如果表示页数有修改的话,进行设定
        String pageSize = (String) httpSession.getAttribute("pageSize");
        if (pageSize != null && !"".equals(pageSize)) {
            itemInfoQuery.setPageSize(Integer.parseInt(pageSize));
        }
        //编辑状态
        String flog = (String) httpSession.getAttribute("flog");
        if (flog != null && !"".equals(flog)) {
            itemInfoQuery.setFlog(Integer.parseInt(flog));
        } else {
            httpSession.setAttribute("flog", String.valueOf(0));
            itemInfoQuery.setFlog(0);
        }
        //取得送料设定值
        List<Config> configList = configService.findDeliveryConfig();
        model.addAttribute("configList", configList);
        //siteshop一覧
        List<SiteShop> siteShopList = siteShopService.findAllSiteShop(new SiteShop());
        model.addAttribute("siteShopList", siteShopList);
        PageInfo<Item> itemList = itemService.findItemBySearchConditions(itemInfoQuery);
        model.addAttribute("page", itemList);
        return "iteminfo";
    }

    //模糊查询
    @PostMapping("/iteminfo")
    public String findIteminfo(Model model, ItemInfoQuery itemInfoQuery, HttpSession httpSession) {

        //没有输入查询条件时把session中的值删掉
        if (itemInfoQuery.getSearchConditions() == null || itemInfoQuery.getSearchConditions() == "") {
            httpSession.removeAttribute("siteShop");
            httpSession.removeAttribute("pageSize");
            httpSession.removeAttribute("flog");
        }
        //为了条件查询后的分页 (有这条会出现编辑状态切换时数据错误)
        httpSession.setAttribute("searchConditions", itemInfoQuery.getSearchConditions());
        //如果是店铺查询后的模糊查询,并且分页
        String siteShop = (String) httpSession.getAttribute("siteShop");
        itemInfoQuery.setShopName(siteShop);
        //如果表示页数有修改的话,进行设定
        String pageSize = (String) httpSession.getAttribute("pageSize");
        if (pageSize != null && !"".equals(pageSize)) {
            itemInfoQuery.setPageSize(Integer.parseInt(pageSize));
        }
        //编辑状态
        String flog = (String) httpSession.getAttribute("flog");
        if (flog != null && !"".equals(flog)) {
            itemInfoQuery.setFlog(Integer.parseInt(flog));
        } else {
            httpSession.setAttribute("flog", String.valueOf(0));
            itemInfoQuery.setFlog(0);
        }
        //取得送料设定值
        List<Config> configList = configService.findDeliveryConfig();
        model.addAttribute("configList", configList);
        //siteshop一覧
        List<SiteShop> siteShopList = siteShopService.findAllSiteShop(new SiteShop());
        model.addAttribute("siteShopList", siteShopList);
        PageInfo<Item> itemList = itemService.findItemBySearchConditions(itemInfoQuery);
        model.addAttribute("page", itemList);
        return "iteminfo";
    }

    //店铺区分查询
    @PostMapping("/findIteminfoBySiteShop")
    public String findIteminfoBySiteShop(Model model, ItemInfoQuery itemInfoQuery, HttpSession httpSession) {

        //把siteShop值放到全局变量中
        httpSession.setAttribute("siteShop", itemInfoQuery.getSearchConditions());
        //如果表示页数有修改的话,进行设定
        String pageSize = (String) httpSession.getAttribute("pageSize");
        if (pageSize != null && !"".equals(pageSize)) {
            itemInfoQuery.setPageSize(Integer.parseInt(pageSize));
        }
        //编辑状态
        String flog = (String) httpSession.getAttribute("flog");
        if (flog != null && !"".equals(flog)) {
            itemInfoQuery.setFlog(Integer.parseInt(flog));
        }
        //取得送料设定值
        List<Config> configList = configService.findDeliveryConfig();
        model.addAttribute("configList", configList);
        //siteshop一覧
        List<SiteShop> siteShopList = siteShopService.findAllSiteShop(new SiteShop());
        model.addAttribute("siteShopList", siteShopList);
        PageInfo<Item> itemList = itemService.findItemBySiteShop(itemInfoQuery);
        model.addAttribute("page", itemList);
        return "iteminfo";
    }

    //下载iteminfo和产品照片拷贝
    @ResponseBody
    @PostMapping("/bulkOperation")
    public String bulkOperation(@RequestParam("listString[]") List<String> itemCodeList, @RequestParam("checkFlog") String checkFlog, HttpSession httpSession) {

        Gson gson = new Gson();
        String flog = null;
        //检索下载iteminfo
        List<Item> itemList = itemService.findItemByItemCodes(itemCodeList);
        switch (checkFlog) {
            case "0":
                //导出CSV文件
                ExportItemInfoCsvUtil.exportYahooItemInfoToCsv(itemList, null);
                break;
            case "1":
                //编辑状态
                flog = (String) httpSession.getAttribute("flog");
                if (flog != null && "2".equals(flog)) {
                    return gson.toJson("照片已下载!");
                }
                //产品照片拷贝
                System.out.println("照片拷贝执行开始");
                CopyItemPhotoUtil.read(itemList);
                System.out.println("照片拷贝执行结束");
                break;
            case "2":
                //导出CSV文件
                ExportItemInfoCsvUtil.exportYahooItemInfoToCsv(itemList, null);
                //编辑状态
                flog = (String) httpSession.getAttribute("flog");
                if (flog != null && "2".equals(flog)) {
                    return gson.toJson("照片已下载!");
                }
                //产品照片拷贝
                System.out.println("照片拷贝执行开始");
                CopyItemPhotoUtil.read(itemList);
                System.out.println("照片拷贝执行结束");
                break;
        }

        return gson.toJson("アイテムCSV情報出力完了しました。");
    }

    //削除
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("itemCode") String itemCode, RedirectAttributes redirectAttributes, HttpSession httpSession) {

        Item item = new Item();
        item.setItemCode(itemCode);
        int res = itemService.deleteItem(item);
        if (res == 1) {
            redirectAttributes.addFlashAttribute("message", "削除しました。");
        } else {
            redirectAttributes.addFlashAttribute("message", "削除できなかった。");
        }
        //从session中把pageNum取得,放入ItemInfoQuery对象
        String pageNum = (String) httpSession.getAttribute("pageNum");
        return "redirect:/iteminfo?pageNum=" + pageNum;
    }

    //削除
    @ResponseBody
    @PostMapping("/deleteItems")
    public String deleteItems(@RequestParam("listString[]") List<String> itemCodeList, HttpSession httpSession) {

        Gson gson = new Gson();
        Item item = new Item();
        for (String itemCode : itemCodeList) {
            item.setItemCode(itemCode);
            itemService.deleteItem(item);
        }
        String pageNum = (String) httpSession.getAttribute("pageNum");
        if (pageNum != null && pageNum != "") {
            return gson.toJson(pageNum);
        }
        return gson.toJson(1);
    }

    //修改值
    @PostMapping("/setItemInfo")
    public String setItemInfo(
            @RequestParam("itemName") String itemName,
            @RequestParam("salePrice") String salePrice,
            @RequestParam("delivery") Long deliveryId,
            @RequestParam("purchase-price") String purchasePrice,
            @RequestParam("itemCode") String intemCode,
            @RequestParam("url1") String url1,
            @RequestParam("url2") String url2,
            @RequestParam("url3") String url3,
            RedirectAttributes redirectAttributes, HttpSession httpSession) {

        //保存iteminfo更新值
        Item item = new Item();
        item.setItemCode(intemCode);

        int flog = 0;

        //条件设置
        Config config = new Config();
        //调用查询送料方法
        //如果itemname有值的话
        if (itemName != "") {
            //把大写的空格改为小写的
            itemName = itemName.replaceAll("　", " ");
            item.setItemName(itemName);
            flog++;
        }
        //进货价
        if (purchasePrice != "" && salePrice != "") {
            item.setPurchasePrice(Integer.parseInt(purchasePrice));
            flog++;
        }
        //如果送料有修改的话
        if (deliveryId != null && salePrice != "") {
            //保存结果
            Config resConfig = new Config();
            config.setId(deliveryId);
            resConfig = configService.findDeliveryValue(config);
            item.setDelivery(Integer.parseInt(resConfig.getValue2()));
            flog++;
        }
        //如果卖价有修改的话
        if (salePrice != "") {
            item.setSalePrice(Integer.parseInt(salePrice));
            flog++;
        }
        //如果URL1有修改的话
        if (url1 != "") {
            item.setUrl1(url1);
            flog++;
        }
        //如果URL2有修改的话
        if (url2 != "") {
            item.setUrl2(url2);
            flog++;
        }
        //如果URL3有修改的话
        if (url3 != "") {
            item.setUrl3(url3);
            flog++;
        }
        //保存返回结果
        int res = 0;
        //如果条件在1以上的话进行修改
        if (flog > 0) {
            res = itemService.setItemSalePrice(item);
            //如果没有修改值的话
        } else {
            redirectAttributes.addFlashAttribute("message", "修正内容を入力してください。");
        }
        //给前端返回信息
        if (res == 1) {
            redirectAttributes.addFlashAttribute("message", "アイテム情報更新できました。");
        }
        //从session中把pageNum取得
        String pageNum = (String) httpSession.getAttribute("pageNum");
        if (pageNum != null && pageNum != "") {
            return "redirect:/iteminfo?pageNum=" + pageNum;
        }

        return "redirect:/iteminfo?pageNum=" + 1;
    }

    //一页显示数设定
    @PostMapping("/setPageSize")
    public String setPageNum(Model model, HttpSession httpSession, ItemInfoQuery itemInfoQuery) {


        //如果siteShop不为空的话的设定查询条件
        String siteShop = (String) httpSession.getAttribute("siteShop");
        if (siteShop != null && !"".equals(siteShop)) {
            itemInfoQuery.setShopName(siteShop);
        }
        //如果searchConditions不为空的话的设定查询条件
        String searchConditions = (String) httpSession.getAttribute("searchConditions");
        if (searchConditions != null && !"".equals(searchConditions)) {
            itemInfoQuery.setSearchConditions(searchConditions);
        }
        //编辑状态
        String flog = (String) httpSession.getAttribute("flog");
        if (flog != null && !"".equals(flog)) {
            itemInfoQuery.setFlog(Integer.parseInt(flog));
        }
        //取得送料设定值
        List<Config> configList = configService.findDeliveryConfig();
        model.addAttribute("configList", configList);
        //一页表示数发到全局变量中
        httpSession.setAttribute("pageSize", String.valueOf(itemInfoQuery.getPageSize()));
        //siteshop一覧
        List<SiteShop> siteShopList = siteShopService.findAllSiteShop(new SiteShop());
        model.addAttribute("siteShopList", siteShopList);
        //一页显示数设定
        PageInfo<Item> itemList = itemService.findItemBySearchConditions(itemInfoQuery);
        model.addAttribute("page", itemList);
        return "iteminfo";
    }

    //编辑与否检索
    @PostMapping("/findEditedIteminfo")
    public String findEditedIteminfo(Model model, HttpSession httpSession, ItemInfoQuery itemInfoQuery) {

        //如果siteShop不为空的话的设定查询条件
        String siteShop = (String) httpSession.getAttribute("siteShop");
        if (siteShop != null && !"".equals(siteShop)) {
            itemInfoQuery.setShopName(siteShop);
        }
        //如果searchConditions不为空的话的设定查询条件
        String searchConditions = (String) httpSession.getAttribute("searchConditions");
        if (searchConditions != null && !"".equals(searchConditions)) {
            itemInfoQuery.setSearchConditions(searchConditions);
            httpSession.removeAttribute("searchConditions");
        }
        //如果表示页数有修改的话,进行设定
        String pageSize = (String) httpSession.getAttribute("pageSize");
        if (pageSize != null && !"".equals(pageSize)) {
            itemInfoQuery.setPageSize(Integer.parseInt(pageSize));
        }
        //取得送料设定值 后期修改为session
        List<Config> configList = configService.findDeliveryConfig();
        model.addAttribute("configList", configList);
        //编辑状态放到全局变量中
        httpSession.setAttribute("flog", String.valueOf(itemInfoQuery.getFlog()));
        //siteshop一覧
        List<SiteShop> siteShopList = siteShopService.findAllSiteShop(new SiteShop());
        model.addAttribute("siteShopList", siteShopList);
        //一页显示数设定
        PageInfo<Item> itemList = itemService.findItemBySearchConditions(itemInfoQuery);
        model.addAttribute("page", itemList);
        return "iteminfo";
    }

    //削除option
    @ResponseBody
    @PostMapping("/deleteOption")
    public String setOption(HttpSession httpSession,
                            @RequestParam("itemCode") String itemCode,
                            @RequestParam("listString[]") List<String> optionList
    ) {
        Item item = new Item();
        item.setItemCode(itemCode);
        //把有勾选的option全部进行设定
        for (String option : optionList) {
            String optionCode = option.substring(0, option.indexOf(":"));
            switch (optionCode) {
                case "1":
                    item.setOption1(option);
                    break;
                case "2":
                    item.setOption2(option);
                    break;
                case "3":
                    item.setOption3(option);
                    break;
                case "4":
                    item.setOption4(option);
                    break;
                case "5":
                    item.setOption5(option);
                    break;
            }
        }
        Gson gson = new Gson();
        itemService.deleteOption(item);
        //从session中把pageNum取得
        String pageNum = (String) httpSession.getAttribute("pageNum");
        if (pageNum != null && pageNum != "") {
            return gson.toJson(pageNum);
        }

        return gson.toJson(1);
    }

    //编辑option
    @PostMapping("/updateOption")
    public String updateOption(HttpSession httpSession,
                               @RequestParam("itemCode") String itemCode,
                               @RequestParam("option1") String option1,
                               @RequestParam("option2") String option2,
                               @RequestParam("option3") String option3,
                               @RequestParam("option4") String option4,
                               @RequestParam("option5") String option5,
                               @RequestParam("value1") String value1,
                               @RequestParam("value2") String value2,
                               @RequestParam("value3") String value3,
                               @RequestParam("value4") String value4,
                               @RequestParam("value5") String value5
    ) {
        Item item = new Item();
        //设置itemcode
        item.setItemCode(itemCode);
        //把option和value的值进行设定
        if ((option1 != null && option1 != "") && (value1 != null && value1 != "")) {
            //有全角或半角空格全部去除
            option1 = option1.replaceAll("　", "").replaceAll(" ", "");
            //有全角半角,",","、","/","-"全部去除
            value1 = value1.replaceAll("　", " ").replaceAll("、", " ").replaceAll(",", " ").replaceAll("/", " ").replaceAll("-", " ");
            item.setOption1(option1);
            item.setValue1(value1);
        }
        if ((option2 != null && option2 != "") && (value2 != null && value2 != "")) {
            //有全角或半角空格全部去除
            option2 = option2.replaceAll("　", "").replaceAll(" ", "");
            //有全角半角,",","、","/","-"全部去除
            value2 = value2.replaceAll("　", " ").replaceAll("、", " ").replaceAll(",", " ").replaceAll("/", " ").replaceAll("-", " ");
            item.setOption2(option2);
            item.setValue2(value2);
        }
        if ((option3 != null && option3 != "") && (value3 != null && value3 != "")) {
            //有全角或半角空格全部去除
            option3 = option3.replaceAll("　", "").replaceAll(" ", "");
            //有全角半角,",","、","/","-"全部去除
            value3 = value3.replaceAll("　", " ").replaceAll("、", " ").replaceAll(",", " ").replaceAll("/", " ").replaceAll("-", " ");
            item.setOption3(option3);
            item.setValue3(value3);
        }
        if ((option4 != null && option4 != "") && (value4 != null && value4 != "")) {
            //有全角或半角空格全部去除
            option4 = option4.replaceAll("　", "").replaceAll(" ", "");
            //有全角半角,",","、","/","-"全部去除
            value4 = value4.replaceAll("　", " ").replaceAll("、", " ").replaceAll(",", " ").replaceAll("/", " ").replaceAll("-", " ");
            item.setOption4(option4);
            item.setValue4(value4);
        }
        if ((option5 != null && option5 != "") && (value5 != null && value5 != "")) {
            //有全角或半角空格全部去除
            option5 = option5.replaceAll("　", "").replaceAll(" ", "");
            //有全角半角,",","、","/","-"全部去除
            value5 = value5.replaceAll("　", " ").replaceAll("、", " ").replaceAll(",", " ").replaceAll("/", " ").replaceAll("-", " ");
            item.setOption5(option5);
            item.setValue5(value5);
        }
        itemService.updateOption(item);
        //从session中把pageNum取得
        String pageNum = (String) httpSession.getAttribute("pageNum");
        if (pageNum != null && pageNum != "") {
            return "redirect:/iteminfo?pageNum=" + pageNum;
        }

        return "redirect:/iteminfo?pageNum=" + 1;
    }
}


















