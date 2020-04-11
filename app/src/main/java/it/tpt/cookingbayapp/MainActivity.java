package it.tpt.cookingbayapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class MainActivity  extends AppCompatActivity {
    
    private BottomNavigationView bottomNavigationView;
    public static final int LOGIN_REQUEST = 101;
    public static final int RC_SIGN_IN = 105;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        if(preferences.getBoolean("notSignedIn", true)) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST);
        }
        else {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser.isAnonymous()) getSupportActionBar().setTitle("Cooking Bay");
            else getSupportActionBar().setTitle(currentUser.getDisplayName());
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new RdgFragment()).commit();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment =null;

                switch (item.getItemId()){
                    case R.id.nav_home:
                        fragment = new RdgFragment();
                        break;
                    case R.id.nav_favourites:
                        fragment = new FavFragment();
                        break;
                    case R.id.nav_search:
                        fragment = new SearchFragment();
                        break;
                    case R.id.nav_myrecipes:
                        fragment = new LmrFragment();
                        break;
                    case R.id.nav_profile:
                        fragment = new ProfileFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
                return true;
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, intent);
        }
        if(requestCode == LOGIN_REQUEST) {
            if(resultCode == RESULT_OK) {
                String name = intent.getExtras().getString("name");

                getSupportActionBar().setTitle(name); //dà il nome e il cognome in alto nella action bar
                SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("notSignedIn", false);
                editor.apply();

            }
            else {
                getSupportActionBar().setTitle("Ospite");
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(i, LOGIN_REQUEST);
            }
        }
        else if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(intent);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user.isAnonymous()) getSupportActionBar().setTitle("Cooking Bay");
                else {
                    getSupportActionBar().setTitle(user.getDisplayName());
                    writeUserToDb(user.getDisplayName(),user.getEmail(),user.getUid());
                }
                SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("notSignedIn", false);
                editor.apply();
            } else {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user == null) mAuth.signInAnonymously();
                SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("notSignedIn", false);
                editor.apply();
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
    private void writeUserToDb(String name, String email, String uid) {
        Map<String, Object> user = new HashMap<>();
        user.put("nome", name);
        user.put("email", email);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(uid).set(user);
    }

}
