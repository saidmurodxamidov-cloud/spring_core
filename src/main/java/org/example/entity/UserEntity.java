package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"userId"})
@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotNull(message = "First name should be present")
    private String firstName;

    @NotNull(message = "Last name should not be empty")
    private String lastName;

    @NotNull
    @Column(unique = true)
    private String userName;

    private char[] password;

    private boolean isActive;
}
