package org.example.repository;

import org.example.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
=======
import org.springframework.stereotype.Repository;

import java.util.Optional;
>>>>>>> origin/main

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {

    Optional<UserEntity> findByUserName(String username);

    boolean existsByUserName(String username);

<<<<<<< HEAD
    @Query("SELECT u.userName FROM UserEntity u")
    Set<String> findAllUserNames();

=======
>>>>>>> origin/main
}
