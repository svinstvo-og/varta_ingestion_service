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
                    Optional<Long> internalId = userRepo.findInternalUserIdByExternalUserId(externalId);
                    if (internalId.isEmpty()) return null;

                    // 2. Create a lightweight Proxy (no extra DB hit)
                    return entityManager.getReference(CreditUser.class, internalId.get());
                });
    }

    @Override
    public CreditCard process(RawCreditCard raw) throws JsonProcessingException {
        CreditCard card = new CreditCard(raw);

        if (raw.getOwnerStableId() != null) {
            CreditUser userProxy = userProxyCache.get(raw.getOwnerStableId());

            if (userProxy != null) {
                card.setCreditUser(userProxy);
            } else {
                log.error("Orphan Card found! Stable User ID not found in Postgres: {}", raw.getOwnerStableId());
                return null; // Skip this card
            }
        }
        log.error("No credit user reference found for card {}, skipping it", card);
        return null;
    }
}
