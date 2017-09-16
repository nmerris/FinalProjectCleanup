package com.example.demo.controllers;

import com.example.demo.models.Course;
import com.example.demo.models.Person;
import com.example.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class AdminController
{
	@Autowired
	AuthorityRepo authorityRepo;
	@Autowired
	PersonRepo personRepo;
	@Autowired
	CourseRepo courseRepo;
	@Autowired
	AttendanceRepo attendanceRepo;
	@Autowired
	RegistrationTimestampRepo registrationTimestampRepo;
	@Autowired
	EvaluationRepo evaluationRepo;


	@GetMapping("/addcourse")
	public String addCourse(Model model)
	{
		model.addAttribute("course", new Course());
		model.addAttribute("teachers", personRepo.findByAuthoritiesIs(authorityRepo.findByRole("TEACHER")));
		return "addcourse";
	}

	@PostMapping("/addcourse")
	public String submitCourse(@Valid @ModelAttribute("course") Course course, BindingResult result,
	                           Model model, @RequestParam(value = "selectedTeacher")long teacherId) {

		if(result.hasErrors()) {
			return "addcourse";
		}

		// find out what Person was just selected (by the admin) from the drop down list for this course
		// and set them as the teacher to this course, then save the course
		course.addPerson(personRepo.findOne(teacherId));
		courseRepo.save(course);
		model.addAttribute("teacher", personRepo.findOne(teacherId));
		System.out.println("teacher after add course:"+personRepo.findOne(teacherId));

		return "courseconfirm";
	}


	@GetMapping("/editcourse/{courseid}")
	public String editCourse(@PathVariable ("courseid") long id, Model model)
	{
		model.addAttribute("course", courseRepo.findOne(id));
		model.addAttribute("teachers", personRepo.findByAuthoritiesIs(authorityRepo.findByRole("TEACHER")));
		return "addcourse";
	}

	@RequestMapping("/deletecourse/{courseid}")
	public String deleteCourse(@PathVariable ("courseid") long id)
	{
//		Course course = courseRepo.findOne(id);
		courseRepo.delete(id);
		return "redirect:/allcourses";
	}

	@RequestMapping("/allcourses")
	public String allCourses(Model model)
	{
		model.addAttribute("allcourses", courseRepo.findAll());
		return "allcourses";
	}


}
