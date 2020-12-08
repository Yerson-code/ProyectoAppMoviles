package com.appmovil.myappuberclone.modelos;

public class Info {
    double km;
    double min;

    public Info() {
    }

    public Info(double km, double min) {
        this.km = km;
        this.min = min;
    }

    public double getKm() {
        return km;
    }

    public double getMin() {
        return min;
    }

    public void setKm(double km) {
        this.km = km;
    }

    public void setMin(double min) {
        this.min = min;
    }
}
