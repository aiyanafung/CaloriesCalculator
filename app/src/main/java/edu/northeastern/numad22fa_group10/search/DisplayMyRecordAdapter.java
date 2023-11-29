package edu.northeastern.numad22fa_group10.search;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.northeastern.numad22fa_group10.R;

public class DisplayMyRecordAdapter extends RecyclerView.Adapter<DisplayMyRecordAdapter.RecordHolder> {
    private Context context;
    private List<OneRecord> records;

    public DisplayMyRecordAdapter(Context context, List<OneRecord> records) {
        this.context = context;
        this.records = records;
    }


    @NonNull
    @Override
    public RecordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.record_card, parent, false);
        return new DisplayMyRecordAdapter.RecordHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayMyRecordAdapter.RecordHolder holder, int position) {
        int i = position;
        holder.recordData.setText(String.valueOf(records.get(i).getName()));
        holder.recordCalorie.setText(String.valueOf(records.get(i).getCaloriesAmount()));
        holder.recordType = records.get(i).getType();
        holder.btAddRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // the date is originally "440.365kcal", we need to delete the kcal
                double amountOfCal = records.get(i).getCaloriesAmount();
                String type = records.get(i).getType();

                // display the input request dialog
                Dialog dialog = new Dialog(context);
                if (type.equals("ingredients")) {
                    dialog.setContentView(R.layout.add_ingredient_dialog);
                } else if (type.equals("recipes")) {
                    dialog.setContentView(R.layout.add_recipe_dialog);
                }
                EditText inputAmount = dialog.findViewById(R.id.inputAmount);
                Button btnSave = dialog.findViewById(R.id.btnSave);
                Button btnCancel = dialog.findViewById(R.id.btnCancel);

                // use save button to save the input information
                btnSave.setOnClickListener(view1 -> {
                    String inputAmountString =  inputAmount.getText().toString();
                    if (inputAmountString.isEmpty()) {
                        Toast.makeText(context, "Input can't be empty, please try again",
                                Toast.LENGTH_SHORT).show();
                    } else if (Integer.parseInt(inputAmountString) == 0) {
                        Toast.makeText(context, "input is invalided, please try again", Toast.LENGTH_SHORT).show();
                    } else {
                        int amount = Integer.parseInt(inputAmount.getText().toString());
                        // after the the input, dismiss the dialog
                        dialog.dismiss();

                        // find the current user
                        DatabaseReference dbReference;
                        FirebaseAuth auth;
                        auth = FirebaseAuth.getInstance();
                        FirebaseUser firebaseUser = auth.getCurrentUser();

                        // get the current user id
                        assert firebaseUser != null;
                        String userid = firebaseUser.getUid();
                        //TODO:delete it after successfully get user id

                        // get the current date
                        Calendar currentTime = Calendar.getInstance();
                        int year = currentTime.get(Calendar.YEAR);
                        int month = currentTime.get(Calendar.MONTH) + 1;
                        int day = currentTime.get(Calendar.DAY_OF_MONTH);
                        String curDate = makeDateString(year, month, day);

                        // add the current ingredient into firebase
                        int calorieAmount = (int)Math.round(amountOfCal) * amount;
                        String foodName = String.valueOf(records.get(i).getName());
                        String foodId = String.valueOf(records.get(i).getId());

                        dbReference = FirebaseDatabase.getInstance().getReference("calorieHistory")
                                .child(userid).child(curDate).child(holder.recordType).child(foodId);
                        Map<String, String> map = new HashMap<>();
                        map.put("name", foodName);
                        map.put("amount", String.valueOf(calorieAmount));

                        // show a toast when food is successfully added
                        dbReference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "My record has successfully added!",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }

                });

                // click cancel button to dismiss the input
                btnCancel.setOnClickListener(view1 -> {
                    dialog.dismiss();
                });

                dialog.show();
            }

            //make the date into string format
            private String makeDateString(int year, int month, int day) {
                return month + "-" + day + "-" + year;
            }
        });
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public class RecordHolder extends RecyclerView.ViewHolder {
        TextView recordData;
        TextView recordCalorie;
        String recordType;
        Button btAddRecord;

        public RecordHolder(@NonNull View itemView) {
            super(itemView);
            this.recordData = itemView.findViewById(R.id.record_data);
            this.recordCalorie = itemView.findViewById(R.id.record_calorie);
            this.btAddRecord = itemView.findViewById(R.id.btAddRecord);
        }
    }
}
