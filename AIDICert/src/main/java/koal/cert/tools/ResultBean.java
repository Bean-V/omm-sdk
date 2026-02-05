package koal.cert.tools;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 作用：
 * 用于证书管理接口返回值。
 *
 * 对应接口文件：
 * ICertOperSync.aidl
 *
 * 说明：
 * 调用成功：errorCode == 0
 * 调用失败：errorCode == 1 ；message 为错误消息 ；detail 为错误详细日志
 * 其他：errorCode == 见"ResultBean.aidl"。
 * 用于单个证书操作被中断时，当前服务端的证书状态，用户可根据该状态判断如何下一步操作
 *
 * Created by liuwb on 2017/11/1.
 */

public final class ResultBean<T extends Serializable> implements Parcelable {

    public static final int OPER_SUC = 0;
    public static final int OPER_FAIL = 1;

    // 错误代码
    private int errorCode;

    // 错误消息
    private String message;

    // 错误详细详细
    private String detail;

    //
    private T data;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.errorCode);
        dest.writeString(this.message);
        dest.writeString(this.detail);
        dest.writeSerializable(this.data);
    }

    public ResultBean() {
        this.errorCode = OPER_FAIL;
        this.message = "default message";
        this.detail = "default detail";
    }

    protected ResultBean(Parcel in) {
        this.errorCode = in.readInt();
        this.message = in.readString();
        this.detail = in.readString();
        this.data= (T) in.readSerializable();
    }

    public static final Creator<ResultBean> CREATOR = new Creator<ResultBean>() {
        @Override
        public ResultBean createFromParcel(Parcel source) {
            return new ResultBean(source);
        }

        @Override
        public ResultBean[] newArray(int size) {
            return new ResultBean[size];
        }
    };
}
