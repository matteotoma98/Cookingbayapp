package it.tpt.cookingbayapp.stepRecycler;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;
import it.tpt.cookingbayapp.R;
import it.tpt.cookingbayapp.RecyclerItemClickListener;

public class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView stepnumber, stepstar;
    Button delete;
    TextInputEditText steptext, stepHours, stepMinutes;
    CircleImageView imgStep;


    //Interface object
    RecyclerItemClickListener stepDeleteClickListener;
    RecyclerItemClickListener picAddClickListener;

    public StepViewHolder(@NonNull View itemView) {
        super(itemView);
        stepnumber = itemView.findViewById(R.id.stepNumber);
        delete = itemView.findViewById(R.id.deletestep);
        delete.setOnClickListener(this);
        steptext = itemView.findViewById(R.id.steptext);
        stepHours = itemView.findViewById(R.id.stepHours);
        stepMinutes = itemView.findViewById(R.id.stepMinutes);
        imgStep = itemView.findViewById(R.id.imgStepN);
        imgStep.setOnClickListener(this);
        stepstar = itemView.findViewById(R.id.starStepDesc);
        stepstar.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deletestep:
                this.stepDeleteClickListener.onItemClickListener(getAdapterPosition());
                break;
            case R.id.imgStepN:
                this.picAddClickListener.onItemClickListener(getAdapterPosition());
                break;
        }

    }

    public void setPicAddClickListener(RecyclerItemClickListener sc) {
        this.picAddClickListener = sc;
    }

    public void setStepDeleteClickListener(RecyclerItemClickListener sc) {
        this.stepDeleteClickListener = sc;
    }
}
