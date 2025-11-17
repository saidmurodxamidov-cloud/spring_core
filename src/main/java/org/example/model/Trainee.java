package org.example.model;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Trainee extends User{
    @Past
    private LocalDate dateOfBirth;
    private String address;

    public Trainee(Long userId, String firstName, String lastName, String userName, char[] password, boolean isActive,
                   LocalDate dateOfBirth, String address) {
        super(userId, firstName, lastName, userName, password, isActive);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }
}

