package varta.job.processor;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import varta.model.mysql.RawFinancialTransaction;
import varta.model.pgsql.CreditCard;
import varta.model.pgsql.CreditStore;
import varta.model.pgsql.FinancialTransaction;
import varta.repository.pgsql.CreditCardRepository;
import varta.repository.pgsql.CreditStoreRepository;
import varta.repository.pgsql.FinancialTransactionRepository;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class FinancialTransactionProcessor implements ItemProcessor<RawFinancialTransaction, FinancialTransaction> {

    private final LoadingCache<String, CreditStore> storeProxyCache;
    private final LoadingCache<String, CreditCard> cardProxyCache;
    private final EntityManager entityManager;


    public FinancialTransactionProcessor(EntityManager entityManager,
                                         CreditStoreRepository creditStoreRepository,
                                         CreditCardRepository creditCardRepository) {
        this.entityManager = entityManager;

        this.cardProxyCache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(externalId -> {
                    CreditCard card = creditCardRepository.findByExternalCardId(externalId);
                    if (card == null) {
                        log.error("Could not find credit card with external id {}", externalId);
                        return null;
                    }
                    log.info("Found credit card with external id {} and internal id {}", externalId, card.getInternalCardId() );
                    return entityManager.getReference(CreditCard.class, card.getInternalCardId());
                });

        this.storeProxyCache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(merchantExternalId -> {
                    CreditStore creditStore = creditStoreRepository.findByStoreExternalId(merchantExternalId);
                    if (creditStore == null) {
                        log.error("Could not find credit store with external id {}", merchantExternalId);
                        return null;
                    }
                    log.info("Found credit store with external id: {} and internal id: {}", merchantExternalId, creditStore.getStoreInternalId());
                    return entityManager.getReference(CreditStore.class, creditStore.getStoreInternalId());
                });
    }

    @Override
    public FinancialTransaction process(RawFinancialTransaction raw) {
        // check validity
        if (raw == null || raw.getJoinedCardExternalId() == null) {
            assert raw != null;
            log.error("Invalid card parsing error - {}", raw.toString());
            return null;
        }

        var financialTransaction = new FinancialTransaction(raw);
        CreditCard cardProxy = cardProxyCache.get(raw.getJoinedCardExternalId());
        CreditStore storeProxy = storeProxyCache.get(raw.getJoinedMerchantExternalId());

        if (cardProxy == null || storeProxy == null) {
            log.error("No card or store found for transaction with id: {}, cardProxy: {}, storeProxy: {}", raw.getId(), cardProxy, storeProxy);
            return null;
        }

        financialTransaction.setCard(cardProxy);
        financialTransaction.setMerchant(storeProxy);
        log.info("Processed financial transaction with id: {} and card proxy: {}", raw.getId(), cardProxy);
        log.info(financialTransaction.toString());
        return financialTransaction;
    }
}
