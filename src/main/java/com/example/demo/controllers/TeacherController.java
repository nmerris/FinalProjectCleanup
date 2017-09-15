package com.example.demo.controllers;

import com.example.demo.models.Attendance;
import com.example.demo.models.Course;
import com.example.demo.models.Person;
import com.example.demo.models.RegistrationTimestamp;
import com.example.demo.repositories.*;
import com.google.common.collect.Lists;
import it.ozimov.springboot.mail.model.Email;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import it.ozimov.springboot.mail.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Controller
public class TeacherController {
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
	public EmailService emailService;

	//List of courses for a particular Teacher
	@RequestMapping("/mycoursesdetail/{id}")
	public String listTeacherCourses(@PathVariable("id") long id, Principal principal, Model model) {
		Person teacher = personRepo.findByUsername(principal.getName());
		model.addAttribute("teachercourse", teacher);
		model.addAttribute("courselist", courseRepo.findByPersons(teacher));
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


	@RequestMapping("/addstudent/{id}")
	public String registerStudent(@PathVariable("id") long id, Model model) {
		model.addAttribute("newstudent", new Person());
		Course course = courseRepo.findOne(id);
		model.addAttribute("course", course);

		return "addstudenttocourse";
	}

	@PostMapping("/addstudent/{id}")
	public String addStudentToCourse(@PathVariable("id") long courseId, @ModelAttribute("newstudent") Person student) {

		RegistrationTimestamp timestamp = new RegistrationTimestamp();


		student.setEnabled(true);
		Person p = personRepo.save(student);
		Course course = courseRepo.findOne(courseId);

		timestamp.setCourse(course);
		timestamp.setPerson(p);
		timestamp.setTimestamp(new Date());
		registrationTimestampRepo.save(timestamp);

		course.addPerson(p);
		courseRepo.save(course);


		return "redirect:/addstudent/" + courseId;
	}


	@GetMapping("/takeattendance/{courseid}")
	public String takeAttendance(@PathVariable("courseid") long courseId, Model model) {
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
			System.out.println("Date parse error");
		}

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
		for (int i = 0; i < diffInDays; i++) {
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
	public String endClass() {
		System.out.println("Send email to admin");
		return "endcourse";
	}

//Email Sending to admin from the teacher

	public void sendEmailWithoutTemplate(String teacherName, String courseName,String eBody,String adminEmail,String adminName) {

		final Email email;
		try {
			email = DefaultEmail.builder()
					// DOES NOT MATTER what you put in .from address.. it ignores it and uses what is in properties file
					// this may work depending on the email server config that is being used
					// the from NAME does get used though
					.from(new InternetAddress("anyone@anywhere.net", teacherName))
					.to(Lists.newArrayList(
							new InternetAddress(adminEmail, adminName)))
					.subject("Attendance For"+ courseName)
					.body(eBody)
					.encoding("UTF-8").build();

			// conveniently, .send will put a nice INFO message in the console output when it sends
			emailService.send(email);

		} catch (UnsupportedEncodingException e) {
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!! caught an unsupported encoding exception");
			e.printStackTrace();
		}



	}
	@RequestMapping("/sendemail")
	public String sendEmail () {

		sendEmailWithoutTemplate("Fi","Java Boot Camp",
				"Hell Java Boot Camp","behabtuhiwot@gmail.com","Hiwi");

		return "redirect:/allcourses";
	}
}
