package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


// !!!!!!!!!!!!!!!! NOTE: see

@Controller
public class MainController {




    @RequestMapping("/databasetesting")
    public String dbTest() {



        return "dbtest";
    }






}
