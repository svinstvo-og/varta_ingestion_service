package varta.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import varta.model.pgsql.CreditCard;
import varta.model.pgsql.CreditTransaction;
import varta.model.pgsql.CreditUser;
import varta.repository.pgsql.CreditCardRepository;
import varta.repository.pgsql.CreditTransactionRepository;
import varta.repository.pgsql.CreditUserRepository;

@Slf4j
@Service
public class MockService {

    final
    CreditTransactionRepository creditTransactionRepository;
    final
    CreditUserRepository creditUserRepository;
    final
    CreditCardRepository creditCardRepository;

    public MockService(CreditTransactionRepository creditTransactionRepository, CreditUserRepository creditUserRepository, CreditCardRepository creditCardRepository) {
        this.creditTransactionRepository = creditTransactionRepository;
        this.creditUserRepository = creditUserRepository;
        this.creditCardRepository = creditCardRepository;
    }

    @Transactional
    public void createMockTransaction() {
        var user = new CreditUser();
        creditUserRepository.save(user);
        log.info("Saved mock user");

        var card = getMockCreditCard(user);
        creditCardRepository.save(card);
        log.info("Saved mock card");

        var transaction = getMockCreditTransaction(card);
        creditTransactionRepository.save(transaction);
    }

    private CreditTransaction getMockCreditTransaction(CreditCard card) {
        return new CreditTransaction().builder().sourceCard(card).destinationCard(card).build();
    }

    private CreditCard getMockCreditCard(CreditUser user) {
        return  new CreditCard().builder().creditUser(user).build();
    }
}
