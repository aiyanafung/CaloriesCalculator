package edu.northeastern.numad22fa_group10.foodCollector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.numad22fa_group10.R;

public class FoodCollectorAdapter extends RecyclerView.Adapter<FoodCollectorAdapter.FoodCollectorHolder>{
    private Context context;
    List<OneFoodCollection> foodCollections;

    public FoodCollectorAdapter(Context context, List<OneFoodCollection> foodCollections){
        this.context = context;
        this.foodCollections = foodCollections;
    }

    @NonNull
    @Override
    public FoodCollectorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.food_collector_card, parent, false);
        return new FoodCollectorAdapter.FoodCollectorHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodCollectorHolder holder, int position) {
        holder.collection_calorie.setText(String.valueOf(foodCollections.get(position)
                .getCalorieAmount()));
        holder.collection_data.setText(String.valueOf(foodCollections.get(position)
                .getFoodName()));
    }

    @Override
    public int getItemCount() {
        return foodCollections.size();
    }

    public class FoodCollectorHolder extends RecyclerView.ViewHolder {
        TextView collection_data;
        TextView collection_calorie;

        public FoodCollectorHolder(@NonNull View itemView) {
            super(itemView);
            this.collection_data = itemView.findViewById(R.id.collection_data);
            this.collection_calorie = itemView.findViewById(R.id.collection_calorie);
        }
    }
}
