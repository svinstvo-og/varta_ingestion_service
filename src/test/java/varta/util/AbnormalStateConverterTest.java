package varta.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import varta.dto.AbnormalState;

import static org.assertj.core.api.Assertions.assertThat;

class AbnormalStateConverterTest {

    @Test
    @DisplayName("should convert abnormal state from json string")
    void convertAbnormalState_convertsCorrectly() throws JsonProcessingException {
        String json = "{\"NORMAL_TRANSFER\": 0, \"CREDIT_CARD_FRAUD\": 1, \"FAKE_REGISTRATION\": 0" +
                      ", \"SCALPER_MARKETING\": 0, \"GAMBLING_VIOLATION\": 0, \"MERCHANT_VIOLATION\": 0}";

        AbnormalState result = AbnormalStateConverter.convertAbnormalState(json);

        assertThat(result).isEqualTo(AbnormalState.CREDIT_CARD_FRAUD);
    }

    @Test
    @DisplayName("should return null if no state is 1")
    void convertAbnormalState_returnsNullIfNoneActive() throws JsonProcessingException {
        String json = "{\"NORMAL_TRANSFER\": 0, \"CREDIT_CARD_FRAUD\": 0, \"FAKE_REGISTRATION\": 0" +
                      ", \"SCALPER_MARKETING\": 0, \"GAMBLING_VIOLATION\": 0, \"MERCHANT_VIOLATION\": 0}";

        AbnormalState result = AbnormalStateConverter.convertAbnormalState(json);

        assertThat(result).isNull();
    }
}

