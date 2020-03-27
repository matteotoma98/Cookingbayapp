package it.tpt.cookingbayapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ViewRecipeActivity extends AppCompatActivity {

    TextView recipeTitle, recipeAuthor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        recipeTitle = findViewById(R.id.recipeTitle);
        recipeAuthor = findViewById(R.id.recipeAuthor);

        Intent intent = getIntent();
        recipeTitle.setText(intent.getStringExtra("recipeTitle"));
        recipeAuthor.setText(intent.getStringExtra("recipeAuthor"));

        getSupportActionBar().setTitle(intent.getStringExtra("recipeTitle"));
    }
}
