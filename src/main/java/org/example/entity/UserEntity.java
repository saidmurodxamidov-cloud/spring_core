package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "First name should be present")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotNull(message = "Last name should not be empty")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotNull
    @Column(name = "username", nullable = false, unique = true)
    private String userName;

    @Column(name = "password")
    private char[] password;

    @Column(name = "is_active")
    private boolean isActive;
}
