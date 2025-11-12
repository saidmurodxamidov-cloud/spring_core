package org.example.model;

import lombok.*;

import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"userId"})
public abstract class User {
    private Long userId;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private boolean isActive;
}
