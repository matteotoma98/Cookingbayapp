package it.tpt.cookingbayapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecipeCardViewHolder extends RecyclerView.ViewHolder {

    ImageView preview;
    TextView title;
    TextView user;

    public RecipeCardViewHolder(@NonNull View itemView) {
        super(itemView);
        preview = itemView.findViewById(R.id.preview);
        title = itemView.findViewById(R.id.title);
        user = itemView.findViewById(R.id.user);
    }
}
