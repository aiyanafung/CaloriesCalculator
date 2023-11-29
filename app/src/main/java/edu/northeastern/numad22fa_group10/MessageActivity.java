package edu.northeastern.numad22fa_group10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

public class MessageActivity extends AppCompatActivity {

    TextView username;
    ImageButton backButton;
    Button btn_stickers;

    FirebaseUser fuser;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        // recycler view for message display
        recyclerView = findViewById(R.id.message_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        username = findViewById(R.id.chat_title);
        btn_stickers = findViewById(R.id.btn_send);
        backButton = findViewById(R.id.back_to_main);

        intent = getIntent();
        String userid = intent.getStringExtra("userid");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        // send message
        btn_stickers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity.this, SelectStickersActivity.class);
                intent.putExtra("userid", userid);
                intent.putExtra("uid", fuser.getUid());
                startActivity(intent);
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
                //System.out.println("fuser.getUid() " + fuser.getUid());
                //System.out.println("userid " + userid);
                readMessages(fuser.getUid(), userid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // click back button and return to main page
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }

//    private void sendMessage(String sender, String receiver, String message) {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("sender", sender);
//        map.put("receiver", receiver);
//        map.put("message", message);
//        map.put("messageId", getResources().getIdentifier(message, "drawable", getPackageName()));
//
//        reference.child("Chats").push().setValue(map);
//    }

    private void readMessages(final String myid, final String userid) {
        mchat = new ArrayList<>();
        //System.out.println("myid " + myid);
        //System.out.println("userid" + userid);

        FirebaseDatabase.getInstance().getReference().child("Chats")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mchat.clear();
                for (DataSnapshot shot : snapshot.getChildren()) {
                    Chat chat = shot.getValue(Chat.class);
                    //System.out.println("receiver " + chat.getReceiver());
                    //System.out.println("sender" + chat.getSender());

                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        mchat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mchat);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
