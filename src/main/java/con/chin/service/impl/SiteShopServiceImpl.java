package con.chin.service.impl;

import con.chin.mapper.SiteShopMapper;
import con.chin.pojo.SiteShop;
import con.chin.service.SiteShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class SiteShopServiceImpl implements SiteShopService {

    @Autowired
    private SiteShopMapper siteShopMapper;

    //addsiteshopinfo
    @Override
    public Integer saveSiteShopInfo(SiteShop siteShop) {

        //查找数据是否存在
        List<SiteShop> siteShopList = siteShopMapper.findSiteShop(siteShop);
        //不存在数据
        Integer res = -1;
        if(siteShopList.size() == 0){
            res = siteShopMapper.saveSiteShopInfo(siteShop);
        }
        return res;
    }

    //条件查找数据源店铺
    @Override
    public List<SiteShop> findSiteShop(SiteShop siteShop) {
        return siteShopMapper.findSiteShop(siteShop);
    }

    //siteshop一覧を
    @Override
    public List<SiteShop> findAllSiteShop(SiteShop siteShop) {
        return siteShopMapper.findAllSiteShop(siteShop);
    }

}
