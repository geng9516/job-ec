package con.chin.service.impl;

import con.chin.mapper.EcSiteShopMapper;
import con.chin.pojo.EcSiteShop;
import con.chin.service.EcSiteShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcSiteShopImpl implements EcSiteShopService {

    @Autowired
    private EcSiteShopMapper ecSiteShopMapper;

    //ecsiteshop一览取得
    @Override
    public List<EcSiteShop> findAllEcSiteShop() {
        return ecSiteShopMapper.findAllEcSiteShop();
    }
}
