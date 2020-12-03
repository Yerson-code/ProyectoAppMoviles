package com.appmovil.myappuberclone.modelos;

public class Cliente {
    String id;
    String nombre;
    String correo;
    String image;

    public Cliente(String id, String nombre, String correo, String image) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.image = image;
    }

    public Cliente() {
    }

    public Cliente(String id, String nombre, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
