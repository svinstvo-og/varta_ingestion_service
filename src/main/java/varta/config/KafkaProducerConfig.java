package varta.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import varta.dto.FatTransactionDto;
import varta.model.pgsql.CreditTransaction;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> producerConfigs() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return configProps;
    }

    @Bean
    public ProducerFactory<String, FatTransactionDto> fatTransactionProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ProducerFactory<String, CreditTransaction> creditTransactionProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, FatTransactionDto> fatTransactionKafkaTemplate(
            ProducerFactory<String, FatTransactionDto> fatTransactionProducerFactory) {
        return new KafkaTemplate<>(fatTransactionProducerFactory);
    }

    @Bean
    public KafkaTemplate<String, CreditTransaction> creditTransactionKafkaTemplate(
            ProducerFactory<String, CreditTransaction> creditTransactionProducerFactory) {
        return new KafkaTemplate<>(creditTransactionProducerFactory);
    }
}
