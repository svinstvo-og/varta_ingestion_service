package varta.service.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import varta.model.pgsql.CreditTransaction;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreditTransactionPublisher {
    private final KafkaTemplate<String, CreditTransaction> kafkaTemplate;

    @Value("credit-transaction")
    private String topicName;

    public void publish(CreditTransaction payload) {
        kafkaTemplate
                .send(topicName, payload)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish Credit Transaction", ex);
                        return;
                    }
                    log.debug("Published Credit Transaction to topic {} partition {} offset {}", topicName, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                });
    }
}