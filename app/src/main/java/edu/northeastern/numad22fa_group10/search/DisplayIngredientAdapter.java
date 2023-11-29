package edu.northeastern.numad22fa_group10.search;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.northeastern.numad22fa_group10.R;

public class DisplayIngredientAdapter
        extends RecyclerView.Adapter<DisplayIngredientAdapter.IngredientHolder>  {
    private static final String TAG = "DisplayIngredientAdapter";
    private Context context;
    private List<OneIngredient> ingredients;
    private Double amountOfCal;

    public DisplayIngredientAdapter(Context context, List<OneIngredient> ingredients){
        this.context = context;
        this.ingredients = ingredients;
        amountOfCal = 0.0;
    }

    @NonNull
    @Override
    public IngredientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_card, parent, false);
        return new IngredientHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull IngredientHolder holder, int position) {
        int i = position;
        holder.ingredient_data.setText(String.valueOf(ingredients.get(i).getName()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.i("onBindViewHolder", String.valueOf(ingredients.get(i).getId()));
                Intent intent = new Intent(context, IngredientActivity.class);
                intent.putExtra("ingredientId", String.valueOf(ingredients.get(i).getId()));
                context.startActivity(intent);
            }
        });

        // click the add button add current ingredient into firebase
        holder.btAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call api to get the calorie of current ingredient into amountOfCal global variable
                getCalorie(String.valueOf(ingredients.get(i).getId()));

                // display the input request dialog
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.add_ingredient_dialog);
                EditText inputAmount = dialog.findViewById(R.id.inputAmount);
                Button btnSave = dialog.findViewById(R.id.btnSave);
                Button btnCancel = dialog.findViewById(R.id.btnCancel);

                // use save button to save the input information
                btnSave.setOnClickListener(view1 -> {
                    // get the amount input of ingredient
                    String inputAmountString = inputAmount.getText().toString();

                    if (inputAmountString.isEmpty()) {
                        // input can't be empty
                        Toast.makeText(context, "Input can't be empty, please try again",
                                Toast.LENGTH_LONG).show();
                    } else if (Integer.parseInt(inputAmountString) == 0) {
                        // if amountOfCal is 0, it is invalid, ask user try again
                        Toast.makeText(context, "Input can't be 0 gram, please try again",
                                Toast.LENGTH_LONG).show();
                    } else {
                        int amount = Integer.parseInt(inputAmount.getText().toString());
                        // pass validation, dismiss the dialog and put this info to firebase
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

                        // add the current ingredient into firebase
                        int calorieAmount = (int)Math.round(amountOfCal * amount);
                        System.out.println("!!!calorieAmount out " + calorieAmount);
                        String foodName = String.valueOf(ingredients.get(i).getName());
                        //TODO:delete it after successfully get calorie amount

                        String foodId = String.valueOf(ingredients.get(i).getId());
                        dbReference = FirebaseDatabase.getInstance().getReference("calorieHistory")
                                .child(userid).child(curDate).child("ingredients").child(foodId);
                        Map<String, String> map = new HashMap<>();
                        // map.put(foodName, calorieAmount);
                        map.put("name", foodName);
                        map.put("amount", String.valueOf(calorieAmount));

                        // show a toast when food is successfully added
                        dbReference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Ingredient has successfully added!",
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

            private void getCalorie(String ingredientId) {
                ExecutorService executor = Executors.newSingleThreadExecutor();

                executor.execute(new Runnable() {

                    @Override
                    public void run() {
                        //Background work here

                        // Todo: put this in string
                        String apiKey = context.getResources().getString(R.string.apiKey);

                        try {
                            URL url = new URL("https://api.spoonacular.com/food/ingredients/" +
                                    ingredientId +
                                    "/information?" +
                                    "amount=1" +
                                    "&unit=grams" +
                                    "&apiKey=" + apiKey);

                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.setDoInput(true);

                            conn.connect();

                            // Read response.
                            InputStream inputStream = conn.getInputStream();
                            final String resp = convertStreamToString(inputStream);
                            JSONObject jObject = new JSONObject(resp);
                            JSONObject nutrition = (JSONObject) jObject.get("nutrition");
                            JSONArray nutrients = (JSONArray) nutrition.get("nutrients");

                            for (int i = 0; i < nutrients.length(); i++) {
                                JSONObject temp = (JSONObject) nutrients.get(i);
                                String name = String.valueOf(temp.get("name"));
                                if (name.equals("Calories")) {
                                    amountOfCal = Double.valueOf(String.valueOf(temp.get("amount")));
                                    System.out.println("!!!amountOfCal" + amountOfCal);
                                }
                            }
                        } catch (MalformedURLException e) {
                            Log.e(TAG, "MalformedURLException");
                            e.printStackTrace();
                        } catch (ProtocolException e) {
                            Log.e(TAG, "ProtocolException");
                            e.printStackTrace();
                        } catch (IOException e) {
                            Log.e(TAG, "IOException");
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            private String convertStreamToString(InputStream is) {
                Scanner s = new Scanner(is).useDelimiter("\\A");
                return s.hasNext() ? s.next().replace(",", ",\n") : "";
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }


    public class IngredientHolder extends RecyclerView.ViewHolder {
        TextView ingredient_data;
        Button btAddIngredient;

        public IngredientHolder(@NonNull View itemView) {
            super(itemView);
            this.ingredient_data = itemView.findViewById(R.id.ingredient_data);
            this.btAddIngredient = itemView.findViewById(R.id.btAddIngredient);
        }
    }

}