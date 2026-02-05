package cn.com.jit.provider.lib;

import android.os.Bundle;
import android.text.TextUtils;

import java.util.Iterator;

public class Response {

    public int resultCode;

    public String message;

    public String userCredential;

    private String appCredential;

    private String version;

    private String packageName;

    private String messageId;

    private Bundle bundle;

    private Object userCredentialObject;

    private Object appCredentialObject;


    public Object getUserCredentialObject() {
        return userCredentialObject;
    }

    public void setUserCredentialObject(Object userCredentialObject) {
        this.userCredentialObject = userCredentialObject;
    }

    public Object getAppCredentialObject() {
        return appCredentialObject;
    }

    public void setAppCredentialObject(Object appCredentialObject) {
        this.appCredentialObject = appCredentialObject;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getAppCredential() {
        return appCredential;
    }

    public void setAppCredential(String appCredential) {
        this.appCredential = appCredential;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserCredential() {
        return userCredential;
    }

    public void setUserCredential(String userCredential) {
        this.userCredential = userCredential;
    }

    public String show(){
        if(bundle == null) {
            return null;
        } else {
            String content = "Bundle{";
            String key;
            for(Iterator var3 = bundle.keySet().iterator(); var3.hasNext();) {
                key = (String)var3.next();
                if(TextUtils.equals("userCredential",key) || TextUtils.equals("appCredential",key)){
                    userCredentialObject = bundle.get(key);
                }
                content = content + " " + key + " => " + bundle.get(key) + ";";
            }
            content = content + " }Bundle";
            return content;
        }
    }

    @Override
    public String toString() {
        return "Response{" +
                "resultCode=" + resultCode +
                ", message='" + message + '\'' +
                ", userCredential='" + userCredential + '\'' +
                ", appCredential='" + appCredential + '\'' +
                ", version='" + version + '\'' +
                ", packageName='" + packageName + '\'' +
                ", messageId='" + messageId + '\'' +
                '}';
    }
}
