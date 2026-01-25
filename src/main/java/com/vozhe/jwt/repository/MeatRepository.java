package com.vozhe.jwt.repository;

import com.vozhe.jwt.models.Meat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MeatRepository extends JpaRepository<Meat, Long> {

    @Query("SELECT DISTINCT m FROM Meat m LEFT JOIN FETCH m.outputCuts")
    List<Meat> findAllWithOutputCuts();

    Optional<Meat> findByName(String name);
}
