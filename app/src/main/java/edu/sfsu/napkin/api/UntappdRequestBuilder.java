package edu.sfsu.napkin.api;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import edu.sfsu.napkin.Recipe;
import edu.sfsu.napkin.Beer;

public class UntappdRequestBuilder extends RequestBuilder {
    private static UntappdRequestBuilder instance;

    // Untappd API variables
    private final String HOST            = "https://api.untappd.com/v4";
    private final String EP_BEER_SEARCH  = "/search/beer";
    private final String CLIENT_ID       = "7772605F6E671BA3624D42BD48302C14A07564CF";
    private final String CLIENT_SECRET   = "FDC99724F16C6094B1A8D822DAB93CE05E33C1A8";
    private final String AUTH            = "client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET;

    public static UntappdRequestBuilder getInstance() {
        if (instance == null)
            instance = new UntappdRequestBuilder();
        return instance;
    }

    // Algorithm based off the following infographic
    // http://www.craftbeer.com/wp-content/uploads/2009/11/beer_food_guide_web1.jpg
    public String findByRecipe(Recipe r) {
        HashMap<String, String> params = new HashMap<String, String>();

        ArrayList<String> ingredients = r.getIngredients();
        for (int i = 0; i < ingredients.size(); i++) {
            String temp = ingredients.get(i);
            if (temp.indexOf(" ") < 0)
                continue;

        }

        return null;
    }

    public ArrayList<Beer> parseDrinks(JSONObject json) {
        ArrayList<Beer> beers = new ArrayList<Beer>();

        return beers;
    }
}
