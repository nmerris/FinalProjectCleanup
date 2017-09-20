package com.example.demo.repositories;

import com.example.demo.models.Authority;
import com.example.demo.models.Course;
import com.example.demo.models.Person;
import org.springframework.data.repository.CrudRepository;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public interface PersonRepo extends CrudRepository<Person, Long> {
    Person findByUsername(String username);
    Person findByEmail(String email);
    Long countByEmail(String email);
    Long countByUsername(String username);

    Set<Person> findByAuthoritiesIs(Authority authority);

    Set<Person> findByCoursesIsAndUsernameIsOrderByNameLastAsc(Course course, String username);

    long countByMNumberIs(String mNumber);

    //Added by Yun on 09/15,find person according to course id
    Set<Person> findByCoursesIs (Course course);
    Set<Person> findByCoursesIsAndAuthoritiesIs(Course course, Authority authority);

    // get all the STUDENTS for a particular course
    LinkedHashSet<Person> findByCoursesIsAndAuthoritiesIsOrderByNameLastAsc(Course course, Authority authority);

    // get student by contact number
    Person findByContactNumIsAndAuthoritiesIs(String contactNum, Authority authority);

    // use this before saving a new TEACHER or ADMIN, to make sure the username is not already in use
    long countByUsernameIs(String username);

    // count the number of students registered in a given course
    long countByCoursesIsAndAuthoritiesIs(Course course, Authority authority);

    // find an existing person by names, contact num, and email
    long countByNameFirstIsAndNameLastIsAndContactNumIsAndEmailIs(String firstName, String lastName, String contactNum, String email);

    // call this if you are sure there is only one student that will be returned with given data
    Person findFirstByNameFirstIsAndNameLastIsAndContactNumIsAndEmailIs(String firstName, String lastName, String contactNum, String email);

    // call this if you think more than one student will have identical fn, ln, contact num, and email
    Set<Person> findByNameFirstIsAndNameLastIsAndContactNumIsAndEmailIs(String firstName, String lastName, String contactNum, String email);

    Person findByMNumberIs(String mnum);

}
