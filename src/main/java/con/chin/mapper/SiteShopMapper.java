package con.chin.mapper;

import con.chin.pojo.SiteShop;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SiteShopMapper {

    //addsiteshopinfo
    Integer saveSiteShopInfo(SiteShop siteShop);

    //条件查找数据源店铺
    List<SiteShop> findSiteShop(SiteShop siteShop);

}
