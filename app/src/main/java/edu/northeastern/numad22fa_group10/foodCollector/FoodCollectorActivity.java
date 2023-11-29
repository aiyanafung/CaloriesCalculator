package edu.northeastern.numad22fa_group10.foodCollector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import edu.northeastern.numad22fa_group10.R;
import edu.northeastern.numad22fa_group10.StickerCounterActivity;
import edu.northeastern.numad22fa_group10.StickerCounterAdapter;

public class FoodCollectorActivity extends AppCompatActivity {
    private final static String FEMALE = "Female";
    private final static String MALE = "Male";

    List<OneFoodCollection> foodCollections;
    FoodCollectorAdapter foodCollectorAdapter;
    RecyclerView foodCollectorRecyclerView;

    TextView tvTotalCalorie;
    TextView tvCalorieBalance;
    TextView tvCalorieBurned;

    int age;
    double calorieStandard;
    double calorieBurned;
    Long totalCalorie;
    String userid;
    String curDate;

    String name;
    Long calorie;

    Snackbar snackBar;

    DatabaseReference dbReference;

    int steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_collector);

        tvCalorieBalance = findViewById(R.id.calorieBalance);

        tvCalorieBurned = findViewById(R.id.steps);
        // get the number of steps
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            steps = extras.getInt("steps");
        }

        calorieBurned = 0.04 * steps;
        calorieBurned = Math.round(calorieBurned * 100.0) / 100.0;
        tvCalorieBurned.setText("Calories Burned: " + calorieBurned);

        // find the current user
        DatabaseReference dbReference;
        FirebaseAuth auth;
        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        // get the current user id
        assert firebaseUser != null;
        userid = firebaseUser.getUid();

        // get the current date
        Calendar currentTime = Calendar.getInstance();
        int year = currentTime.get(Calendar.YEAR);
        int month = currentTime.get(Calendar.MONTH) + 1;
        int day = currentTime.get(Calendar.DAY_OF_MONTH);
        curDate = makeDateString(year, month, day);

        // get the information of the user
        FirebaseDatabase.getInstance().getReference().child("Users").child(userid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String birthday = String.valueOf(dataSnapshot.child("birthday").getValue());
                        String height = String.valueOf(dataSnapshot.child("height").getValue());
                        String weight = String.valueOf(dataSnapshot.child("weight").getValue());
                        String gender = String.valueOf(dataSnapshot.child("gender").getValue());

                        // get user age
                        String[] dateArray = birthday.split("/");
                        int userMonth = Integer.parseInt(dateArray[0]);
                        int userDay = Integer.parseInt(dateArray[1]);
                        int userYear = Integer.parseInt(dateArray[2]);
                        age = getAge(userYear, userMonth, userDay);

                        // get today's calorie standard
                        calorieStandard = getCalorieStandard(age, height, weight, gender);
                        double calorieRemain = calorieStandard - totalCalorie + calorieBurned;
                        System.out.println("1:" + calorieStandard);
                        System.out.println("2:" + totalCalorie);
                        System.out.println("3:" + calorieBurned);
                        calorieRemain = Math.round(calorieRemain * 100.0) / 100.0;
                        tvCalorieBalance.setText("Calories Balance: " + calorieRemain);
                    }

                    // function to calculate user's age
                    public int getAge(int year, int month, int dayOfMonth) {
                        return Period.between(
                                LocalDate.of(year, month, dayOfMonth),
                                LocalDate.now()
                        ).getYears();
                    }

                    // function to calculate today's calorie standard based on
                    // user age weight, user height, and gender
                    private double getCalorieStandard(int age, String height, String weight, String gender) {
                        double numOfHeight = Double.parseDouble(height);
                        double numOfWeight = Double.parseDouble(weight);

                        // formula from https://www.omnicalculator.com/health/bmr-harris-benedict-equation
                        if (gender.equals(MALE)) {
                            double res = 66.5 + 13.75 * numOfWeight + 5 * numOfHeight - 6.75 * age;
                            res = Math.round(res * 100.0) / 100.0;
                            return res;
                        }

                        double res = 655.096 + 9.563 * numOfWeight + 1.85 * numOfHeight - 4.676 * age;
                        res = Math.round(res * 100.0) / 100.0;
                        return res;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });



        foodCollections = new ArrayList<>();
        foodCollectorRecyclerView = findViewById(R.id.foodCollectorRecyclerView);

        // Calculate how much calorie user eat today
        totalCalorie = 0L;
        dbReference = FirebaseDatabase.getInstance().getReference().child("calorieHistory").child(userid);
        dbReference.child(curDate)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            //todo: remove two print after finish the project
