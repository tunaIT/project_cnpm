package com.example.food;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import DAO.DatabaseHelper;
import model.Recipes;
import model.RecipesAdapter;

public class MainActivity extends AppCompatActivity  implements SearchView.OnQueryTextListener {

    private final String DATABASE_NAME = "data.db";
    public static SQLiteDatabase database;
    private RecyclerView recycler;
    private RecipesAdapter recipesAdapter;
    private TextView favorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = DatabaseHelper.initDatabase(this, DATABASE_NAME);

        recycler = findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(linearLayoutManager);

        List<Recipes> recipesList = DatabaseHelper.getAllRecipes(database);
        recipesAdapter = new RecipesAdapter(this, recipesList);
        recycler.setAdapter(recipesAdapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recycler.addItemDecoration(itemDecoration);

        favorite = findViewById(R.id.favorite);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ListFavorite.class);
                startActivity(intent);
            }
        });

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(MainActivity.this);
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        List<Recipes> listSearch = DatabaseHelper.getListSearch(database, query);
        recipesAdapter.updateData(listSearch);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (recipesAdapter != null) {
            recipesAdapter.release();
        }
    }
}
