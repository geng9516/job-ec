package con.chin.service;

import con.chin.pojo.Config;

import java.util.List;

public interface ConfigService {


    //送料设定值检索
    List<Config> findDeliveryConfig();

    //送料检索
    Config findDeliveryValue(Config config);


}
