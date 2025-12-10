package org.example.service;

public interface UserService {

    boolean passwordMatches(String username, String password);

    boolean changePassword(String username, String oldPassword, String newPassword);

}
