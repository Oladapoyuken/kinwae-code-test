package com.example.kinwaeassessment.utility;


import com.example.kinwaeassessment.model.AppUser;
import com.example.kinwaeassessment.model.Transaction;
import com.example.kinwaeassessment.repository.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Component
public class RandomTransactionGenerator {

    @Autowired
    private TransactionRepo transactionRepo;
    
    public void generate(AppUser user) {
        List<String> type = List.of("CREDIT", "DEBIT");
        List<String> characters = List.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J","K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X","Y", "Z");
        Random random = new Random();

        for(int i = 0; i < 200; i++){
            transactionRepo.save(new Transaction(
                    null,
                    type.get(random.nextInt(2)),
                    Double.parseDouble(String.valueOf(random.nextInt(1000))),
                    characters.get(random.nextInt(26))
                            + characters.get(random.nextInt(26))
                            + characters.get(random.nextInt(26))
                            + characters.get(random.nextInt(26))
                            + characters.get(random.nextInt(26)),
                    LocalDate.now().minusDays(random.nextInt(1000)),
                    user
            ));
        }
    }
}
