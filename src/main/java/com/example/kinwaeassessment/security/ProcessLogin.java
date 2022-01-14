package com.example.kinwaeassessment.security;

import com.example.kinwaeassessment.exception.ApiCustomException;
import com.example.kinwaeassessment.model.AppUser;
import com.example.kinwaeassessment.model.Login;
import com.example.kinwaeassessment.service.AppUserService;
import com.example.kinwaeassessment.templates.LoginCompleted;
import com.example.kinwaeassessment.utility.Validations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
public class ProcessLogin {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private Validations validations;

    public LoginCompleted validateLogin(Login user, HttpServletRequest request){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getName().equals(user.getUsername()))
            validations.validateLogin(user, request.getRemoteAddr());

        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
        }catch (BadCredentialsException bce){
            throw new ApiCustomException("Sorry, bad credentials");
        }
        final UserDetails userDetails = appUserService.loadUserByUsername(user.getUsername());
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(token);
        appUserService.updateIpAddress(user.getUsername(), request.getRemoteAddr());
        AppUser appUser = appUserService.getUserByEmail(user.getUsername());

        return new LoginCompleted(
                "success",
                "Welcome " + appUser.getFirstname() +
                        ", Your device ID is {" + request.getRemoteAddr() +
                        "}Take note of your userId as you're to make further requests with it",
                appUser.getId()
        );
    }
}
