package edu.northeastern.numad22fa_group10.navigation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import edu.northeastern.numad22fa_group10.R;
import edu.northeastern.numad22fa_group10.friendCollector.FriendCollectorActivity;

public class NavigationActivity extends AppCompatActivity implements SensorEventListener {

    Button btn_fragment_search;
    Button btn_fragment_friends;
    Button btn_fragment_profile;

    private static final String CHANNEL_ID="channel_id01";

    private TextView tv_steps;

    private SensorManager mSensorManager;

    private Integer stepCnt = 0;

    private boolean activityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        btn_fragment_search = findViewById(R.id.btn_fragment_search);
        btn_fragment_friends = findViewById(R.id.btn_fragment_friends);
        btn_fragment_profile = findViewById(R.id.btn_fragment_profile);

        // the default fragment is search fragment
        replaceFragment(new FragmentSearch());

        // update the step counter
        // ask for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }

        tv_steps = findViewById(R.id.tv_steps);
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        stepCnt = sharedPreferences.getInt("stepCnt", 0);
        tv_steps.setText(String.valueOf(stepCnt));

        // set up sensor
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager == null){
            Toast.makeText(this, "Sensor not found!", Toast.LENGTH_SHORT).show();
        }
        else{
            Sensor accel = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            if(accel!=null){
                mSensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
            }
            else{
                Toast.makeText(this, "Sorry Sensor is not available!", Toast.LENGTH_LONG).show();
            }
        }

        // get the current date
        Calendar currentTime = Calendar.getInstance();
        int year = currentTime.get(Calendar.YEAR);
        int month = currentTime.get(Calendar.MONTH) + 1;
        int day = currentTime.get(Calendar.DAY_OF_MONTH);
        String curDate = month + "-" + day + "-" + year;

        // reset the counter in a new day
        String preDate = sharedPreferences.getString("date", "0");
        System.out.println("preDate" + preDate);
        if (preDate.equals("0") || !curDate.equals(preDate)) {
            // update the number of steps
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("stepCnt",0);
            editor.apply();
            stepCnt = 0;
            tv_steps.setText(String.valueOf(stepCnt));

            // update the date
            editor.putString("date", curDate);
            editor.apply();
            System.out.println("curDate" + curDate);
        }

        // change to search fragment
        btn_fragment_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new FragmentSearch());
            }
        });

        // change to friends fragment
        btn_fragment_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new FragmentFriends());
            }
        });

        // change to profile fragment
        btn_fragment_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new FragmentProfile());
            }
        });
    }



    private void replaceFragment(Fragment fragment) {
        // replace the current fragment to another fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frag_layout, fragment);
        fragmentTransaction.commit();


    }

    @Override
    protected void onResume() {
        super.onResume();
        activityRunning = true;

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        stepCnt = sharedPreferences.getInt("stepCnt", 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            stepCnt++;
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("stepCnt", stepCnt);
            editor.apply();
            tv_steps.setText(String.valueOf(stepCnt));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    // put number of steps into fragment
    public int getMyData() {
        return stepCnt;
    }

}