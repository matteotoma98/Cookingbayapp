package it.tpt.cookingbayapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText textUsername, textEmail, textPassword;
    private Button btnRegistra;
    private FirebaseAuth mAuth;
    private Button btnAccedi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Istanza database firebase
        mAuth = FirebaseAuth.getInstance();

        textUsername = findViewById(R.id.textUsername);
        textEmail = findViewById(R.id.textEmail);
        textPassword = findViewById(R.id.textPassword);
        btnRegistra = findViewById(R.id.containedButton);
        btnAccedi = findViewById(R.id.loginfromregister);
        btnAccedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                finish();
            }
        });
        btnRegistra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    //Il final serve per renderli visibili nella classe interna OnCompleteListener
                    final String username = textUsername.getText().toString().trim();
                    final String email = textEmail.getText().toString().trim();
                    final String password = textPassword.getText().toString();
                    final String profilepicture = "missingprofile";

                    //Creazione dell'utente
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                final FirebaseUser user = mAuth.getCurrentUser();
                                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(username)
                                        .build();

                                user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        writeUserToDb(username, email, user.getUid(), profilepicture);
                                        Intent intent = new Intent();
                                        intent.putExtra("username", user.getDisplayName());
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                });
                            } else
                                Toast.makeText(RegisterActivity.this, getString(R.string.errorSignup), Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (Exception e) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.required), Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void writeUserToDb(String username, String email, String uid, String profilepicture) {
        //SCRIVO SUL DB DOPO LA REGISTRAZIONE
        Map<String, Object> user = new HashMap<>();
        user.put("nome", username);
        user.put("email", email);
        user.put("profilepic", profilepicture);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("utenti").document(uid).set(user); //IL DOCUMENT E' L'UTENTE (STRING UID) CIOE' L'IDENTIFICATORE DEL DOCUMENTO
        //OPERAZIONE EFFETTUATA IN MODO ASINCRONO, BISOGNEREBBE METTERE UN ONCOMPLETELISTENER (RIGA 62)
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
