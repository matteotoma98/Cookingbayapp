package it.tpt.cookingbayapp.sectionRecycler;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.tpt.cookingbayapp.R;

public class SectionViewHolder extends RecyclerView.ViewHolder {

    TextView sectionTitle, sectionText;
    ImageView sectionPic;
    Button timer;

    public SectionViewHolder(@NonNull View itemView) {
        super(itemView);
        sectionTitle = itemView.findViewById(R.id.sectionTitle);
        sectionText = itemView.findViewById(R.id.sectionText);
        sectionPic = itemView.findViewById(R.id.sectionPic);
        timer = itemView.findViewById(R.id.timerBtn);
    }
}
