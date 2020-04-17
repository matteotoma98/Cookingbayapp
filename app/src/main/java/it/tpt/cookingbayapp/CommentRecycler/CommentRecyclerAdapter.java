package it.tpt.cookingbayapp.CommentRecycler;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import it.tpt.cookingbayapp.R;
import it.tpt.cookingbayapp.recipeObject.Comment;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentViewHolder> {

    private List<Comment> comments;
    private Context mContext;
    FirebaseFirestore db;

    public CommentRecyclerAdapter(List<Comment> comments, Context context) {
        this.comments = comments;
        mContext = context;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        CommentViewHolder holder = new CommentViewHolder(layoutView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentViewHolder holder, final int position) {
        if(comments!=null && position < comments.size()) {

            holder.content.setText(comments.get(position).getContent());

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
                            } else {

                            }
                        }
                    });

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
