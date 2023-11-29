package edu.northeastern.numad22fa_group10.navigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.naishadhparmar.zcustomcalendar.CustomCalendar;
import org.naishadhparmar.zcustomcalendar.OnNavigationButtonClickedListener;
import org.naishadhparmar.zcustomcalendar.OnDateSelectedListener;
import org.naishadhparmar.zcustomcalendar.Property;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.northeastern.numad22fa_group10.R;

public class AttendanceCalendarActivity extends AppCompatActivity implements OnNavigationButtonClickedListener {
    private static final DateTimeFormatter mDateFormatter = DateTimeFormatter.ofPattern("M-d-yyyy");

    CustomCalendar attendanceCalendar;
//    private Calendar selectedDate = null;


    FirebaseAuth auth;
    DatabaseReference dbReference;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_calendar);

        //Get userID
        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        userID = firebaseUser.getUid();


        attendanceCalendar = findViewById(R.id.calendarAttendance);

        // Initialize description descHashMap
        HashMap<Object, Property> descHashMap = new HashMap<>();

        //set default date
        Property defaultProp = new Property();
        defaultProp.layoutResource = R.layout.activity_attendance_default;
        defaultProp.dateTextViewResource = R.id.tvDayOfMonth;
        descHashMap.put("default", defaultProp);

        // for present date
        Property presentProp = new Property();
        presentProp.layoutResource = R.layout.activity_attendance_present;
        presentProp.dateTextViewResource = R.id.tvDayOfMonth;
        descHashMap.put("present", presentProp);

        // for current date
        Property currentProp = new Property();
        currentProp.layoutResource = R.layout.activity_attendance_current;
        currentProp.dateTextViewResource = R.id.tvDayOfMonth;
        descHashMap.put("current", currentProp);

        // set desc hashmap on custom calendar
        attendanceCalendar.setMapDescToProp(descHashMap);


        HashMap<Integer, Object> attendanceDescMap = new HashMap<>();

        attendanceCalendar.setOnNavigationButtonClickedListener(CustomCalendar.PREVIOUS, this);
        attendanceCalendar.setOnNavigationButtonClickedListener(CustomCalendar.NEXT, this);

        retrieveDates(Calendar.getInstance());

    }

    private void retrieveDates(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        HashMap<Integer, Object> attendanceDescMap = new HashMap<>();
        dbReference = FirebaseDatabase.getInstance().getReference("Attendance").child(userID);
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshotYear : snapshot.getChildren()) {
                    String yearStr = snapshotYear.getKey();
                    if (String.valueOf(year).equals(yearStr)) {
                        for (DataSnapshot snapshotMonth : snapshotYear.getChildren()) {
                            String monthStr = snapshotMonth.getKey();
                            if (String.valueOf(month).equals(monthStr)) {
                                for (DataSnapshot snapshotDay : snapshotMonth.getChildren()) {
                                    String dayStr = snapshotDay.getKey();
                                    attendanceDescMap.put(Integer.parseInt(dayStr), "present");
                                }
                            }
                        }
                    }
                }
                attendanceCalendar.setDate(calendar, attendanceDescMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public Map<Integer, Object>[] onNavigationButtonClicked(int whichButton, Calendar newMonth) {
        Map<Integer, Object>[] arr = new Map[2];
        retrieveDates(newMonth);
        return arr;
    }
}