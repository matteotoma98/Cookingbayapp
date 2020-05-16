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
import it.tpt.cookingbayapp.RecyclerItemClickListener;

public class StepAdapter extends RecyclerView.Adapter<StepViewHolder> {

    private List<Step> steps;
    private Context mContext;
    private final static int STEP_REQUEST = 236;
    private int currentPicPosition;
    private boolean disabled;
    private boolean editing;

    public StepAdapter(List<Step> steps, Context context) {
        this.steps = steps;
        mContext = context;
        disabled = false;
        editing = false;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_item, parent, false);
        final StepViewHolder holder = new StepViewHolder(layoutView);

        if (viewType == 0) {
            holder.delete.setVisibility(View.GONE); //Il primo step non può essere eliminato perciò il bottone elimina viene nascosto
            holder.stepstar.setVisibility(View.VISIBLE);
        }
        //Imposta il ClickListener per eliminare lo step
        holder.setStepDeleteClickListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                steps.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
            }
        });

        holder.setPicAddClickListener(new RecyclerItemClickListener() {
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

    /**
     * Aggiunge uno step alla lista e notifica dell'aggiunta
     */
    public void addStep() {
        steps.add(new Step("", Uri.parse("")));
        notifyItemInserted(steps.size() - 1);
    }

    /**
     * Serve per ottenere in modo corretto la posizione nel onCreateViewHolder
     *
     * @param position
     * @return position
     */
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        if (steps != null && position < steps.size()) {
            String stepn = "Step " + (position + 1);
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
            if (disabled) {
                holder.stepMinutes.setEnabled(false);
                holder.stepHours.setEnabled(false);
                holder.steptext.setEnabled(false);
                holder.imgStep.setEnabled(false);
                holder.delete.setEnabled(false);
            }
            if (editing) {
                holder.delete.setVisibility(View.GONE);
            }

        }
    }

    public List<Step> getSteps() {
        return steps;
    }

    /**
     * Serve per ottenere la posizione dell'holder che è stato cliccato
     *
     * @return Posizione dell'holder cliccato
     */
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
