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
import java.util.Set;

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
	@Autowired
	CourseInfoRequestLogRepo courseInfoRequestLogRepo;


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
		course.setDeleted(false);
		courseRepo.save(course);
		model.addAttribute("teacher", personRepo.findOne(teacherId));
		System.out.println("teacher after add course:"+personRepo.findOne(teacherId));

		return "courseconfirm";
	}
	@GetMapping("/addduplicatecourse")
	public String addDuplicateCourse(Model model)
	{
		Course cour=new Course();
		// need dummy data for CRN and name or validation is problematic in post route, both these are set again in post route
		// so it doesn't matter what you set them to here
		cour.setCourseRegistrationNum(12345678);
		cour.setName("fakeName");

//		Set<Course> courseSet = courseRepo.findByDeletedIs(0);
//		System.out.println("============================================================ courseSet.size: " + courseSet.size());

		model.addAttribute("courses", courseRepo.findByDeletedIs(false));
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
		course.setDeleted(false);
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
		// not tested yet!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		Course course = courseRepo.findOne(id);
		course.setDeleted(true);
		courseRepo.save(course);

		return "redirect:/allcourses";
	}

	@RequestMapping("/allcourses")
	public String allCourses(Model model)
	{
		model.addAttribute("allcourses", courseRepo.findByDeletedIs(false));
		return "allcourses";
	}


	// view all the students, admin can click on one to see the courses that particular student is registered in
	@GetMapping("/allstudents")
	public String allStudents(Model model) {

		// TODO implement this


		return "allstudents";
	}


	// view all the teachers, admin can click on one to see that teachers evaluations
	@GetMapping("/allteachers")
	public String allTeachers(Model model) {

		// TODO implement this


		return "allteachers";
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


	// note: the course id is preserved through the form, so does not need to be set again here
	@PostMapping("/loginforequest")
	public String logInfoRequestPost(@Valid @ModelAttribute("courseInfoLog") CourseInfoRequestLog log,
									 BindingResult bindingResult, Model model) {

		// validates email field (if anything entered), validates description for not empty
		if(bindingResult.hasErrors()) {
			return "loginforequestform";
		}

		// manually check if BOTH email and contact num were empty
		if(log.getContactNum().isEmpty() && log.getEmail().isEmpty()) {
			model.addAttribute("noEmailAndNoContactNum", true);
			return "loginforequestform";
		}

		// TODO: waiting to here from Fi about uniqueness of contact number if it's a student, this is not quite done yet
		// check to see if the contact num just entered matches any student in the db
		Person matchedStudent = personRepo.findByContactNumIsAndAuthoritiesIs(log.getContactNum(), authorityRepo.findByRole("STUDENT"));
		if(matchedStudent != null) {
			// found at least one match
			String s = "Found this student with contact number " + log.getContactNum() + ": " + matchedStudent.getFullName() + " - " + matchedStudent.getmNumber();
			model.addAttribute("message", s);
			log.setPerson(matchedStudent);
			courseInfoRequestLogRepo.save(log);
		}
		else {
			model.addAttribute("message", "There are no current students with that contact number.  The info request has been saved");
			courseInfoRequestLogRepo.save(log);
		}

		return "loginforequestconfirmation";
	}



}
