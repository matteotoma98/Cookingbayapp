package it.tpt.cookingbayapp.ingNamesRecyler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.tpt.cookingbayapp.R;

public class IngNamesAdapter extends RecyclerView.Adapter<IngNamesViewHolder> {

    private ArrayList<String> ingredients;

    public IngNamesAdapter(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public IngNamesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingname_item, parent, false);
        return new IngNamesViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull IngNamesViewHolder holder, int position) {
        if (ingredients != null && position < ingredients.size()) {
            String name = ingredients.get(position);
            holder.ingredient.setText(name.substring(0, 1).toUpperCase() + name.substring(1));
        }
    }

    public void addIngredient(String ing) {
        ingredients.add(ing.trim().toLowerCase());
        notifyItemInserted(ingredients.size() - 1);
    }

    public void delIngredient() {
        if (getItemCount() != 0) {
            ingredients.remove(ingredients.size() - 1);
            notifyItemRemoved(ingredients.size());
        }
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }
}
