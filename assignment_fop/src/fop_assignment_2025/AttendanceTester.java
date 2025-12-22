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

public class AttendanceTester{
    public static void main(String[] args) {

        allAttendanceLog all = new allAttendanceLog();
        Scanner input = new Scanner(System.in);

        // 1. Load employees and attendance logs from file
        // NOTE: Ensure your Employee data is in a file named "Employee.csv"
        all.loadEmployees("employees.csv"); 
        
        // Load existing attendance logs (CRITICAL: Added this missing call)
        all.loadAttendanceLog("attendance.csv"); 

        while (true) {
            System.out.println("\n===== Attendance System =====");
            System.out.println("1. Clock In");
            System.out.println("2. Clock Out");
            System.out.println("3. Save Attendance Log");
            System.out.println("4. Exit");
            System.out.print("Choose option: ");
            int choice = input.nextInt();
            input.nextLine(); // clear buffer

            switch (choice) {
                case 1:
                    System.out.print("Enter Employee ID: ");
                    String idIn = input.nextLine();
                    all.ClockIn(idIn);
                    break;

                case 2:
                    System.out.print("Enter Employee ID: ");
                    String idOut = input.nextLine();
                    all.ClockOut(idOut);
                    break;

                case 3:
                    // Saves the current state of logs (now uses consistent constant name)
                    all.SaveAttendanceLog("attendance.csv"); 
                    break;

                case 4:
                    System.out.println("Goodbye!");
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}

