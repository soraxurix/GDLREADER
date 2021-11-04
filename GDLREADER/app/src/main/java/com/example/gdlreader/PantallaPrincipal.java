package com.example.gdlreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gdlreader.ui.Registro;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ValueEventListener;

public class PantallaPrincipal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    Toolbar toolbarNav;


    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    //Creamos la variable para mostrar los datos de la bd
    private TextView mTextViewDataNombre;
    private TextView mTextViewDataNocontrol;
   /* private DatabaseReference mDatabase;*/


    //Creamos la variable de sesion.
    FirebaseAuth mAtuh;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);
        /*Inicializamos variables*/
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        toolbarNav = findViewById(R.id.toolbar);
        imageView1 =  (ImageView) findViewById(R.id.imagePortada1);
        imageView2 =  (ImageView) findViewById(R.id.imagePortada2);
        imageView3 =  (ImageView) findViewById(R.id.imagePortada3);

        //Inicializar la base de datos
        mAtuh = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        /*inicializarFirebase();*/

        //Instanciamos la variable para los datos que necesitemos.
        mTextViewDataNombre = (TextView) findViewById(R.id.textViewEncabezado);
        mTextViewDataNocontrol = (TextView) findViewById(R.id. TextViewNoControlA);

        //Obtenemos el id de la sesion iniciada
        String idAlumno = mAtuh.getCurrentUser().getUid();
        mDatabase.child("Alumno").child(idAlumno).addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String nombre = dataSnapshot.child("Nombre").getValue().toString();
                    String noControl = dataSnapshot.child("Nocontrol").getValue().toString();
                    /*mTextViewDataNombre.setText(nombre);*/
                    /*Toast.makeText(PantallaPrincipal.this, "El nombre del usuario es: "+ nombre, Toast.LENGTH_SHORT).show()*/;

                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    View headerView = navigationView.getHeaderView(0);

                    TextView navUsername = (TextView) headerView.findViewById(R.id.TextViewNombreA);
                    navUsername.setText(nombre);

                    TextView noControlView = (TextView) headerView.findViewById(R.id.TextViewNoControlA);
                    noControlView.setText(noControl);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PantallaPrincipal.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });


        getWindow().setStatusBarColor(ContextCompat.getColor(PantallaPrincipal.this,R.color.color_principal));

        /*Proceso para action bar*/
        navigationView.bringToFront();
        setSupportActionBar(toolbarNav);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbarNav,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);

        /*Creamos el evento para seleccionar un libro*/
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInformacionLibros();
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInformacionLibros();
            }
        });

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInformacionLibros();
            }
        });

        /*Aquí se cambia el color del action bar*/
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
                //Sacamos los valores


        //TomarDatos();

    }

    /*private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        alumnoReference = firebaseDatabase.getReference("Alumno");
        mAuth = FirebaseAuth.getInstance();
    }*/

    public void openInformacionLibros(){
        Intent intent = new Intent(this, InformacionLibro.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.nav_inicio:
                Intent intent = new Intent(this, PantallaPrincipal.class);
                startActivity(intent);
                break;
            case R.id.nav_perfil:
                /*Toast toast = Toast.makeText(getApplicationContext(), "Has dado click en perfil", Toast.LENGTH_SHORT);
                toast.show();*/
                intent = new Intent(PantallaPrincipal.this, PantallaPerfil.class);
                startActivity(intent);
                break;
            case R.id.nav_Busqueda:
                 intent = new Intent(this, PantallaBusqueda.class);
                startActivity(intent);
                break;
            case R.id.nav_tarjeta:
                 intent = new Intent(this, PantallaTarjeta.class);
                startActivity(intent);
                break;
            case R.id.nav_libro:
                intent = new Intent(this, PantallaMisLibros.class);
                startActivity(intent);
                break;
            case R.id.nav_cerrarsesion:
                mAtuh.signOut();
                Toast.makeText(PantallaPrincipal.this, "Sesion Finalizada", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_nosotros:
                intent = new Intent(this, SobreNosotros.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    private void TomarDatos(){


        /*databaseReference.child("Alumno").addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String nombre = dataSnapshot.child("nombre").getValue().toString();
                    mTextViewDataNombre.setText(nombre);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PantallaPrincipal.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

}