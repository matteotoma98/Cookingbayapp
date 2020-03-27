package it.tpt.cookingbayapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

public class RecipeCardRecyclerViewAdapter extends RecyclerView.Adapter<RecipeCardViewHolder> {

    private List<List<String>> recipeList;

    RecipeCardRecyclerViewAdapter() {
        List<String> entry = new ArrayList<String>();
        recipeList=new ArrayList<>();
        entry.add("Pizza Margerita");
        entry.add("Cracco");
        recipeList.add(entry);
    }

    @NonNull
    @Override
    public RecipeCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_card, parent, false);
        return new RecipeCardViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeCardViewHolder holder, int position) {
        if (recipeList != null && position < recipeList.size()) {
            List<String> recipe = recipeList.get(position);
            holder.title.setText(recipe.get(0));
            holder.user.setText(recipe.get(1));
            //imageRequester.setImageFromUrl(holder.productImage, product.url);
            holder.preview.setImageResource(R.drawable.maxresdefault);
        }
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }
}
