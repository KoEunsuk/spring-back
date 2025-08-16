package com.drive.backend.drive_api.repository;
import com.drive.backend.drive_api.entity.Operator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OperatorRepository extends JpaRepository<Operator, Integer> {
    Optional<Operator> findByOperatorCode(String operatorCode);
}