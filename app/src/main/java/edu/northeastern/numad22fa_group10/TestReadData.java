package edu.northeastern.numad22fa_group10;


import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TestReadData {

    private ArrayList<User> users;
    private ArrayList<String> images;

    public TestReadData() {
        users = new ArrayList<>();
        images = new ArrayList<>();
    }

    //read user information from firebase
    public void readUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                users.clear();
                for (DataSnapshot snapshot: snapshots.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    //Log.i("TAG onDataChange: ", user.getUsername());
                    users.add(user);
                }

                // Add UI operation here
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //read Stickers from database
    //same way to retrieve stickers in other class
    public void readImage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Stickers/name");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                images.clear();
                for (DataSnapshot snapshot: snapshots.getChildren()) {
                    String imageName = snapshot.getValue(String.class);
                    images.add(imageName);
                }
                // Add UI operation here
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
