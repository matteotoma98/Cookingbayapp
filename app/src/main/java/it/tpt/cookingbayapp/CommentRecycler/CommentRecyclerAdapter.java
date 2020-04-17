package it.tpt.cookingbayapp.CommentRecycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import it.tpt.cookingbayapp.R;
import it.tpt.cookingbayapp.recipeObject.Comment;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentViewHolder> {

    private List<Comment> comments;
    private Context mContext;

    public CommentRecyclerAdapter(List<Comment> comments, Context context) {
        this.comments = comments;
        mContext = context;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        CommentViewHolder holder = new CommentViewHolder(layoutView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        if(comments!=null && position < comments.size()) {
            holder.username.setText(comments.get(position).getUsername());
            holder.content.setText(comments.get(position).getContent());
            Glide.with(mContext)
                    .load(comments.get(position).getUrl())
                    .error(R.drawable.missingprofile)
                    .into(holder.profilePic);
        }
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
