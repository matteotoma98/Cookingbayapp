package it.tpt.cookingbayapp.cardRecycler;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import it.tpt.cookingbayapp.R;
import it.tpt.cookingbayapp.RecipeClickListener;

public class RecipeCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    ImageView preview;
    CircleImageView profilePic;
    TextView title;
    TextView user;

    //Interface object (ClickListener)
    RecipeClickListener recipeClickListener;

    public RecipeCardViewHolder(@NonNull View itemView) {
        super(itemView);
        preview = itemView.findViewById(R.id.cardPreviewPic);
        profilePic = itemView.findViewById(R.id.cardProfilePic);
        title = itemView.findViewById(R.id.cardRecipeTitle);
        user = itemView.findViewById(R.id.cardRecipeAuthor);

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
