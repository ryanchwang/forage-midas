/**
package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TransactionService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private IncentiveService incentiveService;

    @Transactional
    public void processTransaction(Transaction kafkaTransaction) {
        Optional<UserRecord> senderOpt = Optional.ofNullable(userRepository.findById(kafkaTransaction.getSenderId()));
        Optional<UserRecord> recipientOpt = Optional.ofNullable(userRepository.findById(kafkaTransaction.getRecipientId()));

        if (senderOpt.isPresent() && recipientOpt.isPresent()) {
            UserRecord sender = senderOpt.get();
            UserRecord recipient = recipientOpt.get();

            if (sender.getBalance() >= kafkaTransaction.getAmount()) {
                // Deduct the transaction amount from the sender's balance
                sender.setBalance(sender.getBalance() - kafkaTransaction.getAmount());

                // Retrieve the incentive and calculate the total amount to credit to the recipient
                Transaction incentiveResponse = incentiveService.retrieveIncentive(kafkaTransaction);
                float totalAmount = kafkaTransaction.getAmount() + incentiveResponse.getAmount();

                // Credit the transaction amount plus the incentive to the recipient's balance
                recipient.setBalance(recipient.getBalance() + totalAmount);

                // Create and save the transaction record
                TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, totalAmount);
                transactionRepository.save(transactionRecord);

                // Save the updated user records
                userRepository.save(sender);
                userRepository.save(recipient);
            } else {
                throw new IllegalStateException("Insufficient funds for the transaction");
            }
        } else {
            throw new IllegalStateException("Invalid sender or recipient for the transaction");
        }
    }
}

 **/

package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private IncentiveService incentiveService;

    @Transactional
    public void processTransaction(Transaction kafkaTransaction) {
        log.debug("Processing transaction: {}", kafkaTransaction);
        UserRecord sender = validateAndGetUser(kafkaTransaction.getSenderId());
        UserRecord recipient = validateAndGetUser(kafkaTransaction.getRecipientId());
        validateSufficientFunds(sender, kafkaTransaction.getAmount());

        Transaction incentiveResponse = incentiveService.retrieveIncentive(kafkaTransaction);
        double totalAmount = kafkaTransaction.getAmount() + incentiveResponse.getAmount();

        log.debug("Incentive received: {}, Total transaction amount: {}", incentiveResponse.getAmount(), totalAmount);
        applyTransaction(sender, recipient, totalAmount);
    }

    private UserRecord validateAndGetUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User with ID " + userId + " not found"));
    }

    private void validateSufficientFunds(UserRecord sender, double amount) {
        if (sender.getBalance() < amount) {
            throw new IllegalStateException("Insufficient funds for the transaction");
        }
    }

    private void applyTransaction(UserRecord sender, UserRecord recipient, double totalAmount) {
        sender.setBalance((float) (sender.getBalance() - totalAmount));
        recipient.setBalance((float) (recipient.getBalance() + totalAmount));

        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, (float) totalAmount);
        transactionRepository.save(transactionRecord);

        userRepository.save(sender);
        userRepository.save(recipient);
        log.debug("Updated sender balance: {}, recipient balance: {}", sender.getBalance(), recipient.getBalance());
    }
}
