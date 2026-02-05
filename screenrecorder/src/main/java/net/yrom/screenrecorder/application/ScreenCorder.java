package net.yrom.screenrecorder.application;

import android.content.Context;

/**
 * Created by raomengyang on 12/03/2017.
 */

public class ScreenCorder {


    private static Context sContext;


    static {
        System.loadLibrary("screenrecorderrtmp");
    }

    public static void init(Context context) {

        sContext =context;
    }

    public static Context getContext() {
        return sContext;
    }
}
