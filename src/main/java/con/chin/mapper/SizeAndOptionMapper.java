package con.chin.mapper;

import con.chin.pojo.SizeAndOption;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SizeAndOptionMapper {

    //查询option翻译的是否有
    SizeAndOption findSizeAndOtionByOptionValue(String optionValue);

    //Option登录
    Integer saveSizeAndOption(SizeAndOption sizeAndOption);

    //更新
    int updateSizeAndOption(SizeAndOption sizeAndOption);


}
