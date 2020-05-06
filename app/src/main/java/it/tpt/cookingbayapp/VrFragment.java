package it.tpt.cookingbayapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

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
    private boolean isAlreadyLiked, isAlreadyDisliked;
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
        isAlreadyLiked = false;
        isAlreadyDisliked = false;
        checkIfAlreadyRated(); //Controlla se l'utente ha già messo mi piace o non mi piace

        layout = view.findViewById(R.id.vrfragment_layout); //Layout per lo snackbar
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

    /**
     * L'aggiornamento del like e del dislike counter su Firebase
     * viene fatto solamente alla distruzione per evitare continue call
     * al database al click ripetuto del like
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("VRFRAG", "On destroy called");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (likeClicked && !isAlreadyLiked) { //Se l'utente ha messo mi piace e non lo aveva già messo
            db.collection("Users").document(currentUser.getUid()) //Aggiungi l'id della ricetta alla lista personale
                    .update("liked", FieldValue.arrayUnion(recipeId));
            db.collection("Recipes").document(recipeId) //Incrementa il contatore dei like della ricetta
                    .update("likes", FieldValue.increment(1));
        }
        if (dislikeClicked && !isAlreadyDisliked) { //Se l'utente ha messo non mi piace e non lo aveva già messo
            db.collection("Users").document(currentUser.getUid()) //Aggiungi l'id della ricetta alla lista personale
                    .update("disliked", FieldValue.arrayUnion(recipeId));
            db.collection("Recipes").document(recipeId) //Incrementa il contatore dei dislike della ricetta
                    .update("dislikes", FieldValue.increment(1));
        }
        if (!likeClicked && isAlreadyLiked) { //Se l'utente ha deciso di togliere il mi piace
            db.collection("Users").document(currentUser.getUid()) //Rimuovi l'Id della ricetta dalla lista personale
                    .update("liked", FieldValue.arrayRemove(recipeId));
            db.collection("Recipes").document(recipeId) //Decrementa il contatore
                    .update("likes", FieldValue.increment(-1));
        }
        if (!dislikeClicked && isAlreadyDisliked) { //Se l'utente ha deciso di togliere il non mi piace
            db.collection("Users").document(currentUser.getUid()) //Rimuovi l'Id della ricetta dalla lista personale
                    .update("disliked", FieldValue.arrayRemove(recipeId));
            db.collection("Recipes").document(recipeId) //Decrementa il contatore
                    .update("dislikes", FieldValue.increment(-1));
        }
    }

    /**
     * Controlla che l'utente abbia già valutato la ricetta
     * Richiamato nell'onCreate
     */
    private void checkIfAlreadyRated() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("Users").document(user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //Liste delle ricette piaciute e non piaciute
                                ArrayList<String> liked = (ArrayList<String>) document.get("liked");
                                ArrayList<String> disliked = (ArrayList<String>) document.get("disliked");
                                if(liked.contains(recipeId)) {
                                    isAlreadyLiked = true;
                                    like.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorLightBlue));
                                    dislike.setColorFilter(ContextCompat.getColor(getContext(), R.color.likeDislikeNotClicked));
                                    likeCounterText.setText(String.valueOf(likeCounter));
                                    dislikeCounterText.setText(String.valueOf(dislikeCounter));
                                    likeClicked = true;
                                    dislikeClicked = false;
                                    likeCounter--;
                                }
                                else if(disliked.contains(recipeId)) {
                                    isAlreadyDisliked = true;
                                    dislike.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorLightBlue));
                                    like.setColorFilter(ContextCompat.getColor(getContext(), R.color.likeDislikeNotClicked));
                                    dislikeCounterText.setText(String.valueOf(dislikeCounter));
                                    likeCounterText.setText(String.valueOf(likeCounter));
                                    dislikeClicked = true;
                                    likeClicked = false;
                                    dislikeCounter--;
                                }
                            }
                        } else {

                        }
                    }
                });
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
                        like.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorLightBlue));
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
                        dislike.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorLightBlue));
                        like.setColorFilter(ContextCompat.getColor(getContext(), R.color.likeDislikeNotClicked));
                        dislikeCounterText.setText(String.valueOf(dislikeCounter + 1));
                        likeCounterText.setText(String.valueOf(likeCounter));
                        dislikeClicked = true;
                        likeClicked = false;
                    }
                    break;
            }
        } else Snackbar.make(layout, R.string.like_dislike_anonymous, Snackbar.LENGTH_LONG).show();
    }
}

