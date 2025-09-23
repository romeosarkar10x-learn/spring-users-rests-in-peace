package io.romeosarkar10x.learn.springUsersRestsInPeace.Repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import io.romeosarkar10x.learn.springUsersRestsInPeace.Model.User;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends CassandraRepository<User, UUID> {

    @Query("SELECT * FROM users WHERE username = ?0 ALLOW FILTERING")
    public Optional<User> findByUsername(String username);

    @Query("SELECT * FROM users WHERE email = ?0 ALLOW FILTERING")
    public Optional<User> findByEmail(String email);

    @Query("SELECT * FROM users WHERE age >= ?0 AND age <= ?1 ALLOW FILTERING")
    public List<User> findByAgeBetween(int min, int max);
}