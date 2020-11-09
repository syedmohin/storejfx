package com.sunday.repository;

import com.sunday.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByUsername(String user);
    Optional<User> findByPassword(String password);
    boolean existsByUsername(String user);
}
