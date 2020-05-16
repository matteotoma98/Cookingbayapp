package it.tpt.cookingbayapp.sectionRecycler;

import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import it.tpt.cookingbayapp.R;
import it.tpt.cookingbayapp.recipeObject.Section;

public class SectionAdapter extends RecyclerView.Adapter<SectionViewHolder> {

    private List<Section> sectionList;
    private Context mContext;

    public SectionAdapter(Context context, List<Section> sectionList) {
        this.sectionList = sectionList;
        mContext = context;
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_item, parent, false);
        final SectionViewHolder holder = new SectionViewHolder(layoutView);

        holder.timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlarmClock.ACTION_SET_TIMER);
                intent.putExtra(AlarmClock.EXTRA_LENGTH, sectionList.get(holder.getAdapterPosition()).getTimer());
                intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                mContext.startActivity(intent);
                Toast.makeText(mContext, R.string.timer_launched, Toast.LENGTH_LONG).show();
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
        if (sectionList != null && position < sectionList.size()) {
            holder.sectionTitle.setText("Step " + (position + 1));
            holder.sectionText.setText(sectionList.get(position).getText());
            if (sectionList.get(position).getTimer() == 0) holder.timer.setVisibility(View.GONE);
            if (sectionList.get(position).getImageUrl().equals("")) {
                final LinearLayout.LayoutParams layoutparams = (LinearLayout.LayoutParams) holder.sectionText.getLayoutParams();
                final LinearLayout.LayoutParams layoutparams2 = (LinearLayout.LayoutParams) holder.sectionTitle.getLayoutParams();
                layoutparams.setMargins(0, 0, 0, 0);
                layoutparams2.setMargins(0, 0, 0, 0);
                holder.sectionTitle.setLayoutParams(layoutparams2);
                holder.sectionText.setLayoutParams(layoutparams);
                holder.sectionPic.setVisibility(View.GONE);
            } else {
                Glide.with(holder.sectionPic.getContext())
                        .load(sectionList.get(position).getImageUrl())
                        .into(holder.sectionPic);
            }
        }
    }

    @Override
    public int getItemCount() {
        return sectionList.size();
    }
}
