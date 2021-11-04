package com.example.gdlreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gdlreader.ui.Registro;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.FirebaseAuthCredentialsProvider;
import com.google.firebase.firestore.auth.User;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Registrar extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Button button;
    //Definicion de variables
    EditText NoControlA, NombreA, CorreoA, ContraseñaA;
    Spinner CarreraA, SemestreA;

    String nocontrol ="";
    String nombre ="";
    String correo ="";
    String contraseña ="";
    String carrera ="";
    String semestre ="";

    //Variables para la base de datos.
    FirebaseAuth mAtuh;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        mAtuh = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Igualamos la variable de sesion

        //Cambiar color a la barra del telefono
        getWindow().setStatusBarColor(ContextCompat.getColor(Registrar.this,R.color.color_principal));

        //Listas para el registro
        Spinner spinner = findViewById(R.id.spinner1);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Carreras, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        int PositionSpinner = spinner.getSelectedItemPosition();

        Spinner spinner2 = findViewById(R.id.spinner2);

        ArrayAdapter<CharSequence> adapter2 =ArrayAdapter.createFromResource(this,R.array.Semestre, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(this);

        //Igualamos las variables al id del diseño
        NoControlA = (EditText) findViewById(R.id.editTextNoControl);
        NombreA = (EditText) findViewById(R.id.edittextPass);
        CorreoA = (EditText) findViewById(R.id.editTextCorreo);
        ContraseñaA = (EditText) findViewById(R.id.editTextContraseña);
        CarreraA = (Spinner) findViewById(R.id.spinner1);
        SemestreA = (Spinner) findViewById(R.id.spinner2);

        //Inicializar la base de datos
        //inicializarFirebase();

        //Boton
        button = (Button) findViewById(R.id.buttonInicarSesion);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Agregar.Trim() Para eleminar los espacios que hay de más
                 nocontrol = NoControlA.getText().toString();
                 nombre = NombreA.getText().toString();
                 correo = CorreoA.getText().toString();
                 contraseña = ContraseñaA.getText().toString();
                 carrera = CarreraA.getSelectedItem().toString();
                 semestre = SemestreA.getSelectedItem().toString();


                if(nocontrol.equals("") || nombre.equals("") ||correo.equals("") || contraseña.equals("") || contraseña.length()<6 || !validarEmail(correo) ){
                    validacion();
                }
                else{
                        Mensaje();
                }

            }
        });
    }

    /*private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mAtuh = FirebaseAuth.getInstance();
    }*/

    //Metodo para limpiar los text
    private void limpiar() {
        NoControlA.setText("");
        NombreA.setText("");
        CorreoA.setText("");
        ContraseñaA.setText("");
    }

    //Metodo para validar los text
    private void validacion() {
        if(nocontrol.equals("")){
            NoControlA.setError("Campo vacio");
        }
        else if(nombre.equals("")){
            NombreA.setError("Campo vacio");
        }
        else if(correo.equals("")){
            CorreoA.setError("Campo vacio");
        }
       else if (!validarEmail(correo)){
            CorreoA.setError("Email no válido");
        }
        else if(contraseña.equals("")){
            ContraseñaA.setError("Campo vacio");
        }
        else if(contraseña.length()<6){
            ContraseñaA.setError("La contraseña debe tener 6 más caracteres");
        }

        /*else if(carrera.equals("")){
            CarreraA.setError("Campo vacio");
        }
        else if(semestre.equals("")){
            SemestreA.setError("Campo vacio");
        }*/

    }

    //Con este metodo se guarda el correo y la contraseña en la parte de autenticacion en firebase y mostramos un mensaje de que los datos estan guardados.
    public void Mensaje (){
        /*String correo = CorreoA.getText().toString();
        String contraseña = ContraseñaA.getText().toString();*/
        mAtuh.createUserWithEmailAndPassword(correo,contraseña).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Registrar.this,"Se guardaron los datos",Toast.LENGTH_SHORT).show();
                    Map<String, Object> map = new HashMap<>();
                    map.put("Nocontrol",nocontrol);
                    map.put("Nombre",nombre);
                    map.put("Correo",correo);
                    map.put("Contraseña",contraseña);
                    map.put("Carrera",carrera);
                    map.put("Semestre",semestre);

                    String id = mAtuh.getCurrentUser().getUid();

                    mDatabase.child("Alumno").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task2.isSuccessful()){
                                limpiar();
                                startActivity(new Intent( Registrar.this, MainActivity.class));
                                finish();
                            }
                            else{
                                Toast.makeText(Registrar.this, "Los datos no son validos", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(Registrar.this, "No se pudo registrar", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        /*String text = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(adapterView.getContext(),text,Toast.LENGTH_SHORT).show();*/
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}