package com.example.demo.repositories;

import com.example.demo.models.Evaluation;
import com.example.demo.models.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.LinkedHashSet;

public interface EvaluationRepo extends CrudRepository<Evaluation, Long> {

    // get all the evaluations for a teacher
    LinkedHashSet<Evaluation> findByPersonIsOrderByCourseAsc(Person person);


}
