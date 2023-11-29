package edu.northeastern.numad22fa_group10.search;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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

public class DisplayRecipeAdapter extends RecyclerView.Adapter<DisplayRecipeAdapter.RecipeHolder>{
    private Context context;
    private List<OneRecipe> recipes;

    public DisplayRecipeAdapter(Context context, List<OneRecipe> recipes){
        this.context = context;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipes_card, parent, false);
        return new DisplayRecipeAdapter.RecipeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeHolder holder, int position) {
        int i = position;
        holder.recipe_data.setText(String.valueOf(recipes.get(i).getRecipeTitle()));
        holder.calorie.setText(String.valueOf(recipes.get(i).getCalorieAmount()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.i("onBindViewHolder", String.valueOf(ingredients.get(i).getId()));
                Intent intent = new Intent(context, RecipeActivity.class);
                intent.putExtra("recipeId", String.valueOf(recipes.get(i).getId()));
                context.startActivity(intent);
            }
        });

        holder.btAddRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // the date is originally "440.365kcal", we need to delete the kcal
                String calorieAmountStr = recipes.get(i).getCalorieAmount();
                calorieAmountStr = calorieAmountStr.substring(0, calorieAmountStr.length() - 4);
                double amountOfCal = Double.parseDouble(calorieAmountStr);

                // display the input request dialog
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.add_recipe_dialog);
                EditText inputAmount = dialog.findViewById(R.id.inputAmount);
                Button btnSave = dialog.findViewById(R.id.btnSave);
                Button btnCancel = dialog.findViewById(R.id.btnCancel);

                // use save button to save the input information
                btnSave.setOnClickListener(view1 -> {

                    String inputAmountString = inputAmount.getText().toString();
                    if (inputAmountString.isEmpty()) {
                        // input can't be empty
                        Toast.makeText(context, "Input can't be empty, please try again",
                                Toast.LENGTH_LONG).show();
                    } else if(Integer.parseInt(inputAmountString) == 0) {
                        // if amount is 0, it is invalid, ask user try again
                        Toast.makeText(context, "Input can't be 0 serve, please try again",
                                Toast.LENGTH_LONG).show();
                    } else {
                        // pass validation
                        // get the amount input of ingredient
                        int amount = Integer.parseInt(inputAmountString);

                        // dismiss the dialog
                        dialog.dismiss();

                        // find the current user
                        DatabaseReference dbReference;
                        FirebaseAuth auth;
                        auth = FirebaseAuth.getInstance();
                        FirebaseUser firebaseUser = auth.getCurrentUser();

                        // get the current user id
                        assert firebaseUser != null;
                        String userid = firebaseUser.getUid();

                        // get the current date
                        Calendar currentTime = Calendar.getInstance();
                        int year = currentTime.get(Calendar.YEAR);
                        int month = currentTime.get(Calendar.MONTH) + 1;
                        int day = currentTime.get(Calendar.DAY_OF_MONTH);
                        String curDate = makeDateString(year, month, day);

                        // add the current recipe into firebase
                        int calorieAmount = (int)Math.round(amountOfCal) * amount;
                        System.out.println("here!!!" + calorieAmount);
                        String foodName = String.valueOf(recipes.get(i).getRecipeTitle());

                        String foodId = String.valueOf(recipes.get(i).getId());
                        dbReference = FirebaseDatabase.getInstance().getReference("calorieHistory")
                                .child(userid).child(curDate).child("recipes").child(foodId);
                        Map<String, String> map = new HashMap<>();
                        // map.put(foodName, calorieAmount);
                        map.put("name", foodName);
                        map.put("amount", String.valueOf(calorieAmount));

                        // show a toast when food is successfully added
                        dbReference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Recipe has successfully added!",
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
        return recipes.size();
    }

    public class RecipeHolder extends RecyclerView.ViewHolder {
        TextView recipe_data;
        TextView calorie;
        Button btAddRecipe;


        public RecipeHolder(@NonNull View itemView) {
            super(itemView);
            this.recipe_data = itemView.findViewById(R.id.recipe_data);
            this.calorie = itemView.findViewById(R.id.recipe_calorie);
            this.btAddRecipe = itemView.findViewById(R.id.btAddRecipe);
        }
    }
}
