package edu.northeastern.numad22fa_group10.friendCollector;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.northeastern.numad22fa_group10.ChatActivity;
import edu.northeastern.numad22fa_group10.R;
import edu.northeastern.numad22fa_group10.StartActivity;
import edu.northeastern.numad22fa_group10.StickerCounterActivity;
import edu.northeastern.numad22fa_group10.User;
import edu.northeastern.numad22fa_group10.UserAdapter;
import edu.northeastern.numad22fa_group10.foodCollector.FoodCollectorActivity;
import edu.northeastern.numad22fa_group10.foodCollector.FoodCollectorAdapter;
import edu.northeastern.numad22fa_group10.foodCollector.OneFoodCollection;

public class FriendCollectorActivity extends AppCompatActivity {
    private RecyclerView myFriendList;
    private DatabaseReference FriendsRef,UsersRef;
    private FirebaseAuth mAuth;
    private String online_user_id;
    private FloatingActionButton AddNewFriends;
    private TextView SayHello;
    FirebaseUser firebaseUser;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_friends);

        AddNewFriends=(FloatingActionButton) findViewById(R.id.btn_add_fab);
        AddNewFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchNewFriend();
            }
        });

        SayHello=(TextView)findViewById(R.id.friend_username);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                SayHello.setText("Hello! " + user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mAuth=FirebaseAuth.getInstance();
        online_user_id=mAuth.getCurrentUser().getUid();
        FriendsRef=FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        UsersRef=FirebaseDatabase.getInstance().getReference().child("Users");



        myFriendList=(RecyclerView) findViewById(R.id.users_friendList);
        //myFriendList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        myFriendList.setLayoutManager(linearLayoutManager);

        DisplayAllFriends();

    }

    private void launchNewFriend() {
        Intent newFriendIntent=new Intent(this, AddNewFriends.class);
        startActivity(newFriendIntent);
    }

    private void DisplayAllFriends() {
        FirebaseRecyclerOptions<SingleFriendForFriends> options =
                new FirebaseRecyclerOptions.Builder<SingleFriendForFriends>()
                        .setQuery(FriendsRef, SingleFriendForFriends.class)
                        .build();
        FirebaseRecyclerAdapter<SingleFriendForFriends,FriendsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<SingleFriendForFriends, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendsViewHolder holder, int position, @NonNull SingleFriendForFriends model) {
                //holder.setDate(model.getDate());
                final String usersIDs=getRef(position).getKey();
                UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            final String userName =snapshot.child("username").getValue().toString();
                            holder.setUsername(userName);
                            holder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    CharSequence options[] = new CharSequence[]
                                            {
                                              userName+ "'s Profile",
                                              "Send Message"
                                            };
                                    AlertDialog.Builder builder=new AlertDialog.Builder(FriendCollectorActivity.this);
                                    builder.setTitle("Select Options");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int which) {
                                            if(which == 0){
                                                Intent profileIntent= new Intent(FriendCollectorActivity.this,ProfileSketchActivity.class);
                                                profileIntent.putExtra("visit_user_id",usersIDs);
                                                startActivity(profileIntent);
                                            }
                                            if( which == 1 ){
                                                Intent chatIntent= new Intent(FriendCollectorActivity.this,FriendMassageActivity.class);
                                                chatIntent.putExtra("visit_user_id",usersIDs);
                                                chatIntent.putExtra("username",userName);
                                                startActivity(chatIntent);
                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_friends_card,parent,false);
                return new FriendsViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        myFriendList.addItemDecoration(
                new DividerItemDecoration(myFriendList.getContext(),
                        DividerItemDecoration.VERTICAL)
        );
        myFriendList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setUsername(String username){
            TextView myName=(TextView) mView.findViewById(R.id.friends_name);
            myName.setText(username);
        }

    }
}
