package varta.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConverterTest {

    @Test
    @DisplayName("should convert days to minutes")
    void daysToMinutes_convertsCorrectly() {
        assertThat(Converter.daysToMinutes(1)).isEqualTo(1440);
        assertThat(Converter.daysToMinutes(2)).isEqualTo(2880);
        assertThat(Converter.daysToMinutes(0)).isEqualTo(0);
    }
}

