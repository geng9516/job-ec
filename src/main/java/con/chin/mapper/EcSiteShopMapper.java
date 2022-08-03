package con.chin.mapper;

import con.chin.pojo.EcSiteShop;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface EcSiteShopMapper {

     //ecsiteshop一览取得
     List<EcSiteShop> findAllEcSiteShop();


}
