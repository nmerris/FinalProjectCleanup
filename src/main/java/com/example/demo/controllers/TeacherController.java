package com.example.demo.controllers;

import com.example.demo.AttendanceWrapper;
import com.example.demo.Utilities;
import com.example.demo.models.*;
import com.example.demo.repositories.*;
import com.example.demo.services.UserService;
import com.google.common.collect.Lists;
import it.ozimov.springboot.mail.model.Email;
import it.ozimov.springboot.mail.model.EmailAttachment;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmailAttachment;
import it.ozimov.springboot.mail.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.InternetAddress;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
	private UserService userService;
	@Autowired
	public EmailService emailService;

	// List of courses for a particular Teacher
	@RequestMapping("/mycoursesdetail")
	public String listTeacherCourses(Principal principal, Model model) {
		Person teacher = personRepo.findByUsername(principal.getName());
		model.addAttribute("teachercourse", teacher);
		model.addAttribute("courselist", courseRepo.findByPersonsIsAndDeletedIsOrderByCourseRegistrationNumAsc(teacher, false));
		return "teachercoursedetail";
	}


	// List of Students for a particular Course
	// path variable is the course id
	@GetMapping("/viewregisteredstudent/{id}")
	public String listRegisteredStud(@PathVariable("id") long courseId, Model model) {
		model.addAttribute("liststudent",
				personRepo.findByCoursesIsAndAuthoritiesIsOrderByNameLastAsc(courseRepo.findOne(courseId),
						authorityRepo.findByRole("STUDENT")));

		model.addAttribute("courseName", courseRepo.findOne(courseId).getName());
		model.addAttribute("courseCrn", courseRepo.findOne(courseId).getCourseRegistrationNum());
		return "listregisteredstudent";
	}


	// Display course evaluations for a single course for a single teacher, the teacher is logged in at this point
	@GetMapping("/dispevaluation/{id}")
	public String dipCourseEvaluation(@PathVariable("id") long courseId, Model model, Principal principal) {
		model.addAttribute("evaluations", evaluationRepo.findByPersonIsAndCourseIs(personRepo.findByUsername(principal.getName()),
                courseRepo.findOne(courseId)));
		model.addAttribute("teacherName", personRepo.findByUsername(principal.getName()).getFullName());

		// we are REUSING this view.. both admin and teachers use it, so navbar needs to be correct
		return "viewteacherevaluations";
	}


	// add a new OR existing student to course with id = id
	@GetMapping("/addstudent/{id}")
	public String registerStudent(@PathVariable("id") long courseId, Model model) {
		model.addAttribute("newstudent", new Person());
		Course course = courseRepo.findOne(courseId);
		model.addAttribute("course", course);
		return "addstudenttocourse";
	}


	// students do not have usernames or passwords, but they must enter first, last names, contact num, and email
	@PostMapping("/addstudent/{id}")
	public String addStudentToCourse(@PathVariable("id") long courseId,
									 @Valid @ModelAttribute("newstudent") Person student, BindingResult bindingResult,
									 @RequestParam(value = "registerNew", required = false) String registerNew, Model model) {

		Course course = courseRepo.findOne(courseId);
		model.addAttribute("course", course);
		model.addAttribute("existingStudent", new Person());

		if(bindingResult.hasErrors()) {
			return "addstudenttocourse";
		}

		// 'existing student' box was checked, so register them as a new student
		// TODO this needs to reject the submit more gracefully.. it should not force student to type everything in again
		if(registerNew != null) {
			Person p = userService.saveStudent(student);
			RegistrationTimestamp rt = new RegistrationTimestamp();
			rt.setCourse(course);
			rt.setPerson(p);
			rt.setTimestamp(new Date());
			registrationTimestampRepo.save(rt);
			course.addPerson(p);
			courseRepo.save(course);

			model.addAttribute("message", "Welcome to Montgomery College!  You have been registered for this course.  Make note of your new M-number.");
			model.addAttribute("student", p);
			model.addAttribute("course", course);

			return "addstudenttocourseconfirmation";
		}


		// if 'existing student' box was checked, but can't find a match, display a msg
		if(personRepo.countByNameFirstIsAndNameLastIsAndContactNumIsAndEmailIs(student.getNameFirst(),
				student.getNameLast(), student.getContactNum(), student.getEmail()) == 0) {

			model.addAttribute("message", "No existing student found that matches the info that was entered");
			model.addAttribute("invalidStudent", true);
			return "addstudenttocourseconfirmation";
		}


		// if 'existing student' was checked, and there was only one match, register them for this course if they're not already in it
		if(personRepo.countByNameFirstIsAndNameLastIsAndContactNumIsAndEmailIs(student.getNameFirst(),
				student.getNameLast(), student.getContactNum(), student.getEmail()) == 1) {

			Person p = personRepo.findFirstByNameFirstIsAndNameLastIsAndContactNumIsAndEmailIs(student.getNameFirst(),
					student.getNameLast(), student.getContactNum(), student.getEmail());
			model.addAttribute("message", "Welcome back!  You have been registered for this course.");
			model.addAttribute("student", p);
			model.addAttribute("course", course);

			if(p.getCourses().contains(course)) {
				// student is already registered for this course, no problem, just don't save anything to dbs
				// to the student, it will look like they successfully registered
				return "addstudenttocourseconfirmation";
			}

			// register student for this course, and create a timestamp
			RegistrationTimestamp rt = new RegistrationTimestamp();
			rt.setCourse(course);
			rt.setPerson(p);
			rt.setTimestamp(new Date());
			registrationTimestampRepo.save(rt);
			course.addPerson(p);
			courseRepo.save(course);

			return "addstudenttocourseconfirmation";
		}


		// data entered matches a record in the db AND the 'existing student' box was checked, so now need to show
		// a new page with a drop down of choices and Mnums for student to select.. themselves
		// at this point, the student must know their own M-number to register for this course
		// NOTE: it's fairly unlikely that this will ever happen, as the chances of two students having the exact
		// same first and last names, contact nums, and email addresses are slim.
		// TODO: rework database config to make email address unique, that would solve this ugly problem
		Set<Person> potentials = personRepo.findByNameFirstIsAndNameLastIsAndContactNumIsAndEmailIs(student.getNameFirst(),
				student.getNameLast(), student.getContactNum(), student.getEmail());

		model.addAttribute("potentials", potentials);
		model.addAttribute("course", course);
		return "addstudentmultiplechoices";

	}


	// this route fires only when there were multiple students that matched all the registration data when they were
	// signing up for a course.. ie there were > 1 existing students with same first and last names, emails, and contact nums
	// I feel like we really just should have made email unique!!! it would be much less hassle that way...
	@PostMapping("/addstudentmultiplechoices/{cid}")
	public String selectaStudentToRegister(@PathVariable("cid") long courseId,
										   @RequestParam("selectedStudentId") long selectedStudentId, Model model) {

		Person selectedStudent = personRepo.findOne(selectedStudentId);
		Course course = courseRepo.findOne(courseId);

		model.addAttribute("message", "Welcome back!  You have been registered for this course.");
		model.addAttribute("student", selectedStudent);
		model.addAttribute("course", course);
		
		if(selectedStudent.getCourses().contains(course)) {
			// student is already registered for this course, no problem, just don't save anything to dbs
			return "addstudenttocourseconfirmation";
		}

		// at this point: existing student has been selected from drop down of choices and they are not already registered for this course
		RegistrationTimestamp rt = new RegistrationTimestamp();
		rt.setCourse(course);
		rt.setPerson(selectedStudent);
		rt.setTimestamp(new Date());
		registrationTimestampRepo.save(rt);
		course.addPerson(selectedStudent);
		courseRepo.save(course);

		return "addstudenttocourseconfirmation";
	}


	// each time we create a brand new set of Attendance objects to send to the form
	// all Attendances default to Present, so teacher only has to change for late or absent
	// the ArrayList of Attendance objects are in a wrapper class, because I could not get the form to assign
	// values directly to the Attendance objects any other way!
	// values are not preserved, but duplicates are never created, not ideal, but good enough for now
	// TODO prepopulate the attendance radios with the previously recorded attendance data
	@GetMapping("/takeattendance/{courseid}")
	public String takeAttendance(@PathVariable("courseid") long courseId, Model model) {

		Course course = courseRepo.findOne(courseId);
		LinkedHashSet<Person> students = personRepo.findByCoursesIsAndAuthoritiesIsOrderByNameLastAsc(course, authorityRepo.findByRole("STUDENT"));
		int diffInDays = Utilities.getDiffInDays(course.getDateStart(), course.getDateEnd());
		Date startDate = course.getDateStart();
		List<Date> dates = new ArrayList<>();

		// build a list of dates from course start to course end
		for (int i = 0; i < diffInDays; i++) {
			dates.add(Utilities.addDays(startDate, i));
		}

		// need this to be able to process a list of objects in a single form
		AttendanceWrapper wrapper = new AttendanceWrapper();
		List<Attendance> attendanceArrayList = new ArrayList<>();

		// set up a new Attendance for each student
		for (Person student : students) {
            Attendance attendance = new Attendance();
            // set the person
            attendance.setPerson(student);
            // set the course
            attendance.setCourse(course);
            // pre check it to 'Present', this works because th:field automatically sets checked to whatever the radio input is being set to
            attendance.setAstatus("Present");
            // add it to the list
            attendanceArrayList.add(attendance);
		}

        // set the list in the wrapper
		wrapper.setAttendanceList(attendanceArrayList);

		model.addAttribute("attendanceWrapper", wrapper);
		model.addAttribute("courseName", course.getName());
		model.addAttribute("courseId", courseId);
		model.addAttribute("students", students);
		model.addAttribute("dates", dates);

		return "takeattendance";
	}


	// process the attendance data, the date was selected by the teacher from a drop down of all possible class days
	@PostMapping("/takeattendance/{courseid}")
	public String takeAttendancePost(
			@ModelAttribute("attendanceWrapper") AttendanceWrapper attWrapper,
			@RequestParam("selectedDate") Date selectedDate,
			@PathVariable("courseid") long courseId) {

		Course course = courseRepo.findOne(courseId);
		LinkedHashSet<Person> students = personRepo.findByCoursesIsAndAuthoritiesIsOrderByNameLastAsc(course, authorityRepo.findByRole("STUDENT"));

        // set the date on each Attendance that we just got back from the form
        for (Attendance att : attWrapper.getAttendanceList()) {
            att.setDate(selectedDate);
        }

		Set<Attendance> toDeleteList = new HashSet<>();
		for (Person student : students) {
            // there can only be one Attendance per student, course, and date
            // we delete all the previous records before saving a new set, so we don't get duplicates
            toDeleteList.addAll(attendanceRepo.findByPersonIsAndCourseIsAndDateIs(student, course, selectedDate));
		}
		// wipe out all the existing records for each student for each date for this course
		attendanceRepo.delete(toDeleteList);

		// courseId, personId are all preserved through the form, so just need to save it now, both join columns are set
        // and we set the date to the selected date above, so ready to save to repo
		attendanceRepo.save(attWrapper.getAttendanceList());

		return "redirect:/mycoursesdetail";
	}


	// shows a drop down list of admins, teacher selects one to send an attendance email to
	@GetMapping("/sendemail")
	public String sendEmailGet(@RequestParam("id") long courseId, Model model) {
		// first add a list of admins to the template
		model.addAttribute("adminList", personRepo.findByAuthoritiesIs(authorityRepo.findByRole("ADMIN")));

		// add the course to the model, so we can show the name on the page
		model.addAttribute("course", courseRepo.findOne(courseId));

		return "sendemail";
	}


	// process the email sending
	@PostMapping("/sendemail")
	public String sendEmailPost(@RequestParam("selectedAdminId") long adminId, @RequestParam("type") String emailType,
								@PathVariable("id") long courseId, Principal principal) {
		// get the logged in person
		Person teacher = personRepo.findByUsername(principal.getName());

		// get the selected admin
		Person admin = personRepo.findOne(adminId);

		// get the course
		Course course = courseRepo.findOne(courseId);

		// set the email type: true for attendance, false for evaluations
		boolean isAttendanceEmail = emailType.equals("att");

		sendEmailWithoutTemplate(course,
				teacher.getFullName(),	// teacher name
				course.getName(),		// course name
				admin.getEmail(),		// to email address
				admin.getFullName(),	// to email name
				isAttendanceEmail);	// email will be an attendance email

		return "redirect:/mycoursesdetail";
	}


	// sends an email, the content depends on isAttendanceEmail or not
	private void sendEmailWithoutTemplate(Course course, String teacherName, String courseName, String adminEmail,
										  String adminName, boolean isAttendanceEmail) {

		// we are only sending two types of emails from this app
		// create the appropriate Strings depending on the type of email being sent
		String subjectText = isAttendanceEmail ? String.format("Attendance for Course: %s - Teacher: %s", courseName, teacherName) :
												 String.format("Evaluations for Course: %s - Teacher: %s", courseName, teacherName);

		String bodyHeader = isAttendanceEmail ? "Student attendance details for all recorded dates attached as a CSV file" :
												"Teacher evaluations attached as a CSV file";

		// build an attachment depending on isAttendanceEmail
		EmailAttachment attachment;
		if(isAttendanceEmail) {
			attachment = buildAttendanceEmailCsvAttachment(course);
		}
		else {
			attachment = buildEvaluationEmailCsvAttachment(course);
		}

		final Email email;
		try {
			email = DefaultEmail.builder()
					// DOES NOT MATTER what you put in .from address.. it ignores it and uses what is in properties file
					// this may work depending on the email server config that is being used
					// the from NAME does get used though
					.from(new InternetAddress("anyone@anywhere.net", teacherName))
					.to(Lists.newArrayList(new InternetAddress(adminEmail, adminName)))
					.subject(subjectText)
					.body(bodyHeader)
					.attachment(attachment)
					.encoding("UTF-8").build();

			// conveniently, .send will put a nice INFO message in the console output when it sends
			emailService.send(email);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}


	// builds a course evaluation email attachment for one course for one teacher
	// in this app, there can only be one teacher per course, so the teacher is determined based on the course passed in
	private EmailAttachment buildEvaluationEmailCsvAttachment(Course course) {
		StringBuilder headers= new StringBuilder("Course Content Rating,Instruction Quality Rating,Training Experience Rating,Text Book Rating,Classroom Environment,Equipment Rating,What Did You Like,What didnt You Like,What Improvements,What Other Classes,How Did You Find Out\n");

		// get the teacher for the course in question
		Person teach = personRepo.findFirstByCoursesIsAndAuthoritiesIs(course, authorityRepo.findByRole("TEACHER"));

		// get all the evaluations for this teacher for this course
		Iterable<Evaluation> evaluations = evaluationRepo.findByPersonIsAndCourseIs(teach, course);

		for (Evaluation eval : evaluations) {
			String ccr=eval.getCourseContentRating();
			String iqr=eval.getInstructionQualityRating();
			String ter=eval.getTrainingExperienceRating();
			String tbr=eval.getTextBookRating();
			String cre=eval.getClassroomEnvironment();
			String er=eval.getEquipmentRating();
			String dy=eval.getWhatDidYouLike();
			String dny=eval.getWhatDidntYouLike();
			String wi=eval.getWhatImprovements();
			String woc=eval.getWhatOtherClasses();
			String hdf=eval.getHowDidYouFindOut();

			headers.append(",").append(ccr).append(",").append(iqr).append(",").append(ter)
				    .append(",").append(tbr).append(",").append(cre).append(",").append(er)
					.append(",").append(dy).append(",").append(dny).append(",").append(wi)
					.append(",").append(woc).append(",").append(hdf).append("\n");
		}

		return DefaultEmailAttachment.builder()
				.attachmentName("evaluations" + ".csv")
				.attachmentData(headers.toString().getBytes(Charset.forName("UTF-8")))
				.mediaType(MediaType.TEXT_PLAIN).build();

	}


	// builds an attendance email CSV attachment
	private EmailAttachment buildAttendanceEmailCsvAttachment(Course course) {
		StringBuilder headers = new StringBuilder("M-Number,Last Name,First Name,Date,Status\n");

		// get all the students in this course
		Iterable<Person> students = personRepo.findByCoursesIsAndAuthoritiesIsOrderByNameLastAsc(course,
				authorityRepo.findByRole("STUDENT"));

		// make the dates look nice
		DateFormat df = new SimpleDateFormat(("MMM dd, yyyy"));

		for (Person stud : students) {
			String nameLast = stud.getNameLast();
			String nameFirst = stud.getFullName();
			String mNUM = String.valueOf(stud.getmNumber());
			LinkedHashSet<Attendance> attendances = attendanceRepo.findByPersonIsAndCourseIsOrderByDateAsc(stud, course);

			// iterate through all attendances (in ascending order by date) for each student
			// append to the CSV String as we go
			for (Attendance att : attendances) {
				String date = df.format(att.getDate());
				String status = att.getAstatus();
				headers.append(mNUM).append(",").append(nameLast).append(",").append(nameFirst).append(",").append(date).append(",").append(status).append("\n");
			}
		}

		return DefaultEmailAttachment.builder()
				.attachmentName("attendance" + ".csv")
				.attachmentData(headers.toString().getBytes(Charset.forName("UTF-8")))
				.mediaType(MediaType.TEXT_PLAIN).build();

	}


}
