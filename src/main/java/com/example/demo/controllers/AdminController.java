package com.example.demo.controllers;

import com.example.demo.models.Course;
import com.example.demo.models.CourseInfoRequestLog;
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
	@GetMapping("/addduplicatecourse")
	public String addDuplicateCourse(Model model)
	{
		Course cour=new Course();
		cour.setCourseRegistrationNum(12345678);
		cour.setName("Java");
		model.addAttribute("courses", courseRepo.findAll());
		model.addAttribute("course",cour);
		model.addAttribute("teachers", personRepo.findByAuthoritiesIs(authorityRepo.findByRole("TEACHER")));
		return "addduplicatecourse";
	}

	@PostMapping("/addduplicatecourse")
	public String submitDuplicateCourse(@Valid @ModelAttribute("course")Course course,BindingResult bindingResult,@RequestParam(value = "selectCourse")long courseId, @RequestParam(value = "selectedTeacher")long teacherId,Model model) {
		if(bindingResult.hasErrors()){
			model.addAttribute("course",courseRepo.save(course));
			model.addAttribute("teacher", personRepo.findOne(teacherId));
			return"addduplicatecourse";
		}

		course.setName(courseRepo.findOne(courseId).getName());
		course.setCourseRegistrationNum(courseRepo.findOne(courseId).getCourseRegistrationNum());
		course.addPerson(personRepo.findOne(teacherId));
		model.addAttribute("course",courseRepo.save(course));
		model.addAttribute("teacher", personRepo.findOne(teacherId));

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


//	// this is where the admin can log information requests about a course
	@GetMapping("/loginforequest/{courseId}")
	public String logInfoRequestGet(@PathVariable("courseId") long courseId, Model model) {

		// note that CourseInfoRequestLog has info about person and course

//		model.addAttribute("course", courseRepo.findOne(courseId));
		CourseInfoRequestLog logObject = new CourseInfoRequestLog();
		logObject.setCourse(courseRepo.findOne(courseId));


		model.addAttribute("courseInfoLog", logObject);


		return "loginforequestform";
	}


	@PostMapping("/loginforequest")
	public String logInfoRequestPost(@Valid @ModelAttribute("courseInfoLog") CourseInfoRequestLog log,
									 BindingResult bindingResult,
//									 @RequestParam("enteredContactNum") String enteredContactNum,
//									 @RequestParam("enteredEmail") String enteredEmail,
									 Model model) {

		// validates email field (if anything entered), validates description for not empty
		if(bindingResult.hasErrors()) {
			return "loginforequestform";
		}

		// manually check if BOTH email and contact num were empty
		if(log.getContactNum() == 0 && log.getEmail().isEmpty()) {
			model.addAttribute("noEmailAndNoContactNum", true);
			return "loginforequestform";
		}


		if(log.getContactNum() > 0) {
			// something was entered in the contact num field, now check if it matches and existing student

		}

//		if(enteredContactNum.isEmpty() && enteredEmail.isEmpty())
//		if(enteredContactNum.isEmpty()) {
//			model.addAttribute("noContactNum", true);
//		}

		return "testeroo";
	}



}
