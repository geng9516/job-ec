package con.chin.util;

import java.util.regex.Pattern;

public class FlogUtil {

    /*
     * 判断是否为整数
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */
    public static boolean isInteger(String str) {

        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");

        return pattern.matcher(str).matches();

    }


    //判断是哪个网站
    public static Integer getUri(String url){
        //yahoo
        if(url.contains("store.shopping.yahoo.co.jp")){
            return 1;
            //搜款网
        } else if(url.contains("www.vvic.com")){
            return 2;
            //17
        } else if(url.contains("gz.17zwd.com")) {
            return 3;
            //包牛牛
        }else if(url.contains("bao66.cn")) {
            return 4;
        }else {
            return 0;
        }
    }
}
