package con.chin.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;


/**
 * 店铺类
 */
@Entity
@Data
public class EcSiteShop {

    private Long id;

    private String shopId;

    private String shopName;

    private Integer status;

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }
}
