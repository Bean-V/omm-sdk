package com.oortcloud.contacts.utils;


import com.oortcloud.contacts.bean.Letters;

import java.util.Comparator;

/**
 * @ProjectName: omm-master
 * @FileName: PinyinComparator.java
 * @Function: 比较器
 * @Author: zhangzhijun / @CreateDate: 20/03/14 09:40

 * @Version: 1.0
 */

    public class PinyinComparator implements Comparator<Letters> {

        public int compare(Letters o1, Letters o2) {
            if (o1.getLetters().equals("@")
                    || o2.getLetters().equals("#")) {
                return -1;
            } else if (o1.getLetters().equals("#")
                    || o2.getLetters().equals("@")) {
                return 1;
            } else {
                return o1.getLetters().compareTo(o2.getLetters());
            }
        }

    }

