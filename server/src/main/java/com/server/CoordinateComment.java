package com.server;

import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

public class CoordinateComment {
    private int coordinateId;
    private String commentBody;
    private ZonedDateTime timestamp;

    public CoordinateComment() {

    }

    public CoordinateComment(int coordinateId, String commentBody, String timestampString) 
            throws DateTimeParseException {
        
        this.coordinateId = coordinateId;
        this.commentBody = commentBody;
        this.timestamp = TimeTranslation.parseTime(timestampString);
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

    public String getTimestampString() throws DateTimeException {
        return TimeTranslation.convertToDateString(timestamp);
    }

    public long getTimestampAsLong() {
        return TimeTranslation.convertToEpoch(timestamp);
    }

    public void setTimestamp(long epoch) throws DateTimeException {
        this.timestamp = TimeTranslation.convertToZoned(epoch);
    }
}
