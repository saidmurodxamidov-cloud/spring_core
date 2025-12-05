package org.example.repository;

import org.example.entity.TrainingTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD

import java.util.Optional;

=======
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
>>>>>>> origin/main
public interface TrainingTypeRepository extends JpaRepository<TrainingTypeEntity,Long> {
    Optional<TrainingTypeEntity> findByTrainingTypeName(String trainingTypeName);

    boolean existsByTrainingTypeName(String trainingTypeName);
}
