package com.example.demo.controllers;

import com.example.demo.models.Course;
import com.example.demo.models.Person;
import com.example.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;

@Controller
public class TeacherController
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


	@RequestMapping("/mycourses")
	public String myCourses(Principal principal, Model model)
	{
		//Person teacher = personRepo.findOne(principal.getName());
		//
			//last column shows detail, take attendance, view evals (for this course only), end class, register students (start class)
		//
		//something like-NOT RIGHT!!!!

		model.addAttribute("teachercourses", courseRepo.findAll());//needs to return only logged-in teacher's classes
		return "mycourses";
	}


	@GetMapping("/addstudent/{courseid}")
	public String registerStudent(@PathVariable("courseid")long courseId, Model model)
	{
		model.addAttribute("newstudent", new Person());
		//Course course = courseRepo.findOne(courseId);
		Course course=new Course();

		model.addAttribute("course", course);
		return "addstudenttocourse";
	}

	@PostMapping("/addstudent/{courseid}")
	public String addStudentToCourse(@PathVariable("courseid")long courseId, @ModelAttribute("newstudent")Person student)
	{
		System.out.println("CourseId: "+ courseId);
		System.out.println("Name: "+student.getNameFirst());
		//Course course = courseRepo.findOne(courseId);
		Course course=new Course();
		//add course to person

		//save person

		return "redirect:/addstudent/"+courseId;//PROBLEM:addstudent is not a route
	}

	@RequestMapping("/takeattendance/{courseid}")
	public String takeAttendance(@PathVariable("courseid") long courseId, Model model)
	{
		Course course = courseRepo.findOne(courseId);
		Collection<Person> students = course.getPersons();
		model.addAttribute("students", students);
		//Something more here
		return "takeattendance";
	}

	@RequestMapping("/viewattendance")
	public String viewAttendance()
	{
		return "viewattendance";
	}

	@RequestMapping("/endcourse/{courseid}")
	public String endClass()
	{
		System.out.println("Send email to admin");
		return "endcourse";
	}
}
