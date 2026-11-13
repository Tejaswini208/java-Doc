package com.quizmaster.dao;

import com.quizmaster.model.Admin;
import java.io.*;
import java.nio.file.*;

public class AdminFileHandler {
    private static final String FILE = "data/admins.txt";

    public AdminFileHandler() {
        try {
            Files.createDirectories(Paths.get("data"));
            File file = new File(FILE);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error initializing storage", e);
        }
    }

    public boolean registerAdmin(Admin admin) {
        if (findById(admin.getAdminId()) != null || findByEmail(admin.getEmail()) != null) {
            return false;
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE, true))) {
            bw.write(admin.getAdminId() + "," + admin.getName() + "," + admin.getEmail() + "," 
                     + admin.getDepartment() + "," + admin.getSubject() + "," + admin.getPassword());
            bw.newLine();
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Admin registration failed", e);
        }
    }

    public Admin findById(String id) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length == 6 && data[0].equalsIgnoreCase(id.trim())) {
                    return new Admin(data[0], data[1], data[2], data[3], data[4], data[5]);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Admin findByEmail(String email) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length == 6 && data[2].equalsIgnoreCase(email.trim())) {
                    return new Admin(data[0], data[1], data[2], data[3], data[4], data[5]);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Admin login(String id, String password) {
        Admin admin = findById(id);
        if (admin != null && admin.getPassword().equals(password)) {
            return admin;
        }
        return null;
    }
}