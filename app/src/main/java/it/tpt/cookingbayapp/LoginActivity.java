package it.tpt.cookingbayapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    private TextInputEditText textEmail, textPassword;
    private Button btnRegistrati;
    private Button btnAccedi;
    private Button btnAnonymous;
    private FirebaseAuth mAuth;
    public static final int REGISTER_REQUEST = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        textEmail = findViewById(R.id.loginEmail);
        textPassword = findViewById(R.id.loginPassword);
        btnRegistrati = findViewById(R.id.register);
        btnAccedi = findViewById(R.id.loginButton);
        btnAnonymous = findViewById(R.id.anonymous_button);

        //Click listener per accedere e terminare l'activity
        btnAccedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mAuth.signInWithEmailAndPassword(textEmail.getText().toString(), textPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        mAuth.getUid();
                                        setResult(RESULT_OK);
                                        finish();
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("loginsuccess", "signInWithEmail:success");
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("loginfailure", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Erorre nell'accesso all'account",
                                                Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });
                } catch (NullPointerException e) {
                    Toast.makeText(LoginActivity.this, getString(R.string.required), Toast.LENGTH_LONG).show();
                }
            }
        });
        //Click listener per avviare l'attività di registrazione
        btnRegistrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, REGISTER_REQUEST);
            }
        });
        //Click listener per accedere come ospite
        btnAnonymous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance()
                        .signOut(LoginActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mAuth.signInAnonymously()
                                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    // Sign in success, update UI with the signed-in user's information
                                                    Log.d("signin", "signInAnonymously:success");
                                                    setResult(RESULT_OK);
                                                    finish();
                                                } else {
                                                    // If sign in fails, display a message to the user.
                                                    Log.w("signinerror", "signInAnonymously:failure", task.getException());
                                                    Toast.makeText(LoginActivity.this, R.string.exiterror, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REGISTER_REQUEST) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
