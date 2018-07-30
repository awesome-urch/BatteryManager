package com.example.awesomeurch.batterymanager;

//Import classes and modules that will be used in this activity(page or screen)
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.jaredrummler.android.processes.AndroidProcesses;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;
import static android.os.BatteryManager.BATTERY_PLUGGED_AC;
import static android.os.BatteryManager.BATTERY_PLUGGED_USB;

//Declare activity class which extends Android's AppCompatActivity class
public class MainActivity extends AppCompatActivity {

    //Define all our variables
    private Context mContext;
    private TextView info,report1,report2,report3;
    private TextView percent;
    private TextView mTextViewPercentage;
    private Button button;
    private ProgressBar mProgressBar;
    private Intent intent;
    private int mProgressStatus = 0;
    private int cnt = 0;
    private float batteryLevel1;
    private float batteryLevel2;
    private Calendar now1,now2;
    Date tym1;
    private float time1,time2;
    private String TAG = "Urch-Apps";
    private String charging, chargingMethod;
    private UsageStatsManager mUsageStatsManager;
    Field field = null;

    List<ActivityManager.RunningAppProcessInfo> processes;
    List<UsageStats> queryUsageStats;
    ActivityManager amg;

    //BroadcastReceiver Intent. This intent reports the details of the battery at intervals( approximately 10 seconds )
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1); //get the scale of battery (usually 100)
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1); //get the level of the battery


            // Are we charging or is the phone fully charged?
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;


            float percentage = level / (float) scale; //calculate the battery percentage of the battery according to the scale

            /*We need to get the current time and get the current battery percentage. We can easily know the next battery percentage;
            * if it's charging we get the upper percentage and vice-versa. */
            if(cnt < 1){
                batteryLevel2 = percentage + (float) 0.0001; //if the phone is charging, this is the next battery level we are expecting
                batteryLevel1 = percentage - (float) 0.0001; //if it is discharging, this is the next battery level we are expecting
                tym1 = Calendar.getInstance().getTime();
                //get Time 1
                cnt++;
            }

            //Define what happens when the phone is charging as well as when it's not charging
            if(isCharging){
                charging = "Charging";
                info.setText("Hours Till Battery Full");

                if(percentage >= batteryLevel2){
                    //get Time 2
                    float p_left = (float) 1 - percentage;
                    Date tym2 = Calendar.getInstance().getTime();

                    long tym_diff = tym2.getTime() - tym1.getTime();
                    long mills = Math.abs(tym_diff);

                    long rem = (long) (mills * p_left * 100);

                    int hours = (int) (rem/(1000 * 60 * 60));
                    int mins = (int) (rem/(1000*60)) % 60;
                    long secs = (int) (rem / 1000) % 60;

                    report2.setText("" + hours + "h" + mins + "m");
                    cnt = 0;
                }
            }else{
                info.setText("Hours Left Till Battery Dies");
                charging = "Not Charging";
                if(percentage <= batteryLevel1){
                    //get Time 2

                    Date tym2 = Calendar.getInstance().getTime();

                    long tym_diff = tym2.getTime() - tym1.getTime();
                    long mills = Math.abs(tym_diff);

                    long rem = (long) (mills * percentage * 100);

                    int hours = (int) (rem/(1000 * 60 * 60));
                    int mins = (int) (rem/(1000*60)) % 60;
                    long secs = (int) (rem / 1000) % 60;
                    report2.setText("" + hours + "h" + mins + "m");

                    cnt = 0;
                }
                
            }

            percent.setText("Battery - " + level + "%");
            report1.setText("Charge State : "+charging);

            mProgressStatus = (int) ((percentage) * 100);
            mProgressBar.setProgress(mProgressStatus);
            //mTextViewPercentage.setText("" + mProgressStatus + "%");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mContext.registerReceiver(mBroadcastReceiver, iFilter);
        //info =  findViewById(R.id.first);
        percent = findViewById(R.id.battery);
        report1 = findViewById(R.id.report1);
        report2 = findViewById(R.id.timeEst);
        info = findViewById(R.id.hours);
        //report2 = findViewById(R.id.report2);
        //report3 = findViewById(R.id.report3);
        mProgressBar = findViewById(R.id.pb);
        button = findViewById(R.id.run_app);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proceed();
            }
        });
        //mTextViewPercentage = (TextView) findViewById(R.id.tv_percentage);


        // using Activity service to list all process
        /*amg = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        // list all running process
        processes = amg.getRunningAppProcesses();



        for (ActivityManager.RunningAppProcessInfo process : processes) {

            Toast.makeText(getApplicationContext(),process.processName,Toast.LENGTH_SHORT).show();
        }*/


    }


    public void proceed(){
        intent = new Intent(this,RunningAppsActivity.class);
        startActivity(intent);
    }
    /*private boolean checkForPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, Process.myUid(), context.getPackageName());
        return mode == MODE_ALLOWED;
    }*/
}
