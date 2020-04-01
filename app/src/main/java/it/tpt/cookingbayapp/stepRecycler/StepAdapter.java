package it.tpt.cookingbayapp.stepRecycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.tpt.cookingbayapp.R;
import it.tpt.cookingbayapp.recipeObject.Ingredient;

public class StepAdapter extends RecyclerView.Adapter<StepViewHolder> {

    private List<Step> steps;

    public StepAdapter(List<Step> steps){
        this.steps = steps;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_item, parent, false);
        return new StepViewHolder(layoutView);
    }

    public void addStep(Step step){
        steps.add(step);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        if (steps != null && position < steps.size()){
            holder.stepnumber.setText(steps.get(position).getStepnumber());
        }
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }
}
