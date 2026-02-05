package kl.cds.utils;

import android.widget.EditText;
import android.widget.TextView;

public class StringUtil {
    public static <T> String getViewString(T view) {
        if (view instanceof EditText) {
            return ((EditText) view).getText().toString().trim();
        } else if (view instanceof TextView) {
            return ((TextView) view).getText().toString().trim();
        }
        return "";
    }
}
