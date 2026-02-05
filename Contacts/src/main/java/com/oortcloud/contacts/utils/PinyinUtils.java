package com.oortcloud.contacts.utils;


import android.text.TextUtils;

import com.oortcloud.contacts.bean.Sort;
import com.oortcloud.contacts.bean.UserInfo;
import com.oortcloud.contacts.bean.omm.AttentionUser;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @ProjectName: omm-master
 * @FileName: PinyinUtils.java
 * @Function:
 * @Author: zhangzhijun / @CreateDate: 20/03/13 20:14
 * @Version: 1.0
 */
    public class PinyinUtils {
        private static PinyinComparator pinyinComparator = new PinyinComparator();
        /**
         * 获取拼音
         *
         * @param inputString
         * @return
         */
        private static String getPingYin(String inputString) {

            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            format.setVCharType(HanyuPinyinVCharType.WITH_V);

            char[] input = inputString.trim().toCharArray();
            String output = "";

            try {
                for (char curChar : input) {
                    if (Character.toString(curChar).matches("[\\u4E00-\\u9FA5]+")) {
                        String[] temp = PinyinHelper.toHanyuPinyinStringArray(curChar, format);
                        output += temp[0];
                    } else
                        output += Character.toString(curChar);
                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
            return output;
        }

        /**
         * 获取第一个字的拼音首字母
         * @param chinese
         * @return
         */
        private static String getFirstSpell(String chinese) {
            StringBuffer pinYinBF = new StringBuffer();
            char[] arr = chinese.toCharArray();

            HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
            defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            for (char curChar : arr) {
                if (curChar > 128) {
                    try {
                        String[] temp = PinyinHelper.toHanyuPinyinStringArray(curChar, defaultFormat);
                        if (temp != null) {
                            pinYinBF.append(temp[0].charAt(0));
                        }
                    } catch (BadHanyuPinyinOutputFormatCombination e) {
                        e.printStackTrace();
                    }
                } else {
                    pinYinBF.append(curChar);
                }
            }
            return pinYinBF.toString().replaceAll("\\W", "").trim();
        }

    /**
     * 转换拼音 比较排序
     * @param data
     * @return
     */
    public static List<UserInfo> sortUserIfo(List<UserInfo> data) {
        List<UserInfo> list =   data;
        String pinyin = null;
        for (int i = 0; i < data.size(); i++) {
            if (TextUtils.isEmpty(list.get(i).getOort_namepy())){
                //汉字转换成拼音
                pinyin = PinyinUtils.getPingYin(list.get(i).getOort_name());
            }else {
                pinyin = list.get(i).getOort_namepy();
            }
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                list.get(i).setLetters(sortString.toUpperCase());
            } else {
                list.get(i).setLetters("#");
            }

        }
//        Collections.sort(list, new PinyinComparator());
        return list;
    }
    /**
     * 转换拼音 比较排序
     * @param data
     * @return
     */
    public static List<AttentionUser> sortAtemtionUser(List<AttentionUser> data) {
        if (data != null){
            String pinyin;
            for (int i = 0; i < data.size(); i++) {
                if (!TextUtils.isEmpty(data.get(i).getRemarkName())){
                    //汉字转换成拼音
                    pinyin = PinyinUtils.getPingYin(data.get(i).getRemarkName());
                }else {
                    pinyin = getPingYin(data.get(i).getToNickname());
                }
                String sortString = pinyin.substring(0, 1).toUpperCase();

                // 正则表达式，判断首字母是否是英文字母
                if (sortString.matches("[A-Z]")) {
                    data.get(i).setLetters(sortString.toUpperCase());
                } else {
                    data.get(i).setLetters("#");
                }

            }
            Collections.sort(data, new PinyinComparator());
        }
        return data;
    }

    /**
     * 搜素
     * @param filterStr
     * @param data
     * @return
     */
    public static List filterSort(String filterStr ,List<Sort> data) {
        List<Sort> filterDateList = new ArrayList<>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = data;
        } else {
            filterDateList.clear();
            for (Sort sortModel : data) {
                if (sortModel instanceof UserInfo){
                    UserInfo   userInfo =  (UserInfo) sortModel;
                    String name = userInfo.getOort_name();
                    if (name.indexOf(filterStr) != -1 ||
                            PinyinUtils.getFirstSpell(name).startsWith(filterStr)
                            //不区分大小写
                            || PinyinUtils.getFirstSpell(name).toLowerCase().startsWith(filterStr)
                            || PinyinUtils.getFirstSpell(name).toUpperCase().startsWith(filterStr)
                    ) {
                        filterDateList.add(sortModel);
                    }
                }

            }
        }
        // 根据a-z进行排序
//        Collections.sort(filterDateList, pinyinComparator);
        return filterDateList;
    }
    /**
     * 搜素
     * @param filterStr
     * @param data
     * @return
     */
    public static List filterUserInfo(String filterStr ,List<UserInfo> data) {
        List<UserInfo> filterDateList = new ArrayList<>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = data;
        } else {
            filterDateList.clear();
            for (UserInfo sortModel : data) {
                    String name = sortModel.getOort_name();
                    if (name.indexOf(filterStr) != -1 ||
                            PinyinUtils.getFirstSpell(name).startsWith(filterStr)
                            //不区分大小写
                            || PinyinUtils.getFirstSpell(name).toLowerCase().startsWith(filterStr)
                            || PinyinUtils.getFirstSpell(name).toUpperCase().startsWith(filterStr)
                    ) {
                        filterDateList.add(sortModel);
                    }


            }
        }
        // 根据a-z进行排序
//        Collections.sort(filterDateList, pinyinComparator);
        return filterDateList;
    }
    /**
     * 搜素
     * @param filterStr
     * @param data
     * @return
     */
    public static List filterAttentionUser(String filterStr ,List<AttentionUser> data) {
        List<AttentionUser> filterDateList = new ArrayList<>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = data;
        } else {
            filterDateList.clear();
            String name;
            for (AttentionUser sortModel : data) {
                if (!TextUtils.isEmpty(sortModel.getRemarkName())){
                    name = sortModel.getRemarkName();
                }else {
                    name = sortModel.getToNickname();
                }
                if (name.indexOf(filterStr) != -1 ||
                        PinyinUtils.getFirstSpell(name).startsWith(filterStr)
                        //不区分大小写
                        || PinyinUtils.getFirstSpell(name).toLowerCase().startsWith(filterStr)
                        || PinyinUtils.getFirstSpell(name).toUpperCase().startsWith(filterStr)
                ) {
                    filterDateList.add(sortModel);
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        return filterDateList;
    }
    }
