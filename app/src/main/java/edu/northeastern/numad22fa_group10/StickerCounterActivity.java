package edu.northeastern.numad22fa_group10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class StickerCounterActivity extends AppCompatActivity {
    ArrayList<String> stickers = new ArrayList<>();
    RecyclerView recyclerView;
    StickerCounterAdapter stickerCounterAdapter;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_counter);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        recyclerView = findViewById(R.id.sticker_counter_recycler_view);
        intent = getIntent();
        String userId = intent.getStringExtra("userid");
        FirebaseDatabase.getInstance().getReference().child("Stickers").child("name")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String[] imageFullName = snapshot.getValue(String.class).split("\\.");
                            String imageName = imageFullName[0].toLowerCase(Locale.ROOT);
                            stickers.add(imageName);
                            //stickers arraylist to initial the recyclerview
                        }
                        stickerCounterAdapter = new StickerCounterAdapter(StickerCounterActivity.this, stickers, userId);
                        recyclerView.addItemDecoration(
                                new DividerItemDecoration(recyclerView.getContext(),
                                        DividerItemDecoration.VERTICAL));
                        recyclerView.setAdapter(stickerCounterAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(StickerCounterActivity.this));
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


        // todo: use StickCounterAdapter in activity_sticker_counter.xml
    }
}