package extrafeatures;

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

public class DataLoader {

    // 1. Define File Paths (Where are the files located?)
    // We use relative paths so it works on any computer.
    private static final String EMPLOYEE_FILE_PATH = "employees.csv";
    private static final String MODEL_FILE_PATH = "model.csv";
    private static final String STOCK_FILE_PATH = "stock.csv";
    private static final String ATTENDANCE_FILE_PATH = "attendance.csv";
    private static final String SALES_FILE_PATH = "sales_data.csv";

    // 2. In-Memory Storage (The "Brain")
    // We use ArrayLists because we don't know how many records there are.
    private List<Employee> employees = new ArrayList<>();
    private List<Model> models = new ArrayList<>();
    private List<Stock> stockRecords = new ArrayList<>();
    private List<Attendance> attendanceLogs = new ArrayList<>();
    private List<Sales> salesRecords = new ArrayList<>();

    // 3. Constructor
    // This is the trigger. "new DataLoader()" calls loadAllData() immediately.
    public DataLoader() {
        loadAllData();
    }

    // Master method to load everything
    private void loadAllData() {
        System.out.println("[System] Loading data...");
        loadEmployees(); 
        loadModels();
        loadStock(); 
        loadAttendance();
        loadSales();
        System.out.println("[System] Data loading finished.");
    }

    // --- A. Load Employees ---
    private void loadEmployees() {
        // try-catch block: Safety net. If file is missing, program won't crash.
        try (BufferedReader br = new BufferedReader(new FileReader(FilePathHelper.resolveReadPath(EMPLOYEE_FILE_PATH)))) {
            br.readLine(); // Step 1: Skip the Header row (EmployeeID,Name...)
            String line;
            
            // Step 2: Read line by line until end of file
            while ((line = br.readLine()) != null) {
                // Step 3: Split the line by comma
                // "C6001,Tan,Manager" -> ["C6001", "Tan", "Manager"]
                String[] data = line.split(","); 
                
                // Step 4: Create Object and add to list
                if (data.length >= 4) { 
                    Employee emp = (data.length >= 5)
                        ? new Employee(data[0].trim(), data[1].trim(), data[2].trim(), data[3].trim(), data[4].trim())
                        : new Employee(data[0].trim(), data[1].trim(), data[2].trim(), data[3].trim(), data[4].trim());
                    employees.add(emp);
                }
            }
            System.out.println(" - Loaded " + employees.size() + " Employees.");
        } catch (IOException e) {
            System.err.println(" ! Error loading employees: " + e.getMessage());
        }
    }
    
    // --- B. Load Models ---
    private void loadModels() {
        try (BufferedReader br = new BufferedReader(new FileReader(FilePathHelper.resolveReadPath(MODEL_FILE_PATH)))) {
            br.readLine(); // Skip Header
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                // model.csv may have many columns; we only need Model and Price (first 2)
                if (data.length >= 2) { 
                    try {
                        // CRITICAL: Convert String "349.50" to double 349.50
                        double price = Double.parseDouble(data[1].trim());
                        Model model = new Model(data[0].trim(), price);
                        models.add(model);
                    } catch (NumberFormatException e) {
                        System.err.println(" ! Invalid price for model: " + data[0]);
                    }
                }
            }
            System.out.println(" - Loaded " + models.size() + " Models.");
        } catch (IOException e) {
            System.err.println(" ! Error loading models: " + e.getMessage());
        }
    }

    // --- C. Load Stock ---
    private void loadStock() { 
        try (BufferedReader br = new BufferedReader(new FileReader(FilePathHelper.resolveReadPath(STOCK_FILE_PATH)))) {
            br.readLine(); // Skip Header
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) { 
                    try {
                        // CRITICAL: Convert String "10" to int 10
                        int quantity = Integer.parseInt(data[2].trim());
                        Stock stock = new Stock(data[0].trim(), data[1].trim(), quantity);
                        stockRecords.add(stock);
                    } catch (NumberFormatException e) {
                        System.err.println(" ! Invalid quantity for stock: " + data[0]);
                    }
                }
            }
            System.out.println(" - Loaded " + stockRecords.size() + " Stock records.");
        } catch (IOException e) {
            System.err.println(" ! Error loading stock: " + e.getMessage());
        }
    }

    // --- D. Load Attendance ---
    private void loadAttendance() {
        try (BufferedReader br = new BufferedReader(new FileReader(FilePathHelper.resolveReadPath(ATTENDANCE_FILE_PATH)))) {
            br.readLine(); // header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                // Supported formats:
                // (A) EmployeeID,Date,ClockIn,ClockOut
                // (B) EmployeeID,Date,ClockIn,ClockOut,TotalHoursWorked  (your current attendance.csv)
                if (data.length >= 4) {
                    String empId = data[0].trim();
                    String date = data[1].trim();
                    String in   = data[2].trim();
                    String out  = data[3].trim();
                    attendanceLogs.add(new Attendance(empId, date, in, out));
                }
            }
            System.out.println(" - Loaded " + attendanceLogs.size() + " Attendance records.");
        } catch (IOException e) {
            // It's normal if this file is missing (new system)
            System.out.println(" - No attendance records found yet.");
        }
    }

