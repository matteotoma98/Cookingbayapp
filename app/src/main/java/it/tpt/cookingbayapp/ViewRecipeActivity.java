package it.tpt.cookingbayapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import it.tpt.cookingbayapp.recipeObject.Recipe;
import it.tpt.cookingbayapp.recipeObject.Section;

public class ViewRecipeActivity extends AppCompatActivity {

    //Section text è momentaneo
    TextView recipeTitle, recipeAuthor, sectionText;
    CircleImageView profilePic;
    ImageView previewPic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        recipeTitle = findViewById(R.id.viewRecipeTitle);
        recipeAuthor = findViewById(R.id.viewRecipeAuthor);
        sectionText = findViewById(R.id.sectionText);
        previewPic = findViewById(R.id.viewPreviewPic);
        profilePic = findViewById(R.id.viewProfilePic);

        Intent intent = getIntent();
        Recipe recipe = (Recipe) intent.getSerializableExtra("recipe");

        recipeTitle.setText(recipe.getTitle());
        recipeAuthor.setText(recipe.getAuthor());
        sectionText.setText(recipe.getSections().get(0).getText());

        if(recipe.getProfilePicUrl().toString().equals("missingprofile")){
            profilePic.setImageResource(R.drawable.missingprofile);
        }
        else Glide.with(this).load(recipe.getProfilePicUrl()).into(profilePic);
        Glide.with(this).load(recipe.getPreviewUrl()).into(previewPic);


        getSupportActionBar().setTitle(recipe.getTitle());
    }
}
