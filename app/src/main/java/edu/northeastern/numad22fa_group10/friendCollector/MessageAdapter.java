package edu.northeastern.numad22fa_group10.friendCollector;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


import edu.northeastern.numad22fa_group10.R;
import edu.northeastern.numad22fa_group10.Utils;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static final String SERVER_KEY = "key=AAAAPogs2-I:APA91bG3Gw7VRZnE3PEnCoFD_RJyyXky_9hJrVCaYcP1hn50hnV6ZNR9sN9fml7MQ2WR0z4HD0oe14xtE1qTpfcpWCWU54YJxyosNjWlpgY8bgIk0nS3ok7CSiA7n9Lw9tIFxIX9JzBG";
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersDatabaseRef;
    public MessageAdapter(List<Messages> userMessagesList){
        this.userMessagesList=userMessagesList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView SenderMessageText,ReceiverMessageText;
        //public CircleImageView receiverProfileImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            SenderMessageText = (TextView) itemView.findViewById(R.id.sender_message_text);
            ReceiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            //receiverProfileImage=(CircleImageView) itemView.findViewById(R.id.message_profile_image);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_layout_of_users,parent,false);
        mAuth=FirebaseAuth.getInstance();
        return new MessageViewHolder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        String messageSenderID=mAuth.getCurrentUser().getUid();
        Messages messages=userMessagesList.get(position);

        String fromUserID=messages.getFrom();
        String fromMessageType = messages.getType();

        usersDatabaseRef= FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);
        if(fromMessageType.equals("text")){
            holder.ReceiverMessageText.setVisibility(View.INVISIBLE);
            if(fromUserID.equals(messageSenderID)){
                holder.SenderMessageText.setBackgroundResource(R.drawable.sender_message_text_background);
                holder.SenderMessageText.setTextColor(Color.WHITE);
                holder.SenderMessageText.setGravity(Gravity.RIGHT);
                holder.SenderMessageText.setText(messages.getMessage());
            }
            else {
                holder.SenderMessageText.setVisibility(View.INVISIBLE);
                holder.ReceiverMessageText.setVisibility(View.VISIBLE);

                holder.ReceiverMessageText.setBackgroundResource(R.drawable.receiver_message_text_background);
                holder.ReceiverMessageText.setTextColor(Color.WHITE);
                holder.ReceiverMessageText.setGravity(Gravity.LEFT);
                holder.ReceiverMessageText.setText(messages.getMessage());
            }

        }


    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
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
