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

    // Acestea sunt metodele care lipseau:
    public Node getSource() {
        return source;
    }

    public Node getDestination() {
        return destination;
    }

    public double getSafetyWeight() {
        // Inovația ta: drumul e mai "lung" dacă zona e periculoasă [cite: 12, 31]
        return distance * destination.getSafetyScore();
    }
}