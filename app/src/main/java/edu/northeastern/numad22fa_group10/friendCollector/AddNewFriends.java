package edu.northeastern.numad22fa_group10.friendCollector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import edu.northeastern.numad22fa_group10.R;


public class AddNewFriends extends AppCompatActivity {
    private EditText SearchInputText;
    private RecyclerView searchedFriendList;
    private DatabaseReference allUsersList;
    private Button SearchButton;
    private List<OneFriendCollection> matchedFriends;
    private Button SendFriendRequest;
    private String CURRENT_STATE;
    private DatabaseReference FriendRequestReference;
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter<OneFriendCollection, NewFriendsViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_friends);

        //initialize recyclerview for friend List
        searchedFriendList=(RecyclerView) findViewById(R.id.friendList);
    //    searchedFriendList.setHasFixedSize(true);
        searchedFriendList.setLayoutManager(new LinearLayoutManager(this));

        //initialize Buttons
        SearchInputText=(EditText) findViewById(R.id.etFriendName);
        SearchButton=(Button) findViewById(R.id.btSearchFriends);


        //wrap up activity for search button
        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchUsername=SearchInputText.getText().toString();
                if(TextUtils.isEmpty(searchUsername)){
                    Toast.makeText(AddNewFriends.this,"Please enter a valid username",Toast.LENGTH_SHORT).show();
                }
                SearchForFriends(searchUsername);
            }
        });

        mAuth=FirebaseAuth.getInstance();

        allUsersList= FirebaseDatabase.getInstance().getReference().child("Users");
        System.out.println(allUsersList);

    }


    private void SearchForFriends(String searchUsername) {
        Query searchForFriends = allUsersList.orderByChild("username")
                .startAt(SearchInputText.getText().toString()).endAt(SearchInputText.getText().toString() + "\uf8ff");
        //System.out.println(searchForFriends);
        FirebaseRecyclerOptions<OneFriendCollection> options =
                new FirebaseRecyclerOptions.Builder<OneFriendCollection>()
                        .setQuery(searchForFriends, OneFriendCollection.class)
                        .build();
         firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<OneFriendCollection, NewFriendsViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull NewFriendsViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull OneFriendCollection model) {
                holder.setUsername(model.getUsername());
                holder.setBirthday(model.getBirthday());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String visit_user_id=getRef(position).getKey();
                        Intent profileIntent=new Intent(AddNewFriends.this,ProfileSketchActivity.class);
                        profileIntent.putExtra("visit_user_id",visit_user_id);
                        startActivity(profileIntent);
                    }
                });
            }

            @NonNull
            @Override
            public NewFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_card, parent, false);

                return new NewFriendsViewHolder(view);
            }

        };


        firebaseRecyclerAdapter.startListening();
        searchedFriendList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class NewFriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public NewFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

        }
        public void setUsername(String username){
            TextView myName=(TextView) mView.findViewById(R.id.friendName_data);
            myName.setText(username);
        }
        public void setBirthday(String birthday){
            TextView myBirthday=(TextView) mView.findViewById(R.id.friend_data_birth);
            myBirthday.setText(birthday);
        }

    }
}