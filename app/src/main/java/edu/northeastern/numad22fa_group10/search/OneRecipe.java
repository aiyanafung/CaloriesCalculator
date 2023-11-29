package edu.northeastern.numad22fa_group10.search;

public class OneRecipe {
    private String id;
    private String recipeTitle;
    private String calorieAmount;

    public OneRecipe(String id, String recipeTitle, String calorieAmount) {
        this.id = id;
        this.recipeTitle = recipeTitle;
        this.calorieAmount = calorieAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecipeTitle() {
        return recipeTitle;
    }

    public void setRecipeTitle(String recipeTitle) {
        this.recipeTitle = recipeTitle;
    }

    public String getCalorieAmount() {
        return calorieAmount;
    }

    public void setCalorieAmount(String calorieAmount) {
        this.calorieAmount = calorieAmount;
    }
}
