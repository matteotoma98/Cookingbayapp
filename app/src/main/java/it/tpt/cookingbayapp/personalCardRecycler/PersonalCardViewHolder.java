package it.tpt.cookingbayapp.personalCardRecycler;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import it.tpt.cookingbayapp.R;
import it.tpt.cookingbayapp.RecipeClickListener;

public class PersonalCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView preview;
    CircleImageView profilePic;
    TextView title, user, type, time;

    //Interface object (ClickListener)
    RecipeClickListener recipeClickListener;

    public PersonalCardViewHolder(@NonNull View itemView) {
        super(itemView);
        preview = itemView.findViewById(R.id.myCardPreviewPic);
        profilePic = itemView.findViewById(R.id.myCardProfilePic);
        title = itemView.findViewById(R.id.myCardRecipeTitle);
        user = itemView.findViewById(R.id.myCardRecipeAuthor);
        type = itemView.findViewById(R.id.myCardRecipeType);
        time = itemView.findViewById(R.id.myCardRecipeTime);

        itemView.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        this.recipeClickListener.onRecipeClickListener(v, getLayoutPosition());
    }

    public void setRecipeClickListener(RecipeClickListener rc) {
        this.recipeClickListener = rc;
    }
}