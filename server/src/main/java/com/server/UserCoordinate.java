package com.server;

import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

public class UserCoordinate {
    private String nick;
    private double latitude;
    private double longitude;
    private ZonedDateTime timestamp;
    private String description;
    private int id;

    public UserCoordinate(
        String nick, double latitude, double longitude, String timestampString, String description)
            throws DateTimeParseException {
        this.nick = nick;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = TimeTranslation.parseTime(timestampString);
        this.description = description;
    }

    public UserCoordinate() {

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
        return TimeTranslation.convertToDateString(timestamp);
    }

    public long getTimestampAsLong() {
        return TimeTranslation.convertToEpoch(timestamp);
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public void setTimestamp(long epoch) throws DateTimeException {
        this.timestamp = TimeTranslation.convertToZoned(epoch);
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
