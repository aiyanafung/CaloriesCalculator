package edu.northeastern.numad22fa_group10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;

public class StickersAdapter extends RecyclerView.Adapter<StickersAdapter.StickersHolder>  {
    private static final String SERVER_KEY = "key=AAAAPogs2-I:APA91bG3Gw7VRZnE3PEnCoFD_RJyyXky_9hJrVCaYcP1hn50hnV6ZNR9sN9fml7MQ2WR0z4HD0oe14xtE1qTpfcpWCWU54YJxyosNjWlpgY8bgIk0nS3ok7CSiA7n9Lw9tIFxIX9JzBG";

    private final ArrayList<String> arrayList;
    private Context context;
    private final String receiverId;
    private final String senderId;
    private final String receiverToken;

    public StickersAdapter(Context context, ArrayList<String> arrayList, String receiverId, String senderId, String receiverToken){
        this.arrayList = arrayList;
        this.context = context;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.receiverToken = receiverToken;
    }

    @NonNull
    @Override
    public StickersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_stickers_card, parent, false);
        Log.i("StickersAdapter", "onCreateViewHolder");
        return new StickersHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull StickersHolder holder, int position) {
        int location = position;
        String message =arrayList.get(position);
        int messageID = context.getResources().getIdentifier(message, "drawable",
                context.getPackageName());
        holder.iconView.setImageResource(messageID);

        holder.iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessageToDevice(view, message);
                Log.i("onBindViewHolder", "clicking: " + String.valueOf(location));
                Toast.makeText(view.getContext(), "image " + message +
                        " send!", Toast.LENGTH_SHORT).show();

                sendMessage(senderId, receiverId, message);
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userid", receiverId);
                context.startActivity(intent);
            }
        });
    }

    private void sendMessage(String sender, String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> map = new HashMap<>();
        map.put("sender", sender);
        map.put("receiver", receiver);
        map.put("message", message);

        reference.child("Chats").push().setValue(map);
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class StickersHolder extends RecyclerView.ViewHolder {
        ImageView iconView;

        public StickersHolder(@NonNull View itemView) {
            super(itemView);
            this.iconView = itemView.findViewById(R.id.stickersIcon);
        }
    }

    /**
     * Button Handler; creates a new thread that sends off a message to the target device
     *
     * @param view
     */
    public void sendMessageToDevice(View view, String message) {
        System.out.println("before thread ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("in thread ");
                sendMessageToDevice(senderId, receiverToken, message);
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

}