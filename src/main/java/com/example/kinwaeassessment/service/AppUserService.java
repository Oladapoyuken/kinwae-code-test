package com.example.kinwaeassessment.service;

import com.example.kinwaeassessment.exception.ApiCustomException;
import com.example.kinwaeassessment.model.AppUser;
import com.example.kinwaeassessment.model.Login;
import com.example.kinwaeassessment.model.Transaction;
import com.example.kinwaeassessment.repository.AppUserRepo;
import com.example.kinwaeassessment.repository.LoginRepo;
import com.example.kinwaeassessment.repository.TransactionRepo;
import com.example.kinwaeassessment.templates.SimpleResponse;
import com.example.kinwaeassessment.utility.RandomTransactionGenerator;
import com.example.kinwaeassessment.utility.Validations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Slf4j
public class AppUserService implements UserDetailsService {

    @Autowired
    private  AppUserRepo appUserRepo;

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private RandomTransactionGenerator generator;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private LoginRepo loginRepo;

    @Autowired
    private Validations validations;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Login> user = loginRepo.findByUsername(username);
        if(user.isPresent()){
            return new User(user.get().getUsername(), user.get().getPassword(), new ArrayList<>());
        }
        log.info("Cannot fetch username from database to service the Userdetails");
        throw new ApiCustomException("Sorry, user not found from database!");
    }

    @Transactional
    public SimpleResponse registerUser(AppUser newAppUser){
        String password = bCryptPasswordEncoder.encode("1234");
        newAppUser.setPassword(password);
        if(!validations.validateAge(newAppUser.getDob(), 18))
            throw new ApiCustomException("Sorry, you must be greater than 18yrs of age");

        if(validations.validateEmail(newAppUser.getEmail()))
            throw new ApiCustomException("Sorry, Email must be a cooperate email address");

        if(validations.validateEmailExist(newAppUser.getEmail()))
            throw new ApiCustomException("Sorry, Email already exist");

        newAppUser.setDateCreated(LocalDate.now());

        appUserRepo.save(newAppUser);

        loginRepo.save(new Login(null, newAppUser.getEmail(), password, "No Device Ip found", newAppUser));

        generator.generate(newAppUser);

        Optional<AppUser> registeredUser = appUserRepo.findByEmail(newAppUser.getEmail());

        log.info("An email was sent to {}, please login to view your password", newAppUser.getEmail());

        return new SimpleResponse("success", "User created successfully, an email has been sent to you and your passoword is given as 1234");
    }

    @Transactional
    public void updateIpAddress(String username, String ip){
        Optional<Login> optionalLogin = loginRepo.findByUsername(username);
        if(optionalLogin.isPresent())
            optionalLogin.get().setIpAddress(ip);
    }

    public Page<Transaction> getFilteredPaginatedTransaction(
            Optional<Long> id, int offset, int pageSize, String field, Optional<String> filter){

        if(id.isEmpty()){
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Optional<AppUser> appUser = appUserRepo.findByEmail(email);
            if(appUser.isEmpty())
                throw new ApiCustomException("User account not found");
            return  getTransactions(appUser.get(), offset, pageSize, field, filter);
        }
        Optional<AppUser> user = appUserRepo.findById(id.get());
        if(user.isEmpty())
            throw new ApiCustomException("User account not found");

        Page<Transaction> transactions = getTransactions(user.get(), offset, pageSize, field, filter);
        transactions
                .stream()
                .forEach(page -> page.setAmount(Double.NaN));

        return transactions;
    }

    public Page<Transaction> getTransactions(AppUser appUser, int offset, int pageSize, String field, Optional<String> filter) {

        if(field.equals("date")){
            LocalDate date = LocalDate.parse(filter.get());
            return transactionRepo.findByTransactionDateAndUser(date, appUser, PageRequest.of(offset, pageSize));
        }
        else if(field.equals("amount")){
            Double money = Double.parseDouble(filter.get());
            Page<Transaction> amount = transactionRepo.findByAmountAndUser(
                    money, appUser, PageRequest.of(offset, pageSize));
            return amount;
        }
        else{
            Page<Transaction> narration = transactionRepo.findByUserAndNarrationContaining(
                    appUser, filter.get(), PageRequest.of(offset, pageSize));
            return narration;
        }
    }

    public Transaction getTransactionDetail(Long id) {
        Optional<Transaction> transaction = transactionRepo.findByIdAndUser(id, getCurrentUser());

        if(transaction.isPresent())
            return transaction.get();

        throw new ApiCustomException("Transaction not found");
    }

    public Page<Transaction> getAllTransaction(Optional<Long> id, int offset, int pageSize){
        if(id.isPresent()){
            Optional<AppUser> user = appUserRepo.findById(id.get());
            if(user.isEmpty())
                throw new ApiCustomException("User account not found");

            return transactionRepo.findAllByUser(user.get(), PageRequest.of(offset, pageSize));
        }else{
            return transactionRepo.findAllByUser(getCurrentUser(), PageRequest.of(offset, pageSize));
        }
    }

    private AppUser getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Optional<AppUser> appUser = appUserRepo.findByEmail(email);
        if(appUser.isEmpty())
            throw new ApiCustomException("User account not found");

        return appUser.get();
    }

    public AppUser getUserByEmail(String email){
        return appUserRepo.findByEmail(email).get();
    }
}
