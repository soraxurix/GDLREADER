package com.example.gdlreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

public class PantallaPerfil extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    Toolbar toolbarNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_perfil);

        /*Inicializamos variables*/
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbarNav = findViewById(R.id.toolbar);

        navigationView.setNavigationItemSelectedListener(this);

        /*Proceso para action bar*/
        navigationView.bringToFront();
        setSupportActionBar(toolbarNav);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbarNav,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_perfil:
                /*Toast toast = Toast.makeText(getApplicationContext(), "Has dado click en perfil", Toast.LENGTH_SHORT);
                toast.show();*/
                Intent intent = new Intent(this, PantallaPerfil.class);
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
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}