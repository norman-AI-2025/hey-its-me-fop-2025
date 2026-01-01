package extrafeatures;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class allAttendanceLog {
    private List<Employee> employees = new ArrayList<>();
    private List<AttendanceLog> attendance = new ArrayList<>();
    
    private static final String ATTENDANCE_FILE_NAME = "attendance.csv";

    // Helper to find the active log
    private AttendanceLog findTodayActiveLog(String employeeID, LocalDate date) {
        for (AttendanceLog log : attendance) {
        if (log.getEmployeeID().equals(employeeID) && log.getdate().equals(date.toString()) && log.getClockOut().equals("N/A")) {
            return log;
            }
        }
        return null;
    }

    // Load employees from file
    public void loadEmployees(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine(); // skip header line
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String role = (parts.length > 2) ? parts[2].trim() : "N/A";
                    String password = (parts.length > 3) ? parts[3].trim() : "N/A";
                    String outlet = (parts.length > 4) ? parts[4].trim() : "N/A";
                    employees.add(new Employee(id, name, outlet, password, role));
                    System.out.println("Loaded employee:" + id);
                }
            }
            System.out.println("Employees loaded: " + employees.size());
        } catch(IOException e) {
            System.out.println("Error loading employees: " + e.getMessage());
        } 
    }

    // Load attendance logs from file
  public void loadAttendanceLog(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                AttendanceLog log = new AttendanceLog(parts[0], parts[1]);
                log.setClockIn(parts[2]);

                if (parts.length > 3 && !parts[3].isEmpty()) {
                    log.setClockOut(parts[3]);
                }

                attendance.add(log);
            }
            System.out.println("Attendance logs loaded: " + attendance.size());
        } catch (Exception e) {
            System.out.println("Error loading attendance logs: " + e.getMessage());
        }
    }

    // Clock in
    public void ClockIn(String employeeID) {
        doClockIn(employeeID, false);
    }

    public AttendanceLog clockInGui(String employeeID) {
        return doClockIn(employeeID, true);
    }

    private AttendanceLog doClockIn(String employeeID, boolean quiet) {
        Employee emp = findEmployeeByID(employeeID);
        if (emp == null) {
            if (!quiet) System.out.println("Employee not found!");
            return null;
        }

        LocalDate today = LocalDate.now();
        // Check for double clock-in
        if (findTodayActiveLog(employeeID, today) != null) {
            if (!quiet) System.out.println("Error: Employee " + emp.getName() + " is already clocked in today!");
            return null;
        }

        LocalTime now = LocalTime.now();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");
        String formattedTime = now.format(timeFormat).toLowerCase();

        AttendanceLog log = new AttendanceLog(employeeID, today.toString());
        log.setClockIn(now.format(timeFormat));
        attendance.add(log);

        if (!quiet) {
            System.out.println("\n=== Attendance Clock In ===");
            System.out.println("Employee ID: " + emp.getEmployeeID());
            System.out.println("Name: " + emp.getName());
            System.out.println("Outlet: " + emp.getOutletCode());
            System.out.println();
            System.out.println("Clock In Successful!");
            System.out.println("Date: " + today.format(dateFormat));
            System.out.println("Time: " + formattedTime);
        }
        SaveAttendanceLog(ATTENDANCE_FILE_NAME);
        return log;
    }

    //Clock Out
    public void ClockOut(String employeeID) {
        doClockOut(employeeID, false);
    }

    public AttendanceLog clockOutGui(String employeeID) {
        return doClockOut(employeeID, true);
    }

    private AttendanceLog doClockOut(String employeeID, boolean quiet) {
        Employee emp = findEmployeeByID(employeeID);
        if (emp == null) {
            if (!quiet) System.out.println("Employee not found!");
            return null;
        }

        LocalDate today = LocalDate.now();
        // Find today's active log
        AttendanceLog log = findTodayActiveLog(employeeID, today);
        
        if (log == null) { 
            if (!quiet) System.out.println("No active clock-in record found for " + employeeID + " today. Please Clock In first.");
            return null;
        }

            LocalTime now = LocalTime.now();
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedTime = now.format(timeFormat).toLowerCase();

            log.setClockOut(now.format(timeFormat));

            if (!quiet) {
                System.out.println("\n=== Attendance Clock Out ===");
                System.out.println("Employee ID: " + emp.getEmployeeID());
                System.out.println("Name: " + emp.getName());
                System.out.println("Outlet: " + emp.getOutletCode());
                System.out.println();
                System.out.println("Clock Out Successful!");
                System.out.println("Date: " + today.format(dateFormat));
                System.out.println("Time: " + formattedTime);
                System.out.printf("Total Hours Worked:%.2f hours%n", log.gettotal_hours_worked()); 
            }

            SaveAttendanceLog(ATTENDANCE_FILE_NAME);
            return log;
        }

    //Save attendance log to file
    public void SaveAttendanceLog(String filename) {
        try (PrintWriter pw = new PrintWriter (new FileWriter(filename))) {
            pw.println("EmployeeID,Date,ClockIn,ClockOut,TotalHoursWorked");
            for (AttendanceLog log : attendance) {
                String clockOutStr = (log.getClockOut().equals("N/A") ? "" : log.getClockOut().toString());
                double totalHours = (log.getClockOut().equals("N/A")) ? 0.0 : log.gettotal_hours_worked();

                pw.printf("%s,%s,%s,%s,%.2f%n",
                log.getEmployeeID(),
                log.getdate().toString(), 
                log.getClockIn().toString(),
                clockOutStr, 
                totalHours); 
            }
            System.out.println("Attendance saved to " + filename);
            }catch (IOException e){
            System.out.println("Error saving attendance: " + e.getMessage());
        }
    }
    
    //Find employee by ID 
    private Employee findEmployeeByID(String employeeID) {
        for (Employee emp : employees) {
            if (emp.getEmployeeID().equals(employeeID)) {
                return emp;
            }
        }
        return null;
    }

    //View all attendance logs
    public void viewAttendanceLog(){
        if(attendance.isEmpty()) {
            System.out.println("No attendance records found.");
            return;
        }

        System.out.println("\n========== Attendance Log ==========");
        System.out.printf("%-10s %-25s %-15s %-10s %-10s %-12s%n", "Employee ID", "Name", "Date", "Clock In", "Clock Out", "Worked Hours");

        for(AttendanceLog log: attendance){
            Employee emp = findEmployeeByID(log.getEmployeeID());
            String name = (emp != null) ? emp.getName() : "Unknown";
            String clockIn = log.getClockIn();  
            String clockOut = log.getClockOut(); 
            double hours = (clockOut.equals("N/A")) ? 0.0 : log.gettotal_hours_worked();

            System.out.printf("%-10s %-25s %-15s %-10s %-10s %-12.2f%n",log.getEmployeeID(), 
                               name, log.getdate(), clockIn, clockOut, hours);
        }
    }
}
