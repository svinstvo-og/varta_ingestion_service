package varta.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TimeConverterTest {

    @Test
    @DisplayName("should convert timestamp correctly")
    void convertTimestamp_convertsCorrectly() {
        String rawDate = "20240101";
        // Assuming rawTime has 4 char prefix + HHmmss
        // e.g., "XXXX123045" -> "123045" -> 12:30:45
        String rawTime = "XXXX123045";

        LocalDateTime result = TimeConverter.convertTimestamp(rawDate, rawTime);

        assertThat(result.getYear()).isEqualTo(2024);
        assertThat(result.getMonthValue()).isEqualTo(1);
        assertThat(result.getDayOfMonth()).isEqualTo(1);
        assertThat(result.getHour()).isEqualTo(12);
        assertThat(result.getMinute()).isEqualTo(30);
        assertThat(result.getSecond()).isEqualTo(45);
    }
}

