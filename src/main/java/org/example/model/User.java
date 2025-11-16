package org.example.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"userId"})
public abstract class User {
    private Long userId;
    @NotNull(message = "name should be present")
    private String firstName;
    @NotNull(message = "Lastname should not be empty")
    private String lastName;
    @NotNull
    private String userName;
    @Min(value = 8,message = "password should be at least 8 characters long")
    private String password;
    private boolean isActive;
}
