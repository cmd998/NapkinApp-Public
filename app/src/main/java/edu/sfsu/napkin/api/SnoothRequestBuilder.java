package edu.sfsu.napkin.api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import edu.sfsu.napkin.Recipe;
import edu.sfsu.napkin.Wine;

public class SnoothRequestBuilder extends RequestBuilder implements DrinkRequestBuilder {
    private static SnoothRequestBuilder instance;

    // Snooth API variables
    private final String HOST              = "https://api.snooth.com";
    private final String EP_WINES_SEARCH   = "/wines";
    private final String EP_WINE_DETAILS   = "/wine";
    private final String API_KEY           = "vnqti5w26jhnn9donjgipwcbxlxapoh2mjukfdpo00q87czn";
    private final String AUTH              = "akey=" + API_KEY;

    private SnoothRequestBuilder() {
        baseUrl = HOST;
        authString = AUTH;
    }

    public static SnoothRequestBuilder getInstance() {
        if (instance == null)
            instance = new SnoothRequestBuilder();
        return instance;
    }

    // Algorithm based off of the following infographic
    // http://winefolly.com/wp-content/uploads/2013/01/wine-and-food-pairing-chart.png
    public String findByRecipe(Recipe r) {
        HashMap<String, String> params = new HashMap<String, String>();
        String query, color = "", type = "";

        query = getWineType(r);
        if (query.contains("sparkling")) {
            type = "sparkling";
        }
        else if (query.contains("dessert")) {
            type = "dessert";
        }
        else if (query.contains("white")) {
            color = "white";
            type = "wine";
        }
        else {
            color = "red";
            type = "wine";
        }

        // Change query to allow for region specific wines, e.g. champagne, moscato d'asti, ice wines
        query = "wine";


        params.put("q", query);
        params.put("t", type);
        // Skip if the wine is sparkling or a dessert wine
        if (!color.isEmpty() && !(color.equals("sparkling") || color.equals("dessert"))) {
            params.put("color", color);
        }
        params.put("s", "qpr");
        params.put("akey", API_KEY);

        try {
            return buildQueryString(EP_WINES_SEARCH, params);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "";
    }

    public String findWineById(Wine w) {
        return baseUrl + EP_WINE_DETAILS + "?id=" + w.getId() + "&" + AUTH;
    }

    public ArrayList<Wine> parseDrinks(JSONObject json) {
        ArrayList<Wine> wines = new ArrayList<Wine>();
        try {
            // Check if API request was valid; exit if it was not
            JSONObject meta = json.getJSONObject("meta");
            int status = meta.getInt("status");
            if (status == 0)
                return wines;

            JSONArray wineList = json.getJSONArray("wines");
            for (int i = 0; i < wineList.length(); i++) {
                JSONObject wine = wineList.getJSONObject(i);

                int vintage = wine.optInt("vintage", 0);
                String name = wine.getString("name");
                String id = wine.getString("code");
                String winery = wine.optString("winery", "Unknown");
                String varietal = wine.optString("varietal", "Unknown");
                String wineType = wine.optString("type", "Unknown");
                String imageUrl = wine.optString("image", "");
                imageUrl = imageUrl.replace("\\", "");

                wines.add(new Wine(vintage, name, id, winery, varietal, wineType, imageUrl));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return wines;
    }

    public Wine parseWine(Wine w, JSONObject json) {
        try {

            // Check if API request was valid; exit if it was not
            JSONObject meta = json.getJSONObject("meta");
            int status = meta.getInt("status");
            if (status == 0)
                return w;

            JSONObject details = json.getJSONArray("wines").getJSONObject(0);
            String description = details.optString("wm_notes", "No description provided.");
            if (description.isEmpty())
                description = "No description provided.";

            w.setDescription(description);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return w;
    }

    private String getWineType(Recipe r) {
        String color = "";
        String type = "";
        int dry_w, sweet_w, rich_w, sparkling, light_r, medium_r, bold_r, dessert;
        dry_w = sweet_w = rich_w = sparkling = light_r = medium_r = bold_r = dessert = 0;

        // Attempt to short circuit ingredient by ingredient comparisons by looking for dessert recipes
        // Doesn't catch cheese platters, though, if there's recipes for those
        String name = r.getName().toLowerCase();
        if (r.getDishType().toLowerCase().contains("dessert") || name.contains("dessert") ||
                // Pies, excl. pot pies, shepherd's pies
                (name.contains(" pie") && !(name.contains("shepherd") || name.contains("pot "))) ||
                // Cakes, excl. crab, lobster, shrimp cakes
                (name.contains("cake") && !(name.contains("lobster") || name.contains("crab") || name.contains("shrimp"))) || // excl
                name.contains("cookie") || name.contains("chocolate") || name.contains("sugar") ||
                name.contains("sorbet") || name.contains("gelato") || name.contains("ice cream"))
            return "dessert";
        else {
            ArrayList<String> ingredients = r.getIngredients();
            for (String ingr : ingredients) {
                String ingrType = getIngredientType(ingr);
                switch (ingrType) {
                    case "red meat":
                        medium_r++;
                        bold_r++;
                        break;
                    case "white meat":
                        rich_w++;
                        light_r++;
                        medium_r++;
                        break;
                    case "cured meat":
                        dry_w--;
                        rich_w--;
                        sparkling--;
                        break;
                    case "fish":
                        dry_w++;
                        rich_w++;
                        sparkling++;
                        break;
                    case "rich fish":
                        rich_w++;
                        light_r++;
                        break;
                    case "starches":
                        sweet_w--;
                        break;
                    case "hard cheese":
                        sweet_w++;
                        sparkling++;
                        medium_r++;
                        bold_r++;
                        break;
                    case "soft cheese":
                        sweet_w++;
                        rich_w++;
                        sparkling++;
                        dessert++;
                        break;
                    case "veggie":
                        dry_w++;
                        break;
                }
            }
        }

        // Algorithm will recommend red wines over white wines, with the bolder/richer wines getting priority
        int[] wineScores = {bold_r, medium_r, light_r, sparkling, rich_w, sweet_w, dry_w, dessert};
        String[] wineTypes = {"bold red", "medium red", "light red", "sparkling", "rich white", "sweet white", "dry white", "dessert"};
        int maxScore = Integer.MIN_VALUE;
        for (int i = 0; i < wineScores.length; i++) {
            if (wineScores[i] > maxScore) {
                maxScore = wineScores[i];
                type = wineTypes[i];
            }
        }

        return type;
    }

    private String getIngredientType(String ingr) {

        // Red meat
        // Beef excl. corned beef
        // Duck, as well as poultry thighs, wings
        if ((ingr.contains("beef") && !ingr.contains("corned")) || ingr.contains("duck") ||
                ingr.contains("thigh") || ingr.contains("wing")) {
            return "red meat";
        }
        // White meat
        // Poultry breasts
        // Pork
        if (ingr.contains("breast") || ingr.contains("pork")) {
            return "white meat";
        }
        // Cured meat
        if (ingr.contains("ham") || ingr.contains("bacon") || ingr.contains("salami") ||
                ingr.contains("prosciutto") || ingr.contains("pepperoni") ||
                ingr.contains("soppresata") || ingr.contains("corned") || ingr.contains("lox") ||
                ingr.contains("pancetta") || ingr.contains("capicola") || ingr.contains("lardon")) {
            return "cured meat";
        }
        // Fish
        // Excl. Chilean sea bass
        if ((ingr.contains("bass") && !ingr.contains("chilean")) ||
                ingr.contains("tuna") || ingr.contains("tilapia") ||
                ingr.contains("clam") || ingr.contains("crab") || ingr.contains("shrimp") ||
                ingr.contains("oyster") || ingr.contains("sole") || ingr.contains("catfish") ||
                ingr.contains("tilapia") || ingr.contains("craw") || ingr.contains("lobster") ||
                ingr.contains("mussel") || ingr.contains("flounder") || ingr.contains("fluke") ||
                ingr.contains("pollock") || ingr.contains("haddock") || ingr.contains("cod")) {
            return "fish";
        }
        // Rich fish
        if (ingr.contains("salmon") || ingr.contains("bass") ||
                ingr.contains("mackerel") || ingr.contains("mahi") || ingr.contains("swordfish")) {
            return "rich fish";
        }
        // Starches
        if (ingr.contains("flour") || ingr.contains("bread") || ingr.contains("potato") ||
                ingr.contains("rice") || ingr.contains("noodles") || ingr.contains("dough") ||
                ingr.contains("baguette") || ingr.contains("tortilla") || ingr.contains("meal") ||
                ingr.contains("ciabatta")) {
            return "starches";
        }
        // Hard cheese
        if (ingr.contains("cheese") || ingr.contains("gruyere") || ingr.contains("american") ||
                ingr.contains("cheddar") || ingr.contains("gorgonzola") || ingr.contains("edam") ||
                ingr.contains("roquefort") || ingr.contains("gouda") || ingr.contains("emmental") ||
                ingr.contains("asiago") || ingr.contains("parmesan") || ingr.contains("parmigiano") ||
                ingr.contains("pecorino") || ingr.contains("provolone")) {
            return "hard cheese";
        }
        // Soft cheese
        if (ingr.contains("mozzarella") || ingr.contains("brie") || ingr.contains("burrata") ||
                ingr.contains("camembert") || ingr.contains("ricotta") || ingr.contains("neufchatel") ||
                ingr.contains("mascarpone") || ingr.contains("marscapone")) {
            return "soft cheese";
        }
        // Spices & seasonings
        if (ingr.contains("salt") || ingr.contains("sugar") || ingr.contains("rosemary") ||
                (ingr.contains("pepper") && !(ingr.contains("bell") || ingr.contains("roast"))) ||
                ingr.contains("thyme") || ingr.contains("lemongrass") || ingr.contains("chili") ||
                ingr.contains("sage") || ingr.contains("bay lea") || ingr.contains("old bay") ||
                ingr.contains("seasoning") || ingr.contains("spice") || ingr.contains("paprika") ||
                ingr.contains("parsley") || ingr.contains("cilantro") || ingr.contains("coriander") ||
                ingr.contains("allspice") || ingr.contains("cumin") || ingr.contains("anise") ||
                ingr.contains("soy sauce") || ingr.contains("vinegar") || ingr.contains("wine") ||
                ingr.contains("nutmeg") || ingr.contains("clove")) {
            return "seasoning";
        }

        // Vegetables
        return "veggie";
    }
}
