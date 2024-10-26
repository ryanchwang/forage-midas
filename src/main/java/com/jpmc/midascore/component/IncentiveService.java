package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IncentiveService {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String INCENTIVE_URL = "http://localhost:8080/incentive";

    public Transaction retrieveIncentive(Transaction transaction) {
        // Send the transaction object via POST and receive an incentive response
        return restTemplate.postForObject(INCENTIVE_URL, transaction, Transaction.class);
    }
}
