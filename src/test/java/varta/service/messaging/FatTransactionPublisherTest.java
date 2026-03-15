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
import varta.dto.FatTransactionDto;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FatTransactionPublisherTest {

    @Mock
    private KafkaTemplate<String, FatTransactionDto> kafkaTemplate;

    private FatTransactionPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new FatTransactionPublisher(kafkaTemplate);
        ReflectionTestUtils.setField(publisher, "topicName", "test-topic");
    }

    @Test
    @DisplayName("should publish fat transaction successfully")
    void publish_success() {
        FatTransactionDto payload = new FatTransactionDto();
        SendResult<String, FatTransactionDto> sendResult = mock(SendResult.class);
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
        FatTransactionDto payload = new FatTransactionDto();
        CompletableFuture<SendResult<String, FatTransactionDto>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka error"));

        when(kafkaTemplate.send(eq("test-topic"), eq(payload)))
                .thenReturn(future);

        // Should not throw exception, but log error
        publisher.publish(payload);

        verify(kafkaTemplate).send(eq("test-topic"), eq(payload));
    }
}

