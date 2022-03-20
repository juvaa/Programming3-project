package com.server;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
* An utility class for time format conversion operations
*/
public abstract class TimeTranslation {

    private TimeTranslation() {

    }
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd'T'HH:mm:ss.SSSX"
        );
    
    public static ZonedDateTime parseTime(String timestampString) throws DateTimeParseException {
        return ZonedDateTime.from(formatter.parse(timestampString));
    }

    public static ZonedDateTime convertToZoned(long epoch) throws DateTimeException {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneOffset.UTC);
    }

    public static long convertToEpoch(ZonedDateTime dateTime) {
        return dateTime.toInstant().toEpochMilli();
    }

    public static String convertToDateString(ZonedDateTime dateTime) throws DateTimeException {
        return formatter.format(dateTime);
    }
}
