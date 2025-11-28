package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "training_type")
@ToString
public class TrainingTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String trainingTypeName;

    @ManyToMany(mappedBy = "specializations")
    @ToString.Exclude
    private Set<TrainerEntity> trainerEntities = new HashSet<>();

    @OneToMany(mappedBy = "trainingType")
    private Set<TrainingEntity> trainings = new HashSet<>();
}