// --- E. Load Sales ---
    private void loadSales() {
        try (BufferedReader br = new BufferedReader(new FileReader(FilePathHelper.resolveReadPath(SALES_FILE_PATH)))) {
            br.readLine(); // header (may vary by dataset)
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(",");
                // Support formats:
                // A) SalesID,Date,CustomerName,ModelName,Quantity,TotalPrice
                // B) Date,CustomerName,Model,TotalAmount,PaymentMethod,EmployeeID
                // C) SalesID,Date,Time,CustomerName,ModelName,Quantity,TotalPrice,EmployeeID,PaymentMethod
                try {
                    if (data.length >= 9 && data[0].trim().matches("S\\d+")) {
                        // Format C (with Time + EmployeeID + PaymentMethod)
                        String salesId = data[0].trim();
                        String date = data[1].trim();
                        String customer = data[3].trim();
                        String model = data[4].trim();
                        int qty = Integer.parseInt(data[5].trim());
                        double total = Double.parseDouble(data[6].trim());
                        salesRecords.add(new Sales(salesId, date, customer, model, qty, total));
                    } else if (data.length >= 6 && data[0].trim().matches("S\\d+")) {
                        // Format A
                        String salesId = data[0].trim();
                        String date = data[1].trim();
                        String customer = data[2].trim();
                        String model = data[3].trim();
                        int qty = Integer.parseInt(data[4].trim());
                        double total = Double.parseDouble(data[5].trim());
                        salesRecords.add(new Sales(salesId, date, customer, model, qty, total));
                    } else if (data.length >= 6) {
                        // Format B
                        String date = data[0].trim();
                        String customer = data[1].trim();
                        String model = data[2].trim();
                        double total = Double.parseDouble(data[3].trim());
                        // We keep paymentMethod and employeeId in the CSV, but Sales object doesn't store them.
                        // So we still load the record as a basic Sales entry.
                        String salesId = "S" + String.format("%04d", salesRecords.size() + 1);
                        salesRecords.add(new Sales(salesId, date, customer, model, 1, total));
                    }
                } catch (NumberFormatException e) {
                    System.err.println(" ! Skipped invalid sales record: " + line);
                }
            }
            System.out.println(" - Loaded " + salesRecords.size() + " Sales records.");
        } catch (IOException e) {
            System.err.println(" ! Error loading sales: " + e.getMessage());
        }
    }


    
    // 4. Public Getters
    // This allows SystemManager to access the loaded lists.
    public List<Employee> getEmployees() { return employees; }
    public List<Model> getModels() { return models; }
    public List<Stock> getStockRecords() { return stockRecords; }
    public List<Attendance> getAttendanceLogs() { return attendanceLogs; }
    public List<Sales> getSalesRecords() { return salesRecords; }
}

