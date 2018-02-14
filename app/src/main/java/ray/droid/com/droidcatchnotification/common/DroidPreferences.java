package ray.droid.com.droidcatchnotification.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Robson on 31/07/2017.
 */

public class DroidPreferences {

    public static final String PREF_ID = "DroidBattery";

    public static void SetString(Context context, String chave, String valor) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_ID, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(chave, valor);
        editor.commit();
    }

    public static String GetString(Context context, String chave) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_ID, 0);
        return sharedPreferences.getString(chave, "");
    }
}
