package com.oortcloud.contacts.utils;

import com.oortcloud.contacts.bean.Sort;

import java.util.Comparator;

/**
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/7/1 16:26
 */
public class SortComparator implements Comparator<Sort> {

    public int compare(Sort o1, Sort o2) {
       return o1.getSort() >= o2.getSort() ? 1 : -1;

    }


}
