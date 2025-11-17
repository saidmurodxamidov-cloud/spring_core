package org.example.model;

import lombok.*;


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
    private char[] password;
    private boolean isActive;
}
