
package com.example.gdlreader;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gdlreader.databinding.ActivityMainBinding;
import com.example.gdlreader.ui.Registro;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.novoda.merlin.Bindable;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.NetworkStatus;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

import id.zelory.compressor.Compressor;

public class Registrar extends AppCompatActivity implements AdapterView.OnItemSelectedListener, Connectable, Disconnectable, Bindable {
    private Button button;

    //Definicion de variables
    EditText NoControlA, NombreA, CorreoA, ContraseñaA;
    ImageView FotoA;
    Spinner CarreraA, SemestreA;
    TextView   tvUrl;


    ActivityResultLauncher<String> mGetContent;

    String nocontrol ="";
    String nombre ="";
    String correo ="";
    String contraseña ="";
    String carrera ="";
    String semestre ="";
    String FotoaA="";
    //Variables para el numero random
    Random rand = new Random();
    int abcd = rand.nextInt(100000000);

    String Tarjeta= String.valueOf(abcd);

    //Variables para la base de datos.
    FirebaseAuth mAtuh;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage storage;
    DatabaseReference mDatabase;

    StorageReference storageReference;
    Bitmap thumb_bitmap = null;
    ProgressDialog cargando;
    Uri imageUrl, resultUri;

    private Merlin merlin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        //Merlin
        merlin = new Merlin.Builder().withConnectableCallbacks()
        .withDisconnectableCallbacks()
        .withBindableCallbacks()
        .build(this);
        merlin.registerBindable(this);
        merlin.registerConnectable(this);
        merlin.registerDisconnectable(this);

        //Merlin
        merlin = new Merlin.Builder().withConnectableCallbacks()
                .withDisconnectableCallbacks()
                .withBindableCallbacks()
                .build(this);
        merlin.registerBindable(this);
        merlin.registerConnectable(this);
        merlin.registerDisconnectable(this);

        //Este metodo si funciona
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                FotoA.setImageURI(result);
                imageUrl=result;
            }
        });

        //Instancias las variables de firebase
        mAtuh = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage =FirebaseStorage.getInstance();



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
        FotoA = (ImageView) findViewById(R.id.imagenPerfil);
        tvUrl = (TextView) findViewById(R.id.textViewEncabezado);

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
                FotoaA =imageUrl.toString();
                if(nocontrol.equals("") || nombre.equals("") ||correo.equals("") || contraseña.equals("") || contraseña.length()<6 || !validarEmail(correo) ){
                    validacion();
                }
                else{
                    Mensaje();
                }

            }
        });

        //Creamos el metodo para subir la imagen dando click en la imagen
        FotoA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mGetContent.launch("image/*");


            }
        });



    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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


    }

    //Con este metodo se guarda el correo y la contraseña en la parte de autenticacion en firebase y mostramos un mensaje de que los datos estan guardados.
    public void Mensaje (){

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
                    map.put("Tarjeta",Tarjeta);


                    //Funciona
                    if (imageUrl!= null){
                        StorageReference reference = storage.getReference().child("Imagenes/"+ UUID.randomUUID().toString());
                        String PruebaMensaje = reference.getPath().toString();
                        Toast.makeText(Registrar.this,PruebaMensaje,Toast.LENGTH_SHORT).show();

                        reference.putFile(imageUrl).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){

                                    Toast.makeText(Registrar.this,"Imagen subida correctamente",Toast.LENGTH_SHORT).show();

                                }
                                else{
                                    Toast.makeText(Registrar.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    map.put("Imagen",FotoaA);

                    String id = mAtuh.getCurrentUser().getUid();

                    mDatabase.child("Alumno").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task2.isSuccessful()){
                                limpiar();
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
        Toast.makeText(getApplication(),"Conectado a internet", Toast.LENGTH_SHORT).show();
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
}