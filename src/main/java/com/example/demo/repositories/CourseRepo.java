package com.example.demo.repositories;

import com.example.demo.models.Course;
import org.springframework.data.repository.CrudRepository;

public interface CourseRepo extends CrudRepository<Course, Long> {
}
