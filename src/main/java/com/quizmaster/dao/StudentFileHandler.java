
package com.quizmaster.dao;

import com.quizmaster.model.Student;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class StudentFileHandler {

    private static final String FILE =
            "data/students.txt";

    public StudentFileHandler() {

        try {
            Files.createDirectories(
                Paths.get("data")
            );

            File file = new File(FILE);

            if (!file.exists()) {
                file.createNewFile();
            }

        } catch (IOException e) {
            throw new RuntimeException(
                "Unable to initialize student database", e
            );
        }
    }

    public String generateStudentId() {

        int count = 0;

        try (BufferedReader br =
                 new BufferedReader(new FileReader(FILE))) {

            while (br.readLine() != null) {
                count++;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return String.format("STU%03d", count + 1);
    }

    public boolean registerStudent(Student student) {

    if (findByEmail(student.getEmail()) != null) {
        return false;
    }

    if (findByStudentId(student.getStudentId()) != null) {
        return false;
    }

    try (BufferedWriter bw =
             new BufferedWriter(new FileWriter(FILE, true))) {

        bw.write(
            student.getStudentId() + "," +
            student.getName() + "," +
            student.getEmail() + "," +
            student.getDepartment() + "," +
            student.getSection() + "," +
            student.getPassword()
        );

        bw.newLine();

        return true;

    } catch (IOException e) {

        throw new RuntimeException(
            "Unable to save student", e
        );
    }
}

    public Student findByEmail(String email) {

        try (BufferedReader br =
                 new BufferedReader(new FileReader(FILE))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",", -1);

                if (data.length == 6 &&
                    data[2].equalsIgnoreCase(email.trim())) {

                    return new Student(
                        data[0], data[1], data[2],
                        data[3], data[4], data[5]
                    );
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
    public Student findByStudentId(String studentId) {

    try (BufferedReader br =
             new BufferedReader(new FileReader(FILE))) {

        String line;

        while ((line = br.readLine()) != null) {

            String[] data = line.split(",", -1);

            if (data.length == 6 &&
                data[0].equalsIgnoreCase(studentId.trim())) {

                return new Student(
                    data[0], data[1], data[2],
                    data[3], data[4], data[5]
                );
            }
        }

    } catch (IOException e) {
        throw new RuntimeException(e);
    }

    return null;
}

    public Student login(String studentId, String password) {

        try (BufferedReader br =
                 new BufferedReader(new FileReader(FILE))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",", -1);

                if (data.length == 6 &&
                    data[0].equalsIgnoreCase(studentId.trim()) &&
                    data[5].equals(password)) {

                    return new Student(
                        data[0], data[1], data[2],
                        data[3], data[4], data[5]
                    );
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
