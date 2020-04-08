package it.tpt.cookingbayapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText textName,textSurname,textEmail, textPassword;
    private Button btnRegistrati;
    private Button btnAccedi;
    private FirebaseAuth mAuth;
    public static final int LOGIN_REQUEST = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Istanza database firebase
        mAuth = FirebaseAuth.getInstance();

        textName = findViewById(R.id.textName);
        textSurname = findViewById(R.id.textSurname);
        textEmail = findViewById(R.id.textEmail);
        textPassword = findViewById(R.id.textPassword);
        btnRegistrati = findViewById(R.id.registration);
        btnAccedi= findViewById(R.id.loginr);
        btnAccedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivityForResult(intent, LOGIN_REQUEST);
                }catch(Exception e) {
                    Toast.makeText(LoginActivity.this, getString(R.string.required), Toast.LENGTH_LONG).show();
                }
            }
        });
         /*
        btnRegistrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivityForResult(intent, LOGIN_REQUEST);
                } catch(Exception e) {
                    Toast.makeText(LoginActivity.this, getString(R.string.required), Toast.LENGTH_LONG).show();
                }

            }
        }); */

        //Action Bar rimossa nel theme, di conseguenza la seguente istruzione fa crashare il sistema
        //getSupportActionBar().setTitle(getString(R.string.login));
    }
    private void writeUserToDb(String name, String surname, String email, String uid) {
        //SCRIVO SUL DB DOPO LA REGISTRAZIONE
        Map<String, Object> user = new HashMap<>();
        user.put("nome", name);
        user.put("cognome", surname);
        user.put("email",email);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("utenti").document(uid).set(user); //IL DOCUMENT E' L'UTENTE (STRING UID) CIOE' L'IDENTIFICATORE DEL DOCUMENTO
        //OPERAZIONE EFFETTUATA IN MODO ASINCRONO, BISOGNEREBBE METTERE UN ONCOMPLETELISTENER (RIGA 62)
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
                String surname = intent.getExtras().getString("surname");

                getSupportActionBar().setTitle(name + " " + surname); //dà il nome e il cognome in alto nella action bar

                SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("firstRun", false);
                editor.apply();

            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
}
