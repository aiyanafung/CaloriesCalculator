package edu.northeastern.numad22fa_group10.finalProjLoginNRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import edu.northeastern.numad22fa_group10.ChatActivity;
import edu.northeastern.numad22fa_group10.LoginActivity;
import edu.northeastern.numad22fa_group10.R;
import edu.northeastern.numad22fa_group10.navigation.NavigationActivity;

public class FinalProjectLoginActivity extends AppCompatActivity {

    private static final String TAG = "FinalProjectLoginActivity";
    private EditText loginEmail;
    Button loginBtn;

    DatabaseReference dbReference;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_project_login);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        auth = FirebaseAuth.getInstance();

        loginEmail = findViewById(R.id.login_email);
        loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = "123456";
                String email = loginEmail.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(FinalProjectLoginActivity.this, "No Empty input!",
                            Toast.LENGTH_LONG).show();
                } else {
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    System.out.println("login " + task.isSuccessful());
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(FinalProjectLoginActivity.this,
                                                NavigationActivity.class);

                                        retrieveAndStoreToken();
                                        startActivity(intent);
                                        System.out.println("successful");
                                        finish();
                                    } else {
                                        Toast.makeText(FinalProjectLoginActivity.this,
                                                "Account not found, please try again!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
    private void retrieveAndStoreToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        String userId = auth.getUid();
                        dbReference = FirebaseDatabase.getInstance().getReference("token");
                        dbReference.child(userId).setValue(token);
                    }
                });
    }
}