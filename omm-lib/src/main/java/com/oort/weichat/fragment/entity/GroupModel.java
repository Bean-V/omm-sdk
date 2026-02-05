package com.oort.weichat.fragment.entity;


import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.ModuleInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Depiction:
 * Author: teach
 * Date: 2017/3/20 15:51
 */
public class GroupModel {

    /**
     * 获取组列表数据
     *
     * @param groupCount    组数量
     * @param childrenCount 每个组里的子项数量
     * @return
     */
    public static ArrayList<GroupEntity> getGroups(int groupCount, int childrenCount) {
        ArrayList<GroupEntity> groups = new ArrayList<>();
        for (int i = 0; i < groupCount; i++) {
            ArrayList<ChildEntity> children = new ArrayList<>();
            for (int j = 0; j < childrenCount; j++) {
                children.add(new ChildEntity("第" + (i + 1) + "组第" + (j + 1) + "项"));
            }
            groups.add(new GroupEntity("第" + (i + 1) + "组头部",
                    "第" + (i + 1) + "组尾部", children));
        }
        return groups;
    }
    public static ArrayList<GroupEntity> getAppGroups(List datas) {

        List apps = new ArrayList();
        apps.addAll(datas);

        ArrayList<GroupEntity> groups = new ArrayList<>();
        for (Object o : apps) {

            ModuleInfo type = (ModuleInfo) o;
            ArrayList<ChildEntity> children = new ArrayList<>();
            if(type.getApp_list() != null) {
                for (Object o1 : type.getApp_list()) {

                    AppInfo app = (AppInfo) o1;
                    children.add(new ChildEntity(app.getApplabel(), app.getIcon_url(),app));
                }
            }
            groups.add(new GroupEntity(type.getModule_name(),"",children));
        }
        return groups;
    }



    /**
     * 获取可展开收起的组列表数据(默认展开)
     *
     * @param groupCount    组数量
     * @param childrenCount 每个组里的子项数量
     * @return
     */
    public static ArrayList<ExpandableGroupEntity> getExpandableGroups(int groupCount, int childrenCount) {
        ArrayList<ExpandableGroupEntity> groups = new ArrayList<>();
        for (int i = 0; i < groupCount; i++) {
            ArrayList<ChildEntity> children = new ArrayList<>();
            for (int j = 0; j < childrenCount; j++) {
                children.add(new ChildEntity("第" + (i + 1) + "组第" + (j + 1) + "项"));
            }
            groups.add(new ExpandableGroupEntity("第" + (i + 1) + "组头部",
                    "第" + (i + 1) + "组尾部", true, children));
        }
        return groups;
    }

}
