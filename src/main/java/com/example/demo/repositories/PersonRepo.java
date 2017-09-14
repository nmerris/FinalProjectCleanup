package com.example.demo.repositories;

import com.example.demo.models.Authority;
import com.example.demo.models.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface PersonRepo extends CrudRepository<Person, Long> {
    Person findByUsername(String username);
    Person findByEmail(String email);
    Long countByEmail(String email);
    Long countByUsername(String username);

    Set<Person> findByAuthoritiesIs(Authority authority);

}
