package com.example.gdlreader.ui;

import android.widget.Button;

public class Registro {

    private String NoControl;
    private String Nombre;
    private String CorreoAlumno;
    private String Contraseña;
    private String Carrera;
    private String Semestre;


    public Registro() {
    }
    //Gets and Setter para tomar y dar los datos
    public String getNoControl() {
        return NoControl;
    }

    public void setNoControl(String noControl) {
        NoControl = noControl;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getCorreoAlumno() {
        return CorreoAlumno;
    }

    public void setCorreoAlumno(String correoAlumno) {
        CorreoAlumno = correoAlumno;
    }

    public String getContraseña() {
        return Contraseña;
    }

    public void setContraseña(String contraseña) {
        Contraseña = contraseña;
    }

    public String getCarrera() {
        return Carrera;
    }

    public void setCarrera(String carrera) {
        Carrera = carrera;
    }

    public String getSemestre() {
        return Semestre;
    }

    public void setSemestre(String semestre) {
        Semestre = semestre;
    }

    @Override
    public String toString() {
        return Nombre;
    }
}


