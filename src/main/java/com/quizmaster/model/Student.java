
package com.quizmaster.model;

public class Student {

    private String studentId;
    private String name;
    private String email;
    private String department;
    private String section;
    private String password;

    public Student(String studentId, String name,
                   String email, String department,
                   String section, String password) {

        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.department = department;
        this.section = section;
        this.password = password;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDepartment() {
        return department;
    }

    public String getSection() {
        return section;
    }

    public String getPassword() {
        return password;
    }
}
