package com.tmse.pizza.models;

import java.util.Date;

/**
 * Logs class for system activity logs
 */
public class Logs {
    private String logID;
    private String action;
    private Date timestamp;
    private String userID;

    public Logs(String logID, String action, Date timestamp, String userID) {
        this.logID = logID;
        this.action = action;
        this.timestamp = timestamp;
        this.userID = userID;
    }

    public String getLogID() { return logID; }
    public String getAction() { return action; }
    public Date getTimestamp() { return timestamp; }
    public String getUserID() { return userID; }
}

