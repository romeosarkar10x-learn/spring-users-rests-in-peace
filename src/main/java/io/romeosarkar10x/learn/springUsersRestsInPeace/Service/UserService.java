package io.romeosarkar10x.learn.springUsersRestsInPeace.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import io.romeosarkar10x.learn.springUsersRestsInPeace.Model.User;

public interface UserService {
    public User createUser(User user);

    public Optional<User> getUserById(UUID id);
    public Optional<User> getUserByEmail(String email);
    public Optional<User> getUserByUsername(String username);

    public List<User> getAllUsers();
    public List<User> getUsersByAgeBetween(int min, int max);

    public User updateUser(User user);
    public boolean deleteUser(UUID id);

    public long getCount();
}
