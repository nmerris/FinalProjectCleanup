package com.example.demo.repositories;

import com.example.demo.models.Attendance;
import org.springframework.data.repository.CrudRepository;

public interface RegistrationTimestampRepo extends CrudRepository<Attendance, Long> {
}
