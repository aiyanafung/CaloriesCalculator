package edu.northeastern.numad22fa_group10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class SelectStickersActivity extends AppCompatActivity {
    private String receiverId;
    private String receiverToken;
    private String senderId;
    private String imageName;
    ArrayList<String> stickers = new ArrayList<>();
    RecyclerView recyclerView;
    StickersAdapter stickersAdapter;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_stickers);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        recyclerView = findViewById(R.id.stickersList);
        intent = getIntent();
        receiverId = intent.getStringExtra("userid");
        senderId = intent.getStringExtra("uid");

        FirebaseDatabase.getInstance().getReference().child("token").child(receiverId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check the existence of the receiver
                if (!snapshot.exists()) {
                    Log.i("Select Stickers Activity",
                            "The receiver is offline, no notification will show on their app");
                } else {
                    receiverToken = snapshot.getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Stickers").child("name")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            String[] imageFullName = snapshot.getValue(String.class).split("\\.");
                            imageName = imageFullName[0].toLowerCase(Locale.ROOT);
                            stickers.add(imageName);
                        }
                        stickersAdapter = new StickersAdapter(SelectStickersActivity.this, stickers, receiverId, senderId, receiverToken);
                        recyclerView.addItemDecoration(
                                new DividerItemDecoration(recyclerView.getContext(),
                                        DividerItemDecoration.VERTICAL));
                        recyclerView.setAdapter(stickersAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(SelectStickersActivity.this));
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void createNotificationChannel() {
        // This must be called early because it must be called before a notification is sent.
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}