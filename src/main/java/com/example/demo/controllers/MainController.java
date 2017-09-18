package com.example.demo.controllers;

import com.example.demo.models.*;
import com.example.demo.repositories.*;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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

        // display a welcome page depending on if teacher, admin, or student
        // just to be safe, check for null, so if user logs out and clicks back button, app won't crash
        if(principal != null) {
            switch (personRepo.findByUsername(principal.getName()).getAuthority()) {
                case "ADMIN":
                    return "welcomeAdmin";
                case "TEACHER":
                    return "welcomeTeach";
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
        // error msg is display bia binding result as usual, so no need to check here
        if(person.getSelectVal().equalsIgnoreCase("TEACHER")  )      {
            userService.saveTeacher(person);
            model.addAttribute("message", "Teacher Account Successfully Created");
        }
        else{
            userService.saveAdmin(person);
            model.addAttribute("message","Admin Account Successfully Created");
        }

        return "redirect:/login";
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

    //Teacher and admin
    //Modified by Yun on 09/15, show teacher information in coursedetail page
    @RequestMapping("/coursedetail/{courseid}")
    public String courseDetail(@PathVariable ("courseid") long id, Model model)
    {
        Course course = courseRepo.findOne(id);

        model.addAttribute("numStudents", personRepo.countByCoursesIsAndAuthoritiesIs(course, authorityRepo.findByRole("STUDENT")));
        model.addAttribute("course", course);
//        System.out.println("course after coursedetail:"+courseRepo.findOne(id));
        model.addAttribute("teachers", personRepo.findByCoursesIsAndAuthoritiesIs(course,authorityRepo.findByRole("TEACHER")));
//        System.out.println("teacher after coursedetail---: "+personRepo.findByCoursesIs(courseRepo.findOne(id)));
        return "coursedetail";
    }



}
