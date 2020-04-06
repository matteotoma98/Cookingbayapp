package it.tpt.cookingbayapp;

import android.view.View;

public interface RecipeClickListener {

    void onRecipeClickListener(View v, int position);
    void onDeleteClickListener(View v, int position);
    void onEditClickListener(View v, int position);
}
