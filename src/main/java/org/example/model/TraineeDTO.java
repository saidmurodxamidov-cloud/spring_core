package org.example.model;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TraineeDTO extends UserDTO {
    @Past
    private LocalDate dateOfBirth;
    private String address;

    public TraineeDTO(Long userId, String firstName, String lastName, String userName, char[] password, boolean isActive,
                      LocalDate dateOfBirth, String address) {
        super(userId, firstName, lastName, userName, password, isActive);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }
}

