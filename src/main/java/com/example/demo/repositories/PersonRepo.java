package com.example.demo.repositories;

import com.example.demo.models.Person;
import org.springframework.data.repository.CrudRepository;

public interface PersonRepo extends CrudRepository<Person, Long> {
    Person findByUsername(String username);
    Person findByEmail(String email);
    Long countByEmail(String email);
    Long countByUsername(String username);
}
