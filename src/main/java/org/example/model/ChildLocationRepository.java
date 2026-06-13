package org.example.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildLocationRepository extends JpaRepository<ChildLocation, Long> {

    ChildLocation findFirstByNameOrderByTimestampDesc(String name);
}