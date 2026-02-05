package com.oortcloud.debug;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;


import com.oortcloud.permission.PermissionListener;
import com.oortcloud.permission.PermissionRequestActivity;
import com.oortcloud.utils.Base64Utils;
import com.oortcloud.utils.FileUtils;
import com.oortcloud.utils.MimeUtils;
import com.oort.weichat.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;


public class ChoiceFileActivity extends BaseActivity {
	private static final String ACTION_PHOTO = "choice_file_activity";
	private TextView tv_choice_file;
	static final String[] PERMISSION = new String[]{
			Manifest.permission.WRITE_EXTERNAL_STORAGE,// 写入权限
			Manifest.permission.READ_EXTERNAL_STORAGE, // 读取权限
			Manifest.permission.READ_PHONE_STATE};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chioce_file);
		tv_choice_file = (TextView) findViewById(R.id.tv_choice_file);
		tv_choice_file.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("*/*");// 设置类型，我这里是任意类型，任意后缀的可以这样写。
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				startActivityForResult(intent, 1);
			}
		});
		Intent intent = getIntent();
		if (intent != null && ACTION_PHOTO.equals(intent.getAction())) {
//			Intent intentGetContent = new Intent(Intent.ACTION_GET_CONTENT);
//			intentGetContent.setType("*/*");// 设置类型，我这里是任意类型，任意后缀的可以这样写。
//			intentGetContent.addCategory(Intent.CATEGORY_OPENABLE);
//			startActivityForResult(intentGetContent, 1);

			if (Build.VERSION.SDK_INT >= 23) {
				PermissionRequestActivity.startActivityForResult(this, 101, PERMISSION, new PermissionListener() {
					@Override
					public void onGranted() {
						Intent selectIntent = new Intent(Intent.ACTION_GET_CONTENT);
						Intent wrapperIntent = Intent.createChooser(selectIntent, "Select Picture");
						selectIntent.addCategory(Intent.CATEGORY_OPENABLE);
						selectIntent.setType("*/*");
						startActivityForResult(wrapperIntent, 1);
					}

					@Override
					public void onDenied(List<String> deniedPermission) {
						Toast.makeText(ChoiceFileActivity.this, "请打开存储权限", Toast.LENGTH_SHORT).show();
					}
				});
			} else {
				Intent selectIntent = new Intent(Intent.ACTION_GET_CONTENT);
				Intent wrapperIntent = Intent.createChooser(selectIntent, "Select Picture");
				selectIntent.addCategory(Intent.CATEGORY_OPENABLE);
				selectIntent.setType("*/*");
				startActivityForResult(wrapperIntent, 1);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {// 是否选择，没选择就不会继续
			Uri uri = data.getData();// 得到uri，后面就是将uri转化成file的过程。
			String path = FileUtils.getFilePathByUri(this, uri);
//			String path = getFilePath(this, uri);
			//Log.e("ss", "pathaa: " + pathaa);

            if (path == null) {
                path = getFilePath(this, uri);
            }
            if (path == null){
                return;
            }

			String mimeType = MimeUtils.getMIMEType(new File(path));


			String[] names;
			String[] values;

			if (mimeType.startsWith("image")) {
				int[] size = Base64Utils.getBitmapSize(path);
				names = new String[] { "file_path", "mime_type", "base64Data", "width", "height"};
				values = new String[] { path , mimeType, Base64Utils.imageToBase64(path),
						String.valueOf(size[0]), String.valueOf(size[1])};
			} else if (mimeType.startsWith("video")){
				String thumbPath = getVideoThumbnails(this, path);
				int[] size = Base64Utils.getBitmapSize(thumbPath);
				names = new String[] { "file_path", "mime_type", "base64Data", "width", "height"};
				values = new String[] { path, mimeType, TextUtils.isEmpty(thumbPath) ? "" : Base64Utils.imageToBase64(thumbPath),
						String.valueOf(size[0]), String.valueOf(size[1])};
			} else {
				names = new String[] { "file_path", "mime_type"};
				values = new String[] { path, mimeType};
			}
			JSONObject json = new JSONObject();
			try {
				for (int i = 0; i < names.length; i++) {
					json.put(names[i], values[i]);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.i("choiceFile--->", "onActivityResult: " + json.toString());
			iOortPluginCallback.onOortFrameCallback(json.toString());
			finish();
		}else if (resultCode == Activity.RESULT_CANCELED) {
			finish();
		}
	}

	/**
	 * 获取视频的缩略图路径
	 * @param context
	 * @param targetPath
	 * @return
	 */
	public String getVideoThumbnails(Context context, String targetPath) {
		Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
				new String[]{MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA},
				MediaStore.Video.Media.DATA  + "=?", new String[]{targetPath}, null);

		if (cursor == null) {
			return null;
		}
		String thumbPath = null;

		if (cursor.moveToFirst()) {
			int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
			cursor.close();

			Cursor thumbCursor = context.getContentResolver().query(
					MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
					new String[]{MediaStore.Video.Media.DATA},
					MediaStore.Video.Thumbnails.VIDEO_ID
							+ "=" + id, null, null);
			if (thumbCursor.moveToFirst()) {
				thumbPath = thumbCursor.getString(thumbCursor
						.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
			}
			thumbCursor.close();
		}
		return thumbPath;
	}

	public static String getFilePath(Context context, Uri uri) {

		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;

			try {
				cursor = context.getContentResolver().query(uri, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index);
				}
			} catch (Exception e) {
				// Eat it
			}
		}

		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}
}
