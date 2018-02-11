package ray.droid.com.droidcatchnotification.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ray.droid.com.droidcatchnotification.R;
import ray.droid.com.droidcatchnotification.common.DroidCommon;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DroidCommon.ShowListener(getBaseContext());
        finish();
    }
}
