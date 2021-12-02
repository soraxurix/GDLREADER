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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.novoda.merlin.Bindable;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.NetworkStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class InformacionLibro extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Connectable, Disconnectable, Bindable {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    Toolbar toolbarNav;

    //Creamos la variable de sesion.
    FirebaseAuth mAtuh;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mDatabase, mDatabaseLibro, mDatabaseLibroApartado;
    Random rand = new Random();
    int idrandom = rand.nextInt(100000000);
    private Button buttonApartar;

    //Merlin
    private Merlin merlin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_libro);

        getWindow().setStatusBarColor(ContextCompat.getColor(InformacionLibro.this,R.color.color_principal));

        /*Inicializamos variables*/
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbarNav = findViewById(R.id.toolbar);
        buttonApartar = (Button) findViewById(R.id.buttonApartar);

        navigationView.setNavigationItemSelectedListener(this);

        /*Proceso para action bar*/
        navigationView.bringToFront();
        setSupportActionBar(toolbarNav);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbarNav,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Inicializar la base de datos
        mAtuh = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabaseLibro = FirebaseDatabase.getInstance().getReference();
        mDatabaseLibroApartado = FirebaseDatabase.getInstance().getReference("Apartado");

        //Merlin
        merlin = new Merlin.Builder().withConnectableCallbacks()
                .withDisconnectableCallbacks()
                .withBindableCallbacks()
                .build(this);
        merlin.registerBindable(this);
        merlin.registerConnectable(this);
        merlin.registerDisconnectable(this);

        //Obtenemos el id de la sesion iniciada
        String idAlumno = mAtuh.getCurrentUser().getUid();

        String idLibro = getIntent().getStringExtra("keyLibro");
        //Toast.makeText(InformacionLibro.this, idLibro, Toast.LENGTH_SHORT).show();

        mDatabaseLibro.child("Libro").child(idLibro).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tituloLibro = snapshot.child("Titulo").getValue().toString();
                //Toast.makeText(InformacionLibro.this, idLibro, Toast.LENGTH_SHORT).show();
                String autorLibro = snapshot.child("Autor").getValue().toString();
                String editorialLibro = snapshot.child("Editorial").getValue().toString();
                String idiomaLibro = snapshot.child("Idioma").getValue().toString();
                String paginasLibro = snapshot.child("Paginas").getValue().toString();
                String dimensionesLibro = snapshot.child("Dimensiones").getValue().toString();
                String sinopsisLibro = snapshot.child("Sinopsis").getValue().toString();
                String imagenLibro = snapshot.child("Url").getValue().toString();


                //String imagenLibro = snapshot.child("Url").getValue().toString();
                TextView TitutloLibro = (TextView) findViewById(R.id.InfoTitulo);
                TitutloLibro.setText(tituloLibro);

                TextView AutorLibro = (TextView) findViewById(R.id.InfoAutor);
                AutorLibro.setText(autorLibro);

                TextView EditorialLibro = (TextView) findViewById(R.id.InfoEditorial);
                EditorialLibro.setText(editorialLibro);

                TextView IdiomaLibro = (TextView) findViewById(R.id.InfoIdioma);
                IdiomaLibro.setText(idiomaLibro);

                TextView PaginasLibro = (TextView) findViewById(R.id.InfoPaginas);
                PaginasLibro.setText(paginasLibro);

                TextView DimensionesLibro = (TextView) findViewById(R.id.InfoDimensiones);
                DimensionesLibro.setText(dimensionesLibro);

                TextView SinopsisLibro = (TextView) findViewById(R.id.sipnosis);
                SinopsisLibro.setText(sinopsisLibro);

                ImageView ImagenLibro = (ImageView) findViewById(R.id.image_Portada);
                Glide.with(InformacionLibro.this).load(imagenLibro).into(ImagenLibro);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mDatabase.child("Alumno").child(idAlumno).addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String nombre = dataSnapshot.child("Nombre").getValue().toString();
                    String noControl = dataSnapshot.child("Nocontrol").getValue().toString();
                    String imagen = dataSnapshot.child("Imagen").getValue().toString();

                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    View headerView = navigationView.getHeaderView(0);

                    TextView navUsername = (TextView) headerView.findViewById(R.id.TextViewNombreA);
                    navUsername.setText(nombre);

                    TextView noControlView = (TextView) headerView.findViewById(R.id.TextViewNoControlA);
                    noControlView.setText(noControl);

                    ImageView imagena = (ImageView) headerView.findViewById(R.id.ImageViewPrincipal);

                    Glide.with(InformacionLibro.this).load(imagen).into(imagena);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InformacionLibro.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        buttonApartar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> map = new HashMap<>();
                map.put("IdLibro",idLibro);
                map.put("IdAlumno",idAlumno);
                ValidarLibroApartado(mDatabaseLibroApartado,idLibro,map);
                /*if(ValidarLibroApartado(mDatabaseLibroApartado,idLibro)){
                    Toast toast = Toast.makeText(getApplicationContext(), "No se puede apatar el libro", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Sí se puede apatar el libro", Toast.LENGTH_SHORT);
                    toast.show();
                }*/

                /*mDatabaseLibroApartado.child(String.valueOf(idrandom)).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast toast = Toast.makeText(getApplicationContext(), "Se ha apartado el libro", Toast.LENGTH_SHORT);
                        toast.show();
                        buttonApartar.setBackgroundColor(getResources().getColor(R.color.gray));
                        buttonApartar.setEnabled(false);
                    }
                });*/
            }
        });

    }

    public void ValidarLibroApartado (DatabaseReference database, String idLibro, Map map){
        Query q = database.orderByChild("IdLibro").equalTo(idLibro);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot datasnapshot: snapshot.getChildren()){
                    count++;
                }

                if(count == 0){
                    //Toast.makeText(InformacionLibro.this, "Sí se puede apartar ", Toast.LENGTH_SHORT).show();
                    mDatabaseLibroApartado.child(String.valueOf(idrandom)).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast toast = Toast.makeText(getApplicationContext(), "Se ha apartado el libro", Toast.LENGTH_SHORT);
                            toast.show();
                            buttonApartar.setBackgroundColor(getResources().getColor(R.color.gray));
                            buttonApartar.setEnabled(false);
                        }
                    });
                }else{
                    Toast.makeText(InformacionLibro.this, "El libro ya está apartado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                intent = new Intent(InformacionLibro.this, PantallaPerfil.class);
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
                Toast.makeText(InformacionLibro.this, "Sesion Finalizada", Toast.LENGTH_SHORT).show();
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