package com.example.demo.repositories;

import com.example.demo.models.Course;
import com.example.demo.models.Person;
import org.springframework.data.repository.CrudRepository;
import sun.awt.image.ImageWatched;

import java.util.*;

public interface CourseRepo extends CrudRepository<Course, Long> {
    Set<Course> findByPersonsIsAndDeletedIs(Person person, boolean value);

    Set<Course> findByDeletedIs(boolean value);
    Set<Course>findByPersons(Person person);

    long countByCourseRegistrationNumIs(String crn);

    Course findFirstByCourseRegistrationNumAndDateStartAndDeleted(String crn, Date date, boolean value);

    LinkedHashSet<Course> findAll();
    Course findFirstByCourseRegistrationNumIs(String crn);

    LinkedHashSet<Course> findByDeletedIsOrderByCourseRegistrationNumAsc(boolean value);

    LinkedHashSet<Course> findByPersonsIsAndDeletedIsOrderByCourseRegistrationNumAsc(Person person, boolean value);

}
