package com.example.demo.controllers;

import com.example.demo.AttendanceWrapper;
import com.example.demo.Utilities;
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
import org.springframework.web.servlet.ModelAndView;

import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
	@RequestMapping("/mycoursesdetail")
	public String listTeacherCourses(Principal principal, Model model) {
		Person teacher = personRepo.findByUsername(principal.getName());
		model.addAttribute("teachercourse", teacher);
		model.addAttribute("courselist", courseRepo.findByPersons(teacher));
		return "teachercoursedetail";
	}

	//List of Students for a particular Course
	// path variable is the course id
	@RequestMapping("/viewregisteredstudent/{id}")
	public String listRegisteredStud(@PathVariable("id") long id, Model model, Principal principal) {
		model.addAttribute("liststudent",
				personRepo.findByCoursesIsAndUsernameIsOrderByNameLastAsc(courseRepo.findOne(id),
						personRepo.findByUsername(principal.getName()).getUsername()));

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



	// UNDER CONSTRUCTION BUT IS SAVING A LIST OF ATTENDANCE OBJECTS TO DB!
	// NOTE: for now, we are taking attendance for one student at a time, not ideal, but works..
	// will update to do all students in course on one page if there is enough time
	@GetMapping("/takeattendance/{courseid}")
	public String takeAttendance(@PathVariable("courseid") long courseId,
								 @RequestParam("studentid") long studentid, Model model) {

		// get the course we are taking attendance for
		Course course = courseRepo.findOne(courseId);
		// get the student we are taking attendance for
		Person student = personRepo.findOne(studentid);
		System.out.println("=================== fist name of student who we are taking attendance for (person.getNameFirst): " + student.getNameFirst());
		System.out.println("=================== id of student who we are taking attendance for (person.getId): " + student.getId());

		// get the difference in days between course start and end dates
		int diffInDays = Utilities.getDiffInDays(course.getDateStart(), course.getDateEnd());
		System.out.printf("======================= Difference between course start and end dates: %d day(s)", diffInDays);

		// need this to be able to process a list of objects in a single form
		AttendanceWrapper wrapper = new AttendanceWrapper();
		// get the course start date
		Date startDate = course.getDateStart();
		// create an empty list of attendance
		List<Attendance> attendanceArrayList = new ArrayList<>();

		// now create diffInDays number of Attendance objects to send to view
		for (int i = 0; i < diffInDays; i++) {
			Attendance attendance = new Attendance();
			// set the person
			attendance.setPerson(student);
			// set the course
			attendance.setCourse(course);
			// set the date, increment by one day for each new Attendance object
			attendance.setDate(Utilities.addDays(startDate, i));
			// add it to the list
			attendanceArrayList.add(attendance);

		}
		wrapper.setAttendanceList(attendanceArrayList);

		model.addAttribute("attendanceWrapper", wrapper);
		model.addAttribute("studentName", student.getNameFirst() + ' ' + student.getNameLast());
		model.addAttribute("courseName", course.getName());
		model.addAttribute("courseId", courseId);

		return "takeattendance";
	}


	@PostMapping("/takeattendance/{courseid}")
	public String takeAttendancePost(
			@ModelAttribute("attendanceWrapper") AttendanceWrapper attWrapper,
			@PathVariable("courseid") long courseId, Model model) {

		System.out.println("================================================ in /takeattendance POST, incoming courseId: " + courseId);
		System.out.println("=================== attWrapper.getStringList.size: " + attWrapper.getAttendanceList().size());
//		for (Attendance att : attWrapper.getAttendanceList()) {
//			System.out.println("========= attWrapper.getAttendanceList element: " + att.getDate() + "..... " + att.getAstatus());
//		}

		// courseId, personId, and date are all preserved through the form, so just need to save it now, both join columns are set
		attendanceRepo.save(attWrapper.getAttendanceList());

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
