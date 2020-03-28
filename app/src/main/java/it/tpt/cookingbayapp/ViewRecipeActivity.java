package it.tpt.cookingbayapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

import it.tpt.cookingbayapp.recipeObject.Section;

public class ViewRecipeActivity extends AppCompatActivity {

    //Section text è momentaneo
    TextView recipeTitle, recipeAuthor, sectionText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        recipeTitle = findViewById(R.id.recipeTitle);
        recipeAuthor = findViewById(R.id.recipeAuthor);
        sectionText = findViewById(R.id.sectionText);

        Intent intent = getIntent();
        ArrayList<Section> sectionList = (ArrayList<Section>) intent.getSerializableExtra("sectionList");

        recipeTitle.setText(intent.getStringExtra("recipeTitle"));
        recipeAuthor.setText(intent.getStringExtra("recipeAuthor"));
        sectionText.setText(sectionList.get(0).getText());

        getSupportActionBar().setTitle(intent.getStringExtra("recipeTitle"));
    }
}
