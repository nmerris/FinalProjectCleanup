package com.example.demo.repositories;

import com.example.demo.models.Course;
import com.example.demo.models.Evaluation;
import com.example.demo.models.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.LinkedHashSet;
import java.util.Set;

public interface EvaluationRepo extends CrudRepository<Evaluation, Long> {

    // get all the evaluations for a teacher for a single course, this is used in TeacherController
    LinkedHashSet<Evaluation> findByPersonIsAndCourseIs(Person teacher, Course course);

    // it's ok if evaluation are pulled up for deleted courses.. in fact that would be desired, used in AdminController
    Set<Evaluation> findByCourseIs(Course course);

    // person here is NOT a student, it is the teacher with whom this Evaluation is associated with
    Set<Evaluation> findByPersonIs(Person person);

}
