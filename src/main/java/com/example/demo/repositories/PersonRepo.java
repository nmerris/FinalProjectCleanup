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

    long countByAuthoritiesIs(Authority authority);

    long countByMNumberIs(String mNumber);

    Set<Person> findByCoursesIsAndAuthoritiesIs(Course course, Authority authority);

    // get all the STUDENTS for a particular course
    LinkedHashSet<Person> findByCoursesIsAndAuthoritiesIsOrderByNameLastAsc(Course course, Authority authority);

    // count the number of students registered in a given course
    long countByCoursesIsAndAuthoritiesIs(Course course, Authority authority);

    // find an existing person by names, contact num, and email
    long countByNameFirstIsAndNameLastIsAndContactNumIsAndEmailIs(String firstName, String lastName, String contactNum, String email);

    // this should only ever return one person
    Person findFirstByNameFirstIsAndNameLastIsAndContactNumIsAndEmailIs(String firstName, String lastName, String contactNum, String email);

    Person findByMNumberIs(String mnum);

    // call this if you think more than one student will have identical fn, ln, contact num, and email
    Set<Person> findByNameFirstIsAndNameLastIsAndContactNumIsAndEmailIs(String firstName, String lastName, String contactNum, String email);

    LinkedHashSet<Person> findByAuthoritiesIsOrderByMNumberAsc(Authority authority);

    // find the single teacher for a given course, pass in TEACHER as the authority
    Person findFirstByCoursesIsAndAuthoritiesIs(Course course, Authority authority);

}
