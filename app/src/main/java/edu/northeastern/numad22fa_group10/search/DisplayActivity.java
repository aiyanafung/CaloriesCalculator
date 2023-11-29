package edu.northeastern.numad22fa_group10.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_group10.R;

public class DisplayActivity extends AppCompatActivity {
    private static final String TAG = "DisplayActivity";
    String apiKey;
    FloatingActionButton fab;

    DisplayIngredientAdapter displayIngredientAdapter;
    DisplayRecipeAdapter displayRecipeAdapter;
    DisplayMyRecordAdapter displayMyRecordAdapter;
    RecyclerView recyclerView;

    List<OneIngredient> ingredients;
    List<OneRecipe> recipes;
    List<OneRecord> records;

    EditText inputFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        inputFood = (EditText) findViewById(R.id.etSearch);

        recyclerView = findViewById(R.id.displayResult);
        recyclerView.addItemDecoration(
                new DividerItemDecoration(recyclerView.getContext(),
                        DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(DisplayActivity.this));

        ingredients = new ArrayList<>();
        recipes = new ArrayList<>();
        records = new ArrayList<>();
        displayIngredientAdapter = new DisplayIngredientAdapter(DisplayActivity.this, ingredients);
        displayRecipeAdapter = new DisplayRecipeAdapter(DisplayActivity.this, recipes);
        displayMyRecordAdapter = new DisplayMyRecordAdapter(DisplayActivity.this, records);

        //find the apiKey from string resource
        apiKey = getResources().getString(R.string.apiKey);

        //Ingredient button
        Button btSearchIngredient = (Button) findViewById(R.id.btSearchIngredient);
        btSearchIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchIngredient();
            }
        });

        //Recipes button
        Button btSearchRecipe = (Button) findViewById(R.id.btSearchRecipe);
        btSearchRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchRecipe();
            }
        });

        //Record button
        Button btSearchMyRecord = (Button)findViewById(R.id.btSearchMyRecord);
        btSearchMyRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { searchMyRecord();}
        });

        //Floating add button
        fab = findViewById(R.id.btn_add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { openAddingPageActivity(); }
        });

    }

    private void openAddingPageActivity() {
        Intent intent = new Intent(this, AddingPage.class);
        startActivity(intent);
    }

    // search ingredient based on input food
    private void searchIngredient() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        String food = inputFood.getText().toString();

        executor.execute(new Runnable() {

            @Override
            public void run() {
                //Background work here
                ingredients.clear();
                // Todo: input box in xml file
                //String zipCode = etZipCode.getText().toString();

                try {
                    URL url = new URL("https://api.spoonacular.com/food/ingredients/search?" +
                            "query=" + food +
                            "&number=10" +
                            "&sort=calories" +
                            "&sortDirection=desc" +
                            "&apiKey=" + apiKey);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);

                    conn.connect();

                    // Read response.
                    InputStream inputStream = conn.getInputStream();
                    final String resp = convertStreamToString(inputStream);

                    JSONObject jObject = new JSONObject(resp);

                    System.out.println(jObject);

                    JSONArray results = (JSONArray) jObject.get("results");

                    for (int i = 0; i < results.length(); i++){
                        // get one result
                        JSONObject result = (JSONObject) results.get(i);

                        String id = String.valueOf(result.get("id"));
                        String name = String.valueOf(result.get("name"));
                        System.out.println("id"+ id + " name" + name);

                        ingredients.add(new OneIngredient(id, name));
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

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(displayIngredientAdapter);
                        displayIngredientAdapter.notifyDataSetChanged();
                    }
                });


            }
        });

    }


    // search recipe based on input food
    private void searchRecipe() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        String food = inputFood.getText().toString();

        executor.execute(new Runnable() {

            @Override
            public void run() {
                //Background work here

                recipes.clear();
                // Todo: input box in xml file
                //String zipCode = etZipCode.getText().toString();

                // result will display 6 results with the amount of calories per serve
                // result will be sorted by popularity
                try {
                    URL url = new URL("https://api.spoonacular.com/" +
                            "recipes/complexSearch?" +
                            "query=" + food +
                            "&maxCalories=800&number=10" +
                            "&sort=popularity" +
                            "&apiKey=" + apiKey);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);

                    conn.connect();

                    // Read response.
                    InputStream inputStream = conn.getInputStream();
                    final String resp = convertStreamToString(inputStream);

                    JSONObject jObject = new JSONObject(resp);

                    System.out.println(jObject);

                    JSONArray results = (JSONArray) jObject.get("results");

                    for (int i = 0; i < results.length(); i++){
                        // get one result
                        JSONObject result = (JSONObject) results.get(i);

                        // get calorie amount from result object
                        JSONObject nutrition = (JSONObject) result.get("nutrition");
                        JSONArray nutrients = (JSONArray) nutrition.get("nutrients");
                        JSONObject calorie = (JSONObject) nutrients.get(0);
                        String unit = String.valueOf(calorie.get("unit"));
                        String calorieAmount = String.valueOf(calorie.get("amount")) + unit;

                        String id = String.valueOf(result.get("id"));
                        String recipeTitle = String.valueOf(result.get("title"));

                        System.out.println("id"+ id + " name" + recipeTitle + "amount" + calorieAmount);
                        OneRecipe test = new OneRecipe(id, recipeTitle, calorieAmount);
                        recipes.add(test);
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

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(displayRecipeAdapter);
                        displayRecipeAdapter.notifyDataSetChanged();
                    }
                });


            }
        });

    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }

    //Display Search my record based on the firebase
    private void searchMyRecord(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        String food = inputFood.getText().toString();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                // find the current user
                DatabaseReference dbReference;
                FirebaseAuth auth;
                auth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = auth.getCurrentUser();

                // get the current user id
                assert firebaseUser != null;
                String userid = firebaseUser.getUid();

                records.clear();
                FirebaseDatabase.getInstance().getReference().child("MyRecord").child(userid)
                        .addListenerForSingleValueEvent(new ValueEventListener(){
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot typeSnapshot : dataSnapshot.getChildren()){
                                    for (DataSnapshot foodIdSnapshot: typeSnapshot.getChildren()) {
                                        String id = foodIdSnapshot.getKey();
                                        for (DataSnapshot childSnapshot: foodIdSnapshot.getChildren()) {
                                            if (typeSnapshot.getKey().equals("ingredients")) {
                                                records.add(new OneRecord(id, childSnapshot.getKey(),
                                                        "ingredients", childSnapshot.getValue(Double.class)));
                                            }
                                            else if (typeSnapshot.getKey().equals("recipes")) {
                                                records.add(new OneRecord(id, childSnapshot.getKey(),
                                                        "recipes", childSnapshot.getValue(Double.class)));
                                            }

                                        }
                                    }
                                }

                                String inputFoodString = inputFood.getText().toString();
                                if (!inputFoodString.matches("")) {
                                    records.removeIf(record -> !record.getName().contains(inputFoodString));
                                }

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        recyclerView.setAdapter(displayMyRecordAdapter);
                                        displayMyRecordAdapter.notifyDataSetChanged();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
    }

}