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
        recipeList=new ArrayList<>();

        List<String> entry = new ArrayList<String>();
        entry.add("Pizza Margerita");
        entry.add("Cracco");
        List<String> entry2 = new ArrayList<String>();
        entry2.add("Pasta");
        entry2.add("Cracco");
        List<String> entry3 = new ArrayList<String>();
        entry3.add("Pasta");
        entry3.add("Cracco");

        recipeList.add(entry);
        recipeList.add(entry2);
        recipeList.add(entry3);
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
