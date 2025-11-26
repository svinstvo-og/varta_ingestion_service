package varta.job.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import varta.model.mysql.RawCreditCard;
import varta.model.pgsql.CreditCard;
import varta.model.pgsql.CreditUser;
import varta.repository.pgsql.CreditUserRepository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CreditCardProcessor implements ItemProcessor<RawCreditCard, CreditCard> {

    private final LoadingCache<String, CreditUser> userProxyCache;
    private final EntityManager entityManager;

    public CreditCardProcessor(CreditUserRepository userRepo, EntityManager entityManager) {
        this.entityManager = entityManager;

        this.userProxyCache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(externalId -> {
                    CreditUser user = userRepo.findUserByExternalUserId(externalId);
                    if (user == null) {
                        log.error("User with externalId {} not found", externalId);
                        return null;
                    }
                    log.info("Found user: internalId - {}, externalId - {}", user.getInternalUserId(), externalId);

                    // 2. Create a lightweight Proxy (no extra DB hit)
                    return entityManager.getReference(CreditUser.class, user.getInternalUserId());
                });
    }

    @Override
    public CreditCard process(RawCreditCard raw) throws JsonProcessingException {
        if (raw.getOwnerStableId() == null) {
            log.error("No credit user reference found for raw card {}, skipping it", raw.toString());
            return null;
        }

        CreditCard card = new CreditCard(raw);
        CreditUser userProxy = userProxyCache.get(raw.getOwnerStableId());

        if (userProxy != null) {
            card.setCreditUser(userProxy);
            return card;
        }

        log.error("Orphan Card found! Stable User ID not found in Postgres: {}", raw.getOwnerStableId());
        return null; // Skip this card
    }
}
