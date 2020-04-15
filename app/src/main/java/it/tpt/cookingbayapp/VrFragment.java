package it.tpt.cookingbayapp;

import android.content.Intent;
import android.os.Bundle;

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


public class VrFragment extends Fragment {

    TextView recipeTitle, recipeAuthor, recipeType, recipeTime;
    CircleImageView profilePic;
    ImageView previewPic;
    RecyclerView iRecyclerView;
    RecyclerView sRecyclerView;

    public VrFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vr, container, false);
        recipeTitle = view.findViewById(R.id.viewRecipeTitle);
        recipeAuthor = view.findViewById(R.id.viewRecipeAuthor);
        recipeTime = view.findViewById(R.id.viewRecipeTime);
        recipeType = view.findViewById(R.id.viewRecipeType);
        previewPic = view.findViewById(R.id.viewPreviewPic);
        profilePic = view.findViewById(R.id.viewProfilePic);

        Bundle vrBundle = this.getArguments();
        Recipe recipe = (Recipe) vrBundle.getSerializable("recipe");

        recipeTitle.setText(recipe.getTitle());
        recipeAuthor.setText(recipe.getAuthorName());
        recipeTime.setText(recipe.getTime() + " min");
        recipeType.setText(recipe.getType());

        if(recipe.getProfilePicUrl().equals("missingprofile")){
            profilePic.setImageResource(R.drawable.missingprofile);
        }
        else Glide.with(this).load(recipe.getProfilePicUrl()).into(profilePic);
        Glide.with(this).load(recipe.getPreviewUrl()).into(previewPic);

        iRecyclerView = view.findViewById(R.id.ingRecycler_view);
        iRecyclerView.setHasFixedSize(true);
        iRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        IngredientsRecyclerViewAdapter iAdapter = new IngredientsRecyclerViewAdapter(recipe.getIngredients());
        iRecyclerView.setAdapter(iAdapter);

        sRecyclerView = view.findViewById(R.id.section_recycler);
        sRecyclerView.setHasFixedSize(true);
        sRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SectionAdapter sAdapter = new SectionAdapter(getContext(), recipe.getSections());
        sRecyclerView.setAdapter(sAdapter);

        return view;
    }
    }

