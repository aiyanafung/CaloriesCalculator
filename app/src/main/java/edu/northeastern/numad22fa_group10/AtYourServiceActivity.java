package edu.northeastern.numad22fa_group10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AtYourServiceActivity extends AppCompatActivity {
    private static final String TAG = "AtYourServiceActivity";

    private EditText etZipCode;
    private Button btSearch;
    private ArrayList<OneDay> res;

    RecyclerView recyclerView;
    WeatherAdapter weatherAdapter;
    LoadingBar loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_at_your_service);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        etZipCode = (EditText) findViewById(R.id.etZipCode);
        btSearch = (Button) findViewById(R.id.btSearch);
        loadingBar = new LoadingBar(this);

        res=new ArrayList<>();
        recyclerView = findViewById(R.id.weatherList);
        recyclerView.setLayoutManager(new LinearLayoutManager(AtYourServiceActivity.this));

        // when user click the search button after they done typing
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "clicking button search");
                // validate input, if input is invalid show a toast
                String responseString = validateInput();

                if (!responseString.isEmpty()) {
                    Toast.makeText(AtYourServiceActivity.this, responseString,
                            Toast.LENGTH_SHORT).show();

                } else {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    Handler handler = new Handler(Looper.getMainLooper());

                    executor.execute(new Runnable() {

                        @Override
                        public void run() {
                            //Background work here
                            String appId = "d8cf5d74";
                            String appKey = "2c0a9312c98a3ef25c6bb8fc48fc4c11";
                            String location = "us";
                            String zipCode = etZipCode.getText().toString();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadingBar.showDialog("Loading");
                                }
                            });

                            try {
                                URL url = new URL("http://api.weatherunlocked.com/api/forecast/" +
                                        location + "." + zipCode + "?app_id=" + appId + "&app_key=" + appKey);

                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                conn.setRequestMethod("GET");
                                conn.setDoInput(true);

                                conn.connect();

                                // Read response.
                                InputStream inputStream = conn.getInputStream();
                                final String resp = convertStreamToString(inputStream);

                                JSONObject jObject = new JSONObject(resp);

                                // get 7 days weather Json arrays
                                JSONArray temp=(JSONArray) jObject.get("Days");

                                // res is an weather array for storing weather info
                                res=new ArrayList<>();
                                for(int i=0;i<temp.length();i++) {
                                    JSONObject temp1 = (JSONObject) temp.get(i);
                                    JSONArray timeframeTemp = (JSONArray) temp1.get("Timeframes");
                                    JSONObject iconTemp = timeframeTemp.getJSONObject(0);
                                    String icon = (String) iconTemp.get("wx_icon");
                                    String weatherDesc = (String) iconTemp.get("wx_desc");

                                    String date = (String) temp1.get("date");
                                    String max_temp = String.valueOf(temp1.get("temp_max_f"));
                                    String min_temp = String.valueOf(temp1.get("temp_min_f"));

                                    String[] iconArray = icon.split("\\.");
                                    String iconName = iconArray[0].toLowerCase(Locale.ROOT);
                                    OneDay oneDay = new OneDay(date, max_temp, min_temp,
                                            getResources().getIdentifier(iconName, "drawable",
                                                    getPackageName()), weatherDesc);
                                    res.add(oneDay);
                                }
                            } catch (MalformedURLException e) {
                                Log.e(TAG, "MalformedURLException");
                                e.printStackTrace();
                            } catch (ProtocolException e) {
                                Log.e(TAG, "ProtocolException");
                                e.printStackTrace();
                            } catch (IOException e) {
                                // when zipcode is incorrect
                                Log.e(TAG, "IOException");
                                e.printStackTrace();
                                res = new ArrayList<>();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AtYourServiceActivity.this,
                                                "invalid ZIP Code, please try again",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                            } catch (JSONException e) {
                                Log.e(TAG, "JSONException");
                                e.printStackTrace();
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadingBar.hideDialog();
                                }
                            });

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //UI Thread work here
                                    weatherAdapter = new WeatherAdapter(AtYourServiceActivity.this, res);
                                    recyclerView.setAdapter(weatherAdapter);
                                }
                            });

                        }
                    });
                }
            }

            /**
             * Helper function
             *
             * @param is
             * @return
             */
            private String convertStreamToString(InputStream is) {
                Scanner s = new Scanner(is).useDelimiter("\\A");
                return s.hasNext() ? s.next().replace(",", ",\n") : "";
            }
            private void displayExceptionMessage(Runnable context, String msg){
                Toast.makeText((Context) context,msg,Toast.LENGTH_LONG).show();
            }

            //Method which runs on a prime finder thread.
            public void runOnExceptionMessageThread (View view) {
                exceptionMessageThread messageThreadThread = new exceptionMessageThread();
                new Thread(messageThreadThread).start();
            }

            class exceptionMessageThread implements Runnable {
                @Override
                public void run() {
                    Log.i("wrong","wrong");
                    Toast.makeText(AtYourServiceActivity.this,"wrong",Toast.LENGTH_LONG).show();
                    Log.i("wrong2","wrong2");
                }
            }

            private String validateInput() {
                String zipCode = etZipCode.getText().toString();
                // Valid us ZIP code is 00501 – 99950
                if (zipCode.isEmpty()) {
                    return "Your input is empty, please try again";
                } else if (zipCode.length() != 5) {
                    return "Your input should be 5 digits, please try again";
                }

                // Check if input is numeric
                int numZipCode;
                try {
                    numZipCode = Integer.parseInt(zipCode);
                } catch (NumberFormatException e) {
                    return "Your input should be numeric, please try again";
                }

                // Check if the input is between 00501 to 99950
                if (numZipCode < 501 || numZipCode > 99950 ) {
                    return "Invalid ZIP Code, please try again";
                }
                return "";
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                weatherAdapter.getFilter().filter(newText);
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }
}