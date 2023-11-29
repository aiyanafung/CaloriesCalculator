package edu.northeastern.numad22fa_group10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    TextView userName;

    FirebaseUser firebaseUser;

    //for recycler view
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        // show the user of this chat app
        userName = findViewById(R.id.chat_username);

        // logout
        Button btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutActivity();
            }
        });

        //counter
        Button btn_sticker_counter = findViewById(R.id.btn_sticker_counter);
        btn_sticker_counter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCounter();

            }
        });

        // set up the name of user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                userName.setText("Hello! " + user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //build recycler view
        recyclerView = findViewById(R.id.usersList);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

        mUsers = new ArrayList<>();
        readUsers();
    }

    public void openCounter() {
        Intent intent=new Intent(this,StickerCounterActivity.class);
        intent.putExtra("userid",firebaseUser.getUid());
        startActivity(intent);

    }

    private void logoutActivity() {
        clearToken(FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
    }

    private void clearToken(String userID) {
        FirebaseDatabase.getInstance().getReference("token").child(userID).removeValue();
    }

    // read users from database
    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot shot : snapshot.getChildren()) {
                    User user = shot.getValue(User.class);
                    if (!user.getId().equals(firebaseUser.getUid())) {
                        mUsers.add(user);
                    }
                }

                userAdapter = new UserAdapter(ChatActivity.this, mUsers);
                recyclerView.addItemDecoration(
                        new DividerItemDecoration(recyclerView.getContext(),
                                DividerItemDecoration.VERTICAL));
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        
    }
}