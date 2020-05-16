package it.tpt.cookingbayapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    public static final int LOGIN_REQUEST = 101;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        if (preferences.getBoolean("notSignedIn", true)) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST);
        } else {
            if (currentUser.isAnonymous()) getSupportActionBar().setSubtitle("Ospite");
            else getSupportActionBar().setSubtitle(currentUser.getDisplayName());
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RdgFragment()).commit();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                switch (item.getItemId()) {
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
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                return true;
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, intent);
        }
        if (requestCode == LOGIN_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user.isAnonymous()) getSupportActionBar().setTitle("Ospite");
                else {
                    getSupportActionBar().setSubtitle(user.getDisplayName());
                }
                //String username = intent.getExtras().getString("username");
                //getSupportActionBar().setTitle(username); //dà il nome e il cognome in alto nella action bar
                SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("notSignedIn", false);
                editor.apply();

            } else {
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


}
