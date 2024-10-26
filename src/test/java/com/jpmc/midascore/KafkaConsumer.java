package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;



@Component
public class KafkaConsumer {

    @Autowired
    private com.jpmc.midascore.component.TransactionService transactionService;

    @KafkaListener(topics = "transactions-topic", groupId = "midas-core-group")
    public void consume(Transaction transaction) {
        transactionService.processTransaction(transaction);
    }
}
