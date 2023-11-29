package edu.northeastern.numad22fa_group10.search;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.ImageView;

import edu.northeastern.numad22fa_group10.R;

// shaking phone to get a random recipe
public class ShakeARandomRecipeActivity extends AppCompatActivity {
    private static final String TAG = "ShapeRandomRecipeActivity";

    // sensor
    private SensorManager sensorManager;
    private ShakeSensorListener shakeListener;

    // if user shake their phone
    private boolean isShake;

    private ImageView imgHand;
    // shaking animator for shaking image
    ObjectAnimator anim;

    private String recipeSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isShake = false;
        setContentView(R.layout.activity_shake_random_recipe);
        imgHand = findViewById(R.id.imgHand);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        shakeListener = new ShakeSensorListener();

        // set up animator
        anim = ObjectAnimator.ofFloat(imgHand,"rotation",0f,45f,-30f,0f);
        anim.setDuration(500);
        anim.setRepeatCount(ValueAnimator.INFINITE);
    }

    @Override
    protected void onResume() {
        // register shake listener
        sensorManager.registerListener(shakeListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
        isShake = false;
        super.onResume();
    }

    @Override
    protected void onPause() {
        // release resource
        sensorManager.unregisterListener(shakeListener);
        super.onPause();
    }

    private class ShakeSensorListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            // only shake once at a time
            if (isShake) {
                return;
            }
            // 开始动画
            anim.start();
            float[] values = event.values;
            /*
             * x : right is positive
             * y : front is positive
             * z : up is positive
             * read those position from sensor
             */
            float x = Math.abs(values[0]);
            float y = Math.abs(values[1]);
            float z = Math.abs(values[2]);
            // if accelerator is greater than 19, shake it.
            if (x > 19 || y > 19 || z > 19) {
                isShake = true;
                // play sound
                playSound(ShakeARandomRecipeActivity.this);
                System.out.println("!!!shaking");

                // vibrate the phone, require permission from VIBRATE and HIGH_SAMPLING_RATE_SENSORS
                vibrate( 500);

                Intent intent = new Intent(ShakeARandomRecipeActivity.this,
                        ShowRandomRecipeActivity.class);
                anim.cancel();
                startActivity(intent);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    private void playSound(Context context) {
        MediaPlayer player = MediaPlayer.create(context,R.raw.shake_sound);
        player.start();
    }

    private void vibrate(long milliseconds) {
        Vibrator vibrator = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
        // vibrate is deprecate for sde 26 and older
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect
                    .createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(milliseconds);
        }
    }
}