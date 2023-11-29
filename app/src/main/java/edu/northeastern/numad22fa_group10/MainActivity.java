package edu.northeastern.numad22fa_group10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import edu.northeastern.numad22fa_group10.finalProjLoginNRegister.FinalProjectStartActivity;
import edu.northeastern.numad22fa_group10.navigation.NavigationActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // At Your Service button
        Button btAtYourService = (Button) findViewById(R.id.btAtYourService);
        btAtYourService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("MainActivity","Clicking At Your Service button.");
                openAtYourServiceActivity();
            }
        });

        //Chatting app button
        Button btChattingActivity = (Button) findViewById(R.id.btChattingActivity);
        btChattingActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openStartActivity();
            }
        });

        //About me button
        Button btAboutActivity = (Button) findViewById(R.id.btAboutActivity);
        btAboutActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAboutActivity();
            }
        });

        //Final Project button
        Button btnFinalProject = (Button) findViewById(R.id.btnFinalProject);
        btnFinalProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFinalProjectActivity();
            }
        });
    }

    private void openAtYourServiceActivity() {
        Intent intent = new Intent(this, AtYourServiceActivity.class);
        startActivity(intent);
    }

    private void openStartActivity() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
    }

    private void openAboutActivity() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    private void openFinalProjectActivity() {
        Log.i("FinalProjectStartActivity", "gogogo");
        Intent intent = new Intent(this, FinalProjectStartActivity.class);
        startActivity(intent);
    }
}