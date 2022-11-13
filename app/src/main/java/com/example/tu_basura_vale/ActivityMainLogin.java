package com.example.tu_basura_vale;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


    public class ActivityMainLogin extends AppCompatActivity {

        public static final String GOOGLE_ID_CLIENT_TOKEN = BuildConfig.GOOGLE_ID_CLIENT_TOKEN;
        public static final int GOOGLE_SIGNIN_REQUEST_CODE = 1001;
        //Agregar cliente de inicio de sesion de google
        private GoogleSignInClient gmsClient;
        //Variable para gestionar FirebaseAuth
        private FirebaseAuth auth;
        private Button btnSingIn;

        @SuppressLint("MissingInflatedId")
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView (R.layout.activity_main_login);
//            ActionBar actionBar = getSupportActionBar ();
//            if (actionBar != null) {
//                actionBar.setTitle ("Tu Basura Vale !!");
//            }

            auth = FirebaseAuth.getInstance ();
            EditText edtEmail =findViewById (R.id.etEmailLogin);
            EditText edtPassword =findViewById (R.id.etPasswordlLogin);

            Button btnLogin =findViewById (R.id.btnLogin);
            btnLogin.setOnClickListener (v -> {
                login (edtEmail.getText ().toString (), edtPassword.getText().toString ());
            });

            GoogleSignInOptions.Builder builder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN);
            builder.requestIdToken(GOOGLE_ID_CLIENT_TOKEN);
            builder.requestEmail();
            GoogleSignInOptions gso = builder
                    .build ();
            gmsClient = GoogleSignIn.getClient (this, gso);

            btnSingIn =findViewById (R.id.btn_Google);
            btnSingIn.setOnClickListener (v -> {
                showGoogleSignInView ();
            });




        }



        private void showGoogleSignInView () {
            auth = FirebaseAuth.getInstance ();

            Intent intent = gmsClient.getSignInIntent ();
            myActivityResultLauncher.launch (intent);
            //startActivityForResult (intent, GOOGLE_SIGNIN_REQUEST_CODE);
        }

        ActivityResultLauncher<Intent> myActivityResultLauncher = registerForActivityResult (
                new ActivityResultContracts.StartActivityForResult (),
                result -> {
                    if (result.getResultCode() == GOOGLE_SIGNIN_REQUEST_CODE) {
                        // if (result.getData() == null) return;
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent (result.getData ());

                        GoogleSignInAccount account = task.getResult();
                        if (account != null) firebaseAuthWithGoogleServices (account);
                    }
                }
        );


        private void firebaseAuthWithGoogleServices (GoogleSignInAccount account) {
            AuthCredential credential = GoogleAuthProvider.getCredential (account.getIdToken (), null);
            auth.signInWithCredential (credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful ()) {
                            Toast.makeText (getBaseContext (), "Google SignIn Successful!", Toast.LENGTH_LONG).show ();
                        } else {
                            Toast.makeText (getBaseContext (),
                                    "SignIn with Google services failed with exception " +
                                            (task.getException () != null ? task.getException().getMessage () : "None"),
                                    Toast.LENGTH_LONG).show ();
                        }
                    });
        }

        private void login (String email, String password) {
            auth.signInWithEmailAndPassword (email, password)
                    .addOnCompleteListener (task -> {
                        if (task.isSuccessful ()) {
                            FirebaseUser user = auth.getCurrentUser ();
                            String name = "Encontrado ";

                            if (user != null) {
                                name = user.getDisplayName ();
                            }

                            Toast.makeText (getBaseContext(), "Usuario " + name, Toast.LENGTH_LONG).show ();
                            Intent mainActivity=new Intent(ActivityMainLogin.this, MainActivity.class);
                            startActivity(mainActivity);
                            ActivityMainLogin.this.finish();

                        } else {
                            Toast.makeText (getBaseContext(), "Usuario y/o contraseña no reconocida!", Toast.LENGTH_LONG).show ();
                        }
                    });
        }
    }



