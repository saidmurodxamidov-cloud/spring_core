package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.converter.CharArrayToStringConverter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString
@Entity
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "First name should be present")
    private String firstName;

    @NotNull(message = "Last name should not be empty")
    private String lastName;

    @NotNull
    @Column(unique = true)
    private String userName;

    @ToString.Exclude
    private char[] password;

    private boolean isActive;
}
