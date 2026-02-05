package com.oortcloud.contacts.utils.omm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.request.target.Target;
import com.oortcloud.basemodule.utils.SkinUtils;
import com.oortcloud.contacts.R;

import java.util.ArrayList;
import java.util.List;

import static com.oortcloud.basemodule.constant.Constant.IM_DOWN_URL;

/**
 * @ProjectName: omm-master
 * @FileName: AvatarHelper.java
 * @Function:
 * @Author: zhangzhijun / @CreateDate: 20/03/17 10:42
 * @Version: 1.0
 */
public class AvatarHelper {
    public static String getAvatarUrl(String userId, boolean isThumb) {

        if (TextUtils.isEmpty(userId) || userId.length() > 8) {
            return null;
        }

        int userIdInt = -1;
        try {
            userIdInt = Integer.parseInt(userId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (userIdInt == -1 || userIdInt == 0) {
            return null;
        }

        int dirName = userIdInt % 10000;
        String url = null;
        if (isThumb) {
            url = IM_DOWN_URL + "avatar/t/" + "/" + dirName + "/" + userId + ".jpg";
        } else {
            url = IM_DOWN_URL + "avatar/o/" + "/" + dirName + "/" + userId + ".jpg";
        }
        return url;
    }


    public static Bitmap displayAvatar(Context mContext , String nickName, String userId , ImageView imageView) {
        List<Object> bitmapList = new ArrayList<>();
        bitmapList.add(nickName);
        Bitmap avatar = AvatarUtil.getBuilder(mContext)
                .setShape(AvatarUtil.Shape.SQUARE)
                .setList(bitmapList)
                .setTextSize(DisplayUtil.dip2px(mContext, 40))
                .setTextColor(R.color.whiteff_contact)
                .setTextBgColor(SkinUtils.getSkin(mContext).getAccentColor())
                .setBitmapSize(DisplayUtil.dip2px(mContext, 120), DisplayUtil.dip2px(mContext, 120))
                .create();
        BitmapDrawable bitmapDrawable = new BitmapDrawable(avatar);
        bitmapDrawable.setAntiAlias(true);
        imageView.setImageDrawable(bitmapDrawable);
        return avatar;



    }

    @SuppressWarnings("ResourceAsColor")
    public static Bitmap buildSrcFromName(Context context ,final String firstChar, int w, int h) {
        if (w == Target.SIZE_ORIGINAL || w <= 0)
            w = 80;
        if (h == Target.SIZE_ORIGINAL || h <= 0)
            h = 80;

        final int size = Math.max(Math.min(Math.min(w, h), 220), 64);
        final float fontSize = size * 0.4f;

        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);

        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(fontSize);
        paint.setTypeface(Typeface.SANS_SERIF);

        // check ASCII
        final int charNum = Character.getNumericValue(firstChar.charAt(0));
        if (charNum > 0 && charNum < 177) {
            Typeface typeface = getFont(context, "Numans-Regular.otf");
            if (typeface != null)
                paint.setTypeface(typeface);
        }

        Rect rect = new Rect();
        paint.getTextBounds(firstChar, 0, 1, rect);
        int fontHeight = rect.height();

        int fontHalfH = fontHeight >> 1;
        int centerX = bitmap.getWidth() >> 1;
        int centerY = bitmap.getHeight() >> 1;

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(getBackgroundColor(firstChar));
        canvas.drawText(firstChar, centerX, centerY + fontHalfH, paint);

        return bitmap;
    }

    public static Typeface getFont(Context context, String fontFile) {
        String fontPath = "fonts/" + fontFile;

        try {
            return Typeface.createFromAsset(context.getAssets(), fontPath);
        } catch (Exception var4) {
            return null;
        }
    }

    private static int getBackgroundColor(String firstChar) {
        int len = COLORS.length;
        int index = firstChar.charAt(0) - 64;
        int colorIndex = Math.abs(index) % len;
        return COLORS[colorIndex];
    }
    static final int[] COLORS = new int[]{
            0xFF1abc9c, 0xFF2ecc71, 0xFF3498db, 0xFF9b59b6, 0xFF34495e, 0xFF16a085, 0xFF27ae60, 0xFF2980b9, 0xFF8e44ad, 0xFF2c3e50,
            0xFFf1c40f, 0xFFe67e22, 0xFFe74c3c, 0xFFeca0f1, 0xFF95a5a6, 0xFFf39c12, 0xFFd35400, 0xFFc0392b, 0xFFbdc3c7, 0xFF7f8c8d
    };
}
