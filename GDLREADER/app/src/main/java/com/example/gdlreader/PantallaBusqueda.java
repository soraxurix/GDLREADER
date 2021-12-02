package com.example.gdlreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

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

public class PantallaBusqueda extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Connectable, Disconnectable, Bindable {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    Toolbar toolbarNav;
    //Creamos la variable de sesion.
    FirebaseAuth mAtuh;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mDatabase;
    CardView cardView1, cardView2, cardView3, cardView4, cardView5, cardView6;
    SearchView searchView;

    //Merlin
    private Merlin merlin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_busqueda);

        getWindow().setStatusBarColor(ContextCompat.getColor(PantallaBusqueda.this,R.color.color_principal));

        /*Inicializamos variables*/
        searchView = findViewById(R.id.buscador);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbarNav = findViewById(R.id.toolbar);
        //Definimos el id de las tarjetas
        cardView1 =  (CardView) findViewById(R.id.CardView1);
        cardView2 =  (CardView) findViewById(R.id.CardView2);
        cardView3 =  (CardView) findViewById(R.id.CardView3);
        cardView4 =  (CardView) findViewById(R.id.CardView4);
        cardView5 =  (CardView) findViewById(R.id.CardView5);
        cardView6 =  (CardView) findViewById(R.id.CardView6);

        //Merlin
        merlin = new Merlin.Builder().withConnectableCallbacks()
                .withDisconnectableCallbacks()
                .withBindableCallbacks()
                .build(this);
        merlin.registerBindable(this);
        merlin.registerConnectable(this);
        merlin.registerDisconnectable(this);

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

                    Glide.with(PantallaBusqueda.this).load(imagen).into(imagena);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PantallaBusqueda.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        navigationView.setNavigationItemSelectedListener(this);

        /*Proceso para action bar*/
        navigationView.bringToFront();
        setSupportActionBar(toolbarNav);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbarNav,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Caso que se seleccione Tic's
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGeneroLibrosTics();
            }
        });
        //Caso que se seleccione Química
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGeneroLibrosQuimica();
            }
        });
        //Caso que se seleccione Matemáticas
        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGeneroLibrosMatematicas();
            }
        });
        //Caso que se seleccione Logística
        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGeneroLibrosLogistica();
            }
        });
        //Caso que se seleccione Electrónica
        cardView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGeneroLibrosElectronica();
            }
        });
        //Caso que se seleccione Negocios
        cardView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGeneroLibrosNegocios();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                openResultadoBusqueda(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (searchView.getQuery().length() == 0) {
                    Toast.makeText(PantallaBusqueda.this, "No has dado submit", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

    }

    public void openResultadoBusqueda(String ResultadoBusqueda) {
        Intent intent = new Intent(this, PantallaResultadoBusqueda.class);
        intent.putExtra("keyBuscador", ResultadoBusqueda);
        startActivity(intent);
    }
    public void openGeneroLibrosTics() {
        Intent intent = new Intent(this, GeneroLibros.class);
        intent.putExtra("keyGenero","Tic's");
        startActivity(intent);
    }
    public void openGeneroLibrosQuimica() {
        Intent intent = new Intent(this, GeneroLibros.class);
        intent.putExtra("keyGenero","Química");
        startActivity(intent);
    }
    public void openGeneroLibrosMatematicas() {
        Intent intent = new Intent(this, GeneroLibros.class);
        intent.putExtra("keyGenero","Matemáticas");
        startActivity(intent);
    }
    public void openGeneroLibrosLogistica() {
        Intent intent = new Intent(this, GeneroLibros.class);
        intent.putExtra("keyGenero","Logistíca");
        startActivity(intent);
    }
    public void openGeneroLibrosElectronica() {
        Intent intent = new Intent(this, GeneroLibros.class);
        intent.putExtra("keyGenero","Electrónica");
        startActivity(intent);
    }
    public void openGeneroLibrosNegocios() {
        Intent intent = new Intent(this, GeneroLibros.class);
        intent.putExtra("keyGenero","Negocios");
        startActivity(intent);
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
                intent = new Intent(PantallaBusqueda.this, PantallaPerfil.class);
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
                Toast.makeText(PantallaBusqueda.this, "Sesion Finalizada", Toast.LENGTH_SHORT).show();
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