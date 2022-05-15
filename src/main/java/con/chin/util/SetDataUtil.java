package con.chin.util;

import con.chin.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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

    //推荐产品的itemcode追加
    public static String getRelevantLinks(String path) {

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
        str = str.substring(0, str.lastIndexOf(" "));
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
    }

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


}
