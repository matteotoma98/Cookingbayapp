package it.tpt.cookingbayapp.CommentRecycler;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import it.tpt.cookingbayapp.R;
import it.tpt.cookingbayapp.RecyclerItemClickListener;

public class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    CircleImageView profilePic;
    ImageView delete;
    TextView username;
    TextView content;
    private RecyclerItemClickListener deleteListener;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        profilePic = itemView.findViewById(R.id.commentProfilePic);
        username = itemView.findViewById(R.id.commentUsername);
        content = itemView.findViewById(R.id.commentText);
        delete = itemView.findViewById(R.id.deleteComment);
        delete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        deleteListener.onItemClickListener(getAdapterPosition());
    }

    public void setDeleteListener(RecyclerItemClickListener deleteListener) {
        this.deleteListener = deleteListener;
    }
}
