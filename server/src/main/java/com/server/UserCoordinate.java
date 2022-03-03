package com.server;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class UserCoordinate {
    private String nick;
    private String latitude;
    private String longitude;
    private ZonedDateTime timestamp;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd'T'HH:mm:ss.SSSX"
        );

    public UserCoordinate(
        String nick, String latitude, String longitude, String timestampString
    ) throws DateTimeParseException {
        this.nick = nick;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = parseTime(timestampString);
    }

    private ZonedDateTime parseTime(String timestampString) throws DateTimeParseException {
        return ZonedDateTime.from(formatter.parse(timestampString));
    }

    public String getNick() {
        return nick;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public String getTimestampString() {
        return formatter.format(timestamp);
    }
}
