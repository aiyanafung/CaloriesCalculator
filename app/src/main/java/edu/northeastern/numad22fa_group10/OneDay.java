package edu.northeastern.numad22fa_group10;

public class OneDay {
    // one-day forecast contains date, max temperature, min temperature,
    // and a icon represent the general weather
    private String date;
    private String max_temp;
    private String min_temp;
    // gif is the resource of a icon in drawable
    private int gif;
    private String weatherDesc;

    public OneDay(String date, String max_temp, String min_temp, int gif, String weatherDesc) {
        this.date = date;
        this.max_temp = max_temp;
        this.min_temp = min_temp;
        this.gif = gif;
        this.weatherDesc = weatherDesc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMax_temp() {
        return max_temp;
    }

    public void setMax_temp(String max_temp) {
        this.max_temp = max_temp;
    }

    public String getMin_temp() {
        return min_temp;
    }

    public void setMin_temp(String min_temp) {
        this.min_temp = min_temp;
    }

    public int getGif() {
        return gif;
    }

    public void setGif(int gif) {
        this.gif = gif;
    }

    public String getWeatherDesc() {
        return weatherDesc;
    }

    public void setWeatherDesc(String weatherDesc) {
        this.weatherDesc = weatherDesc;
    }
}
