package it.tpt.cookingbayapp.CommentRecycler;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import it.tpt.cookingbayapp.R;

public class CommentViewHolder extends RecyclerView.ViewHolder {

    CircleImageView profilePic;
    TextView username;
    TextView content;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        profilePic = itemView.findViewById(R.id.commentProfilePic);
        username = itemView.findViewById(R.id.commentUsername);
        content = itemView.findViewById(R.id.commentText);
    }
}
