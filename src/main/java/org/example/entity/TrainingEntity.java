package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.Duration;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString
@Entity
public class TrainingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String trainingName;
    private LocalDate date;
    @NotNull
    @PositiveOrZero
    private Duration trainingDuration;

    @ManyToOne
    @JoinColumn(name = "trainee_id")
    @ToString.Exclude
    private TraineeEntity trainee;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    @ToString.Exclude
    private TrainerEntity trainer;

    @ManyToOne
    @JoinColumn(name = "training_type_id")
    @ToString.Exclude
    private TrainingTypeEntity trainingType;
}
