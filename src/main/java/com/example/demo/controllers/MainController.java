package com.example.demo.controllers;

import com.example.demo.models.*;
import com.example.demo.repositories.*;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

import java.sql.Timestamp;
import java.util.*;


@Controller
public class MainController
{
    @Autowired
    AuthorityRepo authorityRepo;
    @Autowired
    private UserService userService;
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




    @RequestMapping("/databasetesting")
    public String dbTest() {

        // create two authorities/roles
        Authority adminAuth = new Authority();
        adminAuth.setRole("ADMIN");
        authorityRepo.save(adminAuth);

        Authority teacherAuth = new Authority();
        teacherAuth.setRole("TEACHER");
        authorityRepo.save(teacherAuth);
        System.out.println("================================== created teacher and admin authorites");

        // create some people, first a teacher
        Set<Authority> auths = new HashSet<>();

        auths.add(teacherAuth);
        Person teacherPerson = new Person();
        teacherPerson.setAuthorities(auths);
        teacherPerson.setContactNum("1234567890");
        teacherPerson.setEmail("teachermail@abc.com");
        teacherPerson.setEnabled(true);
        teacherPerson.setNameFirst("TeacherSueFN");
        teacherPerson.setNameLast("TeacherSueLN");
        teacherPerson.setPassword("pass");
        teacherPerson.setUsername("teacher");
        personRepo.save(teacherPerson);
        System.out.println("================================== created teacher Sue");

        // creat another teacher
        Person teacherPerson2 = new Person();
        teacherPerson2.setAuthorities(auths);
        teacherPerson2.setContactNum("1234567890");
        teacherPerson2.setEmail("teachermail2@abc.com");
        teacherPerson2.setEnabled(true);
        teacherPerson2.setNameFirst("TeacherBobFN");
        teacherPerson2.setNameLast("TeacherBobLN");
        teacherPerson2.setPassword("pass");
        teacherPerson2.setUsername("teacher2");
        personRepo.save(teacherPerson2);
        System.out.println("================================== created teacher Bob");

        auths.add(adminAuth);
        Person adminPerson = new Person();
        adminPerson.setAuthorities(auths);
        adminPerson.setContactNum("789012345");
        adminPerson.setEmail("adminmail@abc.com");
        adminPerson.setEnabled(true);
        adminPerson.setNameFirst("AdminSueFN");
        adminPerson.setNameLast("AdminSueLN");
        adminPerson.setPassword("pass");
        adminPerson.setUsername("admin");
        personRepo.save(adminPerson);
        System.out.println("================================== created admin Person");

        // create a new Course
        Course courseJava = new Course();
        courseJava.setName("Java 101");
        courseJava.setDateStart(new Date());// set to todays date with new Date object
        courseJava.setDateEnd(new Date());// for testing, set end date to be the same
        courseJava.setNumEvaluationsCompleted(10);
        courseJava.setCourseRegistrationNum(1000);
        // attach teacher Sue to this course
        courseJava.addPerson(teacherPerson); // this is teacher Sue
        courseRepo.save(courseJava);

        // create another new course
        Course coursePython = new Course();
        coursePython.setName("Python 500");
        coursePython.setDateStart(new Date());// set to todays date with new Date object
        coursePython.setDateEnd(new Date());// for testing, set end date to be the same
        coursePython.setNumEvaluationsCompleted(20);
        coursePython.setCourseRegistrationNum(2000);
        // attach teacher Bob to this course
        coursePython.addPerson(teacherPerson2);
        courseRepo.save(coursePython);

       // create an evaluation
        Evaluation evaluation1 = new Evaluation();
        evaluation1.setClassroomEnvironment("Excellent");
        evaluation1.setCourseContentRating("Average");
        evaluation1.setEquipmentRating("Good");
        evaluation1.setHowDidYouFindOut("internet");
        evaluation1.setInstructionQualityRating("Poor");
        evaluation1.setTraningExperienceRating("Fair");
        evaluation1.setTextBookRating("Average");
        evaluation1.setWhatDidntYouLike("slow computers");
        // attach a course to this evaluation
        evaluation1.setCourse(courseJava);
        evaluationRepo.save(evaluation1);



        // create an bunch of Attendance
        Date d = new Date();
        // create as student
        Person studentJoe = new Person();
        studentJoe.setNameFirst("Joe");
        studentJoe.setNameLast("Dimaggio");
        studentJoe.setEmail("abc@def.ghi");
        studentJoe.setContactNum("1723894836");
        personRepo.save(studentJoe);
        for(int i = 0; i < 20; i++) {
            Attendance att = new Attendance();
            att.setDate(d);
            att.setCourse(coursePython);
            att.setPerson(studentJoe);
            att.setAstatus("Present");
            attendanceRepo.save(att);
        }


        // create a new registration timestamp
        RegistrationTimestamp timestamp = new RegistrationTimestamp();
        timestamp.setTimestamp(new Date());
        timestamp.setPerson(studentJoe);
        timestamp.setCourse(courseJava);
        registrationTimestampRepo.save(timestamp);

        return "dbtest";
    }




