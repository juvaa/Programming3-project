package com.server;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CoordinateComment {
    private int coordinateId;
    private String commentBody;
    private ZonedDateTime timestamp;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd'T'HH:mm:ss.SSSX"
        );

    public CoordinateComment() {

    }

    public CoordinateComment(String commentBody, String timestampString) throws DateTimeParseException { 
        this.commentBody = commentBody;
        this.timestamp = parseTime(timestampString);
    }

    public int getCoordinateId() {
        return coordinateId;
    }

    public void setCoordinateId(int coordinateId) {
        this.coordinateId = coordinateId;
    }

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    private ZonedDateTime parseTime(String timestampString) throws DateTimeParseException {
        return ZonedDateTime.from(formatter.parse(timestampString));
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
}
