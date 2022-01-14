package com.example.kinwaeassessment.repository;

import com.example.kinwaeassessment.model.AppUser;
import com.example.kinwaeassessment.model.Login;
import lombok.extern.java.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginRepo extends JpaRepository<Login, Long> {
    Optional<Login> findByUsername(String username);
    Optional<Login> findByUsernameAndIpAddress(String user, String ip);
}
