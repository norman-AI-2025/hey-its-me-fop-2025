package extrafeatures;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class DataPersister {
    
    // 1. File Paths (Must match DataLoader)
    private static final String EMPLOYEE_FILE_PATH = "employees.csv";
    private static final String MODEL_FILE_PATH = "model.csv";
    private static final String STOCK_FILE_PATH = "stock.csv";
    private static final String ATTENDANCE_FILE_PATH = "attendance.csv";
    private static final String SALES_FILE_PATH = "sales_data.csv";

    // --- A. Save Employees ---
    public void saveEmployees(List<Employee> employees) {
        // try-with-resources: Automatically closes the file after writing
        try (PrintWriter pw = new PrintWriter(new FileWriter(EMPLOYEE_FILE_PATH))) {
            
            // Step 1: Write Header
            pw.println("EmployeeID,Name,Role,Password,OutletCode");
            
            // Step 2: Loop through list and write each object
            for (Employee emp : employees) {
                String line = String.format("%s,%s,%s,%s,%s",
                    emp.getEmployeeID(),
                    emp.getName(),
                    emp.getRole(),
                    emp.getPassword(),
                    emp.getOutletCode()
                );
                pw.println(line);
            }
            System.out.println(" [Save] Employee data saved.");
            
        } catch (IOException e) {
            System.err.println(" ! Error saving employees: " + e.getMessage());
        }
    }
    
    // --- B. Save Models ---
    public void saveModels(List<Model> models) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(MODEL_FILE_PATH))) {
            pw.println("ModelName,UnitPrice");
            for (Model m : models) {
                String line = String.format("%s,%.2f", // %.2f means 2 decimal places for price
                    m.getName(),
                    m.getUnitPrice()
                );
                pw.println(line);
            }
            System.out.println(" [Save] Model data saved.");
        } catch (IOException e) {
            System.err.println(" ! Error saving models: " + e.getMessage());
        }
    }

    // --- C. Save Stock ---
    public void saveStock(List<Stock> stockRecords) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(STOCK_FILE_PATH))) {
            pw.println("ModelName,OutletCode,Quantity");
            for (Stock s : stockRecords) {
                String line = String.format("%s,%s,%d", // %d for integer
                    s.getModelName(),
                    s.getOutletCode(),
                    s.getQuantity()
                );
                pw.println(line);
            }
            System.out.println(" [Save] Stock data saved.");
        } catch (IOException e) {
            System.err.println(" ! Error saving stock: " + e.getMessage());
        }
    }

    // --- D. Save Attendance ---
    public void saveAttendance(List<Attendance> logs) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ATTENDANCE_FILE_PATH))) {
            pw.println("EmployeeID,Date,TimeIn,TimeOut");
            for (Attendance log : logs) {
                // Using the helper method we wrote in Attendance.java
                pw.println(log.toCSV());
            }
            System.out.println(" [Save] Attendance data saved.");
        } catch (IOException e) {
            System.err.println(" ! Error saving attendance: " + e.getMessage());
        }
    }

    // --- E. Save Sales ---
    public void saveSales(List<Sales> sales) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(SALES_FILE_PATH))) {
            pw.println("SalesID,Date,CustomerName,ModelName,Quantity,TotalPrice");
            for (Sales s : sales) {
                pw.println(s.toCSV());
            }
            System.out.println(" [Save] Sales data saved.");
        } catch (IOException e) {
            System.err.println(" ! Error saving sales: " + e.getMessage());
        }
    }
}

