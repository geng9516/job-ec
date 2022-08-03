package con.chin.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 */
@Entity
@Data
public class ShopAndItem {

    private Long id;

    private String shopId;

    private String shopNmae;

    private String itemId;

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }
}
