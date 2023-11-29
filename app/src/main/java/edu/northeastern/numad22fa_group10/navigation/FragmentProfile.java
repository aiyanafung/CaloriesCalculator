package edu.northeastern.numad22fa_group10.navigation;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.northeastern.numad22fa_group10.R;
import edu.northeastern.numad22fa_group10.finalProjLoginNRegister.FinalProjectLoginActivity;
import edu.northeastern.numad22fa_group10.finalProjLoginNRegister.FinalProjectStartActivity;
import edu.northeastern.numad22fa_group10.foodCollector.OneFoodCollection;
import edu.northeastern.numad22fa_group10.search.DisplayActivity;

public class FragmentProfile extends Fragment {

    private static final String TAG = " FragmentProfile";
    private static final DateTimeFormatter mDateFormatter = DateTimeFormatter.ofPattern("M-d-yyyy");
    View view;

    String strUserName, strBDay, strGender, strWeight, strHeight;
    TextView tvUserName;
    Button btnBday, btnUpdate, btnLogout;
    EditText weight, height;

    DatePickerDialog datePickerDialog;
    Spinner profileGender;
    ArrayList<String> genders = new ArrayList<>(Arrays.asList("Male", "Female"));
    
    LineChart lineChart;
    ArrayList<ILineDataSet> mLineDataSets = new ArrayList<>();

    ImageButton btnAttendance;

    FirebaseAuth auth;
    String userID;


    public FragmentProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        initDataPicker();

        //Logout
        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        //Get userID
        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        userID = firebaseUser.getUid();

        tvUserName = view.findViewById(R.id.tvUserName);
        btnBday = view.findViewById(R.id.profileBTN_edit_birthday);
        profileGender = view.findViewById(R.id.profile_gender_spinner);

