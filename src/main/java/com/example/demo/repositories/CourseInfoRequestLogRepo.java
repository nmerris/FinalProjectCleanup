package com.example.demo.repositories;

import com.example.demo.models.Course;
import com.example.demo.models.CourseInfoRequestLog;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface CourseInfoRequestLogRepo extends CrudRepository<CourseInfoRequestLog, Long> {
    Set<CourseInfoRequestLog> findByCourseIs(Course course);

}
