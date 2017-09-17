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



    /************************
     *
     *  Welcome/login pages
     *
     ************************/
    @RequestMapping("/")
    public String welcomePage()
    {
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
        model.addAttribute("listRoles", authorityRepo.findByRoleIsOrRoleIs("TEACHER","ADMIN"));
        return "signup";
    }

    @PostMapping("/signup")
    public String addUserInfo(@Valid @ModelAttribute("newPerson") Person person, BindingResult bindingResult,Model model){
        model.addAttribute("listRoles", authorityRepo.findByRoleIsOrRoleIs("TEACHER","ADMIN"));
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

        if(person.getSelectVal().equalsIgnoreCase("TEACHER")  )      {

            userService.saveTeacher(person);
            model.addAttribute("message", "Teacher Account Successfully Created");
        }
        else{

            userService.saveAdmin(person);
            model.addAttribute("message","Admin Account Successfully Created");
        }

        return "redirect:/login;";
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
        model.addAttribute("course", courseRepo.findOne(id));
//        System.out.println("course after coursedetail:"+courseRepo.findOne(id));
        model.addAttribute("teachers", personRepo.findByCoursesIs(courseRepo.findOne(id)));
//        System.out.println("teacher after coursedetail---: "+personRepo.findByCoursesIs(courseRepo.findOne(id)));
        return "coursedetail";
    }



}
