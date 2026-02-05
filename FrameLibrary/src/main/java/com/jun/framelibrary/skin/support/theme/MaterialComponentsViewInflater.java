package com.jun.framelibrary.skin.support.theme;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textview.MaterialTextView;
import com.jun.framelibrary.skin.support.SkinAppCompatViewInflater;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/16 13:32
 * Version 1.0
 * Description：
 */
public class MaterialComponentsViewInflater extends SkinAppCompatViewInflater {
    private static int floatingToolbarItemBackgroundResId = -1;

    @NonNull
    @Override
    protected AppCompatButton createButton(@NonNull Context context, @NonNull AttributeSet attrs) {
        if (shouldInflateAppCompatButton(context, attrs)) {
            return new AppCompatButton(context, attrs);
        }

        return new MaterialButton(context, attrs);
    }

    /** @hide */
    @RestrictTo(LIBRARY_GROUP)
    protected boolean shouldInflateAppCompatButton(
            @NonNull Context context, @NonNull AttributeSet attrs) {
        // Workaround for FloatingToolbar inflating floating_popup_menu_button.xml on API 23-25, which
        // should not have MaterialButton styling.

        if (!(VERSION.SDK_INT == VERSION_CODES.M
                || VERSION.SDK_INT == VERSION_CODES.N
                || VERSION.SDK_INT == VERSION_CODES.N_MR1)) {
            return false;

        }

        if (floatingToolbarItemBackgroundResId == -1) {
            floatingToolbarItemBackgroundResId =
                    context
                            .getResources()
                            .getIdentifier("floatingToolbarItemBackgroundDrawable", "^attr-private", "android");
        }

        if (floatingToolbarItemBackgroundResId != 0 && floatingToolbarItemBackgroundResId != -1) {
            for (int i = 0; i < attrs.getAttributeCount(); i++) {
                if (attrs.getAttributeNameResource(i) == android.R.attr.background) {
                    int backgroundResourceId = attrs.getAttributeListValue(i, null, 0);
                    if (floatingToolbarItemBackgroundResId == backgroundResourceId) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @NonNull
    @Override
    protected AppCompatCheckBox createCheckBox(Context context, AttributeSet attrs) {
        return new MaterialCheckBox(context, attrs);
    }

    @NonNull
    @Override
    protected AppCompatRadioButton createRadioButton(Context context, AttributeSet attrs) {
        return new MaterialRadioButton(context, attrs);
    }

    @NonNull
    @Override
    protected AppCompatTextView createTextView(Context context, AttributeSet attrs) {
        return new MaterialTextView(context, attrs);
    }
}
