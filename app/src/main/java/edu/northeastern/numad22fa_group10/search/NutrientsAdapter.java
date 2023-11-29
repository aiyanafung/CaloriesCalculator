package edu.northeastern.numad22fa_group10.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import edu.northeastern.numad22fa_group10.R;

public class NutrientsAdapter extends RecyclerView.Adapter<NutrientsAdapter.NutrientsHolder>  {
    private Context context;
    private List<String> nutrientsList;


    public NutrientsAdapter(Context context, LinkedHashMap<String, String> nutrientsMap){
        this.context = context;
        nutrientsList = new ArrayList<>();
        for(String key : nutrientsMap.keySet()) {
            nutrientsList.add(nutrientsMap.get(key));
        }
    }

    @NonNull
    @Override
    public NutrientsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nutrients_card, parent, false);
        return new NutrientsHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NutrientsHolder holder, int position) {
        int i = position;
        holder.nutrients_data.setText(String.valueOf(nutrientsList.get(i)));
    }

    @Override
    public int getItemCount() {
        return nutrientsList.size();
    }


    public class NutrientsHolder extends RecyclerView.ViewHolder {
        TextView nutrients_data;

        public NutrientsHolder(@NonNull View itemView) {
            super(itemView);
            this.nutrients_data = itemView.findViewById(R.id.nutrients_data);
        }
    }

}