package com.example.kinwaeassessment.repository;

import com.example.kinwaeassessment.model.AppUser;
import com.example.kinwaeassessment.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByIdAndUser(Long id, AppUser user);

    Page<Transaction> findAllByUser(AppUser user, Pageable pageable);

//    @Query("SELECT s FROM Transaction s WHERE transaction_date = ?1")
    Page<Transaction> findByTransactionDateAndUser(LocalDate date, AppUser user, Pageable pageable);

    Page<Transaction> findByAmountAndUser(double amount, AppUser user, Pageable pageable);

    Page<Transaction> findByUserAndNarrationContaining(AppUser user, String narration, Pageable pageable);

}
