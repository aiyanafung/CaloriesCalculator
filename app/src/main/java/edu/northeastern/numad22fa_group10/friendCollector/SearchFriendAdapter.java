package edu.northeastern.numad22fa_group10.friendCollector;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.firebase.ui.database.FirebaseRecyclerAdapter;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
//
//import edu.northeastern.numad22fa_group10.R;
//
//public class SearchFriendAdapter extends FirebaseRecyclerAdapter<OneFriendCollection,SearchFriendAdapter.SearchFriendViewholder> {
//    public SearchFriendAdapter(
//            @NonNull FirebaseRecyclerOptions<OneFriendCollection> options)
//    {
//        super(options);
//    }
//    @Override
//    protected void
//    onBindViewHolder(@NonNull SearchFriendViewholder holder,
//                     int position, @NonNull OneFriendCollection model)
//    {
//
//        // Add firstname from model class (here
//        // "person.class")to appropriate view in Card
//        // view (here "person.xml")
//        holder.username.setText(model.getUsername());
//
//        // Add lastname from model class (here
//        // "person.class")to appropriate view in Card
//        // view (here "person.xml")
//        holder.birthday.setText(model.getBirthday());
//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String visit_user_id=getRef(position).getKey();
//                Intent profileIntent=new Intent(SearchFriendAdapter.this,ProfileSketchActivity.class);
//                profileIntent.putExtra("visit_user_id",visit_user_id);
//                startActivity(profileIntent);
//            }
//        });
//
//
//    }
//    @NonNull
//    @Override
//    public SearchFriendViewholder
//    onCreateViewHolder(@NonNull ViewGroup parent,
//                       int viewType)
//    {
//        View view
//                = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.friend_card, parent, false);
//        return new SearchFriendViewholder(view);
//    }
//
//    // Sub Class to create references of the views in Crad
//    // view (here "person.xml")
//    static class SearchFriendViewholder
//            extends RecyclerView.ViewHolder {
//        View mView;
//        TextView username, birthday;
//        public SearchFriendViewholder(@NonNull View itemView)
//        {
//            super(itemView);
//            mView=itemView;
//
//            username
//                    = itemView.findViewById(R.id.friendName_data);
//            birthday = itemView.findViewById(R.id.friend_data_birth);
//        }
//    }
//}
