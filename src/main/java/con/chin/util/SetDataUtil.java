package con.chin.util;

import con.chin.mapper.SizeAndOptionMapper;
import con.chin.pojo.Item;
import con.chin.pojo.PurchasingItem;
import con.chin.pojo.SizeAndOption;
import con.chin.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;

@Component
public class SetDataUtil {

    private static SetDataUtil randomGet;

    //??
    @PostConstruct
    public void init() {
        randomGet = this;
    }

    @Autowired
    private ItemService itemService;

    @Autowired
    private SizeAndOptionMapper sizeAndOptionMapper;


    //推荐产品的itemcode追加
    public static String getRelevantLinks(String path) {

        if (path == null || path == "") {
            return "";
        }
        //数据库中一个种类的产品code全拿出来
        List<String> relevantLinksList = randomGet.itemService.findItemCodeByPath(path);
        //种类的产品code只有一个的话直接返回null
        if (relevantLinksList.size() <= 1) {
            return "";
        }
        //推荐产品的itemcode保存用
        String relevantLinks = "";
        //去重
        Set<String> set = new HashSet<>();
        //生成随机数
        Random rand = new Random();
        //数据库中一个种类的产品不超过20个的时候
        if (relevantLinksList.size() <= 20) {
            for (String s : relevantLinksList) {
                //推荐产品的itemcode多个拼接保存
                relevantLinks += s + " ";
            }
        } else {
            //数据库中一个种类的产品超过20个的时候,随机取出20个放到set集合里
            for (int i = 0; i < 20; i++) {
                //List中的元素随机添加
                set.add(relevantLinksList.get(rand.nextInt(relevantLinksList.size())) + " ");
            }
            //随机到set中去重后个数不满20的话
//            for (int i = set.size(); i <= 20; i++) {
            set = addRelevantLinks(relevantLinksList, set);
//            }
            //Set集合遍历拼接
            for (String s : set) {
                //推荐产品的itemcode多个拼接保存
                relevantLinks += s;
            }
        }
        //返回结果
        return relevantLinks.substring(0, relevantLinks.length() - 1);
    }

    //随机到set中去重后个数不满20的话
    public static Set<String> addRelevantLinks(List<String> relevantLinksList, Set<String> set) {
        Random rand = new Random();
        //Set集合不到20个的空缺的进行填充,最多20个
        for (int i = set.size(); i <= 19; i++) {
            //List中的元素随机添加
            set.add(relevantLinksList.get(rand.nextInt(relevantLinksList.size())) + " ");
        }
        //返回结果
        return set;
    }

    //str长度控制
    public static String setStrLength(String str, int strLength) {
        str = str.substring(0, strLength);
        if (str.contains(" ")) {
            str = str.substring(0, str.lastIndexOf(" "));
        }
        return str;
    }


    //去重并保留原来的顺序
    public static String[] distinctByArr(String[] arr) {
        if (arr != null && arr.length > 0) {
            SetList<String> setList = new SetList<>();
            for (String str : arr) {
                setList.add(str);
            }
            String[] arr_new = new String[setList.size()];
            for (int i = 0; i < setList.size(); i++) {
                arr_new[i] = setList.get(i);
            }
            return arr_new;
        } else {
            return arr;
        }
    }//新サイズ追加 ズボン 収納ケース パンツ収納 タンス収納 レギンス収納 Ｔシャツ収納 シャツ 収納ボックス ケース 仕切り 7ポケット 収納ボックス ジーンズ 衣類

    //修改item名

