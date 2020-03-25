package it.tpt.cookingbayapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import it.tpt.cookingbayapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText textEmail, textPassword;
    private Button btnRegistra;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Istanza database firebase
        mAuth = FirebaseAuth.getInstance();

        textEmail = findViewById(R.id.textEmail);
        textPassword = findViewById(R.id.textPassword);
        btnRegistra = findViewById(R.id.containedButton);
        btnRegistra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.putExtra("email", textEmail.getText().toString());
                intent.putExtra("password", textPassword.getText().toString());

                setResult(RESULT_OK, intent);
                finish();
            }
        });

        getSupportActionBar().setTitle(getString(R.string.login));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
