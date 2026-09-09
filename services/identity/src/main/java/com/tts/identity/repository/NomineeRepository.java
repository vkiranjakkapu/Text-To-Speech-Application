package com.tts.identity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tts.identity.entities.Nominee;
import com.tts.identity.entities.User;

public interface NomineeRepository extends JpaRepository<Nominee, Long> {

    List<Nominee> findAllByCustomer(User customer);
    
}
