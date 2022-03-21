package con.chin.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class OrderItemInfo {

    private Long id;//ID

    private String orderId;//注文ID
    private String lineId;//注文商品別
    private Integer quantity;//注文数量
    private String itemId;//商品コード
    private String subCode;//商品サブコード
    private String title;//商品タイトル
    private String itemOptionName;//商品オプション名、複数の場合は：で区切り
    private String itemOptionValue;//商品オプション内容、複数の場合は：で区切り
    private String itemOptionPrice;//商品オプション価格、複数の場合は：で区切り
    private String subCodeOption;//商品サブコードオプション内容文字列
    private String inscriptionName;//インスクリプション番号、複数の場合は：で区切り
    private String inscriptionValue;//インスクリプション内容、複数の場合は：で区切り
    private Integer unitPrice;//商品の通常販売価格または、特別販売価格または、会員価格
    private String priceType;//商品の販売価格種別
    private Integer unitGetPoint;//価格対する注文によるポイント
    private Integer lineSubTotal;//Lineごと小計
    private Integer lineGetPoint;//Lineごとの付与ポイント小計
    private String pointFspCode;//商品に付与したポイントコード
    private String couponId;//クーポンID
    private String couponDiscount;//クーポン利用値引額
    private Integer originalPrice;//値引き前の単価
    private String isGetPointFix;//ポイント確定状態
    private String getPointFixDate;//ポイント確定予定日
    private String getPointType;//付与ポイント種別
    private Integer lineGetPointChargedToStore;//商品ごとのストア負担ポイント
    private String leadTimeStart;//発送日スタート
    private String leadTimeEnd;//発送日エンド
    private String leadTimeText;//発送日テキスト

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }
}
