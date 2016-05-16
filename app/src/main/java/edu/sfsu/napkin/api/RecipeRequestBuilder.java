package edu.sfsu.napkin.api;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import edu.sfsu.napkin.Recipe;

/** Interface used for Recipe API classes **/
public interface RecipeRequestBuilder {
    /**
     * {@inheritDoc}
     * Returns a url which can be used for getting more details about a recipe
     * @param recipe An instantiated recipe object
     * @return A URL for the request
     */
    String findRecipeById(Recipe recipe);

    /**
     * {@inheritDoc}
     * Returns a URL for searching for recipes by ingredients
     * @param ingredients A String array of ingredients to search for recipes with
     * @return A String url which can be used to search BigOven for the given ingredients
     */
    String findRecipesByIngredients(String...ingredients) throws UnsupportedEncodingException;
    /**
     * {@inheritDoc}
     * Parses a JSON response from the API and returns a list of Recipe objects
     * @param json JSON response from a recipe search
     * @return A list of Recipe objects
     */
    ArrayList<Recipe> parseRecipes(JSONObject json);

    /**
     * {@inheritDoc}
     * Parses a JSON response from the API and returns a Recipe object
     * @param json JSON response from a recipe search
     * @return A Recipe object
     */
    Recipe parseRecipe(Recipe recipe, JSONObject json);
}
