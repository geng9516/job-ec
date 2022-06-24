package con.chin.pojo.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemInfoQuery {

    private Integer pageNum = 1;//当前页码

    private Integer pageSize = 20;//每一页所显示的数量

    private String searchConditions;

    private String shopName;

    private String startDate;

    private String endDate;

    //产品的类别是否有值时判断
    private Integer itemPathFlog;

    private Integer flog;
}
