package org.example.model;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;


@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Trainee extends User{
    private LocalDate dateOfBirth;
    private String address;

    public Trainee(Long userId, String firstName, String lastName, String userName, String password, boolean isActive,
                   LocalDate dateOfBirth, String address) {
        super(userId, firstName, lastName, userName, password, isActive);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }
}

