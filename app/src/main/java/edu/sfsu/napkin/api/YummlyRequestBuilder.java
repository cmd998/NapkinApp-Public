package edu.sfsu.napkin.api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import edu.sfsu.napkin.Recipe;

/**
 * Builds Yummly API request URLs and parses the response into Recipe objects
 */

public class YummlyRequestBuilder extends RequestBuilder implements RecipeRequestBuilder {

    private final int SECONDS_IN_MINUTE = 60;

    private static YummlyRequestBuilder instance = null;

    // Yummly API variables
    private final String HOST              = "https://api.yummly.com/v1";

    // EP is a prefix used for endpoints
    private final String EP_RECIPES_SEARCH = "/api/recipes";
    private final String EP_RECIPE_SEARCH  = "/api/recipe";

    // credentials/ids/identifiers/keys/whatever is necessary for auth
    private final String APP_ID            = "9170c9f2";
    private final String APP_KEY           = "614ef3bc75cfebe0dfde1e6b3be6cd4b";

    private final String AUTH              = "_app_id=" + APP_ID + "&_app_key=" + APP_KEY;

    // Keeps track of previous queries
    private String prevQuery;
    private int queryOffset;

    /**
     * Creates the Yummly API object. Set to private to conform to the singleton pattern.
     */
    private YummlyRequestBuilder() {
        baseUrl     = HOST;
        authString  = AUTH;
        prevQuery   = "";
        queryOffset = 0;
    }

    /**
     * Makes the Yummly API instance accessible, in accordance to the singleton pattern.
     *
     * @return  The only instance of the YummlyRequestBuilder.
     */
    public static YummlyRequestBuilder getInstance() {
        if (instance == null)
            instance = new YummlyRequestBuilder();
        return instance;
    }

    public String findRecipesByIngredients(String...ingredients) {
        HashMap<String, String> params = new HashMap<>();
        StringBuilder url = new StringBuilder();
        params.put("_app_id", APP_ID);
        params.put("_app_key", APP_KEY);

        try {
            url.append(buildQueryString(EP_RECIPES_SEARCH, params));
            for (String ingr : ingredients) {
                url.append("&allowedIngredient[]=").append(URLEncoder.encode(ingr, "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return url.toString();
    }

    public String findRecipeById(Recipe recipe) {
        System.out.println(HOST + EP_RECIPE_SEARCH + "/" + recipe.getRecipeID() + "?" + AUTH);
        try {
            return buildQueryString(EP_RECIPE_SEARCH, recipe.getRecipeID());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public ArrayList<Recipe> parseRecipes(JSONObject json) {
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();
        JSONArray list;

        try {
            list = json.getJSONArray("matches");
            System.out.println("JSONArray length (matches): " + list.length());
            for (int i = 0; i < list.length(); i++) {
                JSONObject jsonData = list.getJSONObject(i);

                ArrayList<String> ingredients = new ArrayList<String>();

                String instructions = "";
                String recipeURL = "";
                try {
                    String recipeID = jsonData.getString("id");
                    String name = jsonData.getString("recipeName");

                    // Set dishType, cuisine
                    JSONObject attr = jsonData.optJSONObject("attributes");
                    String dishType = "";
                    String dishCuisine = "";
                    if (attr != null) {
                        JSONArray course = attr.optJSONArray("course");
                        if (course != null && course.length() > 0)
                            dishType = course.getString(0);

                        JSONArray cuisine = attr.optJSONArray("cuisine");
                        if (cuisine != null && cuisine.length() > 0)
                            dishCuisine = cuisine.getString(0);
                    }

                    // Had an interesting issue pop up early in the project's history...

                    // Original code
                    // Throws JSONException, says totalTimeInSeconds is a null object (even though it's there)
                    // cookTime = jsonData.getInt("totalTimeInSeconds") / SECONDS_IN_MINUTE;

                    /* Attempt 1
                     * Add null check to field
                     * Complains it can't cast JSONObject into Integer
                     */
                    // JSONObject temp = (JSONObject) jsonData.get("totalTimeInSeconds");
                    // if (temp != null) {
                    //     cookTime = ((Integer) temp).intValue() / SECONDS_IN_MINUTE;
                    // }

                    /* Attempt 2
                     * Use intermediary temp var, try to cast to Integer & get int value
                     * Stack trace complains, saying .get() returns JSONObject which cannot be cast to Integer
                     * (even though method signature should return Object)
                     */
                    // Integer temp = (Integer) jsonData.get("totalTimeInSeconds");
                    // cookTime = temp.intValue() / SECONDS_IN_MINUTE;

                    /* Attempt 4
                     * Works, but cookTime for some Recipe in ArrayList can now be 0
                     */
                    int cookTime = jsonData.optInt("totalTimeInSeconds", -60) / SECONDS_IN_MINUTE;

                    JSONArray ingr = jsonData.getJSONArray("ingredients");
                    for (int j = 0; j < ingr.length(); j++)
                        ingredients.add(ingr.getString(j));

                    recipes.add(new Recipe("Yummly",
                                           recipeID, name, dishType, dishCuisine, cookTime,
                                           ingredients, instructions, recipeURL));
                    System.out.print(" - " + recipes.get(recipes.size() - 1) + ", ");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return recipes;
    }

    public Recipe parseRecipe(Recipe recipe, JSONObject json) {
        JSONObject images, source, attributes;

        try {
            // Recipe Image URL
            if (json.has("images")) {
                images = json.getJSONArray("images").getJSONObject(0);
                String imageURL = images.getJSONObject("imageUrlsBySize").getString("90");
                recipe.setRecipeImageURL(imageURL);
            }
            else {
                recipe.setRecipeImageURL("None");
            }

            // Recipe source URL
            if (json.has("source")) {
                source = json.getJSONObject("source");
                String recipeURL = source.getString("sourceRecipeUrl");
                recipe.setRecipeURL(recipeURL);
            }
            else {
                source = json.getJSONObject("attribution");
                String recipeURL = source.getString("url");
                recipe.setRecipeURL(recipeURL);
            }

            // Recipe Attributes - Dish type and Cuisine
            if (json.has("attributes")) {
                attributes = json.getJSONObject("attributes");

                // Recipe Dish Type
                if (json.has("course")) {
                    JSONArray courseArray = attributes.getJSONArray("course");
                    System.out.println("courseArray length: " + courseArray.length());
                    System.out.println(courseArray);
                    String course = courseArray.length() > 0 ? courseArray.getString(0) : "Unknown";
                    recipe.setRecipeType(course);
                }
                else {
                    recipe.setRecipeType("Unknown");
                }

                // Recipe Cuisine
                if (json.has("cuisine")) {
                    JSONArray cuisineArray = attributes.getJSONArray("cuisine");
                    String cuisine = cuisineArray.length() > 0 ? cuisineArray.getString(0) : "Unknown";
                    recipe.setCuisine(cuisine);
                }
                else {
                    recipe.setCuisine("Unknown");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return recipe;
    }
}
