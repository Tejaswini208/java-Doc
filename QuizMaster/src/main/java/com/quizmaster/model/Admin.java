package com.quizmaster.model;

public class Admin {
    private String adminId; // College Register Number
    private String name;
    private String email;
    private String department;
    private String subject;
    private String password;

    public Admin(String adminId, String name, String email, String department, String subject, String password) {
        this.adminId = adminId;
        this.name = name;
        this.email = email;
        this.department = department;
        this.subject = subject;
        this.password = password;
    }

    public String getAdminId() { return adminId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
    public String getSubject() { return subject; }
    public String getPassword() { return password; }
}