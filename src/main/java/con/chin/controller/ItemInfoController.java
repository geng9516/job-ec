package con.chin.controller;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import con.chin.pojo.Config;
import con.chin.pojo.Item;
import con.chin.pojo.ItemKeyword;
import con.chin.pojo.SiteShop;
import con.chin.pojo.query.ItemInfoQuery;
import con.chin.service.*;
import con.chin.util.DataExportUtil;
import con.chin.util.ItemPhotoCopyUtil;
import con.chin.util.ItemInfoCsvExportUtil;
import con.chin.util.SetDataUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemInfoController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private SiteShopService siteShopService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ItemCategoryService itemCategoryService;

    @Autowired
    private ItemKeywordService itemKeywordService;

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
        if (siteShop != null && siteShop != "") {
            itemInfoQuery.setShopName(siteShop);
        }
        //如果是店铺查询后的模糊查询,并且分页
        String searchConditions = (String) httpSession.getAttribute("searchConditions");
        if (searchConditions != null && searchConditions != "") {
            itemInfoQuery.setSearchConditions(searchConditions);
        }
        //如果表示页数有修改的话,进行设定
        String pageSize = (String) httpSession.getAttribute("pageSize");
        if (pageSize != null && !"".equals(pageSize)) {
            itemInfoQuery.setPageSize(Integer.parseInt(pageSize));
            model.addAttribute("setPageSize", itemInfoQuery.getPageSize());
        }
        //编辑状态
        String flog = (String) httpSession.getAttribute("flog");
        if (flog != null && !"".equals(flog)) {
            itemInfoQuery.setFlog(Integer.parseInt(flog));
        } else {
            httpSession.setAttribute("flog", String.valueOf(1));
//            itemInfoQuery.setFlog(1);
        }
        //ecsite
        String ecSite = (String) httpSession.getAttribute("ecSite");
        //ecSite已经在全局变量中存在时
        if (ecSite != null && !"".equals(ecSite)) {
            //前端使用
            model.addAttribute("ecSite", ecSite);
        } else {
            //ecSite设定到全局变量中
            httpSession.setAttribute("ecSite", "yahoo");
            //前端使用
            model.addAttribute("ecSite", "yahoo");
        }
        //取得送料设定值
        List<Config> configList = configService.findDeliveryConfig();
        model.addAttribute("configList", configList);
        //前端使用
        model.addAttribute("editFlogSelect", itemInfoQuery.getFlog());
        model.addAttribute("siteShop", itemInfoQuery.getShopName());
        model.addAttribute("setPageSize", itemInfoQuery.getPageSize());
        model.addAttribute("deleteFlog", itemInfoQuery.getFlog());
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
        if (itemInfoQuery.getSearchConditions() == null || "".equals(itemInfoQuery.getSearchConditions())) {
            httpSession.removeAttribute("siteShop");
            httpSession.removeAttribute("pageSize");
            httpSession.removeAttribute("flog");
        } else {
            //如果表示页数有修改的话,进行设定
            String pageSize = (String) httpSession.getAttribute("pageSize");
            if (pageSize != null && !"".equals(pageSize)) {
                itemInfoQuery.setPageSize(Integer.parseInt(pageSize));
                //前端使用
                model.addAttribute("setPageSize", itemInfoQuery.getPageSize());
            }
            //如果是店铺查询后的模糊查询,并且分页
            String siteShop = (String) httpSession.getAttribute("siteShop");
            if (siteShop != null && !"".equals(siteShop)) {
                itemInfoQuery.setShopName(siteShop);
                //前端使用
                model.addAttribute("siteShop", itemInfoQuery.getShopName());
            }
            //编辑状态
            String flog = (String) httpSession.getAttribute("flog");
            if (flog != null && !"".equals(flog)) {
                itemInfoQuery.setFlog(Integer.parseInt(flog));
                //前端使用
                model.addAttribute("editFlogSelect", itemInfoQuery.getFlog());
                model.addAttribute("deleteFlog", itemInfoQuery.getFlog());
            }
            //为了条件查询后的分页 (有这条会出现编辑状态切换时数据错误)
            httpSession.setAttribute("searchConditions", itemInfoQuery.getSearchConditions());
            //前端使用
            model.addAttribute("searchConditions", itemInfoQuery.getSearchConditions());
        }
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

        //
        String searchConditions = (String) httpSession.getAttribute("searchConditions");
        //变量另存
        String siteShop = (String) httpSession.getAttribute("siteShop");
        if (siteShop != null && !"".equals(siteShop)) {
            if (searchConditions != null && !"".equals(searchConditions)) {
                //检索关键字删除
                httpSession.removeAttribute("searchConditions");
            }
            //如果表示页数有修改的话,进行设定
            String pageSize = (String) httpSession.getAttribute("pageSize");
            if (pageSize != null && !"".equals(pageSize)) {
                itemInfoQuery.setPageSize(Integer.parseInt(pageSize));
                //前端使用
                model.addAttribute("setPageSize", Integer.parseInt(pageSize));
            }
            //编辑状态
            String flog = (String) httpSession.getAttribute("flog");
            if (flog != null && !"".equals(flog)) {
                itemInfoQuery.setFlog(Integer.parseInt(flog));
                //前端使用
                model.addAttribute("editFlogSelect", Integer.parseInt(flog));
                model.addAttribute("deleteFlog", Integer.parseInt(flog));
            }
            //把siteShop值放到全局变量中
            httpSession.setAttribute("siteShop", itemInfoQuery.getShopName());
            //前端使用
            model.addAttribute("siteShop", itemInfoQuery.getShopName());
        } else {
            //siteshop不为空时
            if (itemInfoQuery.getShopName() != null && !"".equals(itemInfoQuery.getShopName())) {
                //把siteShop值放到全局变量中
                httpSession.setAttribute("siteShop", itemInfoQuery.getShopName());
                //前端使用
                model.addAttribute("siteShop", itemInfoQuery.getShopName());
            }
            if (searchConditions != null && !"".equals(searchConditions)) {
                //检索关键字设置为条件
//                itemInfoQuery.setSearchConditions(searchConditions);
                //检索关键字删除
                httpSession.removeAttribute("searchConditions");
            }
            //如果表示页数有修改的话,进行设定
            String pageSize = (String) httpSession.getAttribute("pageSize");
            if (pageSize != null && !"".equals(pageSize)) {
                itemInfoQuery.setPageSize(Integer.parseInt(pageSize));
                //前端使用
                model.addAttribute("setPageSize", Integer.parseInt(pageSize));
            }
            //编辑状态
            String flog = (String) httpSession.getAttribute("flog");
            if (flog != null && !"".equals(flog)) {
                itemInfoQuery.setFlog(Integer.parseInt(flog));
                //前端使用
                model.addAttribute("editFlogSelect", Integer.parseInt(flog));
                model.addAttribute("deleteFlog", Integer.parseInt(flog));
            }
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

    //编辑与否检索
    @PostMapping("/findEditedIteminfo")
    public String findEditedIteminfo(Model model, HttpSession httpSession, ItemInfoQuery itemInfoQuery) {
        //搜索关键字还存在时删除
        String searchConditions = (String) httpSession.getAttribute("searchConditions");
        if (searchConditions != null && searchConditions != "") {
            httpSession.removeAttribute("searchConditions");
        }
        //如果siteShop不为空的话的设定查询条件
        String siteShop = (String) httpSession.getAttribute("siteShop");
        if (siteShop != null && !"".equals(siteShop)) {
            itemInfoQuery.setShopName(siteShop);
            //前端使用
            model.addAttribute("siteShop", itemInfoQuery.getShopName());
        }
        //如果表示页数有修改的话,进行设定
        String pageSize = (String) httpSession.getAttribute("pageSize");
        if (pageSize != null && !"".equals(pageSize)) {
            itemInfoQuery.setPageSize(Integer.parseInt(pageSize));
            //前端使用
            model.addAttribute("setPageSize", itemInfoQuery.getPageSize());
        }
        //编辑状态放到全局变量中
        if (itemInfoQuery.getFlog() != null) {
            httpSession.setAttribute("flog", String.valueOf(itemInfoQuery.getFlog()));
            model.addAttribute("deleteFlog", itemInfoQuery.getFlog());
            //前端使用
            model.addAttribute("editFlogSelect", itemInfoQuery.getFlog());
        }
        //取得送料设定值 后期修改为session
        List<Config> configList = configService.findDeliveryConfig();
        model.addAttribute("configList", configList);
        //siteshop一覧
        List<SiteShop> siteShopList = siteShopService.findAllSiteShop(new SiteShop());
        model.addAttribute("siteShopList", siteShopList);
        //一页显示数设定
        PageInfo<Item> itemList = itemService.findItemBySearchConditions(itemInfoQuery);
        model.addAttribute("page", itemList);
        return "iteminfo";
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
            //前端使用
            model.addAttribute("editFlogSelect", itemInfoQuery.getFlog());
            model.addAttribute("deleteFlog", itemInfoQuery.getFlog());
        }
        //一页表示数发到全局变量中
        if (itemInfoQuery.getPageSize() != null) {
            httpSession.setAttribute("pageSize", String.valueOf(itemInfoQuery.getPageSize()));
            //前端使用
            model.addAttribute("setPageSize", itemInfoQuery.getPageSize());
        }
        //siteshop一覧
        List<SiteShop> siteShopList = siteShopService.findAllSiteShop(new SiteShop());
        model.addAttribute("siteShopList", siteShopList);
        //一页显示数设定
        PageInfo<Item> itemList = itemService.findItemBySearchConditions(itemInfoQuery);
        model.addAttribute("page", itemList);
        return "iteminfo";
    }

    @Value("${ITEMCSVPATH}")
    private String itemCsvPath;

    //下载iteminfo和产品照片拷贝
    @ResponseBody
    @PostMapping("/bulkOperation")
    public String bulkOperation(@RequestParam("listString[]") List<String> itemCodeList, @RequestParam("checkFlog") String checkFlog, HttpSession httpSession) {

        //ecsite
        String ecSite = (String) httpSession.getAttribute("ecSite");
        Gson gson = new Gson();
        String flog = null;
        List<Item> itemList = new ArrayList<>();
        List<Item> itemList1 = new ArrayList<>();
        switch (checkFlog) {
            //选中的产品导出产品CSV文件
            case "0":
                //检索下载iteminfo
                itemList = itemService.findItemByItemCodes(itemCodeList);
                itemList1 = new ArrayList<>();
                for (Item item : itemList) {
                    //flog为CSVダウンロード待ち时修改
                    if (item.getFlog() == 0) {
                        item.setFlog(1);
                        itemList1.add(item);
                    }
                }
                //存在值时
                if (itemList1.size() > 0) {
                    itemService.setItemFlog(itemList1);
                }
                //ecSite已经在全局变量中存在时
                if (ecSite != null && !"".equals(ecSite)) {
                    if (ecSite.equals("yahoo")) {
                        //导出CSV文件
                        ItemInfoCsvExportUtil.exportYahooItemInfoToCsv(itemList, itemCsvPath, "data_spy");
                        //导出库存CSV文件
                        DataExportUtil.exportItemStockCsv(itemCodeList, itemCsvPath, "quantity");
                        //导出optionCSV文件
                        DataExportUtil.exportItemOptionCsv(itemList, itemCsvPath, "option_add");
                    } else if (ecSite.equals("au")) {
                        //导出auCSV文件
                        ItemInfoCsvExportUtil.exportAuItemInfoToCsv(itemList, itemCsvPath, "item");
                        //导出optionCSV文件
//                        DataExportUtil.exportAuItemOptionCsv(itemList, itemCsvPath, "stock");
                    }
                    //不存在值时默认
                } else {
                    //导出CSV文件
                    ItemInfoCsvExportUtil.exportYahooItemInfoToCsv(itemList, itemCsvPath, "data_spy");
                    //导出库存CSV文件
                    DataExportUtil.exportItemStockCsv(itemCodeList, itemCsvPath, "quantity");
                    //导出optionCSV文件
                    DataExportUtil.exportItemOptionCsv(itemList, itemCsvPath, "option_add");
                }
                break;
            //选中的产品下载照片
            case "1":
                //编辑状态
//                flog = (String) httpSession.getAttribute("flog");
//                if (flog != null && "2".equals(flog)) {
//                    return gson.toJson("照片已下载!");
//                }
                //产品照片拷贝
                System.out.println("照片拷贝执行开始");
                ItemPhotoCopyUtil.read(itemCodeList);
                System.out.println("照片拷贝执行结束");
                break;
            //选中的产品导出产品CSV和照片
            case "2":
                //检索下载iteminfo
                itemList = itemService.findItemByItemCodes(itemCodeList);
                itemList1 = new ArrayList<>();
                for (Item item : itemList) {
                    if (item.getFlog() == 0) {
                        item.setFlog(1);
                        itemList1.add(item);
                    }
                }
                //存在值时
                if (itemList1.size() > 0) {
                    itemService.setItemFlog(itemList1);
                }
                //ecSite已经在全局变量中存在时
                if (ecSite != null && !"".equals(ecSite)) {
                    if (ecSite.equals("yahoo")) {
                        //导出CSV文件
                        ItemInfoCsvExportUtil.exportYahooItemInfoToCsv(itemList, itemCsvPath, "data_spy");
                        //导出库存CSV文件
                        DataExportUtil.exportItemStockCsv(itemCodeList, itemCsvPath, "quantity");
                        //导出optionCSV文件
                        DataExportUtil.exportItemOptionCsv(itemList, itemCsvPath, "option_add");
                    } else if (ecSite.equals("au")) {
                        //导出auCSV文件
                        ItemInfoCsvExportUtil.exportAuItemInfoToCsv(itemList, itemCsvPath, "item");
                        //导出optionCSV文件
//                        DataExportUtil.exportAuItemOptionCsv(itemList, itemCsvPath, "stock");
                    }
                    //不存在值时默认
                } else {
                    //导出CSV文件
                    ItemInfoCsvExportUtil.exportYahooItemInfoToCsv(itemList, itemCsvPath, "data_spy");
                    //导出库存CSV文件
                    DataExportUtil.exportItemStockCsv(itemCodeList, itemCsvPath, "quantity");
                    //导出optionCSV文件
                    DataExportUtil.exportItemOptionCsv(itemList, itemCsvPath, "option_add");
                }
                //编辑状态
//                flog = (String) httpSession.getAttribute("flog");
//                if (flog != null && "2".equals(flog)) {
//                    return gson.toJson("照片已下载!");
//                }
                //开始时间
                long start = System.currentTimeMillis();
                //产品照片拷贝
                System.out.println("照片拷贝执行开始");
                ItemPhotoCopyUtil.read(itemCodeList);
                //结束时间
                long end = System.currentTimeMillis();
                System.out.println("照片拷贝完成!    总耗时：" + (end - start) / 1000 + " 秒");
                break;
            //选中的产品导出optionCSV文件
            case "3":
                //检索下载iteminfo
                itemList = itemService.findItemByItemCodes(itemCodeList);
                //导出optionCSV文件
                DataExportUtil.exportItemOptionCsv(itemList, itemCsvPath, "option_add");
                break;
            //选中的产品导出库存CSV文件
            case "4":
                //导出库存CSV文件
                DataExportUtil.exportItemStockCsv(itemCodeList, itemCsvPath, "quantity");
                break;
        }

        return gson.toJson("アイテムCSV情報出力完了しました。");
    }

    //列入已编辑列表
    @GetMapping("/setItemFlogToEdit")
    public String setItemFlogToEdit(@RequestParam("itemCode") String itemCode, RedirectAttributes redirectAttributes, HttpSession httpSession) {

        //编辑状态
        String flog = (String) httpSession.getAttribute("flog");

        List<Item> itemList = new ArrayList<>();
        if (flog != null && !"".equals(flog)) {
            if ("3".equals(flog)) {
                Item item = new Item();
                item.setItemCode(itemCode);
                item.setFlog(1);
                itemList.add(item);
            } else if ("5".equals(flog)) {
                Item item = new Item();
                item.setItemCode(itemCode);
                item.setFlog(0);
                itemList.add(item);
            }

        }

        //调用修改flog方法
        int res = itemService.setItemFlog(itemList);
        System.out.println("产品ID为  " + itemCode + "  的状态修改完成");
        if (res == 1) {
            if ("3".equals(flog)) {
                redirectAttributes.addFlashAttribute("message", "商品コードが " + itemCode + " を未編集に戻しました。");
            } else if ("5".equals(flog)) {
                redirectAttributes.addFlashAttribute("message", "商品コードが " + itemCode + " をCSVダウンロード待ちに戻しました。");
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "商品コードが " + itemCode + " 未編集に戻せなかった。");
        }
        //从session中把pageNum取得
        String pageNum = (String) httpSession.getAttribute("pageNum");
        if (pageNum != null && pageNum != "") {
            return "redirect:/iteminfo?pageNum=" + pageNum;
        }

        return "redirect:/iteminfo?pageNum=" + 1;
    }

    //列入删除列表
    @GetMapping("/setDeleteItem")
    public String setDeleteItem(@RequestParam("itemCode") String itemCode, RedirectAttributes redirectAttributes, HttpSession httpSession) {

        List<Item> itemList = new ArrayList<>();
        Item item = new Item();
        item.setItemCode(itemCode);
        item.setFlog(3);
        itemList.add(item);
        //调用修改flog方法
        int res = itemService.setItemFlog(itemList);
        if (res == 1) {
            redirectAttributes.addFlashAttribute("message", "商品コードが " + itemCode + " を削除リストに追加しました。");
        } else {
            redirectAttributes.addFlashAttribute("message", "商品コードが " + itemCode + " リストに追加できなかった。");
        }
        //从session中把pageNum取得
        String pageNum = (String) httpSession.getAttribute("pageNum");
        if (pageNum != null && pageNum != "") {
            return "redirect:/iteminfo?pageNum=" + pageNum;
        }

        return "redirect:/iteminfo?pageNum=" + 1;
    }

    //削除
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("itemCode") String itemCode, RedirectAttributes redirectAttributes, HttpSession httpSession) {

        List<String> itemCodeList = new ArrayList<>();
        itemCodeList.add(itemCode);
        int res = itemService.deleteItems(itemCodeList);
        if (res > 0) {
            redirectAttributes.addFlashAttribute("message", "商品コードが " + itemCode + " を削除しました。");
        } else {
            redirectAttributes.addFlashAttribute("message", "商品コードが " + itemCode + "削除できなかった。");
        }
        //从session中把pageNum取得
        String pageNum = (String) httpSession.getAttribute("pageNum");
        if (pageNum != null && pageNum != "") {
            return "redirect:/iteminfo?pageNum=" + pageNum;
        }

        return "redirect:/iteminfo?pageNum=" + 1;
    }

    //多个返回列表
    @ResponseBody
    @PostMapping("/setItemsFlogToEdit")
    public String setItemsFlogToEdit(@RequestParam("listString[]") List<String> itemCodeList, HttpSession httpSession) {

        Gson gson = new Gson();
        List<Item> itemList = new ArrayList<>();
        for (String itemCode : itemCodeList) {
            Item item = new Item();
            item.setItemCode(itemCode);
            item.setFlog(0);
            itemList.add(item);
        }
        //调用修改flog方法
        itemService.setItemFlog(itemList);
        String pageNum = (String) httpSession.getAttribute("pageNum");
        if (pageNum != null && pageNum != "") {
            return gson.toJson(pageNum);
        }
        return gson.toJson(1);
    }

    //多个列入删除列表
    @ResponseBody
    @PostMapping("/setDeleteItems")
    public String setDeleteItems(@RequestParam("listString[]") List<String> itemCodeList, HttpSession httpSession) {

        Gson gson = new Gson();
        List<Item> itemList = new ArrayList<>();
        for (String itemCode : itemCodeList) {
            Item item = new Item();
            item.setItemCode(itemCode);
            item.setFlog(3);
            itemList.add(item);
        }
        //调用修改flog方法
        itemService.setItemFlog(itemList);
        String pageNum = (String) httpSession.getAttribute("pageNum");
        if (pageNum != null && pageNum != "") {
            return gson.toJson(pageNum);
        }
        return gson.toJson(1);
    }

    //削除多个
    @ResponseBody
    @PostMapping("/deleteItems")
    public String deleteItems(@RequestParam("listString[]") List<String> itemCodeList, HttpSession httpSession) {

        Gson gson = new Gson();
        itemService.deleteItems(itemCodeList);
        String pageNum = (String) httpSession.getAttribute("pageNum");
        if (pageNum != null && pageNum != "") {
            return gson.toJson(pageNum);
        }
        return gson.toJson(1);
    }

    //下载未被下载的新爬取产品
    @GetMapping("/selectItemByNewDownloaded")
    public String selectItemByNewDownloaded(HttpSession httpSession, RedirectAttributes redirectAttributes) {

        List<Item> newDownloadedItems = itemService.findNewDownloaded(0);
        //开始时间
        long start = System.currentTimeMillis();
        //调用下载方法
        ItemInfoCsvExportUtil.exportYahooItemInfoToCsv(newDownloadedItems, itemCsvPath, "data_spy");
        //把itemcode单独取出
        List<String> itemCodeList = new ArrayList<>();
        for (Item item : newDownloadedItems) {
            itemCodeList.add(item.getItemCode());
        }
        //导出库存CSV文件
        DataExportUtil.exportItemStockCsv(itemCodeList, itemCsvPath, "quantity");
        //导出optionCSV文件
        DataExportUtil.exportItemOptionCsv(newDownloadedItems, itemCsvPath, "option_add");
        long end = System.currentTimeMillis();
        System.out.println("产品数据导出完成!    总耗时：" + (end - start) + " ms");
        //完成输出信息
        redirectAttributes.addFlashAttribute("message", "アイテム情報が " + newDownloadedItems.size() + " 件出力されました。");
        //从session中把pageNum取得
        String pageNum = (String) httpSession.getAttribute("pageNum");
        if (pageNum != null && pageNum != "") {
            return "redirect:/iteminfo?pageNum=" + pageNum;
        }

        return "redirect:/iteminfo?pageNum=" + 1;
    }

    //下载ショップ商品一括
    @GetMapping("/downloadSiteShopAll")
    public String downloadSiteShopAll(@RequestParam("siteShop") String siteShop, HttpSession httpSession, RedirectAttributes redirectAttributes) {

        List<Item> downloadedShopItems = itemService.downloadFindItemBySiteShop(siteShop);
        //开始时间
        long start = System.currentTimeMillis();
        //调用下载方法
        ItemInfoCsvExportUtil.exportYahooItemInfoToCsv(downloadedShopItems, itemCsvPath, "data_spy");
        //把itemcode单独取出
        List<String> itemCodeList = new ArrayList<>();
        for (Item item : downloadedShopItems) {
            itemCodeList.add(item.getItemCode());
        }
        //导出库存CSV文件
        DataExportUtil.exportItemStockCsv(itemCodeList, itemCsvPath, "quantity");
        //导出optionCSV文件
        DataExportUtil.exportItemOptionCsv(downloadedShopItems, itemCsvPath, "option_add");
        long end = System.currentTimeMillis();
        System.out.println("产品数据导出完成!    总耗时：" + (end - start) + " ms");
        //完成输出信息
        redirectAttributes.addFlashAttribute("message", "アイテム情報が " + downloadedShopItems.size() + " 件出力されました。");
        //从session中把pageNum取得
        String pageNum = (String) httpSession.getAttribute("pageNum");
        if (pageNum != null && pageNum != "") {
            return "redirect:/iteminfo?pageNum=" + pageNum;
        }

        return "redirect:/iteminfo?pageNum=" + 1;
    }

    //下载检索结果商品一览
    @GetMapping("/downloadsearchConditionsAll")
    public String downloadsearchConditionsAll(HttpSession httpSession, RedirectAttributes redirectAttributes) {

        //如果searchConditions不为空的话的设定查询条件
        String searchConditions = (String) httpSession.getAttribute("searchConditions");
        List<Item> downloadsearchConditionsAll = itemService.downloadFindItemBysearchConditions(searchConditions);
        //开始时间
        long start = System.currentTimeMillis();
        //调用下载方法
        ItemInfoCsvExportUtil.exportYahooItemInfoToCsv(downloadsearchConditionsAll, itemCsvPath, "iteminfo");
        //把itemcode单独取出
        List<String> itemCodeList = new ArrayList<>();
        for (Item item : downloadsearchConditionsAll) {
            itemCodeList.add(item.getItemCode());
        }
        //导出库存CSV文件
//        DataExportUtil.exportItemStockCsv(itemCodeList, itemCsvPath, "quantity");
//        //导出optionCSV文件
//        DataExportUtil.exportItemOptionCsv(downloadsearchConditionsAll, itemCsvPath, "option_add");
        long end = System.currentTimeMillis();
        System.out.println("产品数据导出完成!    总耗时：" + (end - start) + " ms");
        //完成输出信息
        redirectAttributes.addFlashAttribute("message", "アイテム情報が " + downloadsearchConditionsAll.size() + " 件出力されました。");
        //从session中把pageNum取得
        String pageNum = (String) httpSession.getAttribute("pageNum");
        if (pageNum != null && pageNum != "") {
            return "redirect:/iteminfo?pageNum=" + pageNum;
        }

        return "redirect:/iteminfo?pageNum=" + 1;
    }

    //修改值
    @PostMapping("/setItemInfo")
    public String setItemInfo(
            @RequestParam("headline") String headline,
            @RequestParam("itemName") String itemName,
            @RequestParam("salePrice") String salePrice,
            @RequestParam("itemPath") String itemPath,
            @RequestParam("explanation") String explanation,
            @RequestParam("itemCode") String itemCode,
            @RequestParam("url1") String url1,
            @RequestParam("url2") String url2,
            @RequestParam("url3") String url3,
            @RequestParam("option1") String option1,
            @RequestParam("option2") String option2,
            @RequestParam("option3") String option3,
            @RequestParam("option4") String option4,
            @RequestParam("option5") String option5,
            @RequestParam("value1") String value1,
            @RequestParam("value2") String value2,
            @RequestParam("value3") String value3,
            @RequestParam("value4") String value4,
            @RequestParam("value5") String value5,
            RedirectAttributes redirectAttributes, HttpSession httpSession) {

        //保存iteminfo更新值
        Item item = new Item();
        item.setItemCode(itemCode);

        int flog = 0;

        //编辑状态
        String explanationKeyword = "";
        String itemFlog = (String) httpSession.getAttribute("flog");
        if (itemPath != null && !"".equals(itemPath)) {
            //设置产品种类番号(条件はpathとサイト名)
            Map<String, String> map = new HashMap<>();
            map.put("itempath", itemPath);
            map.put("shopName", "yahoo");
            Integer itemCategorCode = itemCategoryService.findItemCategoryByPath(map);
            item.setItemCategoryCode(itemCategorCode);
            //
            if (itemFlog != null && !"".equals(itemFlog) && "5".equals(itemFlog)) {
                ItemKeyword itemKeyword = new ItemKeyword();
                itemKeyword.setProductCategory(itemPath);
                List<ItemKeyword> itemKeyword1 = itemKeywordService.findGoodItemKeyword(itemKeyword);
                for (ItemKeyword keyword : itemKeyword1) {
                    explanationKeyword += keyword.getKeyword() + " ";
                }
                if (!explanation.contains("関連キーワード")) {
                    explanation = explanation + "\n" + "\n" +
                            "■関連キーワード：" + "\n" +
                            explanationKeyword;
                }
            }
        }
        //产品名字
        itemName = itemName.replaceAll("　", " ");
        if (itemName != null && !"".equals(itemName) && !itemName.contains(" ")) {
            itemName = explanationKeyword;
            //把大写的空格改为小写的
            itemName = itemName.replaceAll("　", " ");
//            item.setItemName(itemName);
            flog++;
        } else if (itemName != null && !"".equals(itemName)) {
            //把大写的空格改为小写的
            itemName = itemName.replaceAll("　", " ");
            item.setItemName(itemName);
            flog++;
        }
        //如果headline有值的话
        if (headline != null && !"".equals(headline)) {
            //把大写的空格改为小写的
            headline = headline.replaceAll("　", " ");
            item.setHeadline(headline);
            flog++;
        } else {
            headline = explanationKeyword.replaceAll("　", " ");

            if (headline.length() > 30 && headline.contains(" ")) {
                //把产品名称的长度调整
                headline = SetDataUtil.setStrLength(headline, 30);
            }
            item.setHeadline(headline);
        }
        //商品情報
        if (explanation != null && !"".equals(explanation)) {
            //把数据中中文改为日文
            explanation = SetDataUtil.setDatetoJapanese(explanation);
            item.setExplanation(explanation);
            flog++;
        }
        //
        if (itemPath != null && !"".equals(itemPath)) {
            item.setItemPath(itemPath);
            flog++;
        }
        //如果卖价有修改的话
        if (salePrice != null && !"".equals(salePrice)) {
            Integer salePrice1 = Integer.parseInt(salePrice);
            if (salePrice1 < 250) {
                salePrice1 = SetDataUtil.setSalePrice(salePrice1);
            }//else{
//                Item item1 = new Item();
//                item1.setItemCode(itemCode);
//                Item item2 = itemService.findItemByItemCode(item1);
//                salePrice1 = SetDataUtil.setSalePrice(item2.getPrice());
//            }
            item.setSalePrice(salePrice1);
            flog++;
        }
        //如果URL1有修改的话
        if (url1 != null && !"".equals(url1)) {
            item.setUrl1(url1);
            flog++;
        }
        //如果URL2有修改的话
        if (url2 != null && !"".equals(url2)) {
            item.setUrl2(url2);
            flog++;
        }
        //如果URL3有修改的话
        if (url3 != null && !"".equals(url3)) {
            item.setUrl3(url3);
            flog++;
        }
        //把option和value的值进行设定
        if ((option1 != null && !"".equals(option1)) && (value1 != null && !"".equals(value1))) {
            //有全角或半角空格全部去除
            option1 = option1.replaceAll("　", "").replaceAll(" ", "");
            option1 = SetDataUtil.setDatetoJapanese(option1);
            //有全角半角,",","、","/","-"全部去除
            value1 = value1.replaceAll("　", " ").replaceAll("、", " ").replaceAll(",", " ").replaceAll("/", " ").replaceAll("-", " ");
            value1 = SetDataUtil.setDatetoJapanese(value1);
            item.setOption1(option1);
            item.setValue1(value1);
        }
        if ((option2 != null && !"".equals(option2)) && (value2 != null && !"".equals(value2))) {
            //有全角或半角空格全部去除
            option2 = option2.replaceAll("　", "").replaceAll(" ", "");
            option2 = SetDataUtil.setDatetoJapanese(option2);
            //有全角半角,",","、","/","-"全部去除
            value2 = value2.replaceAll("　", " ").replaceAll("、", " ").replaceAll(",", " ").replaceAll("/", " ").replaceAll("-", " ");
            value2 = SetDataUtil.setDatetoJapanese(value2);
            item.setOption2(option2);
            item.setValue2(value2);
        }
        if ((option3 != null && !"".equals(option3)) && (value3 != null && !"".equals(value3))) {
            //有全角或半角空格全部去除
            option3 = option3.replaceAll("　", "").replaceAll(" ", "");
            option3 = SetDataUtil.setDatetoJapanese(option3);
            //有全角半角,",","、","/","-"全部去除
            value3 = value3.replaceAll("　", " ").replaceAll("、", " ").replaceAll(",", " ").replaceAll("/", " ").replaceAll("-", " ");
            value3 = SetDataUtil.setDatetoJapanese(value3);
            item.setOption3(option3);
            item.setValue3(value3);
        }
        if ((option4 != null && !"".equals(option4)) && (value4 != null && !"".equals(value4))) {
            //有全角或半角空格全部去除
            option4 = option4.replaceAll("　", "").replaceAll(" ", "");
            option4 = SetDataUtil.setDatetoJapanese(option4);
            //有全角半角,",","、","/","-"全部去除
            value4 = value4.replaceAll("　", " ").replaceAll("、", " ").replaceAll(",", " ").replaceAll("/", " ").replaceAll("-", " ");
            value4 = SetDataUtil.setDatetoJapanese(value4);
            item.setOption4(option4);
            item.setValue4(value4);
        }
        if ((option5 != null && !"".equals(option5)) && (value5 != null && !"".equals(value5))) {
            //有全角或半角空格全部去除
            option5 = option5.replaceAll("　", "").replaceAll(" ", "");
            option5 = SetDataUtil.setDatetoJapanese(option5);
            //有全角半角,",","、","/","-"全部去除
            value5 = value5.replaceAll("　", " ").replaceAll("、", " ").replaceAll(",", " ").replaceAll("/", " ").replaceAll("-", " ");
            value5 = SetDataUtil.setDatetoJapanese(value5);
            item.setOption5(option5);
            item.setValue5(value5);
        }
        //保存返回结果
        int res = 0;
        //如果条件在1以上的话进行修改
        if (flog > 0) {
            res = itemService.setItemInfo(item);
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

    //修改多个值
    @ResponseBody
    @PostMapping("/setIteminfos")
    public String setIteminfos(HttpSession httpSession,
                               @RequestParam("listString[]") List<String> itemList,
                               @RequestParam("path") String path,
                               @RequestParam("itemName") String itemName
    ) {

        //把选中的itemcode的数据取得
        List<Item> itemByItemCodes = itemService.findItemByItemCodes(itemList);
        //条件设置
        Map<String, String> map = new HashMap<>();
        if (path != null && !"".equals(path)) {
            map.put("itemPath", path);
        }
        if (itemName != null && !"".equals(itemName)) {
            map.put("itemName", itemName);
        }
        //修改值
        itemService.setIteminfos(itemByItemCodes, map);

        Gson gson = new Gson();

        String pageNum = (String) httpSession.getAttribute("pageNum");
        if (pageNum != null && pageNum != "") {
            return gson.toJson(pageNum);
        }
        return gson.toJson(1);
    }

    @GetMapping("/setFlogToEdit")
    public String setFlogToEdit(HttpSession httpSession, @RequestParam("editFlog") Integer flog) {

        List<Item> itemList = new ArrayList<>();
        List<Item> newDownloadedList = itemService.findNewDownloaded(flog);
        for (Item item : newDownloadedList) {
            //需要修改的是为下载的产品时
            if (flog == 0) {
                item.setFlog(1);
                itemList.add(item);
                //需要修改的是为中文的产品时
            } else if (flog == 5) {
                item.setFlog(0);
                itemList.add(item);
            }
        }
        itemService.setItemFlog(itemList);
        //从session中把pageNum取得
        String pageNum = (String) httpSession.getAttribute("pageNum");
        if (pageNum != null && pageNum != "") {
            return "redirect:/iteminfo?pageNum=" + pageNum;
        }

        return "redirect:/iteminfo?pageNum=" + 1;
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
        if ((option1 != null && !"".equals(option1)) && (value1 != null && !"".equals(value1))) {
            //有全角或半角空格全部去除
            option1 = option1.replaceAll("　", "").replaceAll(" ", "");
            //有全角半角,",","、","/","-"全部去除
            value1 = value1.replaceAll("　", " ").replaceAll("、", " ").replaceAll(",", " ").replaceAll("/", " ").replaceAll("-", " ");
            item.setOption1(option1);
            item.setValue1(value1);
        }
        if ((option2 != null && !"".equals(option2)) && (value2 != null && !"".equals(value2))) {
            //有全角或半角空格全部去除
            option2 = option2.replaceAll("　", "").replaceAll(" ", "");
            //有全角半角,",","、","/","-"全部去除
            value2 = value2.replaceAll("　", " ").replaceAll("、", " ").replaceAll(",", " ").replaceAll("/", " ").replaceAll("-", " ");
            item.setOption2(option2);
            item.setValue2(value2);
        }
        if ((option3 != null && !"".equals(option3)) && (value3 != null && !"".equals(value3))) {
            //有全角或半角空格全部去除
            option3 = option3.replaceAll("　", "").replaceAll(" ", "");
            //有全角半角,",","、","/","-"全部去除
            value3 = value3.replaceAll("　", " ").replaceAll("、", " ").replaceAll(",", " ").replaceAll("/", " ").replaceAll("-", " ");
            item.setOption3(option3);
            item.setValue3(value3);
        }
        if ((option4 != null && !"".equals(option4)) && (value4 != null && !"".equals(value4))) {
            //有全角或半角空格全部去除
            option4 = option4.replaceAll("　", "").replaceAll(" ", "");
            //有全角半角,",","、","/","-"全部去除
            value4 = value4.replaceAll("　", " ").replaceAll("、", " ").replaceAll(",", " ").replaceAll("/", " ").replaceAll("-", " ");
            item.setOption4(option4);
            item.setValue4(value4);
        }
        if ((option5 != null && !"".equals(option5)) && (value5 != null && !"".equals(value5))) {
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

    @PostMapping("/setEcSite")
    public String setEcSite(HttpSession httpSession, @RequestParam("ecSite") String ecSite) {

        String ecSite1 = (String) httpSession.getAttribute("ecSite");
        //ecSite已经在全局变量中存在时
        if (ecSite1 != null && !"".equals(ecSite1)) {
            //删除
            httpSession.removeAttribute("ecSite");
        }
        //把ecsite放到全局变量中
        httpSession.setAttribute("ecSite", ecSite);
        //从session中把pageNum取得
        String pageNum = (String) httpSession.getAttribute("pageNum");
        if (pageNum != null && pageNum != "") {
            return "redirect:/iteminfo?pageNum=" + pageNum;
        }

        return "redirect:/iteminfo?pageNum=" + 1;
    }


}


















