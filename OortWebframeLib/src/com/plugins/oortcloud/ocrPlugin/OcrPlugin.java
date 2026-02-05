/**
 * PhoneGap is available under *either* the terms of the modified BSD license *or* the
 * MIT License (2008). See http://opensource.org/licenses/alphabetical for full text.
 *
 * Copyright (c) Matt Kane 2010
 * Copyright (c) 2011, IBM Corporation
 * Copyright (c) 2013, Maciej Nux Jaros
 */
package com.plugins.oortcloud.ocrPlugin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.idcard.CardInfo;
import com.idcard.TFieldID;
import com.idcard.TRECAPIImpl;
import com.idcard.TStatus;
import com.idcard.TengineID;
import com.turui.bank.ocr.CaptureActivity;
import com.ui.card.TRCardScan;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import proxy.Proxy;

/**
 * This calls out to the ocr and returns the result.
 *
 */
public class OcrPlugin extends CordovaPlugin {
    public static final int REQUEST_CODE = 0x0aaa;

    private static final String CANCELLED = "cancelled";
    private static final String FORMAT = "format";
    private static final String TEXT = "text";
    private static final String LOG_TAG = "ocr";

    private String [] permissions = { Manifest.permission.CAMERA };

    private JSONArray requestArgs;
    private CallbackContext callbackContext;

    private TengineID tengineID = TengineID.TUNCERTAIN;
    OCRFORBITMAP ocrforbitmap = new OCRFORBITMAP();
    OCRFORPATH ocrforpath = new OCRFORPATH();
    private TRECAPIImpl engineOcr = new TRECAPIImpl();

    /**
     * Constructor.
     */
    public OcrPlugin() {
    }

