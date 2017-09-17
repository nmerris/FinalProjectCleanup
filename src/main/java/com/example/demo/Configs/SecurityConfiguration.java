package com.example.demo.Configs;

import com.example.demo.repositories.PersonRepo;
import com.example.demo.services.SSUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private SSUserDetailsService userDetailsSerevice;

    @Autowired
    private PersonRepo personRepo;

    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return new SSUserDetailsService(personRepo);
    }


    // TODO hiwot updated this sunday afternoon, but it will need a bit more touch up after everything is done, because I will prob
    // need to add a few more routes as I work through the evaluations and students lists in admin section
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                .antMatchers("/","/signup","/css/**","/js/**", "/img/**", "/fonts/**","/welcome", "/evaluation").permitAll()
                .antMatchers( "/welcomeAdmin","/addcourse","/addduplicatecourse","/allcourses","/allteachers","/allevaluations","/courseconfirm","/coursedetail","/coursestakenbystudent","/loginforequestform","/loginforequestconfirmation").access("hasAuthority('ADMIN')")
                .antMatchers("/addstudenttocourse","/listregisteredstudent","/sendemail","/takeattendance","/teachercoursedetail","/viewstudentattendance").access("hasAuthority('TEACHER')")
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login").permitAll()
                .defaultSuccessUrl("/welcome")
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/").permitAll().permitAll()
                .and()
                .httpBasic();
        http
                .csrf().disable();
        http
                .headers().frameOptions().disable();

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsServiceBean());
    }
}


