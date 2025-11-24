package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class TraineeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Past
    private LocalDate dateOfBirth;

    private String address;


    @OneToOne
    @JoinColumn(name = "id")
    private UserEntity user;
}
