package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
@Table(name = "trainee")
public class TraineeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Past
    private LocalDate dateOfBirth;

    private String address;


    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToMany(mappedBy = "trainees")
    private Set<TrainerEntity> trainers = new HashSet<>();

    @OneToMany(mappedBy = "trainee")
    private Set<TrainingEntity> trainings = new HashSet<>();
}
