package com.example.kinwaeassessment.utility;

import com.example.kinwaeassessment.exception.ApiCustomException;
import com.example.kinwaeassessment.model.AppUser;
import com.example.kinwaeassessment.model.Login;
import com.example.kinwaeassessment.repository.AppUserRepo;
import com.example.kinwaeassessment.repository.LoginRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Component
@Transactional
public class Validations {

    @Autowired
    private LoginRepo loginRepo;

    @Autowired
    private AppUserRepo appUserRepo;


    public void validateLogin(Login user, String ip){
        Optional<Login> optionalLogin = loginRepo.findByUsernameAndIpAddress(user.getUsername(), ip);
        Optional<AppUser> appUser = appUserRepo.findByEmail(user.getUsername());

        if(optionalLogin.isEmpty() & appUser.isPresent()){
            if(!validateAge(appUser.get().getDob(), 40) ) {
                throw new ApiCustomException("Sorry, User already logged in with a device");
            }
        }
    }

    public boolean validateAge(LocalDate date, int age){
        Period interval = Period.between(date, LocalDate.now());
        return interval.getYears() >= age;
    }

    public boolean validateIp(String username, String ip){
        Optional<Login> optionalLogin = loginRepo.findByUsernameAndIpAddress(username, ip);
        return optionalLogin.isEmpty();

    }

    public boolean validateEmail(String email){
        String suffix = email.toLowerCase().substring(email.indexOf('@') + 1);
        return List.of("gmail.com", "outlook.com", "yahoo.com").contains(suffix);
    }

    public boolean validateEmailExist(String email){
        Optional<AppUser> optionalUser = appUserRepo.findByEmail(email);
        return optionalUser.isPresent();
    }
}
