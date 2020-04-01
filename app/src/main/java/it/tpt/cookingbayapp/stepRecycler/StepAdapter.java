package it.tpt.cookingbayapp.stepRecycler;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.tpt.cookingbayapp.R;
import it.tpt.cookingbayapp.StepClickListener;

public class StepAdapter extends RecyclerView.Adapter<StepViewHolder> {

    private List<Step> steps;

    public StepAdapter(List<Step> steps) {
        this.steps = steps;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_item, parent, false);
        final StepViewHolder holder = new StepViewHolder(layoutView);

        //Eliminare lo step
        holder.setStepClickListener(new StepClickListener() {
            @Override
            public void onDeleteListener(int position) {
                steps.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
            }
        });

        //Listener dei cambiamenti di testo nell'holder per poterli assegnare in tempo reale allo <Step object> nella lista "steps"
        holder.steptext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                steps.get(holder.getAdapterPosition()).setText(holder.steptext.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return holder;
    }

    public void addStep(Step step) {
        steps.add(step);
        //notifyDataSetChanged();
        notifyItemInserted(steps.size() - 1 );
    }

    public void removeStep(int position) {
        steps.remove(position);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        if (steps != null && position < steps.size()) {
            String steptext = "Step " + (position+2);
            holder.stepnumber.setText(steptext);
            holder.steptext.setText(steps.get(position).getText());

        }
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }
}
