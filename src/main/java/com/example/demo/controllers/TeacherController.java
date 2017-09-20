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
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.internet.InternetAddress;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.text.ParseException;
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

	//List of courses for a particular Teacher
	@RequestMapping("/mycoursesdetail")
	public String listTeacherCourses(Principal principal, Model model) {
		Person teacher = personRepo.findByUsername(principal.getName());
		model.addAttribute("teachercourse", teacher);
		model.addAttribute("courselist", courseRepo.findByPersonsIsAndDeletedIs(teacher, false));
		return "teachercoursedetail";
	}


	//List of Students for a particular Course
	// path variable is the course id
	@RequestMapping("/viewregisteredstudent/{id}")
	public String listRegisteredStud(@PathVariable("id") long id, Model model, Principal principal) {
		model.addAttribute("liststudent",
				personRepo.findByCoursesIsAndAuthoritiesIsOrderByNameLastAsc(courseRepo.findOne(id),
						authorityRepo.findByRole("STUDENT")));

		model.addAttribute("courseId", id);
		return "listregisteredstudent";
	}


	//List of Student attendance for a particular course
	// TODO I DO NOT THINK THIS ROUTE EVEN NEEDS TO EXIST!!!
	// requirements state that teacher should be able to EMAIL attendance details to the admin
	// there is actually nothing in requirements that state that a teacher or admin should be able to
	// VIEW the attendance in any way, other than via email
