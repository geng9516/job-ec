package con.chin.util;

import con.chin.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class RandomGetRelevantLinksUtil {

    private static RandomGetRelevantLinksUtil randomGet;

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
        if(relevantLinksList.size() == 1){
            return null;
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
                relevantLinks += s;
            }
        }else{
            //数据库中一个种类的产品超过20个的时候,随机取出20个放到set集合里
            for (int i = 0; i < 20; i++) {
                //List中的元素随机添加
                set.add(relevantLinksList.get(rand.nextInt(relevantLinksList.size())) + " ");
            }
            //随机到set中去重后个数不满20的话
            for (int i = set.size(); i <= 20; i++) {
                set = addRelevantLinks(relevantLinksList, set);
            }
            //Set集合遍历拼接
            for (String s : set) {
                //推荐产品的itemcode多个拼接保存
                relevantLinks += s;
            }
        }
        //返回结果
        return relevantLinks.substring(0, relevantLinks.length()-1);
    }

    //随机到set中去重后个数不满20的话
    public static Set<String> addRelevantLinks(List<String> relevantLinksList, Set<String> set) {
        Random rand = new Random();
        //Set集合不到20个的空缺的进行填充,最多20个
        for (int i = set.size(); i <= 20; i++) {
            //List中的元素随机添加
            set.add(relevantLinksList.get(rand.nextInt(relevantLinksList.size())) + " ");
        }
        //返回结果
        return set;
    }


}
