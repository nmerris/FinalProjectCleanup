package com.example.demo.repositories;

import com.example.demo.models.Authority;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Set;

public interface AuthorityRepo extends CrudRepository<Authority, Long> {
    Authority findByRole(String role);

    ArrayList<Authority>findByRoleIsOrRoleIsOrderByRoleDesc(String role1, String role2);

}
