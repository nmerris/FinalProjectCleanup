package com.example.demo.repositories;

import com.example.demo.models.Authority;
import org.springframework.data.repository.CrudRepository;

public interface AuthorityRepo extends CrudRepository<Authority, Long> {
}
