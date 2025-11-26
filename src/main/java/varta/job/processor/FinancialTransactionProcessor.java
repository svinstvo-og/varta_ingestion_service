package varta.job.processor;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FinancialTransactionProcessor {

    final
    EntityManager entityManager;

    public FinancialTransactionProcessor(EntityManager entityManager) {
        this.entityManager = entityManager;


    }
}