        //setting array adaptors to spinners
        //ArrayAdapter is a BaseAdapter that is backed by an array of arbitrary objects
        ArrayAdapter<String> spin_adapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                genders);

        //setting adapters to spinners
        profileGender.setAdapter(spin_adapter);

        weight = view.findViewById(R.id.etProfileWeightKg);
        height = view.findViewById(R.id.etProfileHeightCm);

        btnBday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(view);
            }
        });

        updateTotalCalorie();

        //show line chart
        lineChart = view.findViewById(R.id.lineChart);
        retrieveAndShowChartData();

        //show user info
        userInfoDisplay();

        //record and show attendance
        btnAttendance = view.findViewById(R.id.btnAttendance);
        btnAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attendance();
                openAttendanceCalendarActivity();
            }
        });

        //Update user data
        btnUpdate = view.findViewById(R.id.btnUpdateProfile);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update(view);
            }
        });

        return view;
    }

    public void userInfoDisplay() {
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get user data
                strUserName = "" + snapshot.child("username").getValue();
                strBDay = "" + snapshot.child("birthday").getValue();
                strGender = "" + snapshot.child("gender").getValue();
                strWeight = "" + snapshot.child("weight").getValue();
                strHeight = "" + snapshot.child("height").getValue();

                //set user display
                tvUserName.setText("Hi, " + strUserName);
                btnBday.setText(strBDay);
                profileGender.setSelection(genders.indexOf(strGender) );
                weight.setText(strWeight);
                height.setText(strHeight);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void retrieveAndShowChartData() {
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference().child("calorieHistory").child(userID);

        List<LocalDate> lastSevenDaysBeforeToday = getLastSevenDaysBeforeToday();
        List<Entry> chartData = new ArrayList<>();

        //add init data to chart DataSet
        for (LocalDate date: lastSevenDaysBeforeToday) {
            chartData.add(new Entry(date.toEpochDay(), 0));
        }

        //retrieve data from database
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshotDate : snapshot.getChildren()) {
                    if (!snapshotDate.hasChild(("totalCalorie"))) {
                        continue;
                    }
                    String dateStr = snapshotDate.getKey();
                    LocalDate dateRecord = LocalDate.parse(dateStr, mDateFormatter);
                    long epochDays = dateRecord.toEpochDay();
                    for (Entry entry: chartData) {
                        if (entry.getX() == epochDays) {
                            float totalCalorie = Float.parseFloat(snapshotDate.child("totalCalorie").getValue().toString());
                            entry.setY(totalCalorie);
                        }
                    }
                }

                showLineChart(chartData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showLineChart(List<Entry> dataEntries) {

        //put datasets into chart
        //then show chart
        LineDataSet lineDataSet = new LineDataSet(dataEntries, "Daily Total Calories");
        mLineDataSets.clear();
        mLineDataSets.add(lineDataSet);
        lineChart.getDescription().setEnabled(false);
        setLineChartXAxisFormatter();
        LineData lineData = new LineData(mLineDataSets);
        lineChart.clear();
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    public List<LocalDate> getLastSevenDaysBeforeToday() {
        List<LocalDate> lastSevenDaysBeforeToday = new ArrayList<>();

        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            lastSevenDaysBeforeToday.add(today.minusDays(i));
        }

        return lastSevenDaysBeforeToday;
    }

    public void setLineChartXAxisFormatter() {

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(60f);
        xAxis.setEnabled(true);
        xAxis.setValueFormatter(new MyValueFormatter());
    }

    public void updateTotalCalorie() {

        //get current date
        Calendar currentTime = Calendar.getInstance();
        int year = currentTime.get(Calendar.YEAR);
        int month = currentTime.get(Calendar.MONTH) + 1;
        int day = currentTime.get(Calendar.DAY_OF_MONTH);
        String curDate = makeDateString(year, month, day);

        //calculate total calorie from database
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference().child("calorieHistory").child(userID);
        dbReference.child(curDate)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long totalCalorie = 0l;
                        Long calorie = 0l;
                        String name;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String type = snapshot.getKey();
                            for (DataSnapshot snapshotChild : snapshot.getChildren()) {
                                String id = snapshotChild.getKey();
                                System.out.println("id" + id);
                                for (DataSnapshot snapshotChildChild : snapshotChild.getChildren()) {
                                    System.out.println(snapshotChildChild);
                                    // get the name of food
                                    if (snapshotChildChild.getKey().equals("name")) {
                                        name = snapshotChildChild.getValue().toString();
                                    }

                                    // get the amount of calorie of food
                                    if (snapshotChildChild.getKey().equals("amount")) {
                                        calorie = Long.parseLong(snapshotChildChild.getValue().toString());
                                    }
                                }
                                totalCalorie += calorie;
                            }
                        }
                        dbReference.child(curDate).child("totalCalorie").setValue(totalCalorie);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void update(View view) {
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        //update birthday
        if (!strBDay.equals(btnBday.getText().toString())) {
            dbReference.child("birthday").setValue(btnBday.getText().toString());
        }

        //Get user gender from spinner
        String userGender = profileGender.getSelectedItem().toString();

        //update gender
        if (!strGender.equals(userGender)) {
            dbReference.child("gender").setValue(userGender);
        }

        //update weight
        if (!strWeight.equals(weight.getText().toString())) {
            dbReference.child("weight").setValue(weight.getText().toString());
        }

        //update height
        if (!strHeight.equals(height.getText().toString())) {
            dbReference.child("height").setValue(height.getText().toString());
        }
    }

    public void attendance() {
        Calendar currentTime = Calendar.getInstance();
        int year = currentTime.get(Calendar.YEAR);
        int month = currentTime.get(Calendar.MONTH) + 1;
        int day = currentTime.get(Calendar.DAY_OF_MONTH);

        //record attendance in database
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference().child("Attendance").child(userID);
        dbReference.child(String.valueOf(year)).child(String.valueOf(month)).child(String.valueOf(day)).setValue("presented");
    }

    private void logout() {
        clearToken(FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), FinalProjectStartActivity.class);
        startActivity(intent);
    }

    private void clearToken(String userID) {
        FirebaseDatabase.getInstance().getReference("token").child(userID).removeValue();
    }

    private void initDataPicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = profileMakeDateString(year, month, day);
                btnBday.setText(date);
            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(getActivity(), 0, dateSetListener, year, month, day);
    }

    private String makeDateString(int year, int month, int day) {
        return month + "-" + day + "-" + year;
    }

    private String profileMakeDateString(int year, int month, int day) {
        return month + "/" + day + "/" + year;
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    private void openAttendanceCalendarActivity() {
        Intent intent = new Intent(getActivity(), AttendanceCalendarActivity.class);
        startActivity(intent);
    }

    //line chart dataset values and axis label values formatter
    public class MyValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            LocalDate date = LocalDate.ofEpochDay((long) value);
            int year = date.getYear();
            int month = date.getMonthValue();
            int day = date.getDayOfMonth();
            return makeDateString(year, month, day);
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            return getFormattedValue(value);
        }
    }

}