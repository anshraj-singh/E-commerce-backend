package com.quickcart.ecommerce.security;

import com.quickcart.ecommerce.service.CustomeUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomeUserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN") // Only ROLE_ADMIN can access /admin/*
                .antMatchers("/user/me","/update-user").authenticated() // Require authentication for accessing user info
                .antMatchers("/cart/**","/order/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .httpBasic(); // Enable basic authentication

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Use stateless session management
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {  //! hare is work on user and password related authenticate
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder()); //! to match database pass and login password
    }

    @Bean
    public PasswordEncoder passwordEncoder(){ //! take password then change hash form create password
        return new BCryptPasswordEncoder();
    }
}
