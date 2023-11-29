package edu.northeastern.numad22fa_group10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherHolder> implements Filterable {
    private final ArrayList<OneDay> arrayList;
    private final Context context;
    private ArrayList<OneDay> arrayListAll;

    public WeatherAdapter(Context context, ArrayList<OneDay> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        this.arrayListAll = new ArrayList<>(arrayList);
    }

    @NonNull
    @Override
    public WeatherHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        return new WeatherHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull WeatherHolder holder, int position) {
        holder.date.setText(arrayList.get(position).getDate());
        holder.maxTemp.setText("High: " + arrayList.get(position).getMax_temp() + "F");
        holder.minTemp.setText("Low: " + arrayList.get(position).getMin_temp() + "F");
        holder.iconView.setImageResource(arrayList.get(position).getGif());
        holder.weatherDesc.setText(arrayList.get(position).getWeatherDesc());
     }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {

        // runs on background thread
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<OneDay> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(arrayListAll);
            } else {
                for (OneDay oneday: arrayListAll) {
                    if (oneday.getDate().contains(charSequence.toString())) {
                        filteredList.add(oneday);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        // runs on UI thread
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            arrayList.clear();
            arrayList.addAll((Collection<? extends OneDay>) filterResults.values);
            notifyDataSetChanged();
        }
    };
}
