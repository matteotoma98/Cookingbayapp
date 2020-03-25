package it.tpt.cookingbayapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import it.tpt.cookingbayapp.R;

public class MainActivity extends AppCompatActivity {

    public static final int LOGIN_REQUEST = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        if(preferences.getBoolean("firstRun", true)) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == LOGIN_REQUEST) {
            if(resultCode == RESULT_OK) {
                String email = intent.getExtras().getString("email");
                String password = intent.getExtras().getString("password");

                getSupportActionBar().setTitle(email + " " + password);
            }
        }
    }


}
