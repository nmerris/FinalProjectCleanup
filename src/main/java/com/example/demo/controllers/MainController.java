package com.example.demo.controllers;

import com.example.demo.models.*;
import com.example.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Controller
public class MainController
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
    @RequestMapping({"/","/welcome"})
    public String welcomePage()
    {
        return "welcome";
    }

    @RequestMapping("/signup")
    public String signup()
    {
        return "signup";
    }

    @RequestMapping("/login")
    public String login()
    {
        return "login";
    }

    /**************************
     *
     * Admin pages
     *
     **************************/
    @RequestMapping("/addcourse")
    public String addCourse()
    {
        return "addcourse";
    }

    @RequestMapping("/editcourse")
    public String editCourse()
    {
        return "addcourse";
    }

    @RequestMapping("/deletecourse")
    public String deleteCourse()
    {
        return "allcourses";
    }

    @RequestMapping("/allcourses")
    public String allCourses()
    {
        return "allcourses";
    }

    @RequestMapping("/coursedetail")
    public String courseDetail()
    {
        return "coursedetail";
    }

    @RequestMapping("/allevaluations")
    public String allEvals()
    {
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
    public String myCourses()
    {
        //can actually return "allcourses" html, but only send teacher's courses
        return "mycourses";
    }

    @RequestMapping("/takeattendance")
    public String takeAttendance()
    {
        return "takeattendance";
    }

    @RequestMapping("/viewattendance")
    public String viewAttendance()
    {
        return "viewattendance";
    }

    @RequestMapping("/evaluation")
    public String eval()
    {
        return "evaluation";
    }

    @RequestMapping("/endcourse")
    public String endClass()
    {
        System.out.println("Send email to admin");
        return "endcourse";
    }
}
