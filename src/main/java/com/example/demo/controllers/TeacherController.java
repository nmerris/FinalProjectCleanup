package com.example.demo.controllers;

import com.example.demo.models.Attendance;
import com.example.demo.models.Course;
import com.example.demo.models.Person;
import com.example.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

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
//	@RequestMapping("/viewattendance/{id}")
//	public String sendAdmin(@PathVariable("id") long id, Model model) {
//		model.addAttribute("listattendance", attendanceRepo.findAll());
//		return "viewstudentattendance";
//	}


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
		Course course = courseRepo.findOne(courseId);
		student.addCourse(course);
		personRepo.save(student);


		return "redirect:/addstudent/"+courseId;//PROBLEM:addstudent is not a route
	}


	@GetMapping("/takeattendance/{courseid}")
	public String takeAttendance(@PathVariable("courseid") long courseId, Model model)
	{
		Course course = courseRepo.findOne(courseId);
		Collection<Person> students = course.getPersons();

		// convert students Collection to ArrayList
		ArrayList<Person> studsArray = new ArrayList<>(students);

		Person someStudent = studsArray.get(0);
		System.out.println("===================== number of students in studsArray: " + studsArray.size());
		System.out.println("=================== fist name of student picked out for testing (index 0): " + someStudent.getNameFirst());

		// create a test start and end date
		Date date1 = new Date();
		Date date2 = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
		int diffInDays;

//		System.out.print("Enter first date (MM/DD/YY): ");
		try {
			date1 = dateFormat.parse("01/01/2000");
		} catch (ParseException e) {
			System.out.println("Date parse error");		}

//		System.out.print("Enter second date (MM/DD/YY): ");
		try {
			date2 = dateFormat.parse("01/10/2000");
		} catch (ParseException e) {
			System.out.println("Date parse error");
		}

		// * 1000 to convert to seconds
		// * 60 to convert to minutes
		// * 60 to convert to hours
		// * 24 to convert to days
		// absolute value in case user entered later date first
		diffInDays = (int) (Math.abs((date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24)));

		System.out.printf("======================= Difference: %d day(s)", diffInDays);


		// create an empty list of attendance
		ArrayList<Attendance> attendanceArrayList = new ArrayList<>();
		// now create diffInDays Attendance objects to send to view
		for(int i = 0; i <  diffInDays; i++) {
			Attendance attendance = new Attendance();
			// set the person
			attendance.setPerson(someStudent);
			// set the course
			attendance.setCourse(course);
			// add it to the list
			attendanceArrayList.add(attendance);
		}

		System.out.println("========================== attendanceArrayList.size: " + attendanceArrayList.size());


		model.addAttribute("attendanceArrayList", attendanceArrayList);
		model.addAttribute("studentName", someStudent.getNameFirst() + ' ' + someStudent.getNameLast());
		model.addAttribute("courseName", course.getName());


		return "takeattendance";
	}


	@PostMapping("/takeattendance/{courseid}")
	public String takeAttendancePost(@PathVariable("courseid") long courseId, Model model) {



		return "redirect:/mycoursesdetail/" + courseId;
	}


	@RequestMapping("/endcourse/{courseid}")
	public String endClass()
	{
		System.out.println("Send email to admin");
		return "endcourse";
	}
}
