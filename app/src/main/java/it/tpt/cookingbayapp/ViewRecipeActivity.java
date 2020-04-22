package it.tpt.cookingbayapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import it.tpt.cookingbayapp.recipeObject.Recipe;


public class ViewRecipeActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private VrFragment mVrFragment;
    private ComFragment mComFragment;

    private MenuItem favMenuBtn;

    private String recipeId;
    private boolean iconset;
    private boolean alreadyFavourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);

        mVrFragment = new VrFragment();
        mComFragment = new ComFragment();
        iconset = false;

        alreadyFavourite = false;

        //Ottieni la ricetta passata dallo startActivity
        Intent intent = getIntent();
        Recipe recipe = (Recipe) intent.getSerializableExtra("recipe");
        recipeId = intent.getStringExtra("recipeId");
        Bundle recipeBundle = new Bundle();
        recipeBundle.putSerializable("recipe", recipe);
        recipeBundle.putString("recipeId", recipeId);
        mVrFragment.setArguments(recipeBundle); //Passa la ricetta al fragment della visualizzazione della ricetta

        Bundle commentBundle = new Bundle();
        commentBundle.putSerializable("comments", recipe.getComments());
        commentBundle.putString("recipeId", recipeId);
        commentBundle.putString("authorId", recipe.getAuthorId());
        mComFragment.setArguments(commentBundle); //Passa i commenti al fragment della visualizzazione dei commenti

        getSupportActionBar().setTitle(recipe.getTitle());

        //View pager per le Tab "RICETTA" e "COMMENTI"
        mTabLayout.setupWithViewPager(mViewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(mVrFragment, "Ricetta");
        viewPagerAdapter.addFragment(mComFragment, "Commenti");
        mViewPager.setAdapter(viewPagerAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Viene aggiunta o rimossa la ricetta dai preferiti solo al termine dell'activity per evitare continue call al database ad ogni click
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(alreadyFavourite && !iconset) {//Se l'utente ha deciso di rimuoverlo dai preferiti
            db.collection("Users").document(user.getUid())
                    .update("favourites", FieldValue.arrayRemove(recipeId));
        }
        else if(iconset && !alreadyFavourite) { //Altrimenti se l'utente ha deciso di aggiungerlo ai preferiti e non è gia stato aggiunto
            db.collection("Users").document(user.getUid())
                    .update("favourites", FieldValue.arrayUnion(recipeId));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vr_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        favMenuBtn = menu.findItem(R.id.addFavourite);
        checkIfAlreadyFavourite(); //Controlla e setta l'icona se è già nei preferiti
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addFavourite) {
            if (iconset) {
                item.setIcon(R.drawable.ic_favorite_border_black_24dp);
                iconset = false;
            } else {
                item.setIcon(R.drawable.ic_favorite_black_24dp);
                iconset = true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Controlla che l'utente abbia già aggiunto ai preferiti la ricetta
     * Richiamato nell'onPrepareOptionMenu
     */
    private void checkIfAlreadyFavourite() {
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
                                ArrayList<String> favourites = (ArrayList<String>) document.get("favourites");
                                if(favourites.contains(recipeId)) {
                                    alreadyFavourite = true;
                                    favMenuBtn.setIcon(R.drawable.ic_favorite_black_24dp);
                                    iconset = true;
                                }
                            }
                        } else {

                        }
                    }
                });
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentTitle = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitle.add(title);

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }
}
