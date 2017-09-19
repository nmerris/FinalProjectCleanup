package com.example.demo.services;

import com.example.demo.models.Person;
import com.example.demo.repositories.AuthorityRepo;
import com.example.demo.repositories.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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
        setPersonMnumber(person);
        personRepo.save(person);
    }

    public void saveTeacher(Person  person){
        person.setAuthorities(Arrays.asList(authorityRepo.findByRole("TEACHER")));
        person.setEnabled(true);
        setPersonMnumber(person);
        personRepo.save(person);
    }

    public Person saveStudent(Person  person){
        person.setAuthorities(Arrays.asList(authorityRepo.findByRole("STUDENT")));
        person.setEnabled(true);
        setPersonMnumber(person);
        return personRepo.save(person);
    }

    // assigns a unique random number to Person p
    private void setPersonMnumber(Person p) {
        // check to make sure the randomly generated mnumber is not already in the db
        // there is a 1 in 10 million chance of this happening
        boolean generateAnotherRandomNum = true;
        while(generateAnotherRandomNum == true) {
            int randomNum = ThreadLocalRandom.current().nextInt(1000000, 9999999 + 1);
            String mNum = "M" + Integer.valueOf(randomNum);

            if(personRepo.countByMNumberIs(mNum) > 0) {
                // the randomly generated number alreayd exists!
                generateAnotherRandomNum = true;
            }
            else {
                generateAnotherRandomNum = false;
            }

            p.setmNumber(mNum);
        }
    }

}
