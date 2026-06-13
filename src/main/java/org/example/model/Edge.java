package org.example.model;

public class Edge {
    private Node source;
    private Node destination;
    private double distance;

    public Edge(Node source, Node destination, double distance) {
        this.source = source;
        this.destination = destination;
        this.distance = distance;
    }


    public Node getSource() {
        return source;
    }

    public Node getDestination() {
        return destination;
    }

    public double getSafetyWeight() {

        return distance * destination.getSafetyScore();
    }
}