package con.chin.pojo.query;

import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemQuery {

    private Integer pageNum = 1;//当前页码

    private  Integer pageSize = 30;//每一页所显示的数量

    private String itemCode;
}
