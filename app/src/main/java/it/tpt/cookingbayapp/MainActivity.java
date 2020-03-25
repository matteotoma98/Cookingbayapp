package it.tpt.cookingbayapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import it.tpt.cookingbayapp.R;

public class MainActivity extends AppCompatActivity {

    public static final int LOGIN_REQUEST = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this, LoginActivity.class); //pushiamo la login activity
        startActivityForResult(intent, LOGIN_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == LOGIN_REQUEST) {
            if(resultCode == RESULT_OK) {
                String email = intent.getExtras().getString("email");
                String password = intent.getExtras().getString("password");

                 getSupportActionBar().setTitle(email + " " + password); //dà il nome e il cognome in alto nella action bar


            }
        }
    }






}
