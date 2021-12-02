package com.example.gdlreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.novoda.merlin.Bindable;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.NetworkStatus;

public class SobreNosotros extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Connectable, Disconnectable, Bindable {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    Toolbar toolbarNav;
    ImageView imageWhats, imageFace;
    String urlw ="https://www.whatsapp.com/", urlf= "https://www.facebook.com/Servicios-Escolares-ITA-1064745943600915";


    //Creamos la variable de sesion.
    FirebaseAuth mAtuh;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mDatabase;


    //Merlin
    private Merlin merlin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre_nosotros);
        getWindow().setStatusBarColor(ContextCompat.getColor(SobreNosotros.this,R.color.color_principal));
        /*Inicializamos variables*/
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbarNav = findViewById(R.id.toolbar);
        imageWhats =findViewById(R.id.logow);
        imageFace =findViewById(R.id.logof);
        //Inicializar la base de datos
        mAtuh = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Obtenemos el id de la sesion iniciada
        String idAlumno = mAtuh.getCurrentUser().getUid();
        mDatabase.child("Alumno").child(idAlumno).addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String nombre = dataSnapshot.child("Nombre").getValue().toString();
                    String noControl = dataSnapshot.child("Nocontrol").getValue().toString();
                    String imagen = dataSnapshot.child("Imagen").getValue().toString();
                    /*mTextViewDataNombre.setText(nombre);*/
                    /*Toast.makeText(PantallaPrincipal.this, "El nombre del usuario es: "+ nombre, Toast.LENGTH_SHORT).show()*/;

                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    View headerView = navigationView.getHeaderView(0);

                    TextView navUsername = (TextView) headerView.findViewById(R.id.TextViewNombreA);
                    navUsername.setText(nombre);

                    TextView noControlView = (TextView) headerView.findViewById(R.id.TextViewNoControlA);
                    noControlView.setText(noControl);

                    ImageView imagena = (ImageView) headerView.findViewById(R.id.ImageViewPrincipal);

                    Glide.with(SobreNosotros.this).load(imagen).into(imagena);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SobreNosotros.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });


        imageWhats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri linkw = Uri.parse(urlw);
                Intent intent = new Intent(Intent.ACTION_VIEW,linkw);
                startActivity(intent);
            }
        });
        imageFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri linkw = Uri.parse(urlf);
                Intent intent = new Intent(Intent.ACTION_VIEW,linkw);
                startActivity(intent);
            }
        });

        navigationView.setNavigationItemSelectedListener(this);

        /*Proceso para action bar*/
        navigationView.bringToFront();
        setSupportActionBar(toolbarNav);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbarNav,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    //Metodos merlin
    @Override
    public void onResume() {
        super.onResume();

        if(merlin!=null){
            merlin.bind();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(merlin!=null){
            merlin.unbind();
        }
    }

    @Override
    public void onBind(NetworkStatus networkStatus) {
        if(!networkStatus.isAvailable()){
            onDisconnect();
        }
    }

    @Override
    public void onConnect() {
        //Toast.makeText(getApplication(),"Conectado a internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnect() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplication(),"Sin conexión a internet", Toast.LENGTH_SHORT).show();
            }
        });
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
                intent = new Intent(SobreNosotros.this, PantallaPerfil.class);
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
                Toast.makeText(SobreNosotros.this, "Sesion Finalizada", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_nosotros:
                intent = new Intent(this, SobreNosotros.class);
                startActivity(intent);
                break;

            case R.id.nav_config:
                intent = new Intent(this, Configuracion.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}