//	@RequestMapping("/viewattendance/{id}")
//	public String listStudAttendance(@PathVariable("id") long courseId, Model model, Principal principal) {
//		model.addAttribute("listattendance", attendanceRepo.findByPersonIsAndCourseIsOrderByDateAsc(personRepo.
//				findByUsername(principal.getName()), courseRepo.findOne(courseId)));
//
//		return "viewstudentattendance";
//	}


	//Display course evealuations for a single course for a single teacher, the teacher is logged in at this point
	// TODO WAITING FOR JESSE, template is ready to go, and this route should be done
	@GetMapping("/dispevaluation/{id}")
	public String dipCourseEvaluation(@PathVariable("id") long courseId, Model model, Principal principal) {
		model.addAttribute("evaluations", evaluationRepo.findByPersonIsAndCourseIs(personRepo.findByUsername(principal.getName()),
                courseRepo.findOne(courseId)));
		model.addAttribute("teacherName", personRepo.findByUsername(principal.getName()).getFullName());

		// we are REUSING this view.. both admin and teachers use it, so navbar needs to be correct
		return "viewteacherevaluations";
	}


	// TODO needs work: same student should be able to register for multiple courses using the same Mnumber
    // right now we just create a new student every time... this is wrong, needs to be fixed
    // actually it might be easier to just have students register the same way teachers and admins do...
    // then when they are signing up for a course, they would just put in the Mnumber... this would make more sense
	@GetMapping("/addstudent/{id}")
	public String registerStudent(@PathVariable("id") long id, Model model) {
		model.addAttribute("newstudent", new Person());
//		model.addAttribute("existingStudent", new Person());
		Course course = courseRepo.findOne(id);
		model.addAttribute("course", course);
		return "addstudenttocourse";
	}


	// students do not have usernames or passwords, but they must enter first, last names
	// and contact num, email
	@PostMapping("/addstudent/{id}")
	public String addStudentToCourse(@PathVariable("id") long courseId,
									 @Valid @ModelAttribute("newstudent") Person student, BindingResult bindingResult,
									 @RequestParam(value = "registerNew", required = false) String registerNew, Model model) {


//		System.out.println("============================= in /addstudent POST, about to save a brand new student, registerNew: " + registerNew);


		Course course = courseRepo.findOne(courseId);
		model.addAttribute("course", course);
		model.addAttribute("existingStudent", new Person());

		if(bindingResult.hasErrors()) {
			return "addstudenttocourse";
		}

		// 'existing student' box was checked, register them as a new student
		if(registerNew != null) {
			System.out.println("============================= in /addstudent POST, about to save a brand new student, no existing matches were found");
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
//			return "redirect:/addstudent/" + courseId;
		}


		// if 'existing student' box was checked, but can't find a match, display a msg
		if(personRepo.countByNameFirstIsAndNameLastIsAndContactNumIsAndEmailIs(student.getNameFirst(),
				student.getNameLast(), student.getContactNum(), student.getEmail()) == 0) {
			System.out.println("============================= in /addstudent POST, 'new student' box was NOT checked, but could not find a match");

			model.addAttribute("message", "No existing student found that matches the info that was entered");
			model.addAttribute("invalidStudent", true);
			return "addstudenttocourseconfirmation";
		}


		// if 'existing student' was checked, and there was only one match, register them for this course if they're not already in it
		if(personRepo.countByNameFirstIsAndNameLastIsAndContactNumIsAndEmailIs(student.getNameFirst(),
				student.getNameLast(), student.getContactNum(), student.getEmail()) == 1) {

			System.out.println("============================= in /addstudent POST, about to register existing student, only found 1 match");
			Person p = personRepo.findFirstByNameFirstIsAndNameLastIsAndContactNumIsAndEmailIs(student.getNameFirst(),
					student.getNameLast(), student.getContactNum(), student.getEmail());
			model.addAttribute("message", "Welcome back!  You have been registered for this course.");
			model.addAttribute("student", p);
			model.addAttribute("course", course);

			if(p.getCourses().contains(course)) {
				// student is already registered for this course, no problem, just don't save anything to dbs
				return "addstudenttocourseconfirmation";
//			return "redirect:/addstudent/" + courseId;
			}

			// register them for this course, and create a timestamp
			RegistrationTimestamp rt = new RegistrationTimestamp();
			rt.setCourse(course);
			rt.setPerson(p);
			rt.setTimestamp(new Date());
			registrationTimestampRepo.save(rt);
			course.addPerson(p);
			courseRepo.save(course);

			return "addstudenttocourseconfirmation";
//			return "redirect:/addstudent/" + courseId;
		}




		// data entered matches a record in the db AND the 'existing student' box was checked, so now need to show
		// a new page with a drop down of choices and Mnums for student to select.. themselves
		System.out.println("============================= in /addstudent POST, 1 or more matching student was found");
		// if there is more than one match found based on the form data that was just entered,
		// we'll display a new page with a drop down list of possible students and ask them to select one, or not
		Set<Person> potentials = personRepo.findByNameFirstIsAndNameLastIsAndContactNumIsAndEmailIs(student.getNameFirst(),
				student.getNameLast(), student.getContactNum(), student.getEmail());

		model.addAttribute("potentials", potentials);
		model.addAttribute("course", course);
		return "addstudentmultiplechoices";

	}


	// this route fires only when there were multiple students that matched all the registration data when they were
	// signing up for a course.. it there were > 1 existing students with same first and last names, emails, and contact nums
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
//			return "redirect:/addstudent/" + courseId;
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
//		return "redirect:/addstudent/" + courseId;

	}





	// each time we create a brand new set of Attendance objects to send to the form
	// each student is a row, and each column is a single date
	// all Attendances default to Presents, so teacher only has to change for late or absent
	// the Attendance List sent to the form MUST be ordered (it's an ArrayList)
	// a nested thymeleaf loop is used to construct the table
	// the ArrayList of Attendance objects are in a wrapper class, because I could not get the form to assign
	// values directly to the Attendance objects any other way!
	// each time the teacher comes back, they will just have to redo the entire attendance for the entire class,
	// values are not preserved, but duplicates are never created, not ideal, but good enough for now
	@GetMapping("/takeattendance/{courseid}")
	public String takeAttendance(@PathVariable("courseid") long courseId, Model model) {

		Course course = courseRepo.findOne(courseId);
		LinkedHashSet<Person> students = personRepo.findByCoursesIsAndAuthoritiesIsOrderByNameLastAsc(course, authorityRepo.findByRole("STUDENT"));
		int diffInDays = Utilities.getDiffInDays(course.getDateStart(), course.getDateEnd());
		Date startDate = course.getDateStart();
		List<Date> dates = new ArrayList<>();

		for (int i = 0; i < diffInDays; i++) {
			dates.add(Utilities.addDays(startDate, i));
		}


		// get the difference in days between course start and end dates
		System.out.printf("======================= Difference between course start and end dates: %d day(s)\n", diffInDays);
		System.out.println("======================= dates.size: " + dates.size());

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


	@PostMapping("/takeattendance/{courseid}")
	public String takeAttendancePost(
			@ModelAttribute("attendanceWrapper") AttendanceWrapper attWrapper,
			@RequestParam("selectedDate") Date selectedDate,
			@PathVariable("courseid") long courseId) {

		System.out.println("================================================ in /takeattendance POST, incoming courseId: " + courseId);
		System.out.println("=================== attWrapper.getStringList.size: " + attWrapper.getAttendanceList().size());
		System.out.println("=================== selectedDate: " + selectedDate);

		Course course = courseRepo.findOne(courseId);
		LinkedHashSet<Person> students = personRepo.findByCoursesIsAndAuthoritiesIsOrderByNameLastAsc(course, authorityRepo.findByRole("STUDENT"));

        // set the date on each Attendance that we just got back from the form
        for (Attendance att : attWrapper.getAttendanceList()) {
            System.out.println("============================ in /takeattendance POST, att.getPerson.getId: " + att.getPerson().getId());
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
	public String sendEmail (@RequestParam("id") long courseId, Model model) {



		// first add a list of admins to the template
		model.addAttribute("adminList", personRepo.findByAuthoritiesIs(authorityRepo.findByRole("ADMIN")));
		// add the course to the model, so we can show the name on the page
		model.addAttribute("course", courseRepo.findOne(courseId));

		return "sendemail";
	}



//	===========================================================================================================================================
@PostMapping("/sendemail")
public String sendEmailPosts(@RequestParam("selectedAdminId") long adminId,
							@RequestParam("courseId") long courseId,
							Principal principal) {
	// get the logged in person
	Person teacher = personRepo.findByUsername(principal.getName());

	// get the selected admin
	Person admin = personRepo.findOne(adminId);

	// get the course
	Course course = courseRepo.findOne(courseId);

	sendEmailWithoutTemplate(course,
			teacher.getFullName(),	// teacher name
			course.getName(),		// course name
			admin.getEmail(),		// to email address
			admin.getFullName());	// to email name

	return "redirect:/mycoursesdetail";
}

	private EmailAttachment getCsvForecastAttachment(String filename, Course course) {
		String headers="M-Number,Student Name,Date,Status\n";
		Iterable<Person> students = course.getPersons();
		for (Person stud : students) {
			String fullName = stud.getFullName();
			String studentId = String.valueOf(stud.getId());
			String mNUM = String.valueOf(stud.getmNumber());
			Iterable<Attendance> attendances = stud.getAttendances();
			for (Attendance att : attendances) {
				String dates = String.valueOf(att.getDate());
				String status = att.getAstatus();
				headers+= mNUM + "," + fullName  + "," + dates+","+status + "\n";
			}

		}

		DefaultEmailAttachment attachment = DefaultEmailAttachment.builder()
				.attachmentName(filename + ".csv")
				.attachmentData(headers.getBytes(Charset.forName("UTF-8")))
				.mediaType(MediaType.TEXT_PLAIN).build();

		return attachment;
	}



	public void sendEmailWithoutTemplate(Course course, String teacherName, String courseName,String adminEmail, String adminName) {

		final Email email;
		try {
			email = DefaultEmail.builder()
					// DOES NOT MATTER what you put in .from address.. it ignores it and uses what is in properties file
					// this may work depending on the email server config that is being used
					// the from NAME does get used though
					.from(new InternetAddress("anyone@anywhere.net", teacherName))
					.to(Lists.newArrayList(
							new InternetAddress(adminEmail, adminName)))
					.subject("Attendance For " + courseName)
					.body("Student Attendance Details")
					.attachment(getCsvForecastAttachment("Attendance", course))
					.encoding("UTF-8").build();

			// conveniently, .send will put a nice INFO message in the console output when it sends
			emailService.send(email);

		} catch (UnsupportedEncodingException e) {
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!! caught an unsupported encoding exception");
			e.printStackTrace();
		}
	}

	// shows a drop down list of admins, teacher selects one to send an attendance email to
	@GetMapping("/sendemails")
	public String sendEmails(@RequestParam("id") long courseId, Model model) {
		// first add a list of admins to the template
		model.addAttribute("adminList", personRepo.findByAuthoritiesIs(authorityRepo.findByRole("ADMIN")));
		// add the course to the model, so we can show the name on the page
		model.addAttribute("course", courseRepo.findOne(courseId));

		return "sendemailforcourseeval";
	}

	@PostMapping("/sendemails")
	public String sendEmailAdmin(@RequestParam("selectedAdminId") long adminId,
								 @RequestParam("courseId") long courseId,
								 Principal principal) {
		// get the logged in person
		Person teacher = personRepo.findByUsername(principal.getName());

		// get the selected admin
		Person admin = personRepo.findOne(adminId);

		// get the course
		Course course = courseRepo.findOne(courseId);

		sendEmailWithoutTemplates(course,
				teacher.getFullName(),	// teacher name
				course.getName(),		// course name
				admin.getEmail(),		// to email address
				admin.getFullName());	// to email name

		return "redirect:/mycoursesdetail";
	}

	private EmailAttachment getCsvForecastAttachments(String filename, Course course) {
		String headers="Teacher Name,Course Content Rating,Instruction Quality Rating,Training Experience Rating,Text Book Rating,Classroom Environment,Equipment Rating,What Did You Like,What didnt You Like,What Improvements,What Other Classes,How Did You Find Out\n";
		Iterable<Person> teachers = course.getPersons();
		for (Person teach : teachers) {
			String fullName = teach.getFullName();
			Iterable<Evaluation> evaluations = teach.getEvaluations();
			for (Evaluation evas : evaluations) {
				String ccr=evas.getCourseContentRating();
				String iqr=evas.getInstructionQualityRating();
				String ter=evas.getTrainingExperienceRating();
				String tbr=evas.getTextBookRating();
				String cre=evas.getClassroomEnvironment();
				String er=evas.getEquipmentRating();
				String dy=evas.getWhatDidYouLike();
				String dny=evas.getWhatDidntYouLike();
				String  wi=evas.getWhatImprovements();
				String woc=evas.getWhatOtherClasses();
				String hdf=evas.getHowDidYouFindOut();

				headers+=fullName + ","+ ccr + "," + iqr  + "," + ter +","
						+ tbr +","+ cre + ","+ er + "," + dy
						+ "," + dny + "," + wi + ","+ woc +"," + hdf +"\n";
			}

		}

		DefaultEmailAttachment attachment = DefaultEmailAttachment.builder()
				.attachmentName(filename + ".csv")
				.attachmentData(headers.getBytes(Charset.forName("UTF-8")))
				.mediaType(MediaType.TEXT_PLAIN).build();

		return attachment;
	}



	public void sendEmailWithoutTemplates(Course course, String teacherName, String courseName,String adminEmail, String adminName) {

		final Email email;
		try {
			email = DefaultEmail.builder()
					// DOES NOT MATTER what you put in .from address.. it ignores it and uses what is in properties file
					// this may work depending on the email server config that is being used
					// the from NAME does get used though
					.from(new InternetAddress("anyone@anywhere.net", teacherName))
					.to(Lists.newArrayList(
							new InternetAddress(adminEmail, adminName)))
					.subject("Evaluation For " + courseName)
					.body("Teacher Evaluation Details")
					.attachment(getCsvForecastAttachments("Evaluation", course))
					.encoding("UTF-8").build();

			// conveniently, .send will put a nice INFO message in the console output when it sends
			emailService.send(email);

		} catch (UnsupportedEncodingException e) {
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!! caught an unsupported encoding exception");
			e.printStackTrace();
		}
	}

}
