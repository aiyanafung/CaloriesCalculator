package edu.northeastern.numad22fa_group10.friendCollector;

import static edu.northeastern.numad22fa_group10.R.layout.chat_custom_bar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import edu.northeastern.numad22fa_group10.R;
import edu.northeastern.numad22fa_group10.Utils;

public class FriendMassageActivity extends AppCompatActivity {
    private static final String SERVER_KEY = "key=AAAAPogs2-I:APA91bG3Gw7VRZnE3PEnCoFD_RJyyXky_9hJrVCaYcP1hn50hnV6ZNR9sN9fml7MQ2WR0z4HD0oe14xtE1qTpfcpWCWU54YJxyosNjWlpgY8bgIk0nS3ok7CSiA7n9Lw9tIFxIX9JzBG";
    private Toolbar ChatToolBar;
    private ImageButton btn_send;
    private EditText userMessageInput;
    private RecyclerView userMessageList;
    private final List<Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private String messageReceiverID,messageReceiverName,messageSenderID,saveCurrentDate,saveCurrentTime;
    private TextView receiverName;
    //private CircleImageView receiverProfileImage;
    private DatabaseReference RootRef;
    private FirebaseAuth mAuth;
    private String receiverToken;
    private ActionBar actionBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_massage);

        mAuth=FirebaseAuth.getInstance();
        messageSenderID=mAuth.getCurrentUser().getUid();

        RootRef= FirebaseDatabase.getInstance().getReference();

        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View action_bar_view=layoutInflater.inflate(R.layout.chat_custom_bar,null);
        //actionBar.setCustomView(action_bar_view);

        messageReceiverID=getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName=getIntent().getExtras().get("username").toString();
        RootRef.child("token")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.getKey().equals(messageReceiverID)) {
                                receiverToken = snapshot.getValue().toString();
                                System.out.println("receiverToken!!!" + receiverToken);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        messageReceiverName=getIntent().getExtras().get("username").toString();




        receiverName=(TextView) findViewById(R.id.custom_profile_name);
        //receiverProfileImage=(CircleImageView)findViewById(R.id.custom_profile_image);


        btn_send=findViewById(R.id.send_message_button);
        userMessageInput=(EditText) findViewById(R.id.input_message);

        DisplayReceiverInfo();

        messageAdapter=new MessageAdapter(messagesList);
        userMessageList=(RecyclerView) findViewById(R.id.message_recycler_view);
        linearLayoutManager=new LinearLayoutManager(this);
        userMessageList.setHasFixedSize(true);
        userMessageList.setLayoutManager(linearLayoutManager);
        userMessageList.setAdapter(messageAdapter);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage(view);


            }
        });
        FetchMessages();
    }


    private void FetchMessages() {
        RootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if(snapshot.exists()){
                            Messages messages=snapshot.getValue(Messages.class);
                            messagesList.add(messages);
                            messageAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void SendMessage(View view) {
        String messageText=userMessageInput.getText().toString();
        if(TextUtils.isEmpty(messageText)){
            Toast.makeText(this,"Please Type A Message First",Toast.LENGTH_SHORT);
        }
        else{
            String message_sender_ref="Messages/"+ messageSenderID + "/" + messageReceiverID;
            String message_receiver_ref="Messages/"+ messageReceiverID + "/" + messageSenderID;
            DatabaseReference user_message_key=RootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
                    .push();
            String message_push_id=user_message_key.getKey();

            Calendar calForDate=Calendar.getInstance();
            SimpleDateFormat currentDate=new SimpleDateFormat("dd-MMMM-yyyy");
            saveCurrentDate=currentDate.format(calForDate.getTime());

            Calendar calForTime=Calendar.getInstance();
            SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm aa");
            saveCurrentTime=currentDate.format(calForDate.getTime());

            Map messageTextBody= new HashMap<>();
            messageTextBody.put("message",messageText);
            messageTextBody.put("time",saveCurrentTime);
            messageTextBody.put("date",saveCurrentDate);
            messageTextBody.put("type","text");
            messageTextBody.put("from",messageSenderID);

            Map messageBodyDetails=new HashMap<>();
            messageBodyDetails.put(message_sender_ref + "/" + message_push_id ,messageTextBody);
            messageBodyDetails.put(message_receiver_ref + "/" + message_push_id ,messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(FriendMassageActivity.this,"Message Sent Successfully!",Toast.LENGTH_SHORT).show();
                        userMessageInput.setText("");
                    }
                    else{
                        String message=task.getException().getMessage();
                        Toast.makeText(FriendMassageActivity.this,"Error"+ message,Toast.LENGTH_SHORT).show();
                    }
                    userMessageInput.setText("");
                }
            });
        }
        sendMessageToDevice(view,messageText);

    }
    public void sendMessageToDevice(View view, String message) {
        System.out.println("before thread ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("in thread ");
                sendMessageToDevice(messageSenderID, receiverToken, message);
            }
        }).start();
    }

    private void sendMessageToDevice(String senderId, String receiverToken, String message) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jdata = new JSONObject();

        System.out.println("senderId "+senderId);
        System.out.println("receiverToken "+receiverToken);
        FirebaseDatabase.getInstance().getReference().child("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userName = "";
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String currentId = snapshot.child("id").getValue(String.class);

                            if (senderId.equals(currentId)) {
                                userName = snapshot.child("username").getValue(String.class);
                                System.out.println(userName);
                            }
                        }
                        FirebaseMessaging fm = FirebaseMessaging.getInstance();
                        fm.send(new RemoteMessage.Builder(senderId + "@gcm.googleapis.com")
                                //.setMessageId(Integer.toString(msgId.incrementAndGet()))
                                .addData("my_message", "Hello World")
                                .addData("my_action","SAY_HELLO")
                                .build());

                        try {
                            System.out.println("11112+" + "try!");
                            // TODO: update notification message and content
                            //jNotification.put("title", "Message from " + senderId);
                            jNotification.put("title", "A new message from " + userName);
                            jNotification.put("body", message);
                            jNotification.put("sound", "default");
                            jNotification.put("badge", "1");
                            jdata.put("title", "New Message");
                            jdata.put("content", "content");

                            System.out.println(jNotification);
                            // If sending to a single client
                            jPayload.put("to", receiverToken); // CLIENT_REGISTRATION_TOKEN);

                            jPayload.put("priority", "high");
                            jPayload.put("notification", jNotification);
                            jPayload.put("data", jdata);

                            System.out.println("jPayload!!!:" + jPayload.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Thread fcmThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final String resp = Utils.fcmHttpConnection(SERVER_KEY, jPayload);
                                Log.i(this.getClass().getSimpleName(), "resp from fcm:" + resp);
                            }
                        });
                        fcmThread.start();

//        Utils.postToastMessage("Status from Server: " + resp, getApplicationContext());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });



    }

    private void DisplayReceiverInfo() {
        actionBar.setTitle(messageReceiverName);
    }
}