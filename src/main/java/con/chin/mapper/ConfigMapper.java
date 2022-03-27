package con.chin.mapper;


import con.chin.pojo.Config;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ConfigMapper {

    //送料设定值检索
    List<Config> findDeliveryConfig();

    //送料检索
    Config findDeliveryValue(Config config);
}
