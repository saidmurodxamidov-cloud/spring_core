package org.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Setter
@Getter
@ToString
public class User {
    private Long userId;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private boolean isActive;

    public User(){}

    public User(Long userId,String firstName, String lastName, String userName, String password, boolean isActive) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.isActive = isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return isActive == user.isActive && Objects.equals(userId, user.userId) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(userName, user.userName) && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, firstName, lastName, userName, password, isActive);
    }
}
