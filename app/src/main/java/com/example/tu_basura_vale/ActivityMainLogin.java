package com.example.tu_basura_vale;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;


public class ActivityMainLogin extends AppCompatActivity {

        //public static final String GOOGLE_ID_CLIENT_TOKEN = BuildConfig.GOOGLE_ID_CLIENT_TOKEN;
        public static final int GOOGLE_SIGNIN_REQUEST_CODE = 1;
        //Agregar cliente de inicio de sesion de google
        private GoogleSignInClient gmsClient;
        //Variable para gestionar FirebaseAuth
        private FirebaseAuth auth;
        String TAG = "GoogleSignIn";
        User userG=new User();
    private Snackbar snackbar;

    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private DatabaseReference songs;

    private static final String BASE_STORAGE_REFERENCE = "images";
    private static final String BASE_DATABASE_REFERENCE = "Usuarios";

        @SuppressLint("MissingInflatedId")
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main_login);

            auth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance ();
            EditText edtEmail = findViewById(R.id.etEmailLogin);
            EditText edtPassword = findViewById(R.id.etPasswordlLogin);

            Button btnLogin = findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(v -> login(edtEmail.getText().toString(), edtPassword.getText().toString()));

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            // Crear un GoogleSignInClient con las opciones especificadas por gso.
            gmsClient = GoogleSignIn.getClient(this, gso);


            Button btnSingIn = findViewById(R.id.btn_Google);
            btnSingIn.setOnClickListener(v -> signIn());

            Button Register=findViewById((R.id.btn_Registro));

            Register.setOnClickListener(v -> registro());
        }

    private void registro() {
        Intent registro = new Intent(ActivityMainLogin.this, Registro.class);
        startActivity(registro);
        ActivityMainLogin.this.finish();
    }


    @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            //Resultado devuelto al iniciar el Intent de GoogleSignInApi.getSignInIntent (...);
            if (requestCode == GOOGLE_SIGNIN_REQUEST_CODE) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                if (task.isSuccessful()) {
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                        firebaseAuthWithGoogle(account.getIdToken());
                    } catch (ApiException e) {
                        // Google Sign In fallido, actualizar GUI
                        Log.w(TAG, "Google sign in failed", e);
                    }
                } else {
                    Log.d(TAG, "Error, login no exitoso:" + Objects.requireNonNull(task.getException()));
                    Toast.makeText(this, "Ocurrio un error. " + task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        }


        //inicio de Sesion correo y contraseña



        private void signIn() {
            auth=FirebaseAuth.getInstance();
            Intent signInIntent = gmsClient.getSignInIntent();
            startActivityForResult(signInIntent, GOOGLE_SIGNIN_REQUEST_CODE);
        }


        private void firebaseAuthWithGoogle(String idToken) {
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            auth.signInWithCredential(credential)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(getBaseContext(), "Usuario Encontrado " , Toast.LENGTH_LONG).show();

                            FirebaseUser user = auth.getCurrentUser();
                            String id_usuario=user.getUid();
                            userG.totalpuntos=0;
                            userG.id=id_usuario;
                            userG.nombre=user.getDisplayName();
                            userG.telefono=user.getPhoneNumber();
                            userG.foto= String.valueOf(user.getPhotoUrl());
                            getUsers(userG);
                            Intent mainActivity = new Intent(ActivityMainLogin.this, MainActivity.class);
                            startActivity(mainActivity);
                            ActivityMainLogin.this.finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    });
        }

    private void doSaveUsers (User user) {
        String nodeId = user.id;
        HashMap<String, Object> entry = new HashMap<> ();
        entry.put (nodeId, user);

        songs = database.getReference (BASE_DATABASE_REFERENCE);
        songs.updateChildren (entry)
                .addOnSuccessListener (aVoid -> {
                    Toast.makeText(getBaseContext(),"Datos almacenado en la BD",Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener (e -> Toast.makeText (getBaseContext (),
                        "Error actualizando la BD: " + e.getMessage (),
                        Toast.LENGTH_LONG).show ());

    }

    private void getUsers (User User) {
        DatabaseReference ref;
        ref = FirebaseDatabase.getInstance().getReference();
        // Agregamos un listener a la referencia
        ref.child("Usuarios").child(User.id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    System.out.println("El usuario ya existe en la Bd de firebase+++++++++++++++++++++++++++++++++++++++++++++++++++++ ");
                }
                else {
                    System.out.println("Entro aqui ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

                    //Si el usuario no existe en la BD guarda los datos
                    doSaveUsers(User);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Fallo la lectura: " + databaseError.getCode());
            }
        });
    }




        private void login(String email, String password) {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            String name = "Encontrado ";

                            if (user != null) {
                                name = user.getDisplayName();
                            }

                            Toast.makeText(getBaseContext(), "Usuario " + name, Toast.LENGTH_LONG).show();
                            Intent mainActivity = new Intent(ActivityMainLogin.this, MainActivity.class);
                            startActivity(mainActivity);
                            ActivityMainLogin.this.finish();

                        } else {
                            Toast.makeText(getBaseContext(), "Usuario y/o contraseña no reconocida!", Toast.LENGTH_LONG).show();
                        }
                    });
        }

    }



