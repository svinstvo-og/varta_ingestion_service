package varta.service.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import varta.dto.FatTransactionDto;

@Component
@RequiredArgsConstructor
@Slf4j
public class FatTransactionPublisher {

    private final KafkaTemplate<String, FatTransactionDto> kafkaTemplate;

    @Value("${spring.kafka.topic.fat-transactions}")
    private String topicName;

    public void publish(FatTransactionDto payload) {
        kafkaTemplate
                .send(topicName, payload)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish FatTransactionDto", ex);
                        return;
                    }
                    log.debug("Published FatTransactionDto to topic {} partition {} offset {}", topicName, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                });
    }
}

