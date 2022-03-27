package con.chin.service.impl;

import con.chin.mapper.ConfigMapper;
import con.chin.pojo.Config;
import con.chin.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private ConfigMapper configMapper;


    //送料设定值检索
    @Override
    public List<Config> findDeliveryConfig() {
        return configMapper.findDeliveryConfig();
    }

    //送料检索
    @Override
    public Config findDeliveryValue(Config config) {
        return configMapper.findDeliveryValue(config);
    }
}
