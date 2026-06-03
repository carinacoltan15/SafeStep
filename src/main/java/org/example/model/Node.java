package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "nodes")
public class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 500)
    private String description;

    private double latitude;
    private double longitude;

    // Pentru logica de siguranta (ce cauta Edge.java)
    private double safetyScore;

    // Pentru logica de progres (ce cauta WebController.java)
    private boolean isVisited;

    // Constructor gol obligatoriu pentru JPA
    public Node() {}

    // Constructor cu parametri (util pentru teste in Main)
    public Node(String name, double latitude, double longitude, double safetyScore) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.safetyScore = safetyScore;
        this.isVisited = false;
    }

    // --- GETTERS SI SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    // Metoda ceruta de Edge.java
    public double getSafetyScore() {
        return safetyScore;
    }

    public void setSafetyScore(double safetyScore) {
        this.safetyScore = safetyScore;
    }

    // Metoda ceruta de WebController.java
    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }
}