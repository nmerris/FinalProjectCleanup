package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
public class MainController
{

    @RequestMapping("/databasetesting")
    public String dbTest() {

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
