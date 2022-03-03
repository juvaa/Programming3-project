package com.server;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneOffset;
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

    public UserCoordinate() {

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

    public String getTimestampString() throws DateTimeException {
        return formatter.format(timestamp);
    }

    public long getTimestampAsLong() {
        return timestamp.toInstant().toEpochMilli();
    }

    public void setTimestamp(long epoch) throws DateTimeException {
        this.timestamp = ZonedDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneOffset.UTC);
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
