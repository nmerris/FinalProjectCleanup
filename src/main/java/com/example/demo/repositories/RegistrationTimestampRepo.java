package com.example.demo.repositories;

import com.example.demo.models.RegistrationTimestamp;
import org.springframework.data.repository.CrudRepository;

public interface RegistrationTimestampRepo extends CrudRepository<RegistrationTimestamp, Long> {
}
