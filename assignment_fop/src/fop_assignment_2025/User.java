package fop_assignment_2025;

import java.io.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class User {
    private String username; // Acts as Employee ID (e.g., C6001)
    private String password;
    private String role;     // "manager" or "employee"
    private String ID;     // Real Name (e.g., Tan Guan Han)

    public User(String ID, String username, String role, String password) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.ID = ID;
    }

    // ===== Getters =====
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getID() { return ID; }

    // ===== Setters =====
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setID(String ID) { this.ID = ID; }
}

