package varta.service.messaging;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;
import varta.model.pgsql.CreditTransaction;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditTransactionPublisherTest {

    @Mock
    private KafkaTemplate<String, CreditTransaction> kafkaTemplate;

    private CreditTransactionPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new CreditTransactionPublisher(kafkaTemplate);
        ReflectionTestUtils.setField(publisher, "topicName", "test-topic");
    }

    @Test
    @DisplayName("should publish credit transaction successfully")
    void publish_success() {
        CreditTransaction payload = new CreditTransaction();
        SendResult<String, CreditTransaction> sendResult = mock(SendResult.class);
        RecordMetadata metadata = mock(RecordMetadata.class);
        when(sendResult.getRecordMetadata()).thenReturn(metadata);

        when(kafkaTemplate.send(eq("test-topic"), eq(payload)))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        publisher.publish(payload);

        verify(kafkaTemplate).send(eq("test-topic"), eq(payload));
    }

    @Test
    @DisplayName("should handle publish failure")
    void publish_failure() {
        CreditTransaction payload = new CreditTransaction();
        CompletableFuture<SendResult<String, CreditTransaction>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka error"));

        when(kafkaTemplate.send(eq("test-topic"), eq(payload)))
                .thenReturn(future);

        publisher.publish(payload);

        verify(kafkaTemplate).send(eq("test-topic"), eq(payload));
    }
}

