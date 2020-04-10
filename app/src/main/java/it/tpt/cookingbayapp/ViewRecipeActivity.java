package it.tpt.cookingbayapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import it.tpt.cookingbayapp.ingredientsRecycler.IngredientsRecyclerViewAdapter;
import it.tpt.cookingbayapp.recipeObject.Recipe;

public class ViewRecipeActivity extends AppCompatActivity {

    //Section text è momentaneo
    TextView recipeTitle, recipeAuthor, recipeType, recipeTime;
    CircleImageView profilePic;
    ImageView previewPic;
    RecyclerView iRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        recipeTitle = findViewById(R.id.viewRecipeTitle);
        recipeAuthor = findViewById(R.id.viewRecipeAuthor);
        recipeTime = findViewById(R.id.viewRecipeTime);
        recipeType = findViewById(R.id.viewRecipeType);
        previewPic = findViewById(R.id.viewPreviewPic);
        profilePic = findViewById(R.id.viewProfilePic);

        Intent intent = getIntent();
        Recipe recipe = (Recipe) intent.getSerializableExtra("recipe");

        recipeTitle.setText(recipe.getTitle());
        recipeAuthor.setText(recipe.getAuthorName());
        recipeTime.setText(recipe.getTime());
        recipeType.setText(recipe.getType());

        if(recipe.getProfilePicUrl().toString().equals("missingprofile")){
            profilePic.setImageResource(R.drawable.missingprofile);
        }
        else Glide.with(this).load(recipe.getProfilePicUrl()).into(profilePic);
        Glide.with(this).load(recipe.getPreviewUrl()).into(previewPic);

        iRecyclerView = findViewById(R.id.ingRecycler_view);
        iRecyclerView.setHasFixedSize(true);
        iRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        IngredientsRecyclerViewAdapter adapter = new IngredientsRecyclerViewAdapter(recipe.getIngredients());
        iRecyclerView.setAdapter(adapter);



        getSupportActionBar().setTitle(recipe.getTitle());
    }
}
