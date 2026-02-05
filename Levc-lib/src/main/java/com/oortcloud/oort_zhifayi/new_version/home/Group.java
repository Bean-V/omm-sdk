package com.oortcloud.oort_zhifayi.new_version.home;

import java.util.List;

public class Group {
    private String name;
    private String info;
    private String status;
    private List<String> avatarIds;

    public Group(String name, String info, String status, List<String> avatarIds) {
        this.name = name;
        this.info = info;
        this.status = status;
        this.avatarIds = avatarIds;
    }

    public String getName() { return name; }
    public String getInfo() { return info; }
    public String getStatus() { return status; }
    public List<String> getAvatarIds() { return avatarIds; }
}
