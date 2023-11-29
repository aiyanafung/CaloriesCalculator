package edu.northeastern.numad22fa_group10.foodCollector;

public class OneFoodCollection {
    private String id;
    private String foodName;
    private Long calorieAmount;
    private String type;

    public OneFoodCollection(String id, String foodName, Long calorieAmount, String type) {
        this.id = id;
        this.foodName = foodName;
        this.calorieAmount = calorieAmount;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public Long getCalorieAmount() {
        return calorieAmount;
    }

    public void setCalorieAmount(Long calorieAmount) {
        this.calorieAmount = calorieAmount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
