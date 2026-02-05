package com.oort.weichat.util.filter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.ArrayMap;

import androidx.core.content.ContextCompat;

import com.oort.weichat.R;
import com.oort.weichat.util.DisplayUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表情过滤器
 * TextView/EditText set this EmojiInputFilter, Support auto conversion Emoji
 */
public class StateInputFilter implements InputFilter {
    public static final int EMOJI_DRAWABLE_BOUND_SIZE_DP = 20;

    private static final int[] faceImages =
            {
                    R.drawable.state_mzz, R.drawable.state_lk, R.drawable.state_qjl, R.drawable.state_dtq,
                    R.drawable.state_pib, R.drawable.state_fd, R.drawable.state_emo,
                    R.drawable.state_hslx,

                    R.drawable.state_gzzh, R.drawable.state_cmxx,
                    R.drawable.state_mang, R.drawable.state_my, R.drawable.state_cch,
                    R.drawable.state_xb, R.drawable.state_wrms,

                    R.drawable.state_lang,R.drawable.state_dk,
                    R.drawable.state_pb, R.drawable.state_hkf,R.drawable.state_hnc,
                    R.drawable.state_zp, R.drawable.state_gf,

                    R.drawable.state_zdy,
            };
    private static final String[] faceScr =
            {
                    "[mzz]", "[lk]", "[qjl]", "[dtq]"
                    , "[pib]", "[fd]", "[emo]"
                    , "[hslx]",

                    "[gzzh]", "[cmxx]"
                    , "[mang]", "[my]", "[cch]"
                    , "[xb]", "[wrms]",

                    "[lang]", "[dk]"
                    , "[pb]", "[hkf]", "[hnc]"
                    , "[zp]", "[gf]"
                    ,
                    "[zdy]"
            };
    private final String regex;
    private final Pattern pattern;
    private Context context;
    private int emojiDpSize;
    private ArrayMap<String, Integer> faceBook;


    public StateInputFilter(Context context) {
        this.context = context;

        regex = buildRegex();
        pattern = Pattern.compile(regex);
        faceBook = buildEmojiToRes();

        float scale = Resources.getSystem().getDisplayMetrics().density;
        emojiDpSize = DisplayUtil.dip2px(context, StateInputFilter.EMOJI_DRAWABLE_BOUND_SIZE_DP);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (end > start) source = source.subSequence(start, end);

        // 新输入的字符串为空（删除剪切等）
        if (TextUtils.isEmpty(source)) return null;

        return source.toString().matches(".*" + regex + ".*") ? addEmojiSpans(source) : null;
    }

    private CharSequence addEmojiSpans(CharSequence text) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            int resId = faceBook.get(matcher.group());
            Drawable dr = ContextCompat.getDrawable(context, resId);
            dr.setBounds(0, 0, emojiDpSize, emojiDpSize);
            builder.setSpan(new ImageSpan(dr, ImageSpan.ALIGN_BOTTOM), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    private String buildRegex() {
        StringBuilder regex = new StringBuilder(faceScr.length * 3);
        regex.append('(');
        for (String s : faceScr) {
            regex.append(Pattern.quote(s)).append('|');
        }
        regex.replace(regex.length() - 1, regex.length(), ")");
        return regex.toString();
    }

    private ArrayMap<String, Integer> buildEmojiToRes() {
        if (faceImages.length != faceScr.length) {
            throw new IllegalStateException("Emoji resource ID/text mismatch");
        }
        ArrayMap<String, Integer> smileyToRes = new ArrayMap<>(faceScr.length);
        for (int i = 0; i < faceScr.length; i++) {
            int resId = faceImages[i];
            smileyToRes.put(faceScr[i], resId);
        }
        return smileyToRes;
    }
}
