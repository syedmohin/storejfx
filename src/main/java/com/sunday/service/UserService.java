package com.sunday.service;

import com.sunday.model.User;
import com.sunday.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public boolean checkUserAndPassword(String userName, String password) {
        var user = userRepository.findByUsername(userName);
        AtomicBoolean login = new AtomicBoolean(false);
        user.ifPresentOrElse(user1 -> {
            var userPassword = userRepository.findByPassword(password);
            if (userPassword.isPresent()) {
                login.set(true);
            }
        }, () -> login.set(false));
        return login.get();
    }
    public void deleteAll(){
        userRepository.deleteAll();
    }

    public User insertUser(String user, String pass) {
        var u = new User();
        u.setUsername(user);
        u.setPassword(pass);
        return userRepository.save(u);
    }

    public List<User> getAllUserData() {
        var itre = userRepository.findAll();
        List<User> users = new ArrayList<>();
        itre.forEach(users::add);
        return users;
    }

    public User getUser(String username) {
        try {
            var userOp = userRepository.findByUsername(username);
            return userOp.orElseThrow(() -> new IllegalAccessException("User Not Found"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteUser(String username) {
        if (userRepository.existsByUsername(username)) {
            userRepository.delete(getUser(username));
            return true;
        } else {
            return false;
        }
    }
}
