package ray.droid.com.droidcatchnotification.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ray.droid.com.droidcatchnotification.R;
import ray.droid.com.droidcatchnotification.common.DroidCommon;
import ray.droid.com.droidcatchnotification.gdrive.CreateFileActivity;

public class MainActivity extends AppCompatActivity  {
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getBaseContext();
        DroidCommon.ShowListener(context);
        if (DroidCommon.GetDriveFile(context) == null)
        {
            Intent mIntent = new Intent(context, CreateFileActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mIntent);
        }
        finish();
    }
}
