package it.tpt.cookingbayapp.ingNamesRecyler;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.tpt.cookingbayapp.R;

public class IngNamesViewHolder extends RecyclerView.ViewHolder {

    TextView ingredient;

    public IngNamesViewHolder(@NonNull View itemView) {
        super(itemView);
        ingredient = itemView.findViewById(R.id.ingNameTxtView);
    }

}
