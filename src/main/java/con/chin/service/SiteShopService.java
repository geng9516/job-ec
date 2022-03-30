package con.chin.service;

import con.chin.pojo.SiteShop;

import java.util.List;


public interface SiteShopService {
    //addsiteshopinfo
    Integer saveSiteShopInfo(SiteShop siteShop);

    //条件查找数据源店铺
    List<SiteShop> findSiteShop(SiteShop siteShop);

    //全部/条件查找数据源店铺
    List<SiteShop> findAllSiteShop(SiteShop siteShop);

}
