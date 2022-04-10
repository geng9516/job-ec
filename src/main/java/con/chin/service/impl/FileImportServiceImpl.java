package con.chin.service.impl;

import con.chin.mapper.FileImportMapper;
import con.chin.pojo.Item;
import con.chin.pojo.OrderInfo;
import con.chin.pojo.OrderItemInfo;
import con.chin.service.FileImportService;
import con.chin.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FileImportServiceImpl implements FileImportService {

    @Autowired
    private FileImportMapper fileImportMapper;

    @Autowired
    private ItemService itemService;

    //----------------------------------------------------
    //订单信息保存
    @Override
    @Transactional
    public int savaOrderInfo(OrderInfo orderInfo) {

        OrderInfo oldOrderInfo = this.findOrderInfoByOrderId(orderInfo);
        if(oldOrderInfo == null){
            return fileImportMapper.savaOrderInfo(orderInfo);
        }
        this.updataOderInfo(orderInfo);
        //カウントするため
        return -1;
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

    //----------------------------------------------------
    //订单产品
    @Override
    public int savaOrderItemInfo(OrderItemInfo orderItemInfo) {

        OrderItemInfo oldOrderItemInfo = this.findOrderItemInfoByOrderIdAndItemId(orderItemInfo);
        if(oldOrderItemInfo == null){
            return fileImportMapper.savaOrderItemInfo(orderItemInfo);
        }
        this.updataOderItemInfo(orderItemInfo);

        return -1;
    }

    //订单号查找订单
    @Override
    public OrderItemInfo findOrderItemInfoByOrderIdAndItemId(OrderItemInfo orderItemInfo) {
        return fileImportMapper.findOrderItemInfoByOrderIdAndItemId(orderItemInfo);
    }

    //更新订单产品信息
    @Override
    public int updataOderItemInfo(OrderItemInfo orderItemInfo) {
        return fileImportMapper.updataOderItemInfo(orderItemInfo);
    }

    //----------------------------------------------------
    //追加或更新iteminfo
    @Override
    public int savaItem(Item item) {
        return itemService.updateItemByCsv(item);
    }


}
