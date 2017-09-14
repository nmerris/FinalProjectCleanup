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

	//List of courses for a particular Teacher
	@RequestMapping("/mycoursesdetail/{id}")
	public String listTeacherCourses(@PathVariable("id") long id,Principal principal, Model model) {
		Person teacher = personRepo.findByUsername(principal.getName());
		model.addAttribute("teachercourse", teacher);
		model.addAttribute("courselist",courseRepo.findByPersons(teacher));
		return "teachercoursedetail";
	}

	//List of Students for a particular Course
	@RequestMapping("/viewregisteredstudent/{id}")
	public String listRegisteredStud(@PathVariable("id") long id, Model model) {
		model.addAttribute("liststudent", personRepo.findAll());
		// something to do here
		return "listregisteredstudent";
	}

	//List of Student attendance for a particular course
	@RequestMapping("/viewattendance/{id}")
	public String listStudAttendance(@PathVariable("id") long id, Model model) {
		model.addAttribute("listattendance", attendanceRepo.findAll());
		return "viewstudentattendance";
	}

	//Display course evealuation
	@RequestMapping("/dispevaluation/{id}")
	public String dipCourseEvaluation(@PathVariable("id") long id, Model model) {
		model.addAttribute("dispEval", evaluationRepo.findAll());
		return "dispevaluation";
	}
	//Send attendance for admin
	@RequestMapping("/viewattendance/{id}")
	public String sendAdmin(@PathVariable("id") long id, Model model) {
		model.addAttribute("listattendance", attendanceRepo.findAll());
		return "viewstudentattendance";
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
