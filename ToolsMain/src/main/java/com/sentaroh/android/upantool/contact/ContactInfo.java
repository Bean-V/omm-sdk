package com.sentaroh.android.upantool.contact;

import java.io.Serializable;
import java.util.Objects;

/**
 * @filename:
 * @author: zhangzhijun/@date: 2020/12/22 11:47
 * @version： v1.0
 * @function：
 */
public class ContactInfo<T> implements Serializable {
    private int raw_contact_id;
    private String mimetype;
    private  T data1;
    private  T data2;
    private  T data3;
    private  T data4;
    private  T data5;
    private  T data6;
    private  T data7;
    private  T data8;
    private  T data9;
    private  T data10;
    private  T data11;
    private  T data12;
    private  T data13;
    private  T data14;
    private  T data15;

    public int getRaw_contact_id() {
        return raw_contact_id;
    }

    public void setRaw_contact_id(int raw_contact_id) {
        this.raw_contact_id = raw_contact_id;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public T getData1() {
        return data1;
    }

    public void setData1(T data1) {
        this.data1 = data1;
    }

    public T getData2() {
        return data2;
    }

    public void setData2(T data2) {
        this.data2 = data2;
    }

    public T getData3() {
        return data3;
    }

    public void setData3(T data3) {
        this.data3 = data3;
    }

    public T getData4() {
        return data4;
    }

    public void setData4(T data4) {
        this.data4 = data4;
    }

    public T getData5() {
        return data5;
    }

    public void setData5(T data5) {
        this.data5 = data5;
    }

    public T getData6() {
        return data6;
    }

    public void setData6(T data6) {
        this.data6 = data6;
    }

    public T getData7() {
        return data7;
    }

    public void setData7(T data7) {
        this.data7 = data7;
    }

    public T getData8() {
        return data8;
    }

    public void setData8(T data8) {
        this.data8 = data8;
    }

    public T getData9() {
        return data9;
    }

    public void setData9(T data9) {
        this.data9 = data9;
    }

    public T getData10() {
        return data10;
    }

    public void setData10(T data10) {
        this.data10 = data10;
    }

    public T getData11() {
        return data11;
    }

    public void setData11(T data11) {
        this.data11 = data11;
    }

    public T getData12() {
        return data12;
    }

    public void setData12(T data12) {
        this.data12 = data12;
    }

    public T getData13() {
        return data13;
    }

    public void setData13(T data13) {
        this.data13 = data13;
    }

    public T getData14() {
        return data14;
    }

    public void setData14(T data14) {
        this.data14 = data14;
    }

    public T getData15() {
        return data15;
    }

    public void setData15(T data15) {
        this.data15 = data15;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactInfo<?> that = (ContactInfo<?>) o;
        return getRaw_contact_id() == that.getRaw_contact_id() &&
                Objects.equals(getMimetype(), that.getMimetype()) &&
                Objects.equals(getData1(), that.getData1()) &&
                Objects.equals(getData2(), that.getData2()) &&
                Objects.equals(getData3(), that.getData3()) &&
                Objects.equals(getData4(), that.getData4()) &&
                Objects.equals(getData5(), that.getData5()) &&
                Objects.equals(getData6(), that.getData6()) &&
                Objects.equals(getData7(), that.getData7()) &&
                Objects.equals(getData8(), that.getData8()) &&
                Objects.equals(getData9(), that.getData9()) &&
                Objects.equals(getData10(), that.getData10()) &&
                Objects.equals(getData11(), that.getData11()) &&
                Objects.equals(getData12(), that.getData12()) &&
                Objects.equals(getData13(), that.getData13()) &&
                Objects.equals(getData14(), that.getData14()) &&
                Objects.equals(getData15(), that.getData15());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRaw_contact_id(), getMimetype(), getData1(), getData2(), getData3(), getData4(), getData5(), getData6(), getData7(), getData8(), getData9(), getData10(), getData11(), getData12(), getData13(), getData14(), getData15());
    }
}
