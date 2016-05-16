package edu.sfsu.napkin.api;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import edu.sfsu.napkin.Recipe;

public interface DrinkRequestBuilder {
    String findByRecipe(Recipe r);
    ArrayList parseDrinks(JSONObject json);
}
