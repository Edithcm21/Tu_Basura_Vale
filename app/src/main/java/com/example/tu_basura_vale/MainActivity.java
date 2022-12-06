package com.example.tu_basura_vale;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.tu_basura_vale.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    TextView nombreUsuario,email;
    ImageView imagenperfil;
    TextView txtQR;

    FirebaseDatabase database;
    private static final String BASE_DATABASE_REFERENCE = "Usuarios";
    private DatabaseReference songs;

    String id;
    User user=new User();
    Codigo codigo=new Codigo();

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


        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        View hView=navigationView.getHeaderView(0);
        //Referenciamos los TexView a modificar del perfil


         nombreUsuario = hView.findViewById(R.id.nombre_usuario);
         email =  hView.findViewById(R.id.correo);
         imagenperfil = hView.findViewById(R.id.img_user);

        txtQR = findViewById (R.id.txtQR);

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


    }

    private void setUserData(FirebaseUser user) {
        nombreUsuario.setText(user.getDisplayName());
        email.setText(user.getEmail());
        id=user.getUid();
        Glide.with(this).load(user.getPhotoUrl()).into(imagenperfil);
        getUsers(id);
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
                //obtiene el id del Qr y mediante el metodo getCodigo obtiene los puntos y verifica que el codigo no haya sido canjeado
                Toast.makeText(this,"Id del codigo"+result.getContents(),Toast.LENGTH_LONG).show();
                String Codigo =result.getContents();
                getCodigo(Codigo);


                }


            }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    //aqui termina el complemento.

    private void getUsers (String id) {

        DatabaseReference ref;
        ref = FirebaseDatabase.getInstance().getReference();
        // Agregamos un listener a la referencia
        ref.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    user =dataSnapshot.getValue(User.class);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Fallo la lectura: " + databaseError.getCode());
            }
        });
    }

    private void getCodigo (String id) {
        //codigo=new Codigo();
        DatabaseReference ref;
        ref = FirebaseDatabase.getInstance().getReference();
        // Agregamos un listener a la referencia
        ref.child("Codigo-Valor").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                        codigo =dataSnapshot.getValue(Codigo.class);
                    if(codigo.canje) {
                        ActualizarUsuario(codigo.puntos);
                        codigo.canje=false;
                        ActualizarQr();
                    }
                    else{
                        Toast.makeText(getBaseContext(), "El codigo ya ha sido utilizado " , Toast.LENGTH_LONG).show();
                    }

                    }
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Fallo la lectura: " + databaseError.getCode());
            }
        });
    }

    private void ActualizarUsuario(int puntosO) {

        HashMap<String, Object> entry = new HashMap<> ();
        user.totalpuntos=user.totalpuntos+puntosO;
        entry.put(user.id,user);


        songs=FirebaseDatabase.getInstance().getReference();
        songs.child("Usuarios").updateChildren(entry).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this,"Se han actualizado correctamente los datos",Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"Se ha producido un error al actualizar la BD",Toast.LENGTH_LONG).show();
            }
        });


    }

    private void ActualizarQr() {
         HashMap<String, Object> entry = new HashMap<> ();
        entry.put(codigo.id,codigo);

        songs=FirebaseDatabase.getInstance().getReference();
        songs.child("Codigo-Valor").updateChildren(entry).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this,"Se han actualizado correctamente los datos del codigo",Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"Se ha producido un error al actualizar la BD",Toast.LENGTH_LONG).show();
            }
        });

    }





}