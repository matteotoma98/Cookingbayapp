package it.tpt.cookingbayapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecipeCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    ImageView preview;
    TextView title;
    TextView user;

    //Interface object (ClickListener)
    RecipeClickListener recipeClickListener;

    public RecipeCardViewHolder(@NonNull View itemView) {
        super(itemView);
        preview = itemView.findViewById(R.id.preview);
        title = itemView.findViewById(R.id.title);
        user = itemView.findViewById(R.id.user);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        this.recipeClickListener.onRecipeClickListener(v, getLayoutPosition());
    }

    public void setRecipeClickListener(RecipeClickListener rc){
        this.recipeClickListener = rc;
    }
}
