package com.sentaroh.android.upantool.contact;

/**
 * 通讯录实体类
 */
public class MyContact {
    public String name;
    public String phone;
    public String note;
    public String photo;


    @Override
    public String toString() {
        return "MyContacts{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", note='" + note + '\'' +
                ", photo=" + photo +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {

        this.note = note;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

}
