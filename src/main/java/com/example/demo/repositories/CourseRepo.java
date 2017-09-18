package com.example.demo.repositories;

import com.example.demo.models.Course;
import com.example.demo.models.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

public interface CourseRepo extends CrudRepository<Course, Long> {
    Set<Course> findByPersonsIsAndDeletedIs(Person person, boolean value);

    Set<Course> findByDeletedIs(boolean value);

    long countByCourseRegistrationNumIs(long crn);
    Course findFirstByCourseRegistrationNumAndDateStartAndDeleted(long crn, Date date, boolean value);



}
