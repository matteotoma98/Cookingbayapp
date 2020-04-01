package it.tpt.cookingbayapp.cardRecycler;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import java.util.List;

import it.tpt.cookingbayapp.R;
import it.tpt.cookingbayapp.RecipeClickListener;
import it.tpt.cookingbayapp.ViewRecipeActivity;
import it.tpt.cookingbayapp.recipeObject.Recipe;

public class RecipeCardRecyclerViewAdapter extends RecyclerView.Adapter<RecipeCardViewHolder> {

    private List<Recipe> recipeList;
    Context mContext;

    public RecipeCardRecyclerViewAdapter(Context c, List<Recipe> recipeList) {
        mContext = c;
        this.recipeList = recipeList;

    }

    @NonNull
    @Override
    public RecipeCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_card, parent, false);
        return new RecipeCardViewHolder(layoutView);
    }

    //Assegna le informazioni della ricetta dinamicamente alla card
    @Override
    public void onBindViewHolder(@NonNull RecipeCardViewHolder holder, int position) {
        if (recipeList != null && position < recipeList.size()) {
            final Recipe recipe = recipeList.get(position);

            holder.title.setText(recipe.getTitle());
            holder.user.setText(recipe.getAuthor());
            holder.type.setText(recipe.getType());
            holder.time.setText(recipe.getTime());
            if(recipe.getProfilePicUrl().toString().equals("missingprofile")){
                holder.profilePic.setImageResource(R.drawable.missingprofile);
            }
            else Glide.with(holder.profilePic.getContext()).load(recipe.getProfilePicUrl()).into(holder.profilePic);
            Glide.with(holder.preview.getContext()).load(recipe.getPreviewUrl()).into(holder.preview);

            //Rendere cliccabile la card e passare le informazioni all'activity ViewRecipeActivity
            holder.setRecipeClickListener(new RecipeClickListener() {
                @Override
                public void onRecipeClickListener(View v, int position) {
                    Intent intent = new Intent(mContext, ViewRecipeActivity.class);
                    intent.putExtra("recipe", recipe);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }
}