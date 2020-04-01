package it.tpt.cookingbayapp.stepRecycler;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.tpt.cookingbayapp.R;

public class StepViewHolder extends RecyclerView.ViewHolder {

    TextView stepnumber;

    public StepViewHolder(@NonNull View itemView) {
        super(itemView);
        stepnumber = itemView.findViewById(R.id.stepNumber);
    }
}
