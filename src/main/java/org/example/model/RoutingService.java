package org.example.model;

import java.util.*;

public class RoutingService {

    public List<Node> findSafeRoute(Node start, Node end, List<Edge> allEdges) {
        Map<Node, Double> distances = new HashMap<>();
        Map<Node, Node> previousNodes = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(n -> distances.getOrDefault(n, Double.MAX_VALUE)));

        distances.put(start, 0.0);
        queue.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (current.equals(end)) break;

            for (Edge edge : allEdges) {
                if (edge.getSource().equals(current)) {
                    Node neighbor = edge.getDestination();
                    //  Calculăm ponderea folosind scorul de siguranță
                    double newDist = distances.get(current) + edge.getSafetyWeight();

                    if (newDist < distances.getOrDefault(neighbor, Double.MAX_VALUE)) {
                        distances.put(neighbor, newDist);
                        previousNodes.put(neighbor, current);
                        queue.add(neighbor);
                    }
                }
            }
        }

        return reconstructPath(previousNodes, end);
    }

    private List<Node> reconstructPath(Map<Node, Node> previousNodes, Node end) {
        List<Node> path = new ArrayList<>();
        for (Node at = end; at != null; at = previousNodes.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }
}