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

public class allAttendanceLog {
    private List<Employee> employees = new ArrayList<>();
    private List<AttendanceLog> attendance = new ArrayList<>();
    
    private static final String ATTENDANCE_FILE_NAME = "attendance.csv";
    private static final String EMPLOYEE_FILE_NAME = "employees.csv";

    // Helper to find the active log
    private AttendanceLog findTodayActiveLog(String EmployeeID, LocalDate date) {
        for (AttendanceLog log : attendance) {
            if (log.getEmployeeID().equals(EmployeeID) && log.getdate().equals(date) && log.getClockOut() == null) {
                return log;
            }
        }
        return null;
    }

    // Load employees from file
    public void loadEmployees(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                // FIX: Employee constructor requires 5 parts: ID, Name, Role, Password, OutletCode
                if (parts.length >= 4) {
                    // Support both 4-col and 5-col formats
                    if (parts.length >= 5) {
                        employees.add(new Employee(parts[0], parts[1], parts[2], parts[3], parts[4]));
                    } else {
                        employees.add(new Employee(parts[0], parts[1], parts[2], parts[3]));
                    }
                }
            }
            System.out.println("Employees loaded: " + employees.size());
        } catch(IOException e) {
            System.out.println("Error loading employees: " + e.getMessage());
        } 
    }

    // Load attendance logs from file
    public void loadAttendanceLog(String filename) {
        // Support different filenames (your latest file is attendance.csv)
        String[] candidates = { filename, "attendance.csv", "attendance.csv" };
        boolean loadedAny = false;

        for (int i = 0; i < candidates.length; i++) {
            String file = candidates[i];
            String resolved = FilePathHelper.resolveReadPath(file);
            File f = new File(resolved);
            if (!f.exists()) {
                continue;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(resolved))) {
                String line = br.readLine(); // header
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");

                    // Expected (your file): EmployeeID,Date,ClockIn,ClockOut,TotalHoursWorked
                    // We only need ID + Date + ClockIn + ClockOut
                    if (parts.length >= 3) {
                        try {
                            String empId = parts[0].trim();
                            LocalDate date = parseDateFlexible(parts[1].trim());

                            AttendanceLog log = new AttendanceLog(empId, date);

                            // Clock in
                            LocalTime in = parseTimeFlexible(parts[2].trim());
                            log.setClockIn(in);

                            // Clock out (optional)
                            if (parts.length >= 4 && parts[3] != null && !parts[3].trim().isEmpty()) {
                                LocalTime out = parseTimeFlexible(parts[3].trim());
                                log.setClockOut(out);
                            }

                            attendance.add(log);
                        } catch (Exception ex) {
                            // Skip any bad line instead of crashing the whole load
                            System.out.println("Error parsing attendance log line: " + line);
                        }
                    }
                }

                loadedAny = true;
            } catch (IOException e) {
                System.out.println("Error loading attendance log: " + e.getMessage());
            }
        }

        if (loadedAny) {
            System.out.println("Attendance logs loaded: " + attendance.size());
        } else {
            System.out.println("Attendance logs loaded: 0 (attendance file not found)");
        }
    }

    // Parse times like "15:30", "15:30:00", or "03:55 pm"
    private LocalTime parseTimeFlexible(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;

        // 1) ISO formats (HH:mm or HH:mm:ss)
        try { return LocalTime.parse(t); } catch (Exception ignore) {}

        // 2) 12-hour with AM/PM (make AM/PM uppercase to be safe)
        String upper = t.toUpperCase();
        try { return LocalTime.parse(upper, DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)); } catch (Exception ignore) {}
        try { return LocalTime.parse(upper, DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)); } catch (Exception ignore) {}
        try { return LocalTime.parse(upper, DateTimeFormatter.ofPattern("hh:mm:ss a", Locale.ENGLISH)); } catch (Exception ignore) {}
        try { return LocalTime.parse(upper, DateTimeFormatter.ofPattern("h:mm:ss a", Locale.ENGLISH)); } catch (Exception ignore) {}

        // If still cannot parse, throw so caller can skip the line
        throw new IllegalArgumentException("Bad time: " + s);
    }

    // Parse dates like "2025-12-22" or "22/12/2025"
    private LocalDate parseDateFlexible(String s) {
        if (s == null) return null;
        String x = s.trim();
        if (x.isEmpty()) return null;

        DateTimeFormatter[] fmts = new DateTimeFormatter[] {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("d/M/yyyy")
        };
        for (DateTimeFormatter f : fmts) {
            try { return LocalDate.parse(x, f); } catch (Exception ignored) {}
        }
        // last try: ISO
        try { return LocalDate.parse(x); } catch (Exception ignored) {}
        throw new IllegalArgumentException("Unsupported date format: " + x);
    }

    // Clock in
    public void ClockIn(String EmployeeID) {
        Employee emp = findEmployeeByID(EmployeeID);
        if (emp == null) {
            System.out.println("Employee not found!");
            return;
        }

        LocalDate today = ZonedDateTime.now(ZoneId.of("Asia/Kuala_Lumpur")).toLocalDate();
        if (findTodayActiveLog(EmployeeID, today) != null) {
            System.out.println("Error: Employee " + emp.getName() + " is already clocked in today!");
            return;
        }

        LocalTime now = ZonedDateTime.now(ZoneId.of("Asia/Kuala_Lumpur")).toLocalTime().withSecond(0).withNano(0);
        AttendanceLog log = new AttendanceLog(EmployeeID, today);
        log.setClockIn(now);
        attendance.add(log);

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");
        String formattedTime = now.format(timeFormat).toLowerCase();

        System.out.println("\n=== Attendance Clock In ===");
        System.out.println("Employee ID: " + emp.getEmployeeID());
        System.out.println("Name: " + emp.getName());
        // FIX: Changed getOutlet() to getOutletCode() to match Employee.java
        System.out.println("Outlet: " + emp.getOutletCode()); 
        System.out.println();
        System.out.println("Clock In Successful!");
        System.out.println("Date: " + today.format(dateFormat));
        System.out.println("Time: " + formattedTime);
    }

    //Clock Out
    public void ClockOut(String EmployeeID) {
        Employee emp = findEmployeeByID(EmployeeID);
        if (emp == null) {
            System.out.println("Employee not found!");
            return;
        }

        LocalDate today = ZonedDateTime.now(ZoneId.of("Asia/Kuala_Lumpur")).toLocalDate();
        AttendanceLog log = findTodayActiveLog(EmployeeID, today);
        
        if (log != null) { 
            LocalTime now = ZonedDateTime.now(ZoneId.of("Asia/Kuala_Lumpur")).toLocalTime().withSecond(0).withNano(0);
            log.setClockOut(now); 

            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");
            String formattedTime = now.format(timeFormat).toLowerCase();

            System.out.println("\n=== Attendance Clock Out ===");
            System.out.println("Employee ID: " + emp.getEmployeeID());
            System.out.println("Name: " + emp.getName());
            // FIX: Changed getOutlet() to getOutletCode() to match Employee.java
            System.out.println("Outlet: " + emp.getOutletCode());
            System.out.println();
            System.out.println("Clock Out Successful!");
            System.out.println("Date: " + today.format(dateFormat));
            System.out.println("Time: " + formattedTime);
            System.out.printf("Total Hours Worked:%.2f hours%n", log.gettotal_hours_worked()); 

            SaveAttendanceLog(ATTENDANCE_FILE_NAME);
            return;
        }
        
        System.out.println("No active clock-in record found for " + EmployeeID + " today. Please Clock In first."); 
    }

    public void SaveAttendanceLog(String filename) {
        try (PrintWriter pw = new PrintWriter (new FileWriter(FilePathHelper.resolveWritePath(filename)))) {
            pw.println("EmployeeID,Date,ClockIn,ClockOut,TotalHoursWorked");
            for (AttendanceLog log : attendance) {
                String clockOutStr = (log.getClockOut() != null) ? log.getClockOut().toString() : "";
                double totalHours = (log.getClockOut() != null) ? log.gettotal_hours_worked() : 0.0;

                DateTimeFormatter tf = DateTimeFormatter.ofPattern("hh:mm a");
                String inStr = (log.getClockIn() != null) ? log.getClockIn().format(tf).toLowerCase() : "";
                String outStr = (log.getClockOut() != null) ? log.getClockOut().format(tf).toLowerCase() : "";
                pw.printf("%s,%s,%s,%s,%.2f%n",
                log.getEmployeeID(),
                log.getdate().toString(),
                inStr,
                outStr,
                totalHours);
 
            }
            System.out.println("Attendance saved to " + filename);
            } catch (IOException e){
            System.out.println("Error saving attendance: " + e.getMessage());
        }
    }
    
    private Employee findEmployeeByID(String EmployeeID) {
        for (Employee emp : employees) {
            if (emp.getEmployeeID().equals(EmployeeID)) {
                return emp;
            }
        }
        return null;
    }
}