    /************************
     *
     *  Welcome/login pages
     *
     ************************/
    @RequestMapping("/")
    public String welcomePage()
    {
        if(authorityRepo.count()==0) {
            Authority adminAuth = new Authority();
            adminAuth.setRole("ADMIN");
            authorityRepo.save(adminAuth);

            Authority teacherAuth = new Authority();
            teacherAuth.setRole("TEACHER");
            authorityRepo.save(teacherAuth);
        }
        return "welcome";
    }

    @GetMapping("/welcome")
    public String showHomePage() {
        return "welcome";
    }

    @RequestMapping("/login")
    public String login()
    {
        return "login";
    }


    @RequestMapping("/signup")
    public String addUserInfo(Model model) {
        model.addAttribute("newPerson", new Person());
        model.addAttribute("listRoles", authorityRepo.findAll());
        return "signup";
    }

    @PostMapping("/signup")
    public String addUserInfo(@ModelAttribute("newPerson") Person person, Model model){
        model.addAttribute("newPerson",person);
        if(person.getSelectVal().equalsIgnoreCase("TEACHER")  )      {

            userService.saveTeacher(person);
            model.addAttribute("message","Teacher Account Successfully Created");
        }
        else{

            userService.saveAdmin(person);
            model.addAttribute("message","Admin Account Successfully Created");
        }

        return "redirect:/login;";
    }


    /**************************
     *
     * Admin pages
     *
     **************************/
    @GetMapping("/addcourse")
    public String addCourse(Model model)
    {
        model.addAttribute("course", new Course());
        model.addAttribute("teachers", personRepo.findAll()); //needs to send only teachers-query by role
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

        return "coursedetail";
    }


    @GetMapping("/editcourse/{courseid}")
    public String editCourse(@PathVariable ("courseid") long id, Model model)
    {
        model.addAttribute("course", courseRepo.findOne(id));
        return "addcourse";
    }

    @RequestMapping("/deletecourse/{courseid}")
    public String deleteCourse(@PathVariable ("courseid") long id)
    {
        Course course = courseRepo.findOne(id);
		//need more here
        return "allcourses";
    }

    @RequestMapping("/allcourses")
    public String allCourses(Model model)
    {
    	model.addAttribute("allcourses", courseRepo.findAll());
        return "allcourses";
    }

    @RequestMapping("/coursedetail/{courseid}")
    public String courseDetail(@PathVariable ("courseid") long id, Model model)
    {
	    model.addAttribute("course", courseRepo.findOne(id));
	    return "coursedetail";
    }

    @RequestMapping("/allevaluations")
    public String allEvals(Model model)
    {
    	model.addAttribute("allevaluations", evaluationRepo.findAll());
        return "allevaluations";
    }
    /**************************
     *
     * Teacher pages
     *
     **************************/
    //all courses-in admin
    //Course detail-in admin

    @RequestMapping("/mycourses")
    public String myCourses(Principal principal, Model model)
    {
    	//something like-NOT RIGHT!!!!
	    //Person teacher = personRepo.findOne(principal.getName());
        //can actually return "allcourses" html, but only send teacher's courses
	    model.addAttribute("teachercourses", courseRepo.findAll());
        return "mycourses";
    }

	@GetMapping("/addstudent/{courseid}")
	public String registerStudent(@PathVariable("courseid")long courseId, Model model)
	{
		model.addAttribute("newstudent", new Person());
		//Course course = courseRepo.findOne(courseId);
		Course course=new Course();
		System.out.println(course.getId());
		model.addAttribute("course", course);
		return "addstudenttocourse";
	}

	@PostMapping("/addstudent/{courseid}")
	public String addStudentToCourse(@PathVariable("courseid")long courseId, @ModelAttribute("newstudent")Person student, Model model)
	{
		System.out.println("CourseId: "+ courseId);
		System.out.println("Name: "+student.getNameFirst());
		//Course course = courseRepo.findOne(courseId);
		//add course to person OR add person to course
		//save course AND/OR person

		//Course course = courseRepo.findOne(courseId);
		Course course=new Course();
		model.addAttribute("course", course);


		return "addstudent";
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

    @GetMapping("/evaluation")
    public String eval(Model model)
    {
    	model.addAttribute("evaluation", new Evaluation());
        return "evaluation";
    }
    @PostMapping("/evaluation")
    public String submitEvaluation(@ModelAttribute("evaluation") Evaluation eval)
    {
    	evaluationRepo.save(eval);
    	return "welcome";
    }

    @RequestMapping("/endcourse")
    public String endClass()
    {
        System.out.println("Send email to admin");
        return "endcourse";
    }
}
