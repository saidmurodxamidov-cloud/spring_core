package org.example.persistence.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"userId"})
@ToString
public class UserDTO {
    private Long userId;
    @NotNull(message = "name should be present")
    private String firstName;
    @NotNull(message = "Lastname should not be empty")
    private String lastName;
    @NotNull
    private String userName;
    @ToString.Exclude
    private char[] password;
    private boolean isActive;
}
