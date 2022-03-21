package con.chin.service.impl;

import con.chin.mapper.FileImportMapper;
import con.chin.pojo.OrderInfo;
import con.chin.service.FileImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FileImportServiceImpl implements FileImportService {

    @Autowired
    FileImportMapper fileImportMapper;

    //订单信息保存
    @Override
    @Transactional
    public int savaOrderInfo(OrderInfo orderInfo) {

        OrderInfo oldOrderInfo = this.findOrderInfoByOrderId(orderInfo);
        if(oldOrderInfo == null){
            return fileImportMapper.savaOrderInfo(orderInfo);
        }
        return this.updataOderInfo(orderInfo);
    }

    //查找订单
    public OrderInfo findOrderInfoByOrderId(OrderInfo orderInfo){
        return fileImportMapper.findOrderInfoByOrderId(orderInfo);
    }

    //更新订单
    @Override
    public int updataOderInfo(OrderInfo orderInfo) {
        return fileImportMapper.updataOderInfo(orderInfo);
    }


}
