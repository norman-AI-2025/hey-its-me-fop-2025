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

public class Employee {
    
    // 1. Attributes
    //Using private to protect data security
    // ID, Name, Role, Password [cite: 1100]
    private String employeeID;
    private String name;
    private String role;    
    private String password; //login password
    private String outletCode; //Which store they belong to

    // 2. Constructor
    // Create an Employee object reading from the CSV file.
    // Create new employee
    public Employee(String employeeID, String name, String role, String password, String outletCode) {
        this.employeeID = employeeID;
        this.name = name;
        this.role = role;
        this.password = password;
        this.outletCode = outletCode;
    }


    // Overload: for older employees.csv that has only 4 columns (no outletCode)
    public Employee(String employeeID, String name, String role, String password) {
        this(employeeID, name, role, password, "N/A");
    }

    // 3. Getter Methods
    //access the private data from other classes 

    public String getEmployeeID() {
        return employeeID;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }

    public String getOutletCode() {
        return outletCode;
    }

    // 4. toString 
    // Useful for checking data during testings
    public String toString() {
        return "Employee{" + "ID=" + employeeID + ", Name=" + name + ", Role=" + role + '}';
    }
}

