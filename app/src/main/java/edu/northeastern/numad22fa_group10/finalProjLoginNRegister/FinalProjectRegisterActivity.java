package edu.northeastern.numad22fa_group10.finalProjLoginNRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Pattern;

import edu.northeastern.numad22fa_group10.ChatActivity;
import edu.northeastern.numad22fa_group10.R;
import edu.northeastern.numad22fa_group10.RegisterActivity;
import edu.northeastern.numad22fa_group10.navigation.NavigationActivity;

public class FinalProjectRegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    EditText username, email, weight, height;
    Button btn_register;

    FirebaseAuth auth;
    DatabaseReference dbReference;

    DatePickerDialog datePickerDialog;
    Button dateButton;

    Spinner genderSpinner;
    String userGender;
    String[] genders = { "Male", "Female" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_project_register);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        initDataPicker();

        // dialog for user select birthday date
        dateButton = (Button) findViewById(R.id.btn_select_birthday);
        // dialog start with today's date
        dateButton.setText(getTodayDate());

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(view);
            }
        });

        //connect information from xml
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        weight = findViewById(R.id.weight);
        height = findViewById(R.id.height);
        btn_register = findViewById(R.id.btn_register);
        genderSpinner = findViewById(R.id.gender_spinner);

        auth = FirebaseAuth.getInstance();

        //get user gender from spinner
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                //saving the gender value selected
                userGender = genders[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        //setting array adaptors to spinners
        // ArrayAdapter is a BaseAdapter that is backed by an array of arbitrary objects
        ArrayAdapter<String> spin_adapter = new ArrayAdapter<String>(
                FinalProjectRegisterActivity.this,
                android.R.layout.simple_spinner_item,
                genders);

        // setting adapters to spinners
        genderSpinner.setAdapter(spin_adapter);


        //register account base on name, email and password
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameText = username.getText().toString();
                String emailText = email.getText().toString();
                String heightText = height.getText().toString();
                String weightText = weight.getText().toString();
                String birthdayText = dateButton.getText().toString();

                // hardcoded password, so no password needed
                String passText = "123456";

                // if validation failed, return message in string, else return empty string;
                String returnText = validateInput(nameText, emailText, heightText, weightText);


                if (!returnText.isEmpty()) {
                    // if the input is empty or invalid
                    Toast.makeText(FinalProjectRegisterActivity.this, returnText, Toast.LENGTH_LONG).show();
                } else {
                    // pass the validation

                    // keep 1 decimal for height and weight
                    double heightDouble = Double.parseDouble(heightText);
                    double weightDouble = Double.parseDouble(weightText);
                    heightText = String.format("%.1f", heightDouble);
                    weightText = String.format("%.1f", weightDouble);

                    // register and store those info to firebase
                    register(nameText, emailText, heightText, weightText, birthdayText, passText, userGender);
                }
            }
        });

    }

    // validate user input
    private String validateInput(String username, String email, String height, String weight) {

        // input should not be empty
        if (username.isEmpty() || email.isEmpty() || height.isEmpty() || weight.isEmpty()) {
            return "No empty input. Please try again!";
        }

        // validate email format contains @
        String regexPattern = "^(.+)@(\\S+)$";
        boolean isEmailValidate = Pattern.compile(regexPattern)
                .matcher(email)
                .matches();
        if (!isEmailValidate) {
            return "Invalid Email format. Please try again!";
        }

        // email length range is 4 to 320
        if(email.length() < 3 || email.length() > 320) {
            return "Email length should be between 3 to 320 character. Please try again!";
        }
        // username length range is 1 to 20
        if(username.length() > 20) {
            return "username length should be between 1 to 20 character. Please try again!";
        }

        // validate weight and height
        Double weightDouble = Double.parseDouble(weight);
        Double heightDouble = Double.parseDouble(height);
        if(weightDouble < 2 || weightDouble > 700){
            // human weight range is from 2kg to 700kg
            return "Invalid weight. Please try again!";
        } else if (heightDouble < 40 || heightDouble > 300){
            // human height range is from 40cm to 300cm
            return "Invalid height. Please try again!";
        }
            return "";
    }

    //register function
    private void register(String username, String email, String heightText, String weightText,
                          String birthdayText, String password, String gender) {
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
                            map.put("height", heightText);
                            map.put("weight", weightText);
                            map.put("birthday", birthdayText);
                            map.put("gender", gender);

                            dbReference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        retrieveAndStoreToken();
                                        Intent intent = new Intent(FinalProjectRegisterActivity.this,
                                                NavigationActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(FinalProjectRegisterActivity.this, "Register with another email or password", Toast.LENGTH_LONG).show();
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

    private String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(year, month, day);
    }

    private void initDataPicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(year, month, day);
                dateButton.setText(date);


            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, 0, dateSetListener, year, month, day);
    }

    private String makeDateString(int year, int month, int day) {
        return month + "/" + day + "/" + year;
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }
}