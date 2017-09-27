package com.example.demo.repositories;

import com.example.demo.models.Attendance;
import com.example.demo.models.Course;
import com.example.demo.models.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

public interface AttendanceRepo extends CrudRepository<Attendance, Long> {

    Collection<Attendance> findByPersonIsAndCourseIsAndDateIs(Person p, Course c, Date d);

    // get all the attendances for a given student for a given course
    LinkedHashSet<Attendance> findByPersonIsAndCourseIsOrderByDateAsc(Person person, Course course);

}
