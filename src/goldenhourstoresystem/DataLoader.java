package goldenhourstoresystem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The "Loader" class.
 * Its job is to read text from CSV files and convert them into Java Objects.
 * This runs automatically when the system starts.
 */
public class DataLoader {

    // 1. Define File Paths (Where are the files located?)
    // We use relative paths so it works on any computer.
    private static final String EMPLOYEE_FILE_PATH = "data/employees.csv";
    private static final String MODEL_FILE_PATH = "data/models.csv";
    private static final String STOCK_FILE_PATH = "data/stock.csv";
    private static final String ATTENDANCE_FILE_PATH = "data/attendance.csv";
    private static final String SALES_FILE_PATH = "data/sales.csv";

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
        try (BufferedReader br = new BufferedReader(new FileReader(EMPLOYEE_FILE_PATH))) {
            br.readLine(); // Step 1: Skip the Header row (EmployeeID,Name...)
            String line;
            
            // Step 2: Read line by line until end of file
            while ((line = br.readLine()) != null) {
                // Step 3: Split the line by comma
                // "C6001,Tan,Manager" -> ["C6001", "Tan", "Manager"]
                String[] data = line.split(","); 
                
                // Step 4: Create Object and add to list
                if (data.length == 5) { 
                    Employee emp = new Employee(data[0].trim(), data[1].trim(), data[2].trim(), data[3].trim(), data[4].trim());
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
        try (BufferedReader br = new BufferedReader(new FileReader(MODEL_FILE_PATH))) {
            br.readLine(); // Skip Header
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(","); 
                if (data.length == 2) { 
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
        try (BufferedReader br = new BufferedReader(new FileReader(STOCK_FILE_PATH))) {
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
        try (BufferedReader br = new BufferedReader(new FileReader(ATTENDANCE_FILE_PATH))) {
            br.readLine(); 
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 4) {
                    attendanceLogs.add(new Attendance(data[0], data[1], data[2], data[3]));
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
        try (BufferedReader br = new BufferedReader(new FileReader(SALES_FILE_PATH))) {
            br.readLine(); 
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 6) {
                    try {
                        int qty = Integer.parseInt(data[4]);
                        double price = Double.parseDouble(data[5]);
                        salesRecords.add(new Sales(data[0], data[1], data[2], data[3], qty, price));
                    } catch (NumberFormatException e) {
                        System.err.println(" ! Skipped invalid sales record.");
                    }
                }
            }
            System.out.println(" - Loaded " + salesRecords.size() + " Sales records.");
        } catch (IOException e) {
            System.out.println(" - No sales records found yet.");
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
