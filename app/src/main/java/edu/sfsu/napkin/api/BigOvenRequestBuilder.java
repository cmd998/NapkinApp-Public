package edu.sfsu.napkin.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import edu.sfsu.napkin.Recipe;

/** Class which handles generating URLs for BigOven API requests as well as parsing the respones. **/
public class BigOvenRequestBuilder extends RequestBuilder implements RecipeRequestBuilder {

    private static BigOvenRequestBuilder instance = null;

    // BigOven API variables
    private final String HOST               = "https://api.bigoven.com";
    private final String EP_RECIPES_SEARCH  = "/recipes";
    private final String EP_RECIPE_SEARCH   = "/recipe";
    private final String API_KEY            = "I5rHrpaR8tiKd1Q0lwAbC6Jh625z6g46";
    private final String AUTH               = "api_key=" + API_KEY;

    private String prevQuery;
    private int queryOffset;

    /**
     * Creates the BigOven API object. Set to private to conform to the singleton pattern.
     */
    private BigOvenRequestBuilder() {
        baseUrl = HOST;
        authString = AUTH;
        prevQuery = "";
        queryOffset = 0;
    }

   /**
     * Makes the BigOven API instance accessible, in accordance to the singleton pattern.
     *
     * @return  The only instance of the BigOvenRequestBuilder.
     */
    public static BigOvenRequestBuilder getInstance() {
        if (instance == null)
            instance = new BigOvenRequestBuilder();
        return instance;
    }

    public String findRecipesByIngredients(String...ingredients) {
        HashMap<String, String> params = new HashMap<String, String>();
        StringBuilder url = new StringBuilder();

        params.put("rpp", "10"); // results per page (probably shouldn't hardcore but... :shrug:
        params.put("pg", "1");   // page 1
        params.put("api_key", API_KEY);

        try {
            url.append(buildQueryString(EP_RECIPES_SEARCH, params));

            for (int i = 0; i < ingredients.length; i++) {
                url.append("&any_kw=").append(URLEncoder.encode(ingredients[i], "UTF-8"));
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return url.toString();
    }

    /**
     * Returns a URL for searching for recipes by ingredients, with allowable exclusions
     * @param excludedIngredients A String array of ingredients which should be excluded from the recipes
     * @param ingredients A String array of ingredients to search for recipes with
     * @return A URL for the request
     */
    public String findRecipesByIngredients(String[] excludedIngredients, String...ingredients) {
        // Example URL
        // https://api.bigoven.com/recipes?api_key=I5rHrpaR8tiKd1Q0lwAbC6Jh625z6g46&any_kw=brussel+sprouts&pg=1&rpp=10

        HashMap<String, String> params = new HashMap<String, String>();
        StringBuilder includeThese = new StringBuilder("");
        StringBuilder excludeThese = new StringBuilder("");

        // since BigOven only allows us to search by 3 ingredients a time
        for (int i = 0; i < (ingredients.length > 3 ? 3 : ingredients.length); i++) {
            includeThese.append(ingredients[i]).append(",");
        }
        for (int i = 0; i < (excludedIngredients.length > 3 ? 3 : excludedIngredients.length); i++) {
            excludeThese.append(excludedIngredients[i]).append(",");
        }

        params.put("include_ing", includeThese.toString());
        params.put("exclude_ing", excludeThese.toString());
        params.put("api_key", API_KEY);
        params.put("pg", "1");
        params.put("rpp", "10");

        try {
            return buildQueryString(EP_RECIPES_SEARCH, params);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "";
    }

    public String findRecipeById(Recipe recipe) {
        // Example URL
        // https://api.bigoven.com/recipe/712854?api_key=I5rHrpaR8tiKd1Q0lwAbC6Jh625z6g46
        try {
            return buildQueryString(EP_RECIPE_SEARCH, recipe.getRecipeID());
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

    public ArrayList<Recipe> parseRecipes(JSONObject json) {
        ArrayList<Recipe> recipes = new ArrayList<>();

        try {
            JSONArray list = json.getJSONArray("Results");
            for (int i = 0; i < list.length(); i++) {
                JSONObject jsonData = list.getJSONObject(i);
                String recipeID, name, dishType, cuisine, recipeUrl, imageUrl;
                int cookTime;
                ArrayList<String> ingredients = new ArrayList<String>();

                // Optional fields, or fields that may not always show up for every query,
                // have opt() instead of get() calls
                recipeID  = jsonData.getString("RecipeID");
                name      = jsonData.getString("Title");
                dishType  = jsonData.optString("Category", "");
                cuisine   = jsonData.optString("Cuisine", "");
                cookTime  = jsonData.optInt("TotalMinutes", -1);
                recipeUrl = jsonData.optString("WebURL", "");
                imageUrl  = jsonData.optString("ImageURL120", "");

                recipes.add(new Recipe("BigOven",
                                       recipeID, name, dishType, cuisine, cookTime,
                                       ingredients, recipeUrl, imageUrl));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return recipes;
    }


    public Recipe parseRecipe(Recipe recipe, JSONObject json) {
        try {
            // Add ingredients to recipe
            JSONArray ingredients = json.getJSONArray("Ingredients");
            for (int i = 0; i < ingredients.length(); i++) {
                JSONObject ingredient = ingredients.getJSONObject(i);
                StringBuilder ingrStr = new StringBuilder();
                ingrStr.append(ingredient.getString("DisplayQuantity")).append(" ")
                       .append(ingredient.getString("Unit")).append(" ")
                       .append(ingredient.getString("Name"));
                if (ingredient.get("PreparationNotes") != null) {
                    ingrStr.append(", ").append(ingredient.getString("PreparationNotes"));
                }
                recipe.addIngredient(ingrStr.toString());
            }

            // Set cookTime (not available in /api/recipes call)
            recipe.setCookTime(json.optInt("TotalMinutes", -1));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return recipe;
    }
}