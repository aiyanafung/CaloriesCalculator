package edu.northeastern.numad22fa_group10.navigation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import edu.northeastern.numad22fa_group10.R;
import edu.northeastern.numad22fa_group10.foodCollector.FoodCollectorActivity;
import edu.northeastern.numad22fa_group10.search.DisplayActivity;
import edu.northeastern.numad22fa_group10.search.ShakeARandomRecipeActivity;

public class FragmentSearch extends Fragment {

    private static final String TAG = " FragmentSearch";
    ImageButton searchButton_main;
    ImageButton foodCollectorBtn;
    ImageButton shakeRandomRecipeBtn;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);

        //Search button main
        searchButton_main = (ImageButton) view.findViewById(R.id.searchButton_main);
        searchButton_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDisplayActivity();}
        });

        //Food Collector Button
        foodCollectorBtn = (ImageButton) view.findViewById(R.id.foodCollectorBtn);
        foodCollectorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFoodCollectorActivity();
            }
        });

        //Food Collector main
        shakeRandomRecipeBtn = (ImageButton) view.findViewById(R.id.shakeRandomRecipeBtn);
        shakeRandomRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openShakeRandomRecipeActivity();
            }
        });

        return view;
    }

    private void openDisplayActivity() {
        Intent intent = new Intent(getActivity(), DisplayActivity.class);
        startActivity(intent);
    }

    private void openFoodCollectorActivity() {
        // get the number of steps from navigation activity
        NavigationActivity activity = (NavigationActivity) getActivity();
        assert activity != null;
        int steps = activity.getMyData();

        Intent intent = new Intent(getActivity(), FoodCollectorActivity.class);
        // put the number of steps into food collector
        intent.putExtra("steps", steps);
        startActivity(intent);
    }

    private void openShakeRandomRecipeActivity() {
        Intent intent = new Intent(getActivity(), ShakeARandomRecipeActivity.class);
        startActivity(intent);
    }
}