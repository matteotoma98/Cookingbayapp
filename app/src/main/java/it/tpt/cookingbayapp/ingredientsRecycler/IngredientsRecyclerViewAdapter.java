package it.tpt.cookingbayapp.ingredientsRecycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import it.tpt.cookingbayapp.R;
import it.tpt.cookingbayapp.recipeObject.Ingredient;

public class IngredientsRecyclerViewAdapter extends RecyclerView.Adapter<IngredientViewHolder> {

    private ArrayList<Ingredient> ingredients;

    public IngredientsRecyclerViewAdapter(ArrayList<Ingredient> ingredients){
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_layout, parent, false);
        return new IngredientViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        if (ingredients != null && position < ingredients.size()){
            holder.ingredient.setText(ingredients.get(position).getName());
            holder.quantity.setText(ingredients.get(position).getQuantity());
        }
    }

    public void addIngredient(Ingredient ing) {
        ingredients.add(ing);
        //notifyDataSetChanged();
        notifyItemInserted(ingredients.size() - 1);
    }

    public void delIngredient (){
        ingredients.remove(ingredients.size()-1);
        notifyDataSetChanged();
        //notifyItemRemoved(ingredients.size());
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }
}
