package con.chin.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

//@Entity
@Data
public class OrderInfo {

    private Long id;//ID

    private String orderId;//オーダーID
    private String shopNameAndOrderId;//ショップ名付きオーダーID
    private String orderTime;//注文日時
    private String shipNameKana;//お届け先氏名カナ
    private String shipName;//お届け先氏名
    private String shipZipCode;//お届け先郵便番号
    private String shipAddressFull;//お届け先フル住所
    private String shipPrefecture;//都道府県
    private String shipCity;//市区町村
    private String shipAddress1;//お届け先住所１行目
    private String shipAddress2;//お届け先住所２行目
    private String shipPhoneNumber;//お届け先電話番号
    private String shipMethodName;//お届け方法名称
    private String shipCompanyCode;//配送会社code
    private String shipInvoiceNumber1;//問い合わせ番号１
    private String shipInvoiceNumber2;//問い合わせ番号２
    private String shipDate;//出荷日
    private String billMailAddress;//請求先メールアドレス
    private Integer shipCharge;//送料
    private Integer total;//請求金額合計（ポイント分を除いた）
    private String referer;//参照元URL
    private String payMethodName;//お支払い方法名称
    private String combinedPayMethodName;//併用お支払い方法名称
    private String shipStatus;//出荷ステータス
    private String payStatus;//入金ステータス
    private String deviceType;//注文媒体

    private List<OrderItemInfo> orderItemInfoList;


    public void setId(Long id) {
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }
}
