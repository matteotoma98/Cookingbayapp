package it.tpt.cookingbayapp.stepRecycler;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import it.tpt.cookingbayapp.ImagePickActivity;
import it.tpt.cookingbayapp.R;
import it.tpt.cookingbayapp.StepClickListener;

public class StepAdapter extends RecyclerView.Adapter<StepViewHolder> {

    private List<Step> steps;
    private Context mContext;
    private final static int STEP_REQUEST = 236;
    private int currentPicPosition;
    private boolean disabled;

    public StepAdapter(List<Step> steps, Context context) {
        this.steps = steps;
        mContext = context;
        disabled = false;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_item, parent, false);
        final StepViewHolder holder = new StepViewHolder(layoutView);

        //Eliminare lo step
        holder.setStepDeleteClickListener(new StepClickListener() {
            @Override
            public void onItemClickListener(int position) {
                steps.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
            }
        });

        holder.setPicAddClickListener(new StepClickListener() {
            @Override
            public void onItemClickListener(int position) {
                setCurrentPicPosition(position);
                ((Activity) mContext).startActivityForResult(ImagePickActivity.getPickImageChooserIntent(mContext, ("step" + position)), STEP_REQUEST);
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
        holder.stepHours.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                steps.get(holder.getAdapterPosition()).setHours(holder.stepHours.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        holder.stepMinutes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                steps.get(holder.getAdapterPosition()).setMinutes(holder.stepMinutes.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return holder;
    }

    public void addStep() {
        steps.add(new Step("", Uri.parse("")));
        notifyItemInserted(steps.size() - 1);
    }

    public void removeStep(int position) {
        steps.remove(position);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        if (steps != null && position < steps.size()) {
            String stepn = "Step " + (position + 2);
            holder.stepnumber.setText(stepn);
            holder.steptext.setText(steps.get(position).getText());
            holder.stepHours.setText(steps.get(position).getHours());
            holder.stepMinutes.setText(steps.get(position).getMinutes());
            if (!steps.get(position).getStepUri().toString().equals("")) {
                Glide.with(mContext)
                        .load(steps.get(position).getStepUri())
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .centerCrop()
                        .into(holder.imgStep);
            } else {
                if (!steps.get(position).getUrl().equals("")) {
                    Glide.with(mContext)
                            .load(steps.get(position).getUrl())
                            .apply(RequestOptions.skipMemoryCacheOf(true))
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                            .centerCrop()
                            .into(holder.imgStep);
                } else Glide.with(mContext)
                        .load(R.drawable.step_dummy)
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .centerCrop()
                        .into(holder.imgStep);
            }
            if(disabled) {
                holder.stepMinutes.setEnabled(false);
                holder.stepHours.setEnabled(false);
                holder.steptext.setEnabled(false);
                holder.imgStep.setEnabled(false);
                holder.delete.setEnabled(false);
            }

        }
    }

    public List<Step> getSteps() {
        return steps;
    }

    public int getCurrentPicPosition() {
        return currentPicPosition;
    }

    public void setCurrentPicPosition(int currentPicPosition) {
        this.currentPicPosition = currentPicPosition;
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }
}
