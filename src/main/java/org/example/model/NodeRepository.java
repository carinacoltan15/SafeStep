package org.example.model;

import org.springframework.data.jpa.repository.JpaRepository;

// Schimbă al doilea parametru din String în Long
public interface NodeRepository extends JpaRepository<Node, Long> {
}