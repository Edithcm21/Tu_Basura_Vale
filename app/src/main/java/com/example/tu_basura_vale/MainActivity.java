package com.example.tu_basura_vale;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.tu_basura_vale.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//QR
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {
    TextView nombreUsuario,email;
    ImageView imagenperfil;
    TextView txtQR;
    int totalPuntos=0;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(view -> Snackbar.make(view, "Ubicación de los basureros", Snackbar.LENGTH_LONG)
               .setAction("Action", null).show());

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        View hView=navigationView.getHeaderView(0);
        //Referenciamos los TexView a modificar del perfil

         nombreUsuario = (TextView) hView.findViewById(R.id.nombre_usuario);
         email = (TextView) hView.findViewById(R.id.correo);
         imagenperfil = (ImageView) hView.findViewById(R.id.img_user);
        //nombreUsuario.setText("Edith Colorado Morales");

        //Instanciamos Firebase y al Usuario que ingresó
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    setUserData(user);
                }
                else{
                    Intent mainActivity = new Intent(MainActivity.this, ActivityMainLogin.class);
                    startActivity(mainActivity);
                    MainActivity.this.finish();
                }
            }
        };

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_inicio, R.id.nav_basureros, R.id.nav_configuracion, R.id.nav_informacion)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //aqui inicia el codigo de escaner

        Button btnQR = findViewById(R.id.btnQR);
        txtQR=findViewById(R.id.txtQR);


        btnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrador=new IntentIntegrator(MainActivity.this);
                integrador.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrador.setPrompt("Lector QR");
                integrador.setCameraId(0);
                integrador.setBeepEnabled(true);
                integrador.setBarcodeImageEnabled(true);
                integrador.initiateScan();

            }
        });

     //Aqui termina
    }

    private void setUserData(FirebaseUser user) {
        nombreUsuario.setText(user.getDisplayName());
        email.setText(user.getEmail());
        Glide.with(this).load(user.getPhotoUrl()).into(imagenperfil);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

    //complemento del escaner

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Puntos: " + result.getContents(), Toast.LENGTH_LONG).show();
                totalPuntos= Integer.parseInt(result.getContents())+totalPuntos;
                txtQR.setText(String.valueOf(totalPuntos));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //aqui termina el complemento.

}