    /**
     * Executes the request.
     *
     * This method is called from the WebView thread. To do a non-trivial amount of work, use:
     *     cordova.getThreadPool().execute(runnable);
     *
     * To run on the UI thread, use:
     *     cordova.getActivity().runOnUiThread(runnable);
     *
     * @param action          The action to execute.
     * @param args            The exec() arguments.
     * @param callbackContext The callback context used when calling back into JavaScript.
     * @return                Whether the action was valid.
     *
     */
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
        this.requestArgs = args;
        Proxy.setProxyCordovaPlugin(this);
        if (action.equals("OcrScan")) {

            //android permission auto add
            if(!hasPermisssion()) {
              requestPermissions(0);
            } else {
              scan(args);
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Starts an intent to scan and decode a barcode.
     */
    public void scan(final JSONArray args) {

        final CordovaPlugin that = this;

        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                String time = engineOcr.TR_GetEngineTimeKey();
                TStatus tStatus = engineOcr.TR_StartUP(that.cordova.getActivity().getBaseContext(),time);
                if (tStatus == TStatus.TR_TIME_OUT ) {
                    Toast.makeText(that.cordova.getActivity().getBaseContext(), "引擎过期", Toast.LENGTH_SHORT).show();
                    return;
                }
                else  if (tStatus == TStatus.TR_FAIL) {
                    Toast.makeText(that.cordova.getActivity().getBaseContext(), "引擎初始化失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                //CaptureActivity.isOpenLog = true; //打开Log开关， 待需要时打开
                CaptureActivity.tengineID = TengineID.TIDCARD2;
                CaptureActivity.ShowCopyRightTxt = " ";
                Intent intentScan = new Intent(that.cordova.getActivity().getBaseContext(), CaptureActivity.class);
                intentScan.putExtra("engine", engineOcr);

                // add config as intent extras
                /*if (args.length() > 0) {

                    JSONObject obj;
                    JSONArray names;
                    String key;
                    Object value;

                    for (int i = 0; i < args.length(); i++) {

                        try {
                            obj = args.getJSONObject(i);
                        } catch (JSONException e) {
                            Log.i("CordovaLog", e.getLocalizedMessage());
                            continue;
                        }

                        names = obj.names();
                        for (int j = 0; j < names.length(); j++) {
                            try {
                                key = names.getString(j);
                                value = obj.get(key);

                                if (value instanceof Integer) {
                                    intentScan.putExtra(key, (Integer) value);
                                } else if (value instanceof String) {
                                    intentScan.putExtra(key, (String) value);
                                }

                            } catch (JSONException e) {
                                Log.i("CordovaLog", e.getLocalizedMessage());
                            }
                        }

                        intentScan.putExtra(Intents.Scan.CAMERA_ID, obj.optBoolean(PREFER_FRONTCAMERA, false) ? 1 : 0);
                        intentScan.putExtra(Intents.Scan.SHOW_FLIP_CAMERA_BUTTON, obj.optBoolean(SHOW_FLIP_CAMERA_BUTTON, false));
                        intentScan.putExtra(Intents.Scan.SHOW_TORCH_BUTTON, obj.optBoolean(SHOW_TORCH_BUTTON, false));
                        intentScan.putExtra(Intents.Scan.TORCH_ON, obj.optBoolean(TORCH_ON, false));
                        intentScan.putExtra(Intents.Scan.SAVE_HISTORY, obj.optBoolean(SAVE_HISTORY, false));
                        boolean beep = obj.optBoolean(DISABLE_BEEP, false);
                        intentScan.putExtra(Intents.Scan.BEEP_ON_SCAN, !beep);
                        if (obj.has(RESULTDISPLAY_DURATION)) {
                            intentScan.putExtra(Intents.Scan.RESULT_DISPLAY_DURATION_MS, "" + obj.optLong(RESULTDISPLAY_DURATION));
                        }
                        if (obj.has(FORMATS)) {
                            intentScan.putExtra(Intents.Scan.FORMATS, obj.optString(FORMATS));
                        }
                        if (obj.has(PROMPT)) {
                            intentScan.putExtra(Intents.Scan.PROMPT_MESSAGE, obj.optString(PROMPT));
                        }
                        if (obj.has(ORIENTATION)) {
                            intentScan.putExtra(Intents.Scan.ORIENTATION_LOCK, obj.optString(ORIENTATION));
                        }
                    }

                }*/

                // avoid calling other phonegap apps
                intentScan.setPackage(that.cordova.getActivity().getApplicationContext().getPackageName());

                that.cordova.startActivityForResult(that, intentScan, REQUEST_CODE);
            }
        });
    }

    /**
     * Called when the barcode scanner intent completes.
     *
     * @param requestCode The request code originally supplied to startActivityForResult(),
     *                       allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param intent      An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_CODE && this.callbackContext != null) {

            if (intent == null) {
                return;
            }
            CardInfo cardInfo = (CardInfo) intent.getSerializableExtra("cardinfo");
            if (resultCode == CaptureActivity.RESULT_SCAN_BANK_OK) {
                if (CaptureActivity.tengineID == TengineID.TIDBANK) {
                    Bitmap small = CaptureActivity.SmallBitmap;// 银行卡小图
                    Bitmap takeimg = CaptureActivity.TakeBitmap;// 扫描识别的全图
//                    imageView.setImageBitmap(small);
                }
                String result = cardInfo.getAllinfo();
                if (cardInfo.GetError() != null) result += "error=" + cardInfo.GetError();
//                textView.setText("银行卡扫描结果\n" +result);
            }
            else if (resultCode == CaptureActivity.RESULT_SCAN_IDCAD_OK) {
                Bitmap small = CaptureActivity.SmallBitmap;// 身份证人头像
                Bitmap takeimg = CaptureActivity.TakeBitmap;// 扫描识别的全图
//                imageView.setImageBitmap(small);

                String result = cardInfo.getAllinfo();
                String name = cardInfo.getFieldString(TFieldID.NAME);
                String sex = cardInfo.getFieldString(TFieldID.SEX);
                String flok = cardInfo.getFieldString(TFieldID.FOLK);
                String birthday = cardInfo.getFieldString(TFieldID.BIRTHDAY);
                String address = cardInfo.getFieldString(TFieldID.ADDRESS);
                String idcard = cardInfo.getFieldString(TFieldID.NUM);
                String issue = cardInfo.getFieldString(TFieldID.ISSUE);
                String period = cardInfo.getFieldString(TFieldID.PERIOD);
                Log.e("RESULT_SCAN_IDCAD_OK",name + "  "+ sex +"  " + flok + "  "+ birthday + "  "+ address + "  "+ idcard + "  "+ issue + "  "+ period );
                if (cardInfo.GetError() != null) result += "error=" + cardInfo.GetError();
//                textView.setText("身份证扫描结果\n" + result);
                JSONObject obj = new JSONObject();
                try {
                    obj.put("code", 200);  //返回码
                    obj.put("msg", "成功"); //返回消息提示
                    obj.put("error", cardInfo.GetError()); // 扫描错误信息
                    obj.put("name", name);  //姓名
                    obj.put("sex", sex);  //性别
                    obj.put("flok", flok); // 民族
                    obj.put("birthday", birthday); //出生日期
                    obj.put("address", address); // 地址
                    obj.put("idcard", idcard); // 身份证号
                    obj.put("issue", issue); // 发证机关
                    obj.put("period", period); //有效期
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
                this.callbackContext.success(obj);
            }
            else if (resultCode == CaptureActivity.RESULT_SCAN_CANCLE) {
//                textView.setText("扫描点击返回或者引擎过期\n");
//                imageView.setImageBitmap(null);
                JSONObject obj = new JSONObject();
                try {
                    obj.put("code", 201);  //返回码
                    obj.put("msg", "取消扫描"); //返回消息提示
                    obj.put("error", "扫描点击返回或者引擎过期"); // 扫描错误信息
                    obj.put("name", "");  //姓名
                    obj.put("sex", "");  //性别
                    obj.put("flok", ""); // 民族
                    obj.put("birthday", ""); //出生日期
                    obj.put("address", ""); // 地址
                    obj.put("idcard", ""); // 身份证号
                    obj.put("issue", ""); // 发证机关
                    obj.put("period", ""); //有效期
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
                this.callbackContext.success(obj);
            }
            else if (resultCode == TRCardScan.RESULT_GET_CARD_OK) {
                //获取单个栏目识别信息
                //textView.setText(cardInfo.getFieldString(TFieldID.NUM));// 以此类推
                // 获取人头像
//                Bitmap headimg = TRCardScan.HeadImgBitmap;
//                Bitmap Takeimg = TRCardScan.TakeBitmap;
//                imageView.setImageBitmap(headimg);

                String result = cardInfo.getAllinfo();
                String name = cardInfo.getFieldString(TFieldID.NAME);
                String sex = cardInfo.getFieldString(TFieldID.SEX);
                String flok = cardInfo.getFieldString(TFieldID.FOLK);
                String birthday = cardInfo.getFieldString(TFieldID.BIRTHDAY);
                String address = cardInfo.getFieldString(TFieldID.ADDRESS);
                String idcard = cardInfo.getFieldString(TFieldID.NUM);
                String issue = cardInfo.getFieldString(TFieldID.ISSUE);
                String period = cardInfo.getFieldString(TFieldID.PERIOD);
                Log.e("RESULT_GET_CARD_OK",name + "  "+ sex +"  " + flok + "  "+ birthday + "  "+ address + "  "+ idcard + "  "+ issue + "  "+ period );
                if (cardInfo.GetError() != null) result += "error=" + cardInfo.GetError();
//                textView.setText("身份证扫描结果\n" + result);
                JSONObject obj = new JSONObject();
                try {
                    obj.put("code", 200);  //返回码
                    obj.put("msg", "成功"); //返回消息提示
                    obj.put("error", cardInfo.GetError()); // 扫描错误信息
                    obj.put("name", name);  //姓名
                    obj.put("sex", sex);  //性别
                    obj.put("flok", flok); // 民族
                    obj.put("birthday", birthday); //出生日期
                    obj.put("address", address); // 地址
                    obj.put("idcard", idcard); // 身份证号
                    obj.put("issue", issue); // 发证机关
                    obj.put("period", period); //有效期
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
                this.callbackContext.success(obj);

            }
            else if (resultCode == TRCardScan.RESULT_GET_CARD_CANCLE) {
//                textView.setText("拍照点击返回或者引擎过期\n");
//                imageView.setImageBitmap(null);
                JSONObject obj = new JSONObject();
                try {
                    obj.put("code", 202);  //返回码
                    obj.put("msg", "取消拍照"); //返回消息提示
                    obj.put("error", "拍照点击返回或者引擎过期"); // 扫描错误信息
                    obj.put("name", "");  //姓名
                    obj.put("sex", "");  //性别
                    obj.put("flok", ""); // 民族
                    obj.put("birthday", ""); //出生日期
                    obj.put("address", ""); // 地址
                    obj.put("idcard", ""); // 身份证号
                    obj.put("issue", ""); // 发证机关
                    obj.put("period", ""); //有效期
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
                this.callbackContext.success(obj);
            } else {
                //this.error(new PluginResult(PluginResult.Status.ERROR), this.callback);
                this.callbackContext.error("Unexpected error");
            }
        }
    }


    /**
     * check application's permissions
     */
   public boolean hasPermisssion() {
       for(String p : permissions)
       {
           if(!PermissionHelper.hasPermission(this, p))
           {
               return false;
           }
       }
       return true;
   }

    /**
     * We override this so that we can access the permissions variable, which no longer exists in
     * the parent class, since we can't initialize it reliably in the constructor!
     *
     * @param requestCode The code to get request action
     */
   public void requestPermissions(int requestCode)
   {
       PermissionHelper.requestPermissions(this, requestCode, permissions);
   }

   /**
   * processes the result of permission request
   *
   * @param requestCode The code to get request action
   * @param permissions The collection of permissions
   * @param grantResults The result of grant
   */
  public void onRequestPermissionResult(int requestCode, String[] permissions,
                                         int[] grantResults) throws JSONException
   {
       PluginResult result;
       for (int r : grantResults) {
           if (r == PackageManager.PERMISSION_DENIED) {
               Log.d(LOG_TAG, "Permission Denied!");
               result = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
               this.callbackContext.sendPluginResult(result);
               return;
           }
       }

       switch(requestCode)
       {
           case 0:
               scan(this.requestArgs);
               break;
       }
   }

    /**
     * This plugin launches an external Activity when the camera is opened, so we
     * need to implement the save/restore API in case the Activity gets killed
     * by the OS while it's in the background.
     */
    public void onRestoreStateForActivityResult(Bundle state, CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
    }

    public void onDestroy() {
        engineOcr.TR_ClearUP();
        super.onDestroy();
    }
}
