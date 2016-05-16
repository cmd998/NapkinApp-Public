package edu.sfsu.napkin;

import java.util.ArrayList;
import java.util.Comparator;

public class Recipe {

    private String originAPI;
    private String recipeID;
    private String name;
    private String dishType;
    private String cuisine;

    private int cookTime;
    private int ingrMatched;
    private double percentMatch;

    private ArrayList<String> ingredients;
    private String instructions;
    private String recipeUrl;
    private String imageUrl;

    //ANDREY - Adding Comparator

    /** Comparator method used to sort a list of recipies by cookTime
     * @author Andrey Barsukov
     * @return a comparator value
     */
    public static Comparator<Recipe> COMPARE_BY_PREPTIME = new Comparator<Recipe>() {
        public int compare(Recipe lhs, Recipe rhs) {
//            if(lhs.cookTime == -1 || lhs.cookTime == 0){
//                return 1;
//            }
            if(lhs.cookTime < rhs.cookTime){
                return -1;                  //less than: -1
            }
            else if(lhs.cookTime == rhs.cookTime){
                return 0;                   //equal: 0
            }
            else{
                return 1;                   //greater than: 1
            }
        }
    };

    /** Comparator method used to sort a list of recipies by ingrMatched
     * @author Andrey Barsukov
     * @return a comparator value
     */
    public static Comparator<Recipe> COMPARE_BY_INGR_MATCHED = new Comparator<Recipe>() {
        public int compare(Recipe lhs, Recipe rhs) {
            if(lhs.ingrMatched > rhs.ingrMatched){   //Sort decending order
                return -1;                  //less than: -1
            }
            else if(lhs.ingrMatched == rhs.ingrMatched){
                return 0;                   //equal: 0
            }
            else{
                return 1;                   //greater than: 1
            }
        }
    };

    /** Comparator method used to sort a list of recipies by percentMatched
     * @author Andrey Barsukov
     * @return a comparator value
     */
    public static Comparator<Recipe> COMPARE_BY_PERCENT_MATCHED = new Comparator<Recipe>() {
        public int compare(Recipe lhs, Recipe rhs) {
            if(lhs.percentMatch > rhs.percentMatch){   //Sort decending order
                return -1;                  //less than: -1
            }
            else if(lhs.percentMatch == rhs.percentMatch){
                return 0;                   //equal: 0
            }
            else{
                return 1;                   //greater than: 1
            }
        }
    };

    public Recipe(String origin, String id, String name, int cTime,
                  ArrayList<String> ingr, String inst, String recipeUrl) {
        originAPI = origin;
        recipeID = id;

        this.name = name;

        cookTime = cTime;

        ingredients = new ArrayList<String>();
        for (int i = 0; i < ingr.size(); i++)
            ingredients.add(ingr.get(i));

        instructions = inst;
        this.recipeUrl = recipeUrl;
    }

    public Recipe(String origin, String id, String name, String dishType, String cuisine,
                  int cookTime, ArrayList<String> ingr, String recipeUrl, String imageUrl) {
        originAPI = origin;
        recipeID = id;

        this.name = name;
        this.dishType = dishType;
        this.cuisine = cuisine;
        this.cookTime = cookTime;

        ingredients = ingr;

        this.recipeUrl = recipeUrl;
        this.imageUrl = imageUrl;
    }

    public Recipe(String origin, String id, String name, String recipeUrl) {
        this.originAPI = origin;
        this.recipeID = id;
        this.name = name;
        this.instructions = "";
        this.cookTime = 0;
        this.recipeUrl = recipeUrl;
        this.ingredients = new ArrayList<String>();
    }

    public String getAPI() { return originAPI; }
    public String getRecipeID() { return recipeID; }
    public String getName() { return name; }
    public String getDishType() { return dishType; }
    public String getCuisine() { return cuisine; }
    public int getCookTime() { return cookTime; }
    public int getIngrMatched() { return ingrMatched; }
    public double getPercentMatch() { return percentMatch; }
    public ArrayList<String> getIngredients() {
        ArrayList<String> temp = new ArrayList<String>();
        for (String ingredient : ingredients)
            temp.add(ingredient);
        return temp;
    }
    public String getRecipeUrl() { return recipeUrl; }
    public String getImageUrl() { return imageUrl; }

    public boolean addIngredient(String ingredient) {
        return ingredients.add(ingredient);
    }
    public boolean setRecipeType(String type) {
        if (type == null)
            return false;
        dishType = type;
        return dishType.equals(type);
    }
    public boolean setCuisine(String cuisine) {
        if (cuisine == null)
            return false;
        this.cuisine = cuisine;
        return this.cuisine.equals(cuisine);
    }
    public boolean setCookTime(int cookTime) {
        this.cookTime = cookTime;
        return this.cookTime == cookTime;
    }
    public boolean setIngrMatched(int ingrMatched){ //andrey
        this.ingrMatched = ingrMatched;
        return this.ingrMatched == ingrMatched;
    }
    public boolean setPercentMatch(double percentMatch){
        this.percentMatch = percentMatch;
        return this.percentMatch == percentMatch;
    }
    public boolean setRecipeURL(String recipeURL) {
        if (recipeURL == null)
            return false;
        this.recipeUrl = recipeURL;
        return this.recipeUrl.equals(recipeURL);
    }
    public boolean setRecipeImageURL(String imageURL) {
        if (imageURL == null)
            return false;
        this.imageUrl = imageURL;
        return this.imageUrl.equals(imageURL);
    }


    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(originAPI).append(".").append(name)
              .append(" { recipeID: ").append(recipeID)
              .append(", cookTime: ").append(cookTime)
              .append(", dishType: ").append(dishType)
              .append(", cuisine: ").append(cuisine)
              .append(", ingredients: [");
        for (int i = 0; i < ingredients.size(); i++) {
            output.append(ingredients.get(i));
            if (i < ingredients.size() - 1)
                output.append(",");
        }
        output.append("], ")
              .append("instructions: ").append(instructions)
              .append(", recipeUrl: ").append(recipeUrl)
              .append(", imageUrl: ").append(imageUrl)
              .append(" }");

        return output.toString();
    }
}
