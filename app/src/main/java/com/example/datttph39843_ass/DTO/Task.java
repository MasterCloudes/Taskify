package com.example.datttph39843_ass.DTO;

public class Task {
    private int id;
    private String name;
    private String content;
    private int status; // 0: new, 1: in progress, 2: completed, -1: trash
    private String startDate;
    private String endDate;

    public Task() {
    }

    public Task(int id, String name, String content, int status, String startDate, String endDate) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
