package con.chin.pojo;

import com.sun.xml.txw2.output.SaxSerializer;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Data
public class Item implements Serializable {

    private Long id;//id主キー

    private String siteName;//サイト名
    private String shopName;//ショップ名
    private String itemCode;//アイテムコード
    private String oldItemCode;//古いアイテムコード
    private String itemPath;//カテゴリーパス
    private Integer itemCategoryCode;//カテゴリーコード
    private String itemName;//商品名
    private Integer price;//商品価格
    private Integer purchasePrice;//仕入れ価格
    private Integer salePrice;//販売価格
    private Integer delivery;//国际送料
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
    private String url1;//产品url
    private String url2;//产品url
    private String url3;//产品url
    private String url4;//产品url
    private String url5;//产品url
    private String headline;//キャチコピー
    private String caption;//商品詳細（説明文ある場合）
    private String explanation;//商品説明
    private String relevantLinks;//オススメ商品
    private Integer flog;//有効か失効
    private String image;//照片
    private String created;//作成時間
    private String updatetime;//更新時間
    private String endDate;//終わり時間

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }
}
