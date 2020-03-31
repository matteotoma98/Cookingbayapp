package it.tpt.cookingbayapp.ingredientsRecycler;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.tpt.cookingbayapp.R;

public class IngredientViewHolder extends RecyclerView.ViewHolder {

    TextView ingredient, quantity;

    public IngredientViewHolder(@NonNull View itemView) {
        super(itemView);
        ingredient = itemView.findViewById(R.id.ingredient);
        quantity = itemView.findViewById(R.id.quantity);
    }
}
