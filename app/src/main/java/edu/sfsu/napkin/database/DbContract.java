package edu.sfsu.napkin.database;

public class DbContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DbContract() {}

    // Inner class that defines the table contents

    public static final String TYPE_TEXT = " TEXT";
    public static final String TYPE_INT = " INTEGER";
    public static final String TYPE_DATETIME = " DATETIME";

    public static abstract class RecipeEntry {
        public static final String TABLE_NAME = "recipe";

        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_API = "api";
        public static final String COLUMN_NAME_API_ID = "api_id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_CUISINE = "cuisine";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_INGREDIENTS = "ingredients";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_IMAGE_URL = "image_url";

        // Declare columns' SQL types
        public static final String TYPE_TIMESTAMP = TYPE_DATETIME;
        public static final String TYPE_API = TYPE_TEXT;
        public static final String TYPE_API_ID = TYPE_TEXT;
        public static final String TYPE_NAME = TYPE_TEXT;
        public static final String TYPE_TYPE = TYPE_TEXT;
        public static final String TYPE_CUISINE = TYPE_TEXT;
        public static final String TYPE_TIME = TYPE_INT;
        public static final String TYPE_INGREDIENTS = TYPE_TEXT;
        public static final String TYPE_URL = TYPE_TEXT;
        public static final String TYPE_IMAGE_URL = TYPE_TEXT;

    }

    public static abstract class AllergyEntry {
        public static final String TABLE_NAME = "allergy";

        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_INGREDIENT = "ingredient";

        public static final String TYPE_NAME = TYPE_TEXT;
        public static final String TYPE_INGREDIENT = TYPE_TEXT;
    }

    public static abstract class IngredientEntry {
        public static final String TABLE_NAME = "ingredient";

        public static final String COLUMN_NAME_API = "api";
        public static final String COLUMN_NAME_DISPLAY_NAME = "disp_name";
        public static final String COLUMN_NAME_API_NAME = "api_name";

        public static final String TYPE_API = TYPE_TEXT;
        public static final String TYPE_DISPLAY_NAME = TYPE_TEXT;
        public static final String TYPE_API_NAME = TYPE_TEXT;
    }
}
