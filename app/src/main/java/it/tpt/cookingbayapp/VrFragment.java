package it.tpt.cookingbayapp;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import it.tpt.cookingbayapp.ingredientsRecycler.IngredientsRecyclerViewAdapter;
import it.tpt.cookingbayapp.recipeObject.Recipe;
import it.tpt.cookingbayapp.sectionRecycler.SectionAdapter;


public class VrFragment extends Fragment implements View.OnClickListener{

    private TextView recipeTitle, recipeAuthor, recipeType, recipeTime;
    private CircleImageView profilePic;
    private ImageView previewPic;
    private RecyclerView iRecyclerView;
    private RecyclerView sRecyclerView;
    private ImageView like, dislike; //Icone del like e dislike cliccabili
    //Booleani utilizzati per la logica del click su like e dislike
    private boolean likeClicked;
    private boolean dislikeClicked;

    public VrFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vr, container, false);
        recipeTitle = view.findViewById(R.id.viewRecipeTitle);
        recipeAuthor = view.findViewById(R.id.viewRecipeAuthor);
        recipeTime = view.findViewById(R.id.viewRecipeTime);
        recipeType = view.findViewById(R.id.viewRecipeType);
        previewPic = view.findViewById(R.id.viewPreviewPic);
        profilePic = view.findViewById(R.id.viewProfilePic);
        like = view.findViewById(R.id.likeBtn);
        dislike = view.findViewById(R.id.dislikeBtn);
        like.setOnClickListener(this);
        dislike.setOnClickListener(this);
        likeClicked = false;
        dislikeClicked = false;


        //Ottieni la ricetta passata dall'activity ViewRecipe
        Bundle vrBundle = this.getArguments();
        Recipe recipe = (Recipe) vrBundle.getSerializable("recipe");

        //Assegna le informazioni generali
        recipeTitle.setText(recipe.getTitle());
        recipeAuthor.setText(recipe.getAuthorName());
        recipeTime.setText(recipe.getTime() + " min");
        recipeType.setText(recipe.getType());

        //Assegna la foto di profilo default se non è specificata nella ricetta
        if(recipe.getProfilePicUrl().equals("")){
            profilePic.setImageResource(R.drawable.missingprofile);
        }
        else Glide.with(this).load(recipe.getProfilePicUrl()).into(profilePic);
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

    //Gestione del click del like e del dislike
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.likeBtn:
                if(likeClicked) {
                    like.setColorFilter(ContextCompat.getColor(getContext(), R.color.likeDislikeNotClicked));
                    likeClicked = false;
                }
                else {
                    like.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    dislike.setColorFilter(ContextCompat.getColor(getContext(), R.color.likeDislikeNotClicked));
                    likeClicked = true;
                    dislikeClicked = false;
                }
                break;
            case R.id.dislikeBtn:
                if(dislikeClicked) {
                    dislike.setColorFilter(ContextCompat.getColor(getContext(), R.color.likeDislikeNotClicked));
                    dislikeClicked = false;
                }
                else {
                    dislike.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    like.setColorFilter(ContextCompat.getColor(getContext(), R.color.likeDislikeNotClicked));
                    dislikeClicked = true;
                    likeClicked = false;
                }
                break;
        }
    }
}

