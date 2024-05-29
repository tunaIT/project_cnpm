package DAO;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.food.MainActivity;
import com.example.food.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import model.Recipes;

public class DatabaseHelper {

    public static SQLiteDatabase initDatabase(Activity activity, String name) {
        try {
            String outFileName = activity.getApplicationInfo().dataDir +"/databases/" + name;
            File f = new File(outFileName);
            if (f.exists()) {
                InputStream e = activity.getAssets().open(name);
                File folder = new File(activity.getApplicationInfo().dataDir +"/databases/");
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                FileOutputStream output = new FileOutputStream(outFileName);
                byte[] buffer = new byte[1024];

                int length;
                while ((length = e.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
                output.flush();
                output.close();
                e.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return activity.openOrCreateDatabase(name, Context.MODE_PRIVATE, null);
    }

    public static List<Recipes> getAllRecipes(SQLiteDatabase db) {
        List<Recipes> recipesList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM recipes", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String instructions = cursor.getString(2);
                String image = cursor.getString(3);
                Recipes recipe = new Recipes(id, title, instructions, image);
                recipesList.add(recipe);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return recipesList;
    }
    public static List<Recipes> getListSearch(SQLiteDatabase db, String textSearch) {
            List<Recipes> result = new ArrayList<>();
            Cursor cursor = db.rawQuery("SELECT * FROM recipes WHERE title LIKE ?", new String[]{"%" + textSearch + "%"});

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(0);
                    String title = cursor.getString(1);
                    String instructions = cursor.getString(2);
                    String image = cursor.getString(3);
                    Recipes recipe = new Recipes(id, title, instructions, image);
                    result.add(recipe);
                } while (cursor.moveToNext());
            }

            cursor.close();
            return result;
        }

        public static long addFavoriteRecipe(Activity activity, int userId, int recipeId) {
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("recipe_id", recipeId);
        SQLiteDatabase db = initDatabase(activity, "data.db");
        return db.insert("favorite_recipes", null, values);
    }

    public static List<Recipes> getRecipeFavorite(SQLiteDatabase db, int user_id) {
        List<Recipes> recipesList = new ArrayList<>();
        String query = "SELECT recipes.id, recipes.title, recipes.instructions, recipes.image " +
                "FROM recipes " +
                "INNER JOIN favorite_recipes ON recipes.id = favorite_recipes.recipe_id " +
                "WHERE favorite_recipes.user_id = ?";

        Cursor cursor = db.rawQuery(query,  new String[]{String.valueOf(user_id)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String instructions = cursor.getString(2);
                String image = cursor.getString(3);
                Recipes recipe = new Recipes(id, title, instructions, image);
                recipesList.add(recipe);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return recipesList;
    }

    public static String getIngredientName(SQLiteDatabase db, int recipeId) {
        String re = "";
        String query = "Select name from ingredients where recipe_id = ?";
        Cursor cursor = db.rawQuery(query,  new String[]{String.valueOf(recipeId)});
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                re += name + "\n";
            } while (cursor.moveToNext());
        }
        cursor.close();
        return re;
    }
}
