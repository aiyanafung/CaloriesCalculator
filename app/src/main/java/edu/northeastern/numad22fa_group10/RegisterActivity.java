package edu.northeastern.numad22fa_group10;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    EditText username, email;
    Button btn_register;

    FirebaseAuth auth;
    DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        //connect information from xml
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        btn_register = findViewById(R.id.btn_register);

        auth = FirebaseAuth.getInstance();

        //register account base on name, email and password
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameText = username.getText().toString();
                String emailText = email.getText().toString();
                String passText = "123456";

                if (TextUtils.isEmpty(nameText) || TextUtils.isEmpty(emailText)) {
                    Toast.makeText(RegisterActivity.this, "No Empty input!", Toast.LENGTH_LONG).show();
                } else {
                    register(nameText, emailText, passText);
                }
            }
        });
    }

    //register function
    private void register(String username, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.i("user", username);
                        System.out.println(task.isSuccessful());
                        if (task.isSuccessful()) {

                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            dbReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                            HashMap<String, String> map = new HashMap<>();
                            map.put("id", userid);
                            map.put("username", username);

                            dbReference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        retrieveAndStoreToken();
                                        // TODO commend flag if chat runs successful
                                        Intent intent = new Intent(RegisterActivity.this, ChatActivity.class);
                                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, "Register with another email or password", Toast.LENGTH_LONG).show();
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