package it.tpt.cookingbayapp.sectionRecycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        return new SectionViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
        if (sectionList != null && position < sectionList.size()) {
            holder.sectionTitle.setText("Step " + (position + 1));
            holder.sectionText.setText(sectionList.get(position).getText());
            if (sectionList.get(position).getImageUrl().equals(""))
                holder.sectionPic.setVisibility(View.GONE);
            else {
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
