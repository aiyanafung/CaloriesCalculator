package edu.northeastern.numad22fa_group10.search;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.northeastern.numad22fa_group10.R;

public class RecipeActivity extends AppCompatActivity {
    private static final String TAG = "RecipeActivity";

    String recipeSummary;
    String recipeWebsite;

    TextView recipeInfo;
    TextView recipeLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        //connect textview recipe information to activity_recipe
        recipeInfo = findViewById(R.id.tvRecipeInfo);
        recipeInfo.setMovementMethod(new ScrollingMovementMethod());

        recipeLink = findViewById(R.id.tvRecipeLink);

        Intent intent = getIntent();
        String recipeId = intent.getStringExtra("recipeId");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        recipeSummary = "No internet, please try again.";
        recipeWebsite = " ";

        executor.execute(new Runnable() {

            @Override
            public void run() {
                //Background work here
                String apiKey = getResources().getString(R.string.apiKey);

                try {
                    // get recipe detail information
                    URL url = new URL("https://api.spoonacular.com/recipes/" +
                            recipeId +
                            "/information?includeNutrition=false" +
                            "&apiKey=" + apiKey);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);

                    conn.connect();

                    // Read response.
                    InputStream inputStream = conn.getInputStream();
                    final String resp = convertStreamToString(inputStream);

                    JSONObject jObject = new JSONObject(resp);

                    recipeSummary = String.valueOf(jObject.get("summary"));;
                    recipeWebsite = String.valueOf(jObject.get("spoonacularSourceUrl"));;


                    System.out.println("!!!!!!recipeSummary" + recipeSummary);
                    System.out.println("!!!!!!recipeWebsite" + recipeWebsite);
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
                        // UI Thread work here
                        // Set textView in xml file
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            recipeInfo.setText(Html.fromHtml(recipeSummary,
                                    Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            recipeInfo.setText(Html.fromHtml(recipeSummary));
                        }

                        // hyperlink for recipe website
                        recipeLink.setMovementMethod(LinkMovementMethod.getInstance());
                        recipeLink.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                browserIntent.setData(Uri.parse(recipeWebsite));
                                startActivity(browserIntent);
                            }
                        });

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