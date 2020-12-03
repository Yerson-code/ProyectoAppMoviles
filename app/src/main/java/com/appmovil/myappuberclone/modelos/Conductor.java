package com.appmovil.myappuberclone.modelos;

public class Conductor {
    String id;
    String nombre;
    String correo;
    String marcaVehiculo;
    String placaVehiculo;
    String image;

    public Conductor() {
    }

    public Conductor(String id, String nombre, String correo, String marcaVehiculo, String placaVehiculo) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.marcaVehiculo = marcaVehiculo;
        this.placaVehiculo = placaVehiculo;
    }

    public Conductor(String id, String nombre, String correo, String marcaVehiculo, String placaVehiculo, String image) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.marcaVehiculo = marcaVehiculo;
        this.placaVehiculo = placaVehiculo;
        this.image = image;
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

    public String getMarcaVehiculo() {
        return marcaVehiculo;
    }

    public void setMarcaVehiculo(String marcaVehiculo) {
        this.marcaVehiculo = marcaVehiculo;
    }

    public String getPlacaVehiculo() {
        return placaVehiculo;
    }

    public void setPlacaVehiculo(String placaVehiculo) {
        this.placaVehiculo = placaVehiculo;
    }
}
