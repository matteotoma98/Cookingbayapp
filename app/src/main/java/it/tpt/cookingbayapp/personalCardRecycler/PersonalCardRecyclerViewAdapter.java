package it.tpt.cookingbayapp.personalCardRecycler;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import it.tpt.cookingbayapp.CreateRecipe;
import it.tpt.cookingbayapp.R;
import it.tpt.cookingbayapp.RecipeClickListener;
import it.tpt.cookingbayapp.ViewRecipeActivity;
import it.tpt.cookingbayapp.recipeObject.Recipe;

public class PersonalCardRecyclerViewAdapter extends RecyclerView.Adapter<PersonalCardViewHolder> {

    private List<Recipe> recipeList;
    private List<String> recipeIds;
    final static int CREATE_REQUEST = 129;
    Context mContext;

    public PersonalCardRecyclerViewAdapter(Context c, List<Recipe> recipeList, List<String> recipeIds) {
        mContext = c;
        this.recipeList = recipeList;
        this.recipeIds = recipeIds;
    }

    @NonNull
    @Override
    public PersonalCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_recipe_card, parent, false);
        PersonalCardViewHolder holder = new PersonalCardViewHolder(layoutView);
        //Visualizza la ricetta normalmente

        holder.setRecipeClickListener(new RecipeClickListener() {
            @Override
            public void onRecipeClickListener(View v, int position) {
                Intent intent = new Intent(mContext, ViewRecipeActivity.class);
                intent.putExtra("recipe", recipeList.get(position));
                intent.putExtra("recipeId", recipeIds.get(position));
                mContext.startActivity(intent);
            }

            @Override
            public void onDeleteClickListener(View v, final int position) {
                //Dialog di conferma per eliminare la ricetta
                MaterialAlertDialogBuilder confirmDel = new MaterialAlertDialogBuilder(mContext);
                confirmDel.setTitle("Attenzione");
                confirmDel.setMessage(R.string.delete_confirmation);
                confirmDel.setIcon(R.drawable.ic_warning_black_24dp);
                confirmDel.setPositiveButton("Elimina", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("Recipes").document(recipeIds.get(position))
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(mContext, R.string.recipe_deleted, Toast.LENGTH_LONG).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(mContext, R.string.delete_error, Toast.LENGTH_LONG).show();
                                    }
                                });

                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        String deletePath = "images/" + currentUser.getUid() + "/" + recipeIds.get(position);
                        recipeList.remove(position);
                        recipeIds.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());

                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference listRef = storage.getReference().child(deletePath);

                        listRef.listAll()
                                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                    @Override
                                    public void onSuccess(ListResult listResult) {
                                        for (StorageReference prefix : listResult.getPrefixes()) {
                                            //Prefixes sono le cartelle
                                        }

                                        for (StorageReference item : listResult.getItems()) {
                                            item.delete();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // An error occurred!
                                    }
                                });
                    }
                });
                confirmDel.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                confirmDel.show();
            }

            @Override
            public void onEditClickListener(View v, int position) {
                Intent intent = new Intent(mContext, CreateRecipe.class);
                intent.putExtra("edit", true);
                intent.putExtra("recipeToEdit", recipeList.get(position));
                intent.putExtra("recipeId", recipeIds.get(position));
                ((Activity) mContext).startActivityForResult(intent, CREATE_REQUEST);
            }
        });
        return holder;
    }

    /**
     * Aggiunge una ricetta e il suo Id alle rispettive liste
     *
     * @param recipe La ricetta
     * @param id     L'Id della ricetta
     */
    public void addRecipe(Recipe recipe, String id) {
        recipeList.add(recipe);
        recipeIds.add(id);
        notifyDataSetChanged();
        //notifyItemInserted(recipeList.size()-1);
    }

    /**
     * Aggiorna una ricetta
     *
     * @param recipe La ricetta
     * @param id     L'id della ricetta per trovare l'indice
     */
    public void updateRecipe(Recipe recipe, String id) {
        int index = recipeIds.indexOf(id);
        recipeList.set(index, recipe);
        notifyItemChanged(index);
    }


    //Assegna le informazioni della ricetta dinamicamente alla card
    @Override
    public void onBindViewHolder(@NonNull PersonalCardViewHolder holder, int position) {
        if (recipeList != null && position < recipeList.size()) {
            final Recipe recipe = recipeList.get(position);

            holder.title.setText(recipe.getTitle());
            holder.type.setText(recipe.getType());
            holder.time.setText(String.valueOf(recipe.getTime()) + " min");

            Glide.with(holder.preview.getContext()).load(recipe.getPreviewUrl()).into(holder.preview);

        }
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }
}