    /**
     * @param oldItemName 爬取初期的名字
     * @param itemKinds   产品的类别
     * @return
     */
    public static String setItemName(String oldItemName, String itemKinds) {

        //把符号去除
        oldItemName = oldItemName
                .replaceAll("【", "").replaceAll("】", "")
                .replaceAll("！", "").replaceAll("＜", "")
                .replaceAll("＞", "").replaceAll("送料無料 ", "");
        //判断是否时女装
        if (itemKinds.contains("レディースファッション")) {
            //把itemname中的关键字去掉
            oldItemName = oldItemName.replaceAll("レディース ", "");

            //从类别中取得类别名
            String kind = itemKinds.substring(itemKinds.lastIndexOf(":") + 1, itemKinds.length());
            //如果有「、」分割的话
            if (kind.contains("、")) {
                String kind1 = "";
                String[] kindSplits = kind.split("、");
                for (String kindSplit : kindSplits) {
                    oldItemName = oldItemName.replaceAll(kindSplit + " ", "");
                }
                for (int i = 0; i < kindSplits.length; i++) {
                    //包含"その他"跳过
                    if (kindSplits[i].contains("その他")) {
                        continue;
                    }
                    kind1 += kindSplits[i] + " ";
                }
                oldItemName = kind1 + "レディース" + " " + oldItemName;
            } else {
                //把itemname中的关键字去掉
                oldItemName = oldItemName.replaceAll(kind, "");
                oldItemName = kind + " " + "レディース" + " " + oldItemName;
            }

        } else if (itemKinds.contains("メンズファッション")) {
            //把itemname中的关键字去掉
            oldItemName = oldItemName.replaceAll("メンズ ", "");
            //从类别中取得类别名
            String kind = itemKinds.substring(itemKinds.lastIndexOf(":") + 1, itemKinds.length());
            //如果有「、」分割的话
            if (kind.contains("、")) {
                String kind1 = "";
                String[] kindSplits = kind.split("、");
                for (String kindSplit : kindSplits) {
                    oldItemName = oldItemName.replaceAll(kindSplit + " ", "");
                }
                for (int i = 0; i < kindSplits.length; i++) {
                    //包含"その他"跳过
                    if (kindSplits[i].contains("その他")) {
                        continue;
                    }
                    kind1 += kindSplits[i] + " ";
                }
                oldItemName = kind1 + "メンズ" + " " + oldItemName;
            } else {
                //把itemname中的关键字去掉
                oldItemName = oldItemName.replaceAll(kind + " ", "");
                oldItemName = kind + " " + "メンズ" + " " + oldItemName;
            }
        }
        return oldItemName;
    }

    //把中问产品暂时转为日文的格式
    public static List<Item> setPurchasingItemToItemInfo(List<PurchasingItem> purchasingItemList) {
        return null;
    }