//                            System.out.println("!!snapshot " + snapshot.toString());
//                            System.out.println("!!key " + snapshot.getKey());
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
                                foodCollections.add(new OneFoodCollection(id, name, calorie, type));
                            }
                        }

                        // update total calories in textview
                        tvTotalCalorie = findViewById(R.id.tvTotalCalorie);
                        dbReference.child(curDate).child("totalCalorie").setValue(totalCalorie);
                        tvTotalCalorie.setText("Total Calories: " + totalCalorie);
                        // update daily calorie balance
                        double calorieRemain = calorieStandard - totalCalorie + calorieBurned;
                        calorieRemain = Math.round(calorieRemain * 100.0) / 100.0;
                        tvCalorieBalance.setText("Calories Balance: " + calorieRemain);

                        // set up food collector adapter and recycler view
                        foodCollectorAdapter = new FoodCollectorAdapter(FoodCollectorActivity.this, foodCollections);
                        foodCollectorRecyclerView.addItemDecoration(
                                new DividerItemDecoration(foodCollectorRecyclerView.getContext(),
                                        DividerItemDecoration.VERTICAL));
                        foodCollectorRecyclerView.setAdapter(foodCollectorAdapter);
                        foodCollectorRecyclerView.setLayoutManager(new LinearLayoutManager(FoodCollectorActivity.this));
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        // swipe to delete a calorie input
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(foodCollectorRecyclerView);
    }

    // make the date into string format
    private String makeDateString(int year, int month, int day) {
        return month + "-" + day + "-" + year;
    }

    // update the total calorie and calorie balance
    private void updateTotalCalorie() {
        totalCalorie = 0L;
        dbReference = FirebaseDatabase.getInstance().getReference().child("calorieHistory").child(userid);
        FirebaseDatabase.getInstance().getReference().child("calorieHistory").child(userid)
                .child(curDate)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot snapshotChild : snapshot.getChildren()) {
                                for (DataSnapshot snapshotChildChild : snapshotChild.getChildren()) {
                                    // get the amount of calorie of food
                                    if (snapshotChildChild.getKey().equals("amount")) {
                                        calorie = Long.parseLong(snapshotChildChild.getValue().toString());
                                    }
                                }
                                totalCalorie += calorie;
                            }
                        }

                        // update total calories in textview
                        tvTotalCalorie = findViewById(R.id.tvTotalCalorie);
                        dbReference.child(curDate).child("totalCalorie").setValue(totalCalorie);
                        tvTotalCalorie.setText("Total Calories: " + totalCalorie);
                        // update daily calorie balance
                        double calorieRemain = calorieStandard - totalCalorie + calorieBurned;
                        calorieRemain = Math.round(calorieRemain * 100.0) / 100.0;
                        tvCalorieBalance.setText("Calories Balance: " + calorieRemain);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    // swipe to delete function
    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView,
                              @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            // Step1: remove food from firebase
            int curPosition  = viewHolder.getLayoutPosition();
            System.out.println("!!!" + curPosition);
            String foodId = foodCollections.get(curPosition).getId();
            String foodType = foodCollections.get(curPosition).getType();

            // swipe to delete food in firebase
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            rootRef.child("calorieHistory").child(userid).child(curDate).child(foodType)
                    .child(foodId).removeValue().
                    addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "Child deleted!");
                            } else {
                                Log.d("TAG", task.getException().getMessage());
                            }
                        }
                    });

            // update the total calorie and calorie balance
            updateTotalCalorie();

            // Step2: remove food from the recycler view
            foodCollections.remove(viewHolder.getBindingAdapterPosition());
            foodCollectorAdapter.notifyDataSetChanged();

            // Step3: user snack bar to show that food is deleted
            View snackerView = findViewById(R.id.foodCollectorPage);
            snackBar = Snackbar.make(snackerView, "Food was successfully deleted!",
                    Snackbar.LENGTH_LONG);
            // make text at the center of snack bar
            View mView = snackBar.getView();
            TextView mTextView = (TextView) mView.findViewById(com.google.android.material.R.id.snackbar_text);
            mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            snackBar.show();
        }
    };
}