package con.chin.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class ItemCategory {

    private Long id;//id

    @Id
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    private String itempath;//カテゴリー名

    private Integer productCategory;//カテゴリーコード

    private String kinds;//ECサイト

    private String categoryAlias;//カテゴリー别名


}

