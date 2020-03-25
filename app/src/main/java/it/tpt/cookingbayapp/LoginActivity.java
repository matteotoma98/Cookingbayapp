package it.tpt.cookingbayapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import it.tpt.cookingbayapp.R;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText textName, textSurname, textEmail, textPassword;
    private Button btnRegistra;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        textName = findViewById(R.id.textName);
        textSurname = findViewById(R.id.textSurname);
        textEmail = findViewById(R.id.textEmail);
        textPassword = findViewById(R.id.textPassword);
        btnRegistra = findViewById(R.id.containedButton);
        btnRegistra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("name", textName.getText().toString());
                intent.putExtra("surname", textSurname.getText().toString());
                intent.putExtra("email", textEmail.getText().toString()); //il to string nella edit text è necessario altrimenti get text non restituisce una stringa
               intent.putExtra("password", textPassword.getText().toString());

                setResult(RESULT_OK, intent);
                finish();
            }
        });

        getSupportActionBar().setTitle(getString(R.string.login)); //strings è utile per le applicazioni multilingua (file strings per ogni lingua)


    }
}
