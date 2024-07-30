package com.slow3586.micromarket.userservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByLogin(String login);
}
