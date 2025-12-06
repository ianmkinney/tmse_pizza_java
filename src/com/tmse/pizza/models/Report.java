package com.tmse.pizza.models;

/**
 * Report class for system reports
 */
public class Report {
    private String reportID;
    private String reportType;
    private String content;

    public Report(String reportID, String reportType, String content) {
        this.reportID = reportID;
        this.reportType = reportType;
        this.content = content;
    }

    public String getReportID() { return reportID; }
    public String getReportType() { return reportType; }
    public String getContent() { return content; }
}

