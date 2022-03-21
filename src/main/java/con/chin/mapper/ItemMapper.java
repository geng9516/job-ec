package con.chin.mapper;

import org.apache.ibatis.annotations.Mapper;
import con.chin.pojo.Item;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface ItemMapper {

    int saveItem (Item item);

    void updateItem(Item item);

    List<Item> findAllItem();

    Item findItem(Item item);

    //获取产品code
    List<String> findItemCodeByPath(String path);

}
