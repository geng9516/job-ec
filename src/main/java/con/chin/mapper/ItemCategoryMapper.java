package con.chin.mapper;

import con.chin.pojo.ItemCategory;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Mapper
@Repository
public interface ItemCategoryMapper{

    Integer findItemCategoryByPath(Map<String,String> map);

}
