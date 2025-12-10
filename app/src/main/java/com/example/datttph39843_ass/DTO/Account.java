package com.example.datttph39843_ass.DTO;

public class Account {
    private int id;
    private String username;
    private String email;
    private String password;
    private String fullname;
    private String msv;
    private String lop;

    public Account() {
    }

    public Account(int id, String username, String email, String password, String fullname, String msv, String lop) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.msv = msv;
        this.lop = lop;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getMsv() {
        return msv;
    }

    public void setMsv(String msv) {
        this.msv = msv;
    }

    public String getLop() {
        return lop;
    }

    public void setLop(String lop) {
        this.lop = lop;
    }
}
