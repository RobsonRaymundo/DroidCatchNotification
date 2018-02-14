package ray.droid.com.droidcatchnotification.common;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;


/**
 * Created by Robson on 04/08/2017.
 */

public class DroidCommon {
    public static String MESSAGE = DroidCommon.getDateTimeFormated() + " the file was created";
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String TAG = "DroidCatchNotification";
    private static DriveFile driveFile;

    public static void ShowListener(Context context) {
        Intent mIntent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mIntent);
    }

    public static void TimeSleep(Integer seg) {
        try {
            Thread.sleep(seg);
        } catch (Exception ex) {
        }
    }

    public static DriveId GetDriveID(Context context) {
        String driveId = DroidPreferences.GetString(context, "DriveId");
        if (driveId.isEmpty()) return null;
        else return DriveId.decodeFromString(driveId);
    }

    public static DriveFile GetDriveFile(Context context) {
        if (DroidCommon.driveFile == null || DroidCommon.driveFile.toString().isEmpty()) {
            DriveId driveId = DroidCommon.GetDriveID(context);
            if (driveId == null)
                return null;
            else {
                DroidCommon.driveFile = driveId.asDriveFile();
            }
        }
        return DroidCommon.driveFile;
    }

    public static void SetDriveFile(DriveFile driveFile) {
        DroidCommon.driveFile = driveFile;
    }
    public static String getEmail(Context context) {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(context).getAccounts();

        String possibleEmail = "";
        for (Account account : accounts) {
            if (account.type.equalsIgnoreCase("com.google") && emailPattern.matcher(account.name).matches()) {
                possibleEmail = account.name;
                break;
            }
        }

        if (possibleEmail.isEmpty()) {
            for (Account account : accounts) {
                if (emailPattern.matcher(account.name).matches()) {
                    possibleEmail = account.name;
                    break;
                }
            }
        }
        return possibleEmail;
    }

    public static String getAccount(Context context) {
        String account = "padrao";
        try {
            String email = getEmail(context);
            String[] accounts = email.split("@");
            account = accounts[0];
        }catch (Exception ex)
        {
        }
        return account;
    }

    public static String getDateTimeFormated()
    {
        SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        return simpleFormat.format( new Date( System.currentTimeMillis() ));
    }


    public static void showMessage(final Activity activity, String mensagem) {
        AlertDialog.Builder alerta = new AlertDialog.Builder(activity);
        alerta.setMessage(mensagem);
        alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // activity.finish();
            }
        });
        alerta.show();
    }

    public static boolean checkPlayServices(Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, DroidCommon.PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                DroidCommon.showMessage(activity, "Dispositivo não suportado");
                Log.d(DroidCommon.TAG, "This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }

    public static String getNameDevice(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return tm.getMmsUserAgent();
        }
        else return "Padrao";
    }



}
