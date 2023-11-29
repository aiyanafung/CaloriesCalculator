package edu.northeastern.numad22fa_group10.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.northeastern.numad22fa_group10.R;

public class IngredientActivity extends AppCompatActivity {

    private static final String TAG = "IngredientActivity";
    List<String> nutrition;

    RecyclerView recyclerView;
    NutrientsAdapter nutrientsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);
        Intent intent = getIntent();
        String ingredientId = intent.getStringExtra("ingredientId");

        recyclerView = findViewById(R.id.nutrientsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(IngredientActivity.this));

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {

            @Override
            public void run() {
                //Background work here

                // Todo: put this in string
                String apiKey = getResources().getString(R.string.apiKey);


                nutrition = new ArrayList<>();

                List<String> nutrientsList = new ArrayList<>(Arrays.asList("Calories", "Protein",
                        "Carbohydrates", "Fiber", "Sugar", "Fat", "Sodium"));

                LinkedHashMap<String, String> nutrientsMap = new LinkedHashMap<String, String>();

                for (int i = 0; i < nutrientsList.size(); i++) {
                    nutrientsMap.put(nutrientsList.get(i), "");
                }

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

                    System.out.println(jObject.toString());

                    JSONObject nutrition = (JSONObject) jObject.get("nutrition");

                    JSONArray nutrients = (JSONArray) nutrition.get("nutrients");
                    for (int i = 0; i < nutrients.length(); i++) {
                        System.out.println(nutrients.get(i));
                        JSONObject temp = (JSONObject) nutrients.get(i);
                        String name = String.valueOf(temp.get("name"));
                        if (nutrientsMap.containsKey(name)) {
                            String info = name + ": " + String.valueOf(temp.get("amount"))
                                    + String.valueOf(temp.get("unit"));
                            nutrientsMap.put(name, info);

                        }
                    }
                    System.out.println(nutrientsMap);
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
                        //UI Thread work here
                        nutrientsAdapter = new NutrientsAdapter(IngredientActivity.this, nutrientsMap);
                        recyclerView.setAdapter(nutrientsAdapter);
                    }
                });
            }
        });
    }

        private String convertStreamToString(InputStream is) {
            Scanner s = new Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next().replace(",", ",\n") : "";
        }
}