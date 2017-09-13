package com.example.demo.repositories;

import com.example.demo.models.Evaluation;
import org.springframework.data.repository.CrudRepository;

public interface EvaluationRepo extends CrudRepository<Evaluation, Long> {
}
