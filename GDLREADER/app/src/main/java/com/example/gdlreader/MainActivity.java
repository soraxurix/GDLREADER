package com.example.gdlreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    //Creamos variables para guardar los datos de los editext.
    EditText CorreoAL, ContraseñaAL;
    private Button button;

    //Variable de sesion
    private FirebaseAuth mAuth;

    //
    TextView textView;

    //Creamos las variables para la base de datos
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

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
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.color_principal));

        //Inicializar la base de datos
        inicializarFirebase();

        //Instanciamos las variables
        CorreoAL = (EditText) findViewById(R.id.editTextCorreoA);
        ContraseñaAL = (EditText) findViewById(R.id.editTextContraseñaA);


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

                String correoal = CorreoAL.getText().toString();
                String contraseñaal = ContraseñaAL.getText().toString();


                if(correoal.equals("") || contraseñaal.equals("")){
                    validacion();
                }else{
                    InicioSesion();
                }

            }
        });

    }

    /*public void openPantallaPrincipal() {
        Intent intent = new Intent(this, PantallaPrincipal.class);
        startActivity(intent);
    }*/



    public void openRegistrar() {
        Intent intent = new Intent(this, Registrar.class);
        startActivity(intent);
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    //Metodo para validar los text
    private void validacion() {
        String correoal = CorreoAL.getText().toString();
        String contraseñaal = ContraseñaAL.getText().toString();
        if(correoal.equals("")){
            CorreoAL.setError("Campo vacio");
        }
        else if(contraseñaal.equals("")){
            ContraseñaAL.setError("Campo vacio");
        }

    }

    private void InicioSesion(){
        String correoal = CorreoAL.getText().toString();
        String contraseñaal = ContraseñaAL.getText().toString();
        mAuth.signInWithEmailAndPassword(correoal,contraseñaal).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                   /* String id = mAuth.getCurrentUser().getUid();
                    databaseReference.child("Alumno").child(id).setValue();*/
                    startActivity(new Intent(MainActivity.this,PantallaPrincipal.class));
                    Toast.makeText(MainActivity.this, "Sesion iniciada", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Toast.makeText(MainActivity.this, "No se pudo inciar sesion, comprueba tus datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null){
            startActivity(new Intent(MainActivity.this,PantallaPrincipal.class));
            finish();
        }
    }
}