package com.server;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class UserCoordinate {
    private String nick;
    private double latitude;
    private double longitude;
    private ZonedDateTime timestamp;
    private String description;
    private int id;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd'T'HH:mm:ss.SSSX"
        );

    public UserCoordinate(
        String nick, double latitude, double longitude, String timestampString, String description
    ) throws DateTimeParseException {
        this.nick = nick;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = parseTime(timestampString);
        this.description = description;
    }

    public UserCoordinate() {

    }

    private ZonedDateTime parseTime(String timestampString) throws DateTimeParseException {
        return ZonedDateTime.from(formatter.parse(timestampString));
    }

    public String getNick() {
        return nick;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
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

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public void setTimestamp(long epoch) throws DateTimeException {
        this.timestamp = ZonedDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneOffset.UTC);
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }
}
