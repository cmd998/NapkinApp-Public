package edu.sfsu.napkin.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import edu.sfsu.napkin.database.DbContract.*;

public class DbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Napkin.db";

    private static final String COMMA_SEP = ", ";

    // SQL queries
    private static final String SQL_CREATE_PREVIOUS_RECIPE_TABLE =
            "CREATE TABLE " + RecipeEntry.TABLE_NAME + " (" +
                    RecipeEntry.COLUMN_NAME_TIMESTAMP + RecipeEntry.TYPE_TIMESTAMP + COMMA_SEP +
                    RecipeEntry.COLUMN_NAME_API + RecipeEntry.TYPE_API + COMMA_SEP +
                    RecipeEntry.COLUMN_NAME_API_ID + RecipeEntry.TYPE_API_ID + COMMA_SEP +
                    RecipeEntry.COLUMN_NAME_NAME + RecipeEntry.TYPE_NAME + COMMA_SEP +
                    RecipeEntry.COLUMN_NAME_TYPE + RecipeEntry.TYPE_TYPE + COMMA_SEP +
                    RecipeEntry.COLUMN_NAME_CUISINE + RecipeEntry.TYPE_CUISINE + COMMA_SEP +
                    RecipeEntry.COLUMN_NAME_TIME + RecipeEntry.TYPE_TIME + COMMA_SEP +
                    RecipeEntry.COLUMN_NAME_INGREDIENTS + RecipeEntry.TYPE_INGREDIENTS + COMMA_SEP +
                    RecipeEntry.COLUMN_NAME_URL + RecipeEntry.TYPE_URL + COMMA_SEP +
                    RecipeEntry.COLUMN_NAME_IMAGE_URL + RecipeEntry.TYPE_IMAGE_URL +
            ")";
    private static final String SQL_CREATE_RECIPE_TABLE =
            "CREATE TABLE " + RecipeEntry.TABLE_NAME + " (" +
                    RecipeEntry.COLUMN_NAME_TIMESTAMP + RecipeEntry.TYPE_TIMESTAMP + COMMA_SEP +
                    RecipeEntry.COLUMN_NAME_API + RecipeEntry.TYPE_API + COMMA_SEP +
                    RecipeEntry.COLUMN_NAME_API_ID + RecipeEntry.TYPE_API_ID + COMMA_SEP +
                    RecipeEntry.COLUMN_NAME_NAME + RecipeEntry.TYPE_NAME + COMMA_SEP +
                    RecipeEntry.COLUMN_NAME_TYPE + RecipeEntry.TYPE_TYPE + COMMA_SEP +
                    RecipeEntry.COLUMN_NAME_CUISINE + RecipeEntry.TYPE_CUISINE + COMMA_SEP +
                    RecipeEntry.COLUMN_NAME_TIME + RecipeEntry.TYPE_TIME + COMMA_SEP +
                    RecipeEntry.COLUMN_NAME_INGREDIENTS + RecipeEntry.TYPE_INGREDIENTS + COMMA_SEP +
                    RecipeEntry.COLUMN_NAME_URL + RecipeEntry.TYPE_URL + COMMA_SEP +
                    RecipeEntry.COLUMN_NAME_IMAGE_URL + RecipeEntry.TYPE_IMAGE_URL +
                    ")";
    private static final String SQL_CREATE_ALLERGY_TABLE =
            "CREATE TABLE " + AllergyEntry.TABLE_NAME + " (" +
                    AllergyEntry.COLUMN_NAME_NAME + AllergyEntry.TYPE_NAME + COMMA_SEP +
                    AllergyEntry.COLUMN_NAME_INGREDIENT + AllergyEntry.TYPE_INGREDIENT + 
                    ")";
    private static final String SQL_CREATE_INGREDIENT_TABLE =
            "CREATE TABLE " + IngredientEntry.TABLE_NAME + " (" +
                    IngredientEntry.COLUMN_NAME_API + IngredientEntry.TYPE_API + COMMA_SEP +
                    IngredientEntry.COLUMN_NAME_DISPLAY_NAME + IngredientEntry.TYPE_DISPLAY_NAME + COMMA_SEP +
                    IngredientEntry.COLUMN_NAME_API_NAME + IngredientEntry.TYPE_API_NAME +
                    ")";

    private static final String SQL_DELETE_PREVIOUS_RECIPE_TABLE =
            "DROP TABLE IF EXISTS " + RecipeEntry.TABLE_NAME;
    private static final String SQL_DELETE_RECIPE_TABLE =
            "DROP TABLE IF EXISTS " + RecipeEntry.TABLE_NAME;
    private static final String SQL_DELETE_ALLERGY_TABLE =
            "DROP TABLE IF EXISTS " + AllergyEntry.TABLE_NAME;
    private static final String SQL_DELETE_INGREDIENT_TABLE =
            "DROP TABLE IF EXISTS " + IngredientEntry.TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PREVIOUS_RECIPE_TABLE);
        db.execSQL(SQL_CREATE_RECIPE_TABLE);
        db.execSQL(SQL_CREATE_ALLERGY_TABLE);
        db.execSQL(SQL_CREATE_INGREDIENT_TABLE);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_PREVIOUS_RECIPE_TABLE);
        db.execSQL(SQL_DELETE_RECIPE_TABLE);
        db.execSQL(SQL_DELETE_ALLERGY_TABLE);
        db.execSQL(SQL_DELETE_INGREDIENT_TABLE);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
