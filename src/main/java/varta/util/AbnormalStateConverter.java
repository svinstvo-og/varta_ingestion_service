package varta.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import varta.dto.AbnormalState;

import java.util.*;

@Slf4j
public class AbnormalStateConverter {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final List<AbnormalState> abnormalStates = Arrays.stream(AbnormalState.values()).toList();

    public static AbnormalState convertAbnormalState(String abnormalString) throws JsonProcessingException {
        AbnormalState abnormalState = null;

        List<Integer> values = mapper.readValue(abnormalString, Map.class).values().stream().toList();
        for (int i=0; i<values.size(); i++) {
            if (values.get(i) == 1) {
                abnormalState = Arrays.stream(AbnormalState.values()).toList().get(i);
                break;
            }
        }

//        log.info("AbnormalState: {}", abnormalState);
        return abnormalState;
    }

    public static Integer getAbnormalStateId(AbnormalState abnormalState) {
        if (abnormalState == null) return null;
        return abnormalState.ordinal();
    }
}
