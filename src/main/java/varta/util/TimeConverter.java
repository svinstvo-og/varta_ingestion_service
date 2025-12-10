package varta.util;

import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
public class TimeConverter {

    private final static DateTimeFormatter DATE_FMT = DateTimeFormatter.BASIC_ISO_DATE; // YYYYMMDD
    private final static DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HHmmss"); // No colons

    public static LocalDateTime convertTimestamp(String rawTimestamp) {
        LocalDate datePart = LocalDate.parse(rawTimestamp, DATE_FMT);
        String timeString = rawTimestamp.substring(4);

        return  LocalDateTime.of(
                datePart,
                LocalTime.parse(timeString, TIME_FMT)
        );
    }

}
