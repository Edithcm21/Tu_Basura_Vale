package com.example.tu_basura_vale;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.tu_basura_vale.fragments.LoginFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;



public class MainActivityLista extends AppCompatActivity {
    public static final String GOOGLE_ID_CLIENT_TOKEN = BuildConfig.GOOGLE_ID_CLIENT_TOKEN;
    public static final int GOOGLE_SIGNIN_REQUEST_CODE = 1001;
    //Agregar cliente de inicio de sesion de google
    private GoogleSignInClient gmsClient;
/*
    //Variable para gestionar FirebaseAuth
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView (R.layout.container_main);

        ActionBar actionBar = getSupportActionBar ();
        if (actionBar != null) {
            actionBar.setTitle ("Tu Basura Vale !!");
        }


        getSupportFragmentManager ()
                .beginTransaction ()
                .add (R.id.rootContainer, new LoginFragment())
                .setTransition (FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit ();




        GoogleSignInOptions.Builder builder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN);
        builder.requestIdToken(GOOGLE_ID_CLIENT_TOKEN);
        builder.requestEmail();
        GoogleSignInOptions gso = builder
                .build ();

        gmsClient = GoogleSignIn.getClient (getBaseContext(), gso);





    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater ().inflate (R.menu.mainl, menu);
        return super.onCreateOptionsMenu (menu);
    }

    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item) {

        int id = item.getItemId ();
        if (id == R.id.mnuRegister) {
            //showRegisterView ();
        } else if (id == R.id.mnuGoogleSignin) {
            showGoogleSignInView ();
        }

        return super.onOptionsItemSelected(item);
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
    }*/
}