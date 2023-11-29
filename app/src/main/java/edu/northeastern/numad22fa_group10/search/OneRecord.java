package edu.northeastern.numad22fa_group10.search;

public class OneRecord {
    String id;
    String name;
    String type;
    double caloriesAmount;

    public OneRecord(String id, String name, String type, double caloriesAmount) {
        this.name = name;
        this.caloriesAmount = caloriesAmount;
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCaloriesAmount() {
        return caloriesAmount;
    }

    public void setCaloriesAmount(double caloriesAmount) {
        this.caloriesAmount = caloriesAmount;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
