package com.example.lightway;

public class Pump {

    private String type;
    private String id;
    private double[] coordinates;
    private String geometry_name;
    private String object_id;
    private String adress;
    private String ventiler;
    private String modell;
    private String status;


    public Pump (String type, String id, double[] coordinates, String geometry_name, String object_id, String adress, String ventiler, String modell, String status) {
        this.type = type;
        this.id = id;
        this.coordinates = coordinates;
        this.geometry_name = geometry_name;
        this.object_id = object_id;
        this.adress = adress;
        this.ventiler = ventiler;
        this.modell = modell;
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    @Override
    public String toString() {
        return ""+coordinates[0] + "," + coordinates[1];
    }

    public String getGeometry_name() {
        return geometry_name;
    }

    public String getObject_id() {
        return object_id;
    }

    public String getAdress() {
        return adress;
    }

    public String getVentiler() {
        return ventiler;
    }

    public String getModell() {
        return modell;
    }

    public String getStatus() {
        return status;
    }
}
