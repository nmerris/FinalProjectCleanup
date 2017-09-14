package com.example.demo.services;

import com.example.demo.models.Person;
import com.example.demo.repositories.AuthorityRepo;
import com.example.demo.repositories.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;

@Service
public class UserService {
    @Autowired
    PersonRepo personRepo;
    @Autowired
    AuthorityRepo authorityRepo;
    @Autowired
    public UserService(PersonRepo personRep){
        this.personRepo=personRep;
    }
    public Person findByEmail(String email){
        return personRepo.findByEmail(email);

    }
    public Long countByEmail(String email){
        return personRepo.countByEmail(email);

    }
    public Person findByUsername(String username){
        return personRepo.findByUsername(username);

    }
    public void saveAdmin(Person person){
        person.setAuthorities(Arrays.asList(authorityRepo.findByRole("ADMIN")));
        person.setEnabled(true);
        personRepo.save(person);
    }
    public void saveTeacher(Person  person){
        person.setAuthorities(Arrays.asList(authorityRepo.findByRole("TEACHER")));
        person.setEnabled(true);
        personRepo.save(person);
    }

}
