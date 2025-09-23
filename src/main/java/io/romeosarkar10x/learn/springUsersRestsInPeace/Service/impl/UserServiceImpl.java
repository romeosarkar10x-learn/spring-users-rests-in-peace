package io.romeosarkar10x.learn.springUsersRestsInPeace.Service.impl;

import java.util.UUID;
import java.util.Optional;
import java.util.List;

import io.romeosarkar10x.learn.springUsersRestsInPeace.Model.User;
import io.romeosarkar10x.learn.springUsersRestsInPeace.Service.UserService;
import io.romeosarkar10x.learn.springUsersRestsInPeace.Repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        if(userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("An user with `username` \"" + user.getUsername()  + "\" already exists");
        }

        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("An user with `email` \"" + user.getEmail() + "\" already exists");
        }

        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getUsersByAgeBetween(int min, int max) {
        return userRepository.findByAgeBetween(min, max);
    }

    @Override
    public User updateUser(User user) {
        var originalUser = userRepository.findById(user.getId());

        if(originalUser.isEmpty()) {
            throw new RuntimeException("No user with `id` \"" + user.getId().toString() + "\" exists");
        }

        if(!originalUser.get().getEmail().equals(user.getEmail()) && userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("An user with `email` \"" + user.getEmail() + "\" already exists");
        }

        if(!originalUser.get().getUsername().equals(user.getUsername()) && userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("An user with `username` \"" + user.getUsername() + "\" already exists");
        }

        var updatedUser = user.clone();
        updatedUser.setCreatedAt(originalUser.get().getCreatedAt());

        return userRepository.save(updatedUser);
    }

    @Override
    public boolean deleteUser(UUID id) {
        var user = userRepository.findById(id);

        if(user.isEmpty()) {
            throw new RuntimeException("No user with `id` \"" + id + "\" exists");
        }

        userRepository.deleteById(id);
        return true;
    }

    @Override
    public long getCount() {
        return userRepository.count();
    }
}
