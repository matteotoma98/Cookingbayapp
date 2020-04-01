package it.tpt.cookingbayapp.stepRecycler;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.tpt.cookingbayapp.R;
import it.tpt.cookingbayapp.StepClickListener;

public class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView stepnumber;
    Button delete;

    //Interface object
    StepClickListener stepClickListener;

    public StepViewHolder(@NonNull View itemView) {
        super(itemView);
        stepnumber = itemView.findViewById(R.id.stepNumber);
        delete = itemView.findViewById(R.id.deletestep);
        delete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        this.stepClickListener.onDeleteListener(getLayoutPosition());
    }

    public void setStepClickListener(StepClickListener sc) {
        this.stepClickListener = sc;
    }
}
