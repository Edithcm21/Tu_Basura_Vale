package com.example.tu_basura_vale;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Registro extends AppCompatActivity {
    private FirebaseAuth auth;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        auth = FirebaseAuth.getInstance ();

        EditText edtEmail = findViewById (R.id.etEmailRegister);
        EditText edtPassword = findViewById (R.id.etPasswordlRegister);

        Button btnRegister = findViewById (R.id.btnRegister);
        btnRegister.setOnClickListener (v -> {
            registerUser (edtEmail.getText().toString (), edtPassword.getText().toString ());
        });
    }



    private void registerUser (String email, String password) {
        auth.createUserWithEmailAndPassword (email, password)
                .addOnCompleteListener (task -> {
                    if (task.isSuccessful ()) {
                        Toast.makeText (this, "Register completed!", Toast.LENGTH_LONG).show ();
                        Intent mainActivity = new Intent(Registro.this, MainActivity.class);
                        startActivity(mainActivity);
                        Registro.this.finish();
                    } else {
                        if (task.getException () != null) {
                            Log.e("TYAM", task.getException().getMessage());
                        }

                        Toast.makeText (this, "Register failed!", Toast.LENGTH_LONG).show ();
                    }
                });
    }
}
