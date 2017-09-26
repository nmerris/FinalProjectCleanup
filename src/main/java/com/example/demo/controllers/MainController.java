package com.example.demo.controllers;

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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.mail.internet.InternetAddress;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
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
    @Autowired
    public EmailService emailService;


    /************************
     *
     *  Welcome/login pages
     *
     ************************/
    // this route fires for everybody, any anonymous visitor will hit this route
    // including teachers and admins BEFORE they log in
    @RequestMapping("/")
    public String welcomePage()
    {
        // create three authorities and save them to the authority table
        // first make sure there are no authorities already in the table so as not to cause a unique constraint violation
        // this is also in DataLoader, but that wasn't working for hiwot or orr, so now it's here too
        if( authorityRepo.count() == 0) {
            Authority adminAuthority = new Authority();
            adminAuthority.setRole("ADMIN");
            authorityRepo.save(adminAuthority);

            Authority teacherAuthority = new Authority();
            teacherAuthority.setRole("TEACHER");
            authorityRepo.save(teacherAuthority);

            Authority studentAuthority = new Authority();
            studentAuthority.setRole("STUDENT");
            authorityRepo.save(studentAuthority);
        }

        return "welcome";
    }

    // this route only fires after a user logs in
    @GetMapping("/welcome")
    public String showHomePage(Principal principal) {
        // just to be safe, check for null, so if user logs out and clicks back button, app won't crash
        if(principal != null) {
            switch (personRepo.findByUsername(principal.getName()).getAuthority()) {
                case "ADMIN":
                    // take admin directly to the list of courses
                    return "redirect:/allcourses";
                case "TEACHER":
                    // take teachers directly to their list of courses
                    return "redirect:/mycoursesdetail";
            }
        }

        // this should never happen because you can only get to this route if you are logged in
        // but still need to return something for it to compile..
        return "welcome";
    }

    @RequestMapping("/login")
    public String login()
    {
        return "login";
    }


    // form for folks to sign up with a new account: either admin or teacher at this time
    @RequestMapping("/signup")
    public String addUserInfo(Model model) {
        model.addAttribute("newPerson", new Person());
        model.addAttribute("listRoles", authorityRepo.findByRoleIsOrRoleIsOrderByRoleDesc("TEACHER","ADMIN"));
        return "signup";
    }

    @PostMapping("/signup")
    public String addUserInfo(@Valid @ModelAttribute("newPerson") Person person, BindingResult bindingResult,Model model){
        model.addAttribute("listRoles", authorityRepo.findByRoleIsOrRoleIsOrderByRoleDesc("TEACHER","ADMIN"));
        if(bindingResult.hasErrors()) {
            return "signup";
        }


        // manually validate for username is null and also make sure it is unique
        if(person.getUsername().isEmpty() || personRepo.countByUsername(person.getUsername()) > 0) {
            model.addAttribute("usernameWasNull",true);
            return "signup";

        }

        // manually check to make sure password was not null
        if(person.getPassword().isEmpty()){
            model.addAttribute("passwordWasNull",true);
            return "signup";

        }

        // NOTE: username uniqueness is enforced by Unique annotation in Person model, and a validation
        // error msg is display via binding result as usual, so no need to check here
        if(person.getSelectVal().equalsIgnoreCase("TEACHER")) {
            // save a new teacher Person
            userService.saveTeacher(person);
            model.addAttribute("message", "Teacher Account Successfully Created");
        }
        else{
            // save a new admin Person
            userService.saveAdmin(person);
            model.addAttribute("message","Admin Account Successfully Created");
        }

        // go back to login page after signing up
        return "redirect:/login";
    }


    @GetMapping("/evaluation")
    public String eval(Model model)
    {
        // create a course and add some dummy data to send to form, need it to have data in validated fields, doesn't matter because we are not going to save it
        // TODO need to use validation groups.. this is ugly!
        Course course = new Course();
        course.setDateEnd(new Date()); // dummy date never to be saved
        course.setName("fake name never to be saved");
    	model.addAttribute("course", course);
        model.addAttribute("disSubmit", personRepo.countByAuthoritiesIs(authorityRepo.findByRole("TEACHER")) == 0);
    	model.addAttribute("allTeachers", personRepo.findByAuthoritiesIs(authorityRepo.findByRole("TEACHER")));
        return "evaluation";
    }

    // app was crashing if you type in an invalid date format or if you type in letters in the CRN field, so added @Valid to incoming Course
    // this requires some dummy data in the get route, which doesn't matter because the course is never actually saved here
    @PostMapping("/evaluation")
    public String getCourseInfoForEval(@Valid @ModelAttribute("course") Course course, BindingResult bindingResult,
                                       @RequestParam(value = "selectedTeacher") long teacherId, Model model)
    {
        if(bindingResult.hasErrors()) {
            model.addAttribute("allTeachers", personRepo.findByAuthoritiesIs(authorityRepo.findByRole("TEACHER")));
            return "evaluation";
        }

        Course specificCourse = courseRepo.findFirstByCourseRegistrationNumAndDateStartAndDeleted(course.getCourseRegistrationNum(), course.getDateStart(),false);
        Person teacher = personRepo.findOne(teacherId);

        if(specificCourse == null)
        {
            model.addAttribute("courseError", true);
            model.addAttribute("allTeachers", personRepo.findByAuthoritiesIs(authorityRepo.findByRole("TEACHER")));
            return "evaluation";
        }

        if(!specificCourse.getPersons().contains(teacher))
        {
            model.addAttribute("teacherError", true);
            model.addAttribute("allTeachers", personRepo.findByAuthoritiesIs(authorityRepo.findByRole("TEACHER")));
            return "evaluation";
        }

        model.addAttribute("course", specificCourse);
        model.addAttribute("teacher", teacher);
        model.addAttribute("evaluation", new Evaluation());
        return "evaluation2";
    }


    // this is the big form with all the evaluation data
    @PostMapping("/evaluation2")
    public String submitEval(@RequestParam("howDidYouFindOut2") String other,
                             @RequestParam("courseId") long courseId,
                             @RequestParam("teacherId") long teacherId,
                             @ModelAttribute("evaluation") Evaluation eval, Model model)
    {

        // if student chose 'other', set it on the eval object
        if(!eval.getHowDidYouFindOut().isEmpty() && eval.getHowDidYouFindOut().equalsIgnoreCase("Other"))
        {
            eval.setHowDidYouFindOut(other);
        }

        // set the course and teacher, then save
        eval.setCourse(courseRepo.findOne(courseId));
        eval.setPerson(personRepo.findOne(teacherId));
        evaluationRepo.save(eval);

        model.addAttribute("message", "Course Evaluation Submitted");
        model.addAttribute("extraMessage", String.format("Course: %s",
                courseRepo.findOne(eval.getCourse().getId()).getName()));

        return "evaluationconfirmation";
    }

    //Teacher and admin both use this route to view individual course details
    @RequestMapping("/coursedetail/{courseid}")
    public String courseDetail(@PathVariable ("courseid") long id, Model model)
    {
        Course course = courseRepo.findOne(id);
        model.addAttribute("numStudents", personRepo.countByCoursesIsAndAuthoritiesIs(course, authorityRepo.findByRole("STUDENT")));
        model.addAttribute("course", course);
        model.addAttribute("teachers", personRepo.findByCoursesIsAndAuthoritiesIs(course,authorityRepo.findByRole("TEACHER")));
        return "coursedetail";
    }



}
