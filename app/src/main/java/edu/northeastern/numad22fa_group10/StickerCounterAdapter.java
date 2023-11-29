package edu.northeastern.numad22fa_group10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class StickerCounterAdapter extends RecyclerView.Adapter<StickerCounterAdapter.StickersHolder>  {
    private Context context;
    String userid;
    ArrayList<String> stickers;

    public StickerCounterAdapter(Context context, ArrayList<String> stickers, String userid){
        this.context = context;
        this.userid = userid;
        this.stickers = stickers;
        System.out.println(stickers + "1212121");
    }

    @NonNull
    @Override
    public StickersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sticker_counter_card, parent, false);
        return new StickersHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull StickersHolder holder, int position) {
        String message = stickers.get(position);
        Integer messageID = context.getResources().getIdentifier(message, "drawable",
                context.getPackageName());
        holder.iconView.setImageResource(messageID);
        HashMap<String, Integer> receiverMap = new HashMap<>();
        HashMap<String, Integer> senderMap = new HashMap<>();
        for(String stickerName:stickers){
            receiverMap.put(stickerName,0);
            senderMap.put(stickerName,0);
        }
        FirebaseDatabase.getInstance().getReference().child("Chats")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            String messageName = snapshot.child("message").getValue(String.class);
                            //String[] imageFullName = snapshot.getValue(String.class).split("\\.");
                            String receiver = snapshot.child("receiver").getValue(String.class);
                            String sender = snapshot.child("sender").getValue(String.class);
                            if (receiver.equals(userid)) {
                                receiverMap.put(messageName, receiverMap.getOrDefault(messageName, 0) + 1);

                            }

                            if (sender.equals(userid)) {
                                //never use "==" while judging they are equal to each other!!
                                senderMap.put(messageName, senderMap.getOrDefault(messageName, 0) + 1);
                            }

                            //stickers.add(imageName);
                            //Log.i("stickerId", String.valueOf(stickerId));
                            //Log.i("stickers size2", String.valueOf(stickers.size()));
                        }
                        holder.ReceivedCounter.setText(String.valueOf( receiverMap.get(message)));
                        holder.SentCounter.setText(String.valueOf( senderMap.get(message)));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });



    }

    @Override
    public int getItemCount() {
        return stickers.size();
    }


    public class StickersHolder extends RecyclerView.ViewHolder {
        ImageView iconView;
        TextView SentCounter;
        TextView ReceivedCounter;

        public StickersHolder(@NonNull View itemView) {
            super(itemView);
            this.iconView = itemView.findViewById(R.id.stickerCounterIcon);
            this.SentCounter = itemView.findViewById(R.id.sendCounter);
            this.ReceivedCounter = itemView.findViewById(R.id.receivedCounter);

        }
    }

}