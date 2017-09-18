package com.example.demo.repositories;

import com.example.demo.models.Course;
import com.example.demo.models.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public interface CourseRepo extends CrudRepository<Course, Long> {
    Set<Course> findByPersonsIsAndDeletedIs(Person person, boolean value);

    Set<Course> findByDeletedIs(boolean value);

    long countByCourseRegistrationNumIs(long crn);
//    Set<Long> findByCourseRegistrationNumAndDeletedIs(long crn,boolean b);

}
