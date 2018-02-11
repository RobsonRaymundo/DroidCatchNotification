package ray.droid.com.droidcatchnotification.common;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by Robson on 02/03/2016.
 */
public class DroidMethods {

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

    public static String getDateFormated()
    {
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyyMMdd");
        return simpleFormat.format( new Date( System.currentTimeMillis() ));
    }

    public static String getTimeFormated()
    {
        SimpleDateFormat simpleFormat = new SimpleDateFormat("hh:mm");
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
                apiAvailability.getErrorDialog(activity, resultCode, DroidConstantes.PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                DroidMethods.showMessage(activity, "Dispositivo não suportado");
                Log.d(DroidConstantes.TAG, "This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }



}

