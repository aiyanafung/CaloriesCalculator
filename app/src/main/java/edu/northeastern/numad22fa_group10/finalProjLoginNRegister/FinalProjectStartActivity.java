package edu.northeastern.numad22fa_group10.finalProjLoginNRegister;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import edu.northeastern.numad22fa_group10.ChatActivity;
import edu.northeastern.numad22fa_group10.LoginActivity;
import edu.northeastern.numad22fa_group10.R;
import edu.northeastern.numad22fa_group10.RegisterActivity;
import edu.northeastern.numad22fa_group10.StartActivity;
import edu.northeastern.numad22fa_group10.navigation.NavigationActivity;

public class FinalProjectStartActivity extends AppCompatActivity {

    private static final String TAG = FinalProjectStartActivity.class.getSimpleName();
    Button login, register;

    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "start");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //Log.i(TAG, "get current uid"+ firebaseUser.getUid());

        // check if user is already login, go to navigation activity
        if (firebaseUser != null) {
            Log.i(TAG, "to NavigationActivity");
            Intent intent = new Intent(FinalProjectStartActivity.this, NavigationActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_project_start);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        login = findViewById(R.id.btn_final_login);
        register = findViewById(R.id.btn_final_register);

        //go to the login activity
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FinalProjectStartActivity.this, FinalProjectLoginActivity.class));
            }
        });

        //go to the register activity
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FinalProjectStartActivity.this, FinalProjectRegisterActivity.class));
            }
        });
    }
}