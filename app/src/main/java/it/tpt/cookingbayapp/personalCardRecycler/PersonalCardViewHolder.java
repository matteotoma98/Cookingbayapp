package it.tpt.cookingbayapp.personalCardRecycler;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import it.tpt.cookingbayapp.R;
import it.tpt.cookingbayapp.RecipeClickListener;

public class PersonalCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView preview;
    TextView title, user, type, time;
    Button delete, edit;

    //Interface object (ClickListener)
    RecipeClickListener recipeClickListener;

    public PersonalCardViewHolder(@NonNull View itemView) {
        super(itemView);
        preview = itemView.findViewById(R.id.myCardPreviewPic);
        title = itemView.findViewById(R.id.myCardRecipeTitle);
        type = itemView.findViewById(R.id.myCardRecipeType);
        time = itemView.findViewById(R.id.myCardRecipeTime);
        delete = itemView.findViewById(R.id.deleterecipe);
        edit = itemView.findViewById(R.id.modifyrecipe);

        delete.setOnClickListener(this);
        edit.setOnClickListener(this);

        itemView.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.deleterecipe:
                this.recipeClickListener.onDeleteClickListener(v, getAdapterPosition());
                break;
            case R.id.modifyrecipe:
                this.recipeClickListener.onEditClickListener(v, getAdapterPosition());
                break;
            case R.id.my_card:
                this.recipeClickListener.onRecipeClickListener(v, getLayoutPosition());
                break;
        }
    }

    public void setRecipeClickListener(RecipeClickListener rc) {
        this.recipeClickListener = rc;
    }
}