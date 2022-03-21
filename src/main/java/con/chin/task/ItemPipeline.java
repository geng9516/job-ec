package con.chin.task;

import con.chin.pojo.Item;
import con.chin.pojo.ItemKeyword;
import con.chin.service.ItemKeywordService;
import con.chin.service.ItemService;
import con.chin.util.PhotoDownloadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;
import java.util.Map;

//产品数据导出方式改为数据库
@Component
@Transactional
public class ItemPipeline implements Pipeline {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemKeywordService itemKeywordService;

    @Override
    public void process(ResultItems resultItems, Task task) {

        int res =-1;
        //resultItems 页面保存对象
        Item item = resultItems.get("item");
        if(item != null){
            res = itemService.saveItem(item);
            System.out.println("插入一条itemcode为:  " + item.getItemCode());
        }

        List<ItemKeyword> itemKeywordList = resultItems.get("itemKeywordList");
        if (item != null){
            itemKeywordService.save(itemKeywordList);
        }

        Map<String,Object> map  = resultItems.get("photoDownload");
        if(map != null && res !=-1){

            PhotoDownloadUtil.download((List)map.get("photoAll"),(String)map.get("itemCode"),(String)map.get("itemPath"));
            System.out.println("itemcode为:  " + item.getItemCode() + " 的产品照片下载完成");
        }


    }
}
