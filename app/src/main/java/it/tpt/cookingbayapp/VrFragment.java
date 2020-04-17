package it.tpt.cookingbayapp;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;
import it.tpt.cookingbayapp.ingredientsRecycler.IngredientsRecyclerViewAdapter;
import it.tpt.cookingbayapp.recipeObject.Recipe;
import it.tpt.cookingbayapp.sectionRecycler.SectionAdapter;


public class VrFragment extends Fragment implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private TextView recipeTitle, recipeAuthor, recipeType, recipeTime;
    private CircleImageView profilePic;
    private ImageView previewPic;
    private RecyclerView iRecyclerView;
    private RecyclerView sRecyclerView;
    private ImageView like, dislike; //Icone del like e dislike cliccabili
    private TextView likeCounterText, dislikeCounterText;
    private int likeCounter, dislikeCounter; //Contatori per comodità
    //Booleani utilizzati per la logica del click su like e dislike
    private boolean likeClicked;
    private boolean dislikeClicked;
    private View layout;
    private String recipeId;

    public VrFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        View view = inflater.inflate(R.layout.fragment_vr, container, false);
        recipeTitle = view.findViewById(R.id.viewRecipeTitle);
        recipeAuthor = view.findViewById(R.id.viewRecipeAuthor);
        recipeTime = view.findViewById(R.id.viewRecipeTime);
        recipeType = view.findViewById(R.id.viewRecipeType);
        previewPic = view.findViewById(R.id.viewPreviewPic);
        profilePic = view.findViewById(R.id.viewProfilePic);

        likeCounterText = view.findViewById(R.id.likeCounterText);
        dislikeCounterText = view.findViewById(R.id.dislikeCounterText);
        like = view.findViewById(R.id.likeBtn);
        dislike = view.findViewById(R.id.dislikeBtn);
        like.setOnClickListener(this);
        dislike.setOnClickListener(this);
        likeClicked = false;
        dislikeClicked = false;


        //Ottieni la ricetta passata dall'activity ViewRecipe
        Bundle vrBundle = this.getArguments();
        Recipe recipe = (Recipe) vrBundle.getSerializable("recipe");
        recipeId = vrBundle.getString("recipeId");

        //Assegna le informazioni generali
        recipeTitle.setText(recipe.getTitle());
        recipeAuthor.setText(recipe.getAuthorName());
        recipeTime.setText(recipe.getTime() + " min");
        recipeType.setText(recipe.getType());
        likeCounter = recipe.getLikes();
        dislikeCounter = recipe.getDislikes();
        likeCounterText.setText(String.valueOf(likeCounter));
        dislikeCounterText.setText(String.valueOf(dislikeCounter));
        layout = view.findViewById(R.id.vrfragment_layout);
        //Assegna la foto di profilo default se non è specificata nella ricetta
        if (recipe.getProfilePicUrl().equals("")) {
            profilePic.setImageResource(R.drawable.missingprofile);
        } else Glide.with(this).load(recipe.getProfilePicUrl()).into(profilePic);
        Glide.with(this).load(recipe.getPreviewUrl()).into(previewPic); //Assegna la foto di anteprima

        //Passa la lista degli ingredienti al recyclerView
        iRecyclerView = view.findViewById(R.id.ingRecycler_view);
        iRecyclerView.setHasFixedSize(true);
        iRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        IngredientsRecyclerViewAdapter iAdapter = new IngredientsRecyclerViewAdapter(recipe.getIngredients());
        iRecyclerView.setAdapter(iAdapter);

        //Passa la lista degli step al recyclerView
        sRecyclerView = view.findViewById(R.id.section_recycler);
        sRecyclerView.setHasFixedSize(true);
        sRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SectionAdapter sAdapter = new SectionAdapter(getContext(), recipe.getSections());
        sRecyclerView.setAdapter(sAdapter);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("VRFRAG", "On destroy called");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (likeClicked) db.collection("Recipes").document(recipeId)
                .update("likes", FieldValue.increment(1));
        else if (dislikeClicked) db.collection("Recipes").document(recipeId)
                .update("dislikes", FieldValue.increment(1));
    }

    //Gestione del click del like e del dislike
    @Override
    public void onClick(View v) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (!user.isAnonymous()) {
            switch (v.getId()) {
                case R.id.likeBtn:

                    if (likeClicked) {
                        like.setColorFilter(ContextCompat.getColor(getContext(), R.color.likeDislikeNotClicked));
                        likeCounterText.setText(String.valueOf(likeCounter));
                        likeClicked = false;

                    } else {
                        like.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
                        dislike.setColorFilter(ContextCompat.getColor(getContext(), R.color.likeDislikeNotClicked));
                        likeCounterText.setText(String.valueOf(likeCounter + 1));
                        dislikeCounterText.setText(String.valueOf(dislikeCounter));
                        likeClicked = true;
                        dislikeClicked = false;
                    }
                    break;
                case R.id.dislikeBtn:
                    if (dislikeClicked) {
                        dislike.setColorFilter(ContextCompat.getColor(getContext(), R.color.likeDislikeNotClicked));
                        dislikeCounterText.setText(String.valueOf(dislikeCounter));
                        dislikeClicked = false;
                    } else {
                        dislike.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
                        like.setColorFilter(ContextCompat.getColor(getContext(), R.color.likeDislikeNotClicked));
                        dislikeCounterText.setText(String.valueOf(dislikeCounter + 1));
                        likeCounterText.setText(String.valueOf(likeCounter));
                        dislikeClicked = true;
                        likeClicked = false;
                    }
                    break;
            }
        } else Snackbar.make(layout, R.string.likedislikeanonymous, Snackbar.LENGTH_LONG).show();
    }
}

