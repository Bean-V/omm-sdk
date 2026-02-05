package koal.cert.tools;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author lijh
 * @date 2017/11/23
 * @email lijh@koal.com
 *
 * 封装byte数组返回参数
 */

public class ByteBuf implements Parcelable {

    private byte[] _byte;

    public ByteBuf() {
       /*
        * 默认初始化对象。防止AIDL接口的异常，覆盖原始异常错误。
        * */
        _byte = new byte[1];
    }

    public ByteBuf(Parcel in) {
        readFromParcel(in);
    }


    public byte[] getByte() {
        return _byte;
    }

    public void setByte(byte[] _byte) {
        this._byte = _byte;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_byte.length);
        dest.writeByteArray(_byte);
    }

    public void readFromParcel(Parcel in) {
        _byte = new byte[in.readInt()];
        in.readByteArray(_byte);
    }

    public static final Creator CREATOR = new Creator() {
        public ByteBuf createFromParcel(Parcel in) {
            return new ByteBuf(in);
        }

        public ByteBuf[] newArray(int size) {
            return new ByteBuf[size];
        }
    };
}
