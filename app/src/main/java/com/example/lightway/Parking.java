package com.example.lightway;

public class Parking {

    private String type;
    private String id;
    private double[] coordinates;
    private String geometry_name;
    private String object_id;
    private String typ;
    private String antal_enheter;
    private String antal_platser;

    public Parking(String type, String id, double[] coordinates, String geometry_name, String object_id, String typ, String antal_enheter, String antal_platser) {
        this.type = type;
        this.id = id;
        this.coordinates = coordinates;
        this.geometry_name = geometry_name;
        this.object_id = object_id;
        this.typ = typ;
        this.antal_enheter = antal_enheter;
        this.antal_platser = antal_platser;
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

    public String getGeometry_name() {
        return geometry_name;
    }

    public String getObject_id() {
        return object_id;
    }

    public String getTyp() {
        return typ;
    }

    public String getAntal_enheter() {
        return antal_enheter;
    }

    public String getAntal_platser() {
        return antal_platser;
    }
}
