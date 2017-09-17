package com.example.demo.repositories;

import com.example.demo.models.Course;
import com.example.demo.models.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public interface CourseRepo extends CrudRepository<Course, Long> {
    Set<Course> findByPersons(Person persons);

    Set<Course> findByDeletedIs(boolean value);

}
