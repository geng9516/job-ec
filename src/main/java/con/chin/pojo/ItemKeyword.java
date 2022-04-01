package con.chin.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class ItemKeyword {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;//id

    private String productCategory;//商品カテゴリー

    private String keyword;//商品キーワード

    private Integer countKeyword;//キーワード使用回数


}
