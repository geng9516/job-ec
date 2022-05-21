package con.chin.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class PurchasingItem {


    private Long id;

    private String siteName;//サイト名
    private String shopName;//ショップ名
    private String itemCode;//アイテムコード
    private String oldItemCode;//古いアイテムコード
    private String itemName;//商品名
    private Integer purchasePrice1;//仕入れ価格
    private Integer purchasePrice2;//仕入れ価格
    private Integer purchasePrice3;//仕入れ価格
    private String option1;//オプション1　サイズかカラー
    private String value1;//オプション2　サイズかカラー
    private String option2;//その他オプション3
    private String value2;//その他オプション4
    private String option3;//その他オプション5
    private String value3;//その他オプション6
    private String option4;//その他オプション7
    private String value4;//その他オプション8
    private String option5;//その他オプション9
    private String value5;//その他オプション１0
    private String url;//产品url
    private String explanation;//商品説明
    private Integer flog;//有効か失効
    private String image;//照片
    private String created;//作成時間
    private String updatetime;//更新時間

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }
}
