package varta.service;

import org.springframework.stereotype.Service;
import varta.model.pgsql.CreditTransaction;
import varta.repository.pgsql.CreditTransactionRepository;

@Service
public class EnrichmentService {

//    private RedisTemplate<>
    private final CreditTransactionRepository creditTransactionRepository;

    public EnrichmentService(CreditTransactionRepository creditTransactionRepository) {
        this.creditTransactionRepository = creditTransactionRepository;
    }

    // velocity features
//    private Double calculateVelocity(CreditTransaction transaction) {
//
//    }
}
