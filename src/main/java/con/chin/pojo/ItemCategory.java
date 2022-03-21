package con.chin.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class ItemCategory {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;//id

    private String itempath;//カテゴリー名

    private Integer productCategory;//カテゴリーコード

    private String kinds;//ECサイト


}

