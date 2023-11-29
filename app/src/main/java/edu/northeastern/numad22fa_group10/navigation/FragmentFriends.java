package edu.northeastern.numad22fa_group10.navigation;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import edu.northeastern.numad22fa_group10.R;
import edu.northeastern.numad22fa_group10.foodCollector.FoodCollectorActivity;
import edu.northeastern.numad22fa_group10.friendCollector.AddNewFriends;
import edu.northeastern.numad22fa_group10.friendCollector.FriendCollectorActivity;
import edu.northeastern.numad22fa_group10.search.DisplayActivity;
import edu.northeastern.numad22fa_group10.search.ShakeARandomRecipeActivity;

public class FragmentFriends extends Fragment {
    private static final String TAG = " FragmentFriends";
    ImageButton searchFriendButton_main;
    ImageButton friendCollectorBtn;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_friends, container, false);

        //Search button main
        searchFriendButton_main = (ImageButton) view.findViewById(R.id.searchFriendsButton_main);
        searchFriendButton_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddFriendActivity();}
        });

        //Food Collector Button
        friendCollectorBtn = (ImageButton) view.findViewById(R.id.friendCollectorBtn);
        friendCollectorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFriendCollectorActivity();
            }
        });



        return view;
    }

    private void openAddFriendActivity() {
        Intent intent = new Intent(getActivity(), AddNewFriends.class);
        startActivity(intent);
    }


    private void openFriendCollectorActivity() {
        Intent intent = new Intent(getActivity(), FriendCollectorActivity.class);
        startActivity(intent);
    }

}