    public static Item setOptionAndValue(Item item) {
        //option1有数据时
        if (item.getOption1() != null && item.getOption1() != "") {
            //以空格分装成数组
            String[] Values = item.getValue1().split(" ");
            //用来存储optionname
            String optionName = "";
            //用来存储optionValue
            String optionValue = "";
            //SizeAndOption保存用
            //遍历数组
            for (String Value : Values) {
                //SizeAndOption保存
                SizeAndOption sizeAndOption = new SizeAndOption();

                //检索中文名称是否有数据
                SizeAndOption sizeAndOtionByOptionValue = randomGet.sizeAndOptionMapper.findSizeAndOtionByOptionValue(Value);
                //有数据
                if (sizeAndOtionByOptionValue != null) {
                    //optionName还为空时 为了之赋值一次
                    if (optionName == "") {
                        //每一个的value是否有日文翻译 相当于已经存在
                        if (sizeAndOtionByOptionValue.getOptionNameJapanese() != null && !"".equals(sizeAndOtionByOptionValue.getOptionNameJapanese())) {
                            optionName = sizeAndOtionByOptionValue.getOptionNameJapanese();
                        }
                    }
                    //有日文翻译
                    String s = sizeAndOtionByOptionValue.getOptionValueJapanese();
                    if (sizeAndOtionByOptionValue.getOptionValueJapanese() != null && !"".equals(sizeAndOtionByOptionValue.getOptionValueJapanese())) {
                        optionValue += sizeAndOtionByOptionValue.getOptionValueJapanese() + " ";
                    } else {
                        optionValue += Value + " ";
                    }
                    //没有数据时
                } else {
                    //中文名称保存
                    String optionNameByChinese = item.getOption1();
                    sizeAndOption.setOptionNameChinese(optionNameByChinese);
                    //如果包含了[颜]这个字
                    if (optionNameByChinese.contains("颜")) {
                        String optionNameByJapanese = "カラー";
                        //追加日文名称
                        sizeAndOption.setOptionNameJapanese(optionNameByJapanese);
                        //产品数据中追加
                        optionName = optionNameByJapanese;
                    }
                    //如果包含了[尺]这个字
                    if (optionNameByChinese.contains("尺")) {
                        String optionNameByJapanese = "サイズ";
                        //追加日文名称
                        sizeAndOption.setOptionNameJapanese(optionNameByJapanese);
                        //产品数据中追加
                        optionName = optionNameByJapanese;
                    }
                    //中文名称保存
                    sizeAndOption.setOptionValueChinese(Value);
                    //保存SizeAndOption
                    randomGet.sizeAndOptionMapper.saveSizeAndOption(sizeAndOption);
                    optionValue += Value + " ";
                }
            }
            //保存
            //optionName 不为空时
            if (optionName != null && optionName != "") {
                item.setOption1(optionName);
            }
            //把最后的空格去掉
            if (optionValue != null && optionValue != "") {
                optionValue = optionValue.substring(0, optionValue.lastIndexOf(" "));
                item.setValue1(optionValue);
            } else {
                optionValue = item.getValue1();
                optionValue = optionValue.substring(0, optionValue.lastIndexOf(" "));
                item.setValue1(optionValue);
            }
        }
        //option2有数据时
        if (item.getOption2() != null && item.getOption2() != "") {
            //以空格分装成数组
            String[] Values = item.getValue2().split(" ");
            //用来存储optionname
            String optionName = "";
            //用来存储optionValue
            String optionValue = "";
            //SizeAndOption保存用
            //遍历数组
            for (String Value : Values) {
                //SizeAndOption保存
                SizeAndOption sizeAndOption = new SizeAndOption();

                //检索中文名称是否有数据
                SizeAndOption sizeAndOtionByOptionValue = randomGet.sizeAndOptionMapper.findSizeAndOtionByOptionValue(Value);
                //有数据
                if (sizeAndOtionByOptionValue != null) {
                    //optionName还为空时 为了之赋值一次
                    if (optionName == "") {
                        //每一个的value是否有日文翻译 相当于已经存在
                        if (sizeAndOtionByOptionValue.getOptionNameJapanese() != null && !"".equals(sizeAndOtionByOptionValue.getOptionNameJapanese())) {
                            optionName = sizeAndOtionByOptionValue.getOptionNameJapanese();
                        }
                    }
                    //有日文翻译
                    if (sizeAndOtionByOptionValue.getOptionValueJapanese() != null && !"".equals(sizeAndOtionByOptionValue.getOptionValueJapanese())) {
                        optionValue += sizeAndOtionByOptionValue.getOptionValueJapanese() + " ";
                    } else {
//                        optionValue += Value + " ";
                    }
                    //没有数据时
                } else {
                    //中文名称保存
                    String optionNameByChinese = item.getOption2();
                    sizeAndOption.setOptionNameChinese(optionNameByChinese);
                    //如果包含了[颜]这个字
                    if (optionNameByChinese.contains("颜")) {
                        String optionNameByJapanese = "カラー";
                        //追加日文名称
                        sizeAndOption.setOptionNameJapanese(optionNameByJapanese);
                        //产品数据中追加
                        optionName = optionNameByJapanese;
                    }
                    //如果包含了[尺]这个字
                    if (optionNameByChinese.contains("尺")) {
                        String optionNameByJapanese = "サイズ";
                        //追加日文名称
                        sizeAndOption.setOptionNameJapanese(optionNameByJapanese);
                        //产品数据中追加
                        optionName = optionNameByJapanese;
                    }
                    //中文名称保存
                    sizeAndOption.setOptionValueChinese(Value);
                    //保存SizeAndOption
                    randomGet.sizeAndOptionMapper.saveSizeAndOption(sizeAndOption);
                    optionValue += Value + " ";
                }

            }
            //保存
            //optionName 不为空时
            if (optionName != null && optionName != "") {
                item.setOption2(optionName);
            }
            //把最后的空格去掉
            if (optionValue != null && optionValue != "") {
                optionValue = optionValue.substring(0, optionValue.lastIndexOf(" "));
                item.setValue2(optionValue);
            } else {
                optionValue = item.getValue2();
                optionValue = optionValue.substring(0, optionValue.lastIndexOf(" "));
                item.setValue2(optionValue);
            }
        }
        return item;

    }

