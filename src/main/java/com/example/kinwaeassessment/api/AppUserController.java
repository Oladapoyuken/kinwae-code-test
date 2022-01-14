package com.example.kinwaeassessment.api;

import com.example.kinwaeassessment.model.AppUser;
import com.example.kinwaeassessment.model.Login;
import com.example.kinwaeassessment.model.Transaction;
import com.example.kinwaeassessment.security.ProcessLogin;
import com.example.kinwaeassessment.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private ProcessLogin processLogin;

    @PostMapping("/register")
    public ResponseEntity<?> registration(@RequestBody AppUser newUser){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/register").toUriString());
        return ResponseEntity.created(uri).body(appUserService.registerUser(newUser));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auth.setAuthenticated(false);

        return new ResponseEntity<>("Logout was successful", HttpStatus.OK);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody Login user, HttpServletRequest request) throws Exception{
        return new ResponseEntity<>(processLogin.validateLogin(user, request), HttpStatus.OK);
    }

    @GetMapping("/transaction/offset/{offset}/size/{size}/field/{field}/filter/{filter}")
    public ResponseEntity<?> getTransactionWithPaginationAndFilter(
            @PathVariable("offset") int offSet,
            @PathVariable("size") int pageSize,
            @PathVariable("field") String field,
            @PathVariable("filter") Optional<String> filter
    ){
        return new ResponseEntity<>(appUserService.getFilteredPaginatedTransaction(Optional.empty(), offSet, pageSize, field, filter), HttpStatus.OK);
    }

    @GetMapping("/transaction/offset/{offset}/size/{size}/field/{field}/filter/{filter}/user/{id}")
    public ResponseEntity<?> getTransactionWithPaginationAndFilter(
            @PathVariable("offset") int offSet,
            @PathVariable("size") int pageSize,
            @PathVariable("field") String field,
            @PathVariable("filter") Optional<String> filter,
            @PathVariable("id") Optional<Long> id
    ){
        return new ResponseEntity<>(appUserService.getFilteredPaginatedTransaction(id, offSet, pageSize, field, filter), HttpStatus.OK);
    }

    @GetMapping("/transaction/id/{id}")
    public ResponseEntity<?> getTransactionDetail(@PathVariable("id") Long id){
        return new ResponseEntity<>(appUserService.getTransactionDetail(id), HttpStatus.OK);
    }

    @GetMapping("/transaction/offset/{offset}/size/{size}")
    public ResponseEntity<?> getAllTransactions(
            @PathVariable("offset") int offSet,
            @PathVariable("size") int pageSize,
            @RequestParam("id") Optional<Long> id){
        return new ResponseEntity<>(appUserService.getAllTransaction(id, offSet, pageSize), HttpStatus.OK);
    }
}
