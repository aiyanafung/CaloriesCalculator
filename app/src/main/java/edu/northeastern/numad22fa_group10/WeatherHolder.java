package edu.northeastern.numad22fa_group10;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WeatherHolder extends RecyclerView.ViewHolder {

    ImageView iconView;
    TextView date;
    TextView maxTemp;
    TextView minTemp;
    TextView weatherDesc;

    public WeatherHolder(@NonNull View itemView) {
        super(itemView);

        this.iconView = itemView.findViewById(R.id.weatherIcon);
        this.date = itemView.findViewById(R.id.date);
        this.maxTemp = itemView.findViewById(R.id.maxTemp);
        this.minTemp = itemView.findViewById(R.id.minTemp);
        this.weatherDesc = itemView.findViewById(R.id.weatherDesc);
    }
}
