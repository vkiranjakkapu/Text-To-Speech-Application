package com.tts.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tts.identity.entities.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
    
}
