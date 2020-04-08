package it.tpt.cookingbayapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import javax.xml.transform.Result;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText textName, textSurname, textEmail, textPassword;
    private Button btnRegistrati;
    private Button btnAccedi;
    private FirebaseAuth mAuth;
    public static final int LOGIN_REQUEST = 101;
    public static final int REGISTER_REQUEST = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        textEmail = findViewById(R.id.textEmail);
        textPassword = findViewById(R.id.textPassword);
        btnRegistrati = findViewById(R.id.register);
        btnAccedi = findViewById(R.id.loginButton);
        btnAccedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mAuth.signInWithEmailAndPassword(textEmail.getText().toString(), textPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        final FirebaseUser user = mAuth.getCurrentUser();
                                        mAuth.getUid();
                                        Intent intent = new Intent();
                                        intent.putExtra("email", textEmail.getText());
                                        intent.putExtra("password", textPassword.getText());
                                        setResult(RESULT_OK, intent);
                                        finish();
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("loginsuccess", "signInWithEmail:success");
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("loginfailure", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });


                } catch (Exception e) {

                    Toast.makeText(LoginActivity.this, getString(R.string.required), Toast.LENGTH_LONG).show();
                }
            }
        });

        btnRegistrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, REGISTER_REQUEST);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REGISTER_REQUEST) {
            if (resultCode == RESULT_OK) {
                String name = intent.getExtras().getString("name");
                String surname = intent.getExtras().getString("surname");
                Intent i = new Intent();
                i.putExtra("name",name);
                i.putExtra("surname",surname);
                setResult(RESULT_OK,i);
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
