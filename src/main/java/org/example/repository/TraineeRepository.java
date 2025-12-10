package org.example.repository;

import org.example.entity.TraineeEntity;
import org.example.entity.TrainerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<TraineeEntity,Long> {
    Optional<TraineeEntity> findByUserUserName(String username);

    void deleteByUserUserName(String username);

    List<TraineeEntity> findByUserIsActive(boolean isActive);

}
