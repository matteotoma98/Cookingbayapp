package it.tpt.cookingbayapp.personalCardRecycler;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import it.tpt.cookingbayapp.R;
import it.tpt.cookingbayapp.RecipeClickListener;
import it.tpt.cookingbayapp.ViewRecipeActivity;
import it.tpt.cookingbayapp.recipeObject.Recipe;

public class PersonalCardRecyclerViewAdapter extends RecyclerView.Adapter<PersonalCardViewHolder> {

    private List<Recipe> recipeList;
    Context mContext;

    public PersonalCardRecyclerViewAdapter(Context c, List<Recipe> recipeList) {
        mContext = c;
        this.recipeList = recipeList;

    }

    @NonNull
    @Override
    public PersonalCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_recipe_card, parent, false);
        PersonalCardViewHolder holder = new PersonalCardViewHolder(layoutView);
        //Visualizza la ricetta normalmente
        holder.setRecipeClickListener(new RecipeClickListener() {
            @Override
            public void onRecipeClickListener(View v, int position) {
                Intent intent = new Intent(mContext, ViewRecipeActivity.class);
                intent.putExtra("recipe", recipeList.get(position));
                mContext.startActivity(intent);
            }

            @Override
            public void onDeleteClickListener(View v, int position) {
                Toast.makeText(mContext, recipeList.get(position).getTitle() , Toast.LENGTH_LONG).show();
                recipeList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());

            }

            @Override
            public void onEditClickListener(View v, int position) {

            }
        });
        return holder;
    }

    //Assegna le informazioni della ricetta dinamicamente alla card
    @Override
    public void onBindViewHolder(@NonNull PersonalCardViewHolder holder, int position) {
        if (recipeList != null && position < recipeList.size()) {
            final Recipe recipe = recipeList.get(position);

            holder.title.setText(recipe.getTitle());
            holder.type.setText(recipe.getType());
            holder.time.setText(recipe.getTime());
            
            Glide.with(holder.preview.getContext()).load(recipe.getPreviewUrl()).into(holder.preview);

        }
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }
}