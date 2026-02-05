package com.plugins.oortcloud.xshare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ShareChooserPendingIntent extends BroadcastReceiver {
    public static String chosenComponent = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras() != null) {
            ShareChooserPendingIntent.chosenComponent = intent.getExtras().get(Intent.EXTRA_CHOSEN_COMPONENT).toString();
        }
    }
}
