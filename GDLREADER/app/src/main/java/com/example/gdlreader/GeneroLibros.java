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
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GeneroLibros extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    Toolbar toolbarNav;
    ShimmerFrameLayout layout;

    Handler handler = new Handler();

    private TextView textviewEncabezado;

    //Creamos la variable para mostrar los datos de la bd
    private TextView mTextViewDataNombre;
    private TextView mTextViewDataNocontrol;



    //Creamos la variable de sesion.
    FirebaseAuth mAtuh;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mDatabase, mDatabaseLibro, mDatabaseLibroApartado;

    //Variables para mostrar los datos del libro
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    ArrayList<Libro> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_mis_libros);

        //Obtenemos el genero del intent anterior
        String Genero = getIntent().getStringExtra("keyGenero");

        Toast.makeText(GeneroLibros.this, "El genero seleccionado: "+Genero, Toast.LENGTH_SHORT).show();

        //Inicializamos variables
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        toolbarNav = findViewById(R.id.toolbar);

        recyclerView = findViewById(R.id.ListaLibro);
        ShimmerProceso();

        //Inicializar la base de datos
        mAtuh = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Instanciamos la variable para los datos que necesitemos.
        mTextViewDataNombre = (TextView) findViewById(R.id.textViewEncabezado);
        mTextViewDataNocontrol = (TextView) findViewById(R.id. TextViewNoControlA);

        //Obtenemos el id de la sesion iniciada
        String idAlumno = mAtuh.getCurrentUser().getUid();

        //Definir las variables para mostrar los libros
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabaseLibro = FirebaseDatabase.getInstance().getReference("Libro");
        mDatabaseLibroApartado = FirebaseDatabase.getInstance().getReference("Apartado");

        FiltroGenero(mDatabaseLibro,Genero);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        myAdapter = new MyAdapter(this,list);
        recyclerView.setAdapter(myAdapter);


        //Referencia para mostrar los datos en el navigation view
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

                    Glide.with(GeneroLibros.this).load(imagen).into(imagena);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GeneroLibros.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });


        getWindow().setStatusBarColor(ContextCompat.getColor(GeneroLibros.this,R.color.color_principal));

        /*Proceso para action bar*/
        navigationView.bringToFront();
        setSupportActionBar(toolbarNav);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbarNav,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);

        /*Aquí se cambia el color del action bar*/
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));

    }
    public void FiltroGenero (DatabaseReference database, String Genero){
        Query q = database.orderByChild("Genero").equalTo(Genero);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map arrayLibrosApartados = new HashMap();

                int count = 0;
                for (DataSnapshot datasnapshot: snapshot.getChildren()){
                    String value = datasnapshot.child("NoLibro").getValue().toString();
                    arrayLibrosApartados.put(count,value);
                    count++;
                }

                if(count == 0){
                    Toast.makeText(GeneroLibros.this, "No hay libros con ese genero ", Toast.LENGTH_SHORT).show();
                }else{

                    mDatabaseLibro.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int contador = 0;

                            for( DataSnapshot dataSnapshot: snapshot.getChildren()){
                                String value = dataSnapshot.child("NoLibro").getValue().toString();

                                for(int i = 0; i<arrayLibrosApartados.size(); i++){
                                    if(arrayLibrosApartados.get(i).toString().equals(value) ){
                                        Libro libro = dataSnapshot.getValue(Libro.class);
                                        list.add(libro);
                                        contador++;
                                    }
                                }
                            }
                            myAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void ShimmerProceso(){
        layout = (ShimmerFrameLayout) findViewById(R.id.shimmer);

        textviewEncabezado = (TextView)findViewById(R.id.textviewEncabezado);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                layout.stopShimmer();
                layout.hideShimmer();
                layout.setVisibility(View.GONE);

                textviewEncabezado.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        },999);
    }

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
                intent = new Intent(GeneroLibros.this, PantallaPerfil.class);
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
                Toast.makeText(this, "Sesion Finalizada", Toast.LENGTH_SHORT).show();
                intent = new Intent(GeneroLibros.this, MainActivity.class);
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