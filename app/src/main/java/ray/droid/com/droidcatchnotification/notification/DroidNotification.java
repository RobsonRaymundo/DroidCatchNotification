package ray.droid.com.droidcatchnotification.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import ray.droid.com.droidcatchnotification.common.DroidCommon;
import ray.droid.com.droidcatchnotification.gdrive.AppendContentsActivity;

import static ray.droid.com.droidcatchnotification.common.DroidCommon.TAG;


/**
 * Created by Robson on 03/02/2016.
 */

public class DroidNotification extends DroidBaseNotification {
    CharSequence tit;
    String msg;

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i(TAG, "onNotificationPosted");
        Context context = getBaseContext();
        getNotificationKitKat(sbn);


        if (!tit.toString().isEmpty()) {

            try {



                Intent mIntent = new Intent(getBaseContext(), AppendContentsActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           //     mIntent.putExtra(DroidConstantes.MESSAGE, getDataNotification());
                DroidCommon.MESSAGE = getDataNotification();
                startActivity(mIntent);


            } catch (Exception ex) {
                Log.d(TAG, "onNotificationPosted " + ex.getMessage());
            }
        }


    }

    private String getDataNotification() {
        return DroidCommon.getDateTimeFormated() + " " + tit + " " + msg;
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void getNotificationKitKat(StatusBarNotification mStatusBarNotification) {
        String pack = mStatusBarNotification.getPackageName();// Package Name
        msg = "";
        tit = "";
        if (pack.contains("com.whatsapp") ||
                pack.contains("com.android.mms") ||
                pack.contains("com.facebook.orca")) {
            Bundle extras = mStatusBarNotification.getNotification().extras;
            tit = extras.getCharSequence(Notification.EXTRA_TITLE); // Title
            CharSequence desc = extras.getCharSequence(Notification.EXTRA_TEXT); // / Description
            try {
                Bundle bigExtras = mStatusBarNotification.getNotification().extras;
                CharSequence[] descArray = bigExtras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
                msg = descArray[descArray.length - 1].toString();

            } catch (Exception ex) {

            }
            if (msg.isEmpty()) {
                msg = desc.toString();
            }

            if (msg.equals("procurando novas mensagens") || msg.equals("Checking for new messages")) {
                tit = "";
                msg = "";
            }

        }
    }

}
