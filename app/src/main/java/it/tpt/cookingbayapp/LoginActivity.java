package it.tpt.cookingbayapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import it.tpt.cookingbayapp.R;

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
    private Button btnRegistra;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Istanza database firebase
        mAuth = FirebaseAuth.getInstance();

        textName = findViewById(R.id.textName);
        textSurname = findViewById(R.id.textSurname);
        textEmail = findViewById(R.id.textEmail);
        textPassword = findViewById(R.id.textPassword);
        btnRegistra = findViewById(R.id.containedButton);
        btnRegistra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    //Il final serve per renderli visibili nella classe interna OnCompleteListener
                    final String name = textName.getText().toString();
                    final String surname = textSurname.getText().toString();
                    final String email = textEmail.getText().toString();
                    final String password = textPassword.getText().toString();

                    //Creazione dell'utente
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                final FirebaseUser user = mAuth.getCurrentUser();
                                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name + " " + surname)
                                        .build();

                                user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        writeUserToDb(name,surname,email,user.getUid());
                                        Intent intent = new Intent();
                                        intent.putExtra("name", textName.getText().toString());
                                        intent.putExtra("surname", textSurname.getText().toString());
                                        intent.putExtra("email", textEmail.getText().toString());
                                        intent.putExtra("password", textPassword.getText().toString());

                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                });
                            }
                            else Toast.makeText(LoginActivity.this, getString(R.string.errorSignup), Toast.LENGTH_LONG).show();
                        }
                    });

                } catch(Exception e) {
                    Toast.makeText(LoginActivity.this, getString(R.string.required), Toast.LENGTH_LONG).show();
                }

            }
        });

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
    protected void onStart() {
        super.onStart();
    }
}
