package com.burntoburn.easyshift.repository.user;

import com.burntoburn.easyshift.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByName(String username);

    boolean existsByEmail(String email);

    Optional<User> findByName(String username);

    Optional<User> findByEmail(String email);
}
