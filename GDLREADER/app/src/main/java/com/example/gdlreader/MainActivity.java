package com.example.gdlreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button button;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setTheme(R.style.SplashTheme);//Carga el splash
        setTheme(R.style.Theme_GDLREADER);//Carga la pantalla principa
        setContentView(R.layout.activity_main);


        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.color_principal));

        textView = (TextView) findViewById(R.id.textViewRegistrate);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegistrar();
            }
        });


        button = (Button) findViewById(R.id.buttonInicarSesion);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPantallaPrincipal();
            }
        });

    }
    public void openPantallaPrincipal(){
        Intent intent = new Intent(this, PantallaPrincipal.class);
        startActivity(intent);
    }

    public void openRegistrar(){
        Intent intent = new Intent(this, Registrar.class);
        startActivity(intent);
    }
}