package com.example.demo.services;

import com.example.demo.models.Authority;
import com.example.demo.repositories.AuthorityRepo;
import com.example.demo.repositories.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    
    @Autowired
    AuthorityRepo authorityRepo;
    
    @Override
    public void run(String... strings) throws Exception {
        
        // create three authorities and save them to the authority table
        // first make sure there are no authorities already in the table so as not to cause a unique constraint violation
        if( authorityRepo.count() == 0) {
            Authority adminAuthority = new Authority();
            adminAuthority.setRole("ADMIN");
            authorityRepo.save(adminAuthority);

            Authority teacherAuthority = new Authority();
            teacherAuthority.setRole("TEACHER");
            authorityRepo.save(teacherAuthority);

            Authority studentAuthority = new Authority();
            studentAuthority.setRole("STUDENT");
            authorityRepo.save(studentAuthority);
        }

        // could also seed database with other data here
    }
}
