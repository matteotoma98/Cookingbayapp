package it.tpt.cookingbayapp.ingredientsRecycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.tpt.cookingbayapp.R;
import it.tpt.cookingbayapp.recipeObject.Ingredient;

public class IngredientsRecyclerViewAdapter extends RecyclerView.Adapter<IngredientViewHolder> {

    private List<Ingredient> ingredients;

    public IngredientsRecyclerViewAdapter(List<Ingredient> ingredients){
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

    @Override
    public int getItemCount() {
        return ingredients.size();
    }
}
