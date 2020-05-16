package it.tpt.cookingbayapp.commentRecycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import it.tpt.cookingbayapp.R;
import it.tpt.cookingbayapp.RecyclerItemClickListener;
import it.tpt.cookingbayapp.recipeObject.Comment;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentViewHolder> {

    private List<Comment> comments;
    private Context mContext;
    private String userId, recipeId;
    private boolean isOwnRecipe; //Per indicare che la ricetta è propria e quindi mostrare sempre il bottone per eliminare i commenti
    FirebaseFirestore db;

    public CommentRecyclerAdapter(List<Comment> comments, Context context, String recipeId, String userId, boolean isOwnRecipe) {
        this.comments = comments;
        mContext = context;
        db = FirebaseFirestore.getInstance();
        this.recipeId = recipeId;
        this.userId = userId;
        this.isOwnRecipe = isOwnRecipe;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        final CommentViewHolder holder = new CommentViewHolder(layoutView);
        if (isOwnRecipe) {
            holder.delete.setVisibility(View.VISIBLE);
        } else {
            holder.delete.setVisibility(View.GONE);
        }

        holder.setDeleteListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                db.collection("Recipes").document(recipeId) //Rimuove il commento dal database
                        .update("comments", FieldValue.arrayRemove(comments.get(position)));
                comments.remove(position);
                notifyItemRemoved(position);
                //notifyItemRangeChanged(position, comments.size());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentViewHolder holder, final int position) {
        if (comments != null && position < comments.size()) {

            holder.content.setText(comments.get(position).getContent());

            if (comments.get(position).getUserId().equals(userId))
                holder.delete.setVisibility(View.VISIBLE);

            db.collection("Users").document(comments.get(position).getUserId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    holder.username.setText(document.getString("username"));
                                    Glide.with(mContext)
                                            .load(document.getString("profilePicUrl"))
                                            .error(R.drawable.missingprofile)
                                            .into(holder.profilePic);
                                }
                            }
                        }
                    });

        }
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        notifyItemInserted(getItemCount() - 1);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
