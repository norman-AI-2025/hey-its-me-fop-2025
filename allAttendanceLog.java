import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class allAttendanceLog {
    private List<Employee> employees = new ArrayList<>();
    private List<AttendanceLog> attendance = new ArrayList<>();
    
    private static final String ATTENDANCE_FILE_NAME = "attendance.csv";
    private static final String EMPLOYEE_FILE_NAME = "employees.csv";

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
                if (parts.length >=3) {
                    employees.add(new Employee(parts[0], parts[1], parts[2], (parts.length > 3 ? parts[3] : "N/A"),(parts.length > 4 ? parts[4] : "N/A")));
                    System.out.println("Loaded employee:" + parts[0]);
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
        Employee emp = findEmployeeByID(employeeID);
        if (emp == null) {
            System.out.println("Employee not found!");
            return;
        }

        LocalDate today = LocalDate.now();
        // Check for double clock-in
        if (findTodayActiveLog(employeeID, today) != null) {
            System.out.println("Error: Employee " + emp.getName() + " is already clocked in today!");
            return;
        }

        LocalTime now = LocalTime.now();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");
        String formattedTime = now.format(timeFormat).toLowerCase();

        AttendanceLog log = new AttendanceLog(employeeID, today.toString());
        log.setClockIn(now.format(timeFormat));
        attendance.add(log);

        System.out.println("\n=== Attendance Clock In ===");
        System.out.println("Employee ID: " + emp.getEmployeeID());
        System.out.println("Name: " + emp.getName());
        System.out.println("Outlet: " + emp.getOutlet());
        System.out.println();
        System.out.println("Clock In Successful!");
        System.out.println("Date: " + today.format(dateFormat));
        System.out.println("Time: " + formattedTime);
        SaveAttendanceLog(ATTENDANCE_FILE_NAME);
    }

    //Clock Out
    public void ClockOut(String employeeID) {
        Employee emp = findEmployeeByID(employeeID);
        if (emp == null) {
            System.out.println("Employee not found!");
            return;
        }

        LocalDate today = LocalDate.now();
        // Find today's active log
        AttendanceLog log = findTodayActiveLog(employeeID, today);
        
        if (log == null) { 
            System.out.println("No active clock-in record found for " + employeeID + " today. Please Clock In first.");
            return;
        }

            LocalTime now = LocalTime.now();
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedTime = now.format(timeFormat).toLowerCase();

            log.setClockOut(now.format(timeFormat));

            System.out.println("\n=== Attendance Clock Out ===");
            System.out.println("Employee ID: " + emp.getEmployeeID());
            System.out.println("Name: " + emp.getName());
            System.out.println("Outlet: " + emp.getOutlet());
            System.out.println();
            System.out.println("Clock Out Successful!");
            System.out.println("Date: " + today.format(dateFormat));
            System.out.println("Time: " + formattedTime);
            System.out.printf("Total Hours Worked:%.2f hours%n", log.gettotal_hours_worked()); 

            SaveAttendanceLog(ATTENDANCE_FILE_NAME);
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