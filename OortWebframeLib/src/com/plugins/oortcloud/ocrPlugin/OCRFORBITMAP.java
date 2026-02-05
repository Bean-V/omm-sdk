package com.plugins.oortcloud.ocrPlugin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.idcard.TFieldID;
import com.idcard.TParam;
import com.idcard.TRECAPIImpl;
import com.idcard.TStatus;
import com.idcard.TengineID;

public class OCRFORBITMAP {
	void OCRForBitMap565(Bitmap bitmap565,String TimeKey)
	{
		TRECAPIImpl trecapiImpl = new TRECAPIImpl();
		TStatus tStatus = TStatus.TR_FAIL;
		// 打开引擎
		tStatus = trecapiImpl.TR_StartUP(null,TimeKey);
		if (tStatus == TStatus.TR_TIME_OUT) {
			System.out.print("引擎过期\n");
			return;
		}

		// 设置引擎参数
		tStatus = trecapiImpl.TR_SetParam(TParam.T_SET_HEADIMG, 1);// 打开人头像功能
		tStatus = trecapiImpl.TR_SetSupportEngine(TengineID.TIDCARD2);// 打开引擎类型，测试是用身份证， 其他证件以此类推
		if (tStatus != TStatus.TR_OK) {
			System.out.print("引擎不支持\n");
			return;
		}
		// 加载图片到引擎
		tStatus = trecapiImpl.TR_LoadMemBitMap(bitmap565);
		if (tStatus != TStatus.TR_OK) {
			System.out.print("引擎加载图片失败\n");
			return;
		}
		// 引擎识别
		tStatus = trecapiImpl.TR_RECOCR();
		// 释放图片内存
		trecapiImpl.TR_FreeImage();// 有加载图片必须释放图片内存
		// 获取识别结果
		String allinfo = trecapiImpl.TR_GetOCRStringBuf(); // 获取所有识别信息
		String name = trecapiImpl.TR_GetOCRFieldStringBuf(TFieldID.NAME);// 根据对应的字段获取单个栏目信息， 获取其他信息， 以此类推
		System.out.print("name ----\n" +name +"\n");
		// 只有在识别身份证正面时候， 才可以获取人头像信息
		byte []hdata = trecapiImpl.TR_GetHeadImgBuf();
		int size =  trecapiImpl.TR_GetHeadImgBufSize();
		if (size > 0 && hdata != null && hdata.length > 0) {
			Bitmap HeadImgBitmap = BitmapFactory.decodeByteArray(hdata,0,size);
		}
		// 释放整个引擎内存
		trecapiImpl.TR_ClearUP();

	}
}
