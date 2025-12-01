package org.example.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"userId"})
public class User {
    private Long userId;
    @NotNull(message = "name should be present")
    private String firstName;
    @NotNull(message = "Lastname should not be empty")
    private String lastName;
    @NotNull
    private String userName;
    private char[] password;
    private boolean isActive;
}
