package con.chin.service.impl;

import con.chin.mapper.PurchasingItemMapper;
import con.chin.mapper.SizeAndOptionMapper;
import con.chin.pojo.Item;
import con.chin.pojo.PurchasingItem;
import con.chin.pojo.SizeAndOption;
import con.chin.service.PurchasingItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
@Transactional
public class PurchasingItemServiceImpl implements PurchasingItemService {

    @Autowired
    private PurchasingItemMapper purchasingItemMapper;

    @Autowired
    private SizeAndOptionMapper sizeAndOptionMapper;

    //保存产品
    @Override
    public int savePurchasingItem(PurchasingItem purchasingItem) {
        //开始时间
        long start = System.currentTimeMillis();
        //当前时间
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        //option1有数据时
        if (purchasingItem.getOption1() != null && purchasingItem.getOption1() != "") {
            //以空格分装成数组
            String[] Values = purchasingItem.getValue1().split(" ");
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
                SizeAndOption sizeAndOtionByOptionValue = sizeAndOptionMapper.findSizeAndOtionByOptionValue(Value);
                //有数据
                if (sizeAndOtionByOptionValue != null) {
                    //optionName还为空时 为了之赋值一次
                    if (optionName == "") {
                        //每一个的value是否有日文翻译 相当于已经存在
                        if (sizeAndOtionByOptionValue.getOptionNameJapanese() != null && sizeAndOtionByOptionValue.getOptionNameJapanese() != "") {
                            optionName = sizeAndOtionByOptionValue.getOptionNameJapanese();
                        }
                    }
                    //有日文翻译
                    if (sizeAndOtionByOptionValue.getOptionValueJapanese() != null && sizeAndOtionByOptionValue.getOptionValueJapanese() != "") {
                        optionValue += sizeAndOtionByOptionValue.getOptionValueJapanese() + " ";
                    } else {
                        optionValue += Value + " ";
                    }
                    //更新
//                    sizeAndOptionMapper.updateSizeAndOption(sizeAndOption);
                    //没有数据时
                } else {
                    //中文名称保存
                    optionValue = purchasingItem.getOption1();
                    sizeAndOption.setOptionNameChinese(optionValue);
                    //中文名称保存
                    sizeAndOption.setOptionValueChinese(Value);
                    //保存SizeAndOption
                    sizeAndOptionMapper.saveSizeAndOption(sizeAndOption);
                }

            }
            //保存
            //optionName 不为空时
            if (optionName != null && optionName != "") {
                purchasingItem.setOption1(optionName);
            }
            //把最后的空格去掉
            if(optionValue != null && optionValue != ""){
                optionValue = optionValue.substring(0, optionValue.lastIndexOf(" "));
                purchasingItem.setValue1(optionValue);
            }else {
                optionValue = purchasingItem.getValue1();
                optionValue = optionValue.substring(0, optionValue.lastIndexOf(" "));
                purchasingItem.setValue1(optionValue);
            }
        }
        //option2有数据时
        if (purchasingItem.getOption2() != null && purchasingItem.getOption2() != "") {
            //以空格分装成数组
            String[] Values = purchasingItem.getValue2().split(" ");
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
                SizeAndOption sizeAndOtionByOptionValue = sizeAndOptionMapper.findSizeAndOtionByOptionValue(Value);
                //有数据
                if (sizeAndOtionByOptionValue != null) {
                    //optionName还为空时 为了之赋值一次
                    if (optionName == "") {
                        //每一个的value是否有日文翻译 相当于已经存在
                        if (sizeAndOtionByOptionValue.getOptionNameJapanese() != null && sizeAndOtionByOptionValue.getOptionNameJapanese() != "") {
                            optionName = sizeAndOtionByOptionValue.getOptionNameJapanese();
                        }
                    }
                    //有日文翻译
                    if (sizeAndOtionByOptionValue.getOptionValueJapanese() != null && sizeAndOtionByOptionValue.getOptionValueJapanese() != "") {
                        optionValue += sizeAndOtionByOptionValue.getOptionValueJapanese() + " ";
                    } else {
                        optionValue += Value + " ";
                    }
                    //更新
//                    sizeAndOptionMapper.updateSizeAndOption(sizeAndOption);
                    //没有数据时
                } else {
                    //中文名称保存
                    sizeAndOption.setOptionNameChinese(purchasingItem.getOption2());
                    //中文名称保存
                    sizeAndOption.setOptionValueChinese(purchasingItem.getValue2());
                    //保存SizeAndOption
                    sizeAndOptionMapper.saveSizeAndOption(sizeAndOption);
                }

            }
            //保存
            //optionName 不为空时
            if (optionName != null && optionName != "") {
                purchasingItem.setOption2(optionName);
            }
            //把最后的空格去掉
            if(optionValue != null && optionValue != ""){
                optionValue = optionValue.substring(0, optionValue.lastIndexOf(" "));
                purchasingItem.setValue2(optionValue);
            }else {
                optionValue = purchasingItem.getValue2();
                optionValue = optionValue.substring(0, optionValue.lastIndexOf(" "));
                purchasingItem.setValue2(optionValue);
            }
        }
        //旧code检索
        PurchasingItem oldItem = purchasingItemMapper.findPurchasingItem(purchasingItem.getOldItemCode());

        //保存产品数据
        if (oldItem == null) {
            int res = purchasingItemMapper.savePurchasingItem(purchasingItem);
            long end = System.currentTimeMillis();
            System.out.println("登录一件产品:    " + purchasingItem.getItemCode() + "   时间为 : " + now + "    耗时：" + (end - start) + " ms");
            return res;
        }
        //更新产品数据
        //如果是更新并且itemimage是空的就赋值进去
        if (oldItem.getImage() == null) {
            purchasingItem.setImage("/images/itemphoto/" + oldItem.getItemCode() + ".jpg");
        }
        //更新時間
        if (oldItem.getUpdatetime() == null) {
            purchasingItem.setUpdatetime(now);
        }
        //商品情報更新
        purchasingItemMapper.updatePurchasingItem(purchasingItem);
        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("更新一件产品:    " + oldItem.getItemCode() + "   时间为 : " + now + "    耗时：" + (end - start) + " ms");
        //为了更新的话不需要更新照片
        return -1;
    }
}
