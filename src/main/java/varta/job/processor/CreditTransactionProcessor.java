package varta.job.processor;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import varta.model.mysql.RawTransaction;
import varta.model.pgsql.CreditCard;
import varta.model.pgsql.CreditStore;
import varta.model.pgsql.CreditTransaction;
import varta.repository.pgsql.CreditCardRepository;
import varta.repository.pgsql.CreditStoreRepository;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CreditTransactionProcessor implements ItemProcessor<RawTransaction, CreditTransaction> {

    private final EntityManager entityManager;
    private final CreditCardRepository creditCardRepository;
    private final CreditStoreRepository creditStoreRepository;
    private LoadingCache<String, CreditCard> cardCache;
    private LoadingCache<String, CreditStore> merchantCache;

    public CreditTransactionProcessor(EntityManager entityManager, CreditCardRepository creditCardRepository,
                                      CreditStoreRepository creditStoreRepository) {
        this.entityManager = entityManager;
        this.creditCardRepository = creditCardRepository;
        this.creditStoreRepository = creditStoreRepository;

        this.cardCache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(externalId -> {
                    CreditCard card = creditCardRepository.findByExternalCardId(externalId);
                    if (card == null) {
                        log.error("Card with externalId {} not found", externalId);
                        return null;
                    }
                    log.info("Found user: internalId - {}, externalId - {}", card.getInternalCardId(), externalId);

                    // 2. Create a lightweight Proxy (no extra DB hit)
                    return entityManager.getReference(CreditCard.class, card.getInternalCardId());
                });

        this.merchantCache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(externalId -> {
                    CreditStore merchant = creditStoreRepository.findByStoreExternalId(externalId);
                    if (merchant == null) {
                        log.error("Merchant with externalId {} not found", externalId);
                        return null;
                    }
                    log.info("Merchant user: internalId - {}, externalId - {}", merchant.getStoreInternalId(), externalId);

                    return entityManager.getReference(CreditStore.class, merchant.getStoreInternalId());
                });
    }

    @Override
    public CreditTransaction process(RawTransaction raw) throws Exception {
        if (raw.getSourceCardIdentifier() == null) {
            throw new Error("No source card id found for raw credit transaction: " + raw.toString());
        }
        if (raw.getDestinationCardIdentifier() == null) {
            throw new Error("No destination card id found for raw credit transaction: " + raw.toString());
        }

        CreditTransaction transaction = new CreditTransaction(raw);

        transaction.setSourceCard(cardCache.get(raw.getJoinedSourceCardExternalId()));
        transaction.setDestinationCard(cardCache.get(raw.getJoinedSourceCardExternalId()));
        log.info("For transaction {} source and destination card was set", transaction.getSystemTraceId());

        if (raw.getJoinedMerchantExternalId() != null) {
            transaction.setMerchantAcquirer(merchantCache.get(raw.getJoinedMerchantExternalId()));
            log.info("For transaction {} merchant acquirer was set", transaction.getSystemTraceId());
        }

        log.info("Transaction: {} \n Raw transaction: {} ", transaction.toString(), raw.toString());
        return transaction;
    }

}