    //把数据中中文改为日文
    public static String setDatetoJapanese(String str) {

        //: 更改为全角的
        str = str.replaceAll(":", "：");

        //转换空格
        str = str.replaceAll("   ", "!").replace("　　　", "!")
                .replaceAll("  　", "!").replaceAll(" 　 ", "!")
                .replaceAll("　  ", "!").replaceAll("　　 ", "!")
                .replaceAll("　 　", "!").replaceAll(" 　　", "!");
        str = str.replaceAll("!", "　");
        str = str.replaceAll("  ", "!").replaceAll("　　", "!")
                .replaceAll(" 　", "!").replaceAll("　 ", "!");
        str = str.replaceAll("!", "　");

        //尺码名称
        str = str.replaceAll("衣长", "着長").replaceAll("胸围", "バスト")
                .replaceAll("肩宽", "肩幅").replaceAll("袖长", "袖丈")
                .replaceAll("高腰围", "ハイウエスト").replaceAll("腰围", "ウエスト")
                .replaceAll("裙长", "着長").replaceAll("下摆围", "ヘムライン")
                .replaceAll("摆围", "ヘムライン").replaceAll("裤长", "着長")
                .replaceAll("臀围", "ヒップ").replaceAll("裤口", "裾周り")
                .replaceAll("腿围", "もも周り").replaceAll("下摆", "ヘムライン");

        //サイズ
        str = str.replaceAll("尺码", "サイズ").replaceAll("均码", "F")
                .replaceAll("码", "");

        //颜色
        str = str.replaceAll("颜色", "カラー").replaceAll("图片色", "写真参考");

        //材质名称
        str = str.replaceAll("材质成分", "素材").replaceAll("面料", "素材")
                .replaceAll("材质", "素材");

        //材质
        str = str.replaceAll("氨纶", "スパンデックス").replaceAll("毛呢", "ツイード")
                .replaceAll("再生纤维", "再生繊維").replaceAll("聚酯纤维", "ポリエステル")
                .replaceAll("涤纶", "ポリエステル").replaceAll("粘纤", "ビスコース繊維")
                .replaceAll("腈纶", "アクリル繊維").replaceAll("灯芯绒", "コーデュロイ")
                .replaceAll("安哥拉", "アンゴラ").replaceAll("羊绒", "カシミヤ")
                .replaceAll("丝绸", "シルク").replaceAll("人造丝", "レーヨン")
                .replaceAll("亚麻布", "リネン").replaceAll("丙烯酸纤维", "アクリル")
                .replaceAll("天丝", "テンセル").replaceAll("莱赛尔", "テンセル")
                .replaceAll("天丝绒", "テンセル").replaceAll("华尔缎", "サテン")
                .replaceAll("天丝棉", "テンセルコットン").replaceAll("金丝绒", "ゴールドベルベット")
                .replaceAll("仿丝绸", "イミテーションシルク").replaceAll("粘胶", "ビスコース")
                .replaceAll("棉", "綿").replaceAll("羊毛", "ウール")
                .replaceAll("桑蚕丝", "マルベリーシルク").replaceAll("醋酯纤维", "アセテート繊維")
                .replaceAll("锦纶", "ナイロン").replaceAll("丙纶", "ポリプロピレン")
                .replaceAll("氨纶", "スパンデックス").replaceAll("交织麻织物", "交織リネン")
                .replaceAll("薄花呢", "ツイード").replaceAll("长毛绒", "プラッシュ")
                .replaceAll("牛津布", "オックスフォード生地").replaceAll("聚脂纤维", "ポリエステル")
                .replaceAll("蕾丝", "レース").replaceAll("雪纺", "シフォン")
                .replaceAll("綿类混纺", "混紡（ブレンド）生地").replaceAll("针织", "ニット")
                .replaceAll("混纺", "混紡（ブレンド）生地").replaceAll("棉纶","コットンスパンデックス");

        return str;
    }

    //计算价格
    public static Integer setSalePrice(Integer salePrice) {

        Double a = new BigDecimal((((salePrice + 45 + 10) * 20) / 0.7)).setScale(0, BigDecimal.ROUND_UP).doubleValue();
        salePrice = a.intValue();
        return salePrice;
    }


    public static String getRandom26Alphabet(){
        //需要生成几位
        int n = 5;
        //最终生成的字符串
        String str = "";
        for (int i = 0; i < n; i++) {
            str = str + (char)(Math.random()*26+'a');
        }
        return str;

    }


}
