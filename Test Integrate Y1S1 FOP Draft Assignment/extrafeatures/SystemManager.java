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

public class SystemManager {
    
    // Helper classes
    DataLoader loader = new DataLoader();
    DataPersister saver = new DataPersister();
    ReceiptGenerator printer = new ReceiptGenerator();
    
    // Lists
    List<Employee> employees;
    List<Model> models;
    List<Stock> stockList;
    List<Attendance> attendanceList;
    List<Sales> salesList;
    
    // Who is logged in?
    Employee currentUser; 

    

// Resolve outlet code for the current user.
// If employees.csv does not contain OutletCode (so it becomes "N/A"),
// we infer available outlets from the stock list.
private String resolveOutletForUser(Scanner scan) {
    String outlet = (currentUser == null || currentUser.getOutletCode() == null) ? "" : currentUser.getOutletCode();
    outlet = outlet.trim();

    if (outlet.isEmpty() || outlet.equalsIgnoreCase("N/A")) {
        // Collect unique outlet codes from stockList (simple array approach).
        String[] outlets = new String[stockList.size()];
        int count = 0;

        for (Stock st : stockList) {
            String oc = (st == null || st.getOutletCode() == null) ? "" : st.getOutletCode().trim();
            if (oc.isEmpty()) continue;

            boolean exists = false;
            for (int i = 0; i < count; i++) {
                if (outlets[i].equalsIgnoreCase(oc)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                outlets[count] = oc;
                count++;
            }
        }

        if (count == 1) {
            outlet = outlets[0];
            System.out.println("[Info] Outlet not set for this user. Using outlet: " + outlet);
        } else if (count > 1) {
            System.out.println("[Info] Outlet not set for this user. Choose outlet:");
            for (int i = 0; i < count; i++) {
                System.out.println((i + 1) + ". " + outlets[i]);
            }

            int choice = -1;
            while (choice < 1 || choice > count) {
                System.out.print("> ");
                if (scan.hasNextInt()) {
                    choice = scan.nextInt();
                    scan.nextLine(); // consume newline
                } else {
                    scan.nextLine();
                    System.out.println("Invalid input. Please enter a number.");
                }
            }
            outlet = outlets[choice - 1];
        } else {
            outlet = "N/A";
        }
    }

    return outlet;
}

    public SystemManager() {
        // Get data from loader
        this.employees = loader.getEmployees();
        this.models = loader.getModels();
        this.stockList = loader.getStockRecords();      
        this.attendanceList = loader.getAttendanceLogs(); 
        this.salesList = loader.getSalesRecords();        
        
        // --- TEMPORARY CODE FOR TESTING 
        // Auto-login the first person (Manager) for testing.
        if (employees.size() > 0) {
            this.currentUser = employees.get(0); 
            System.out.println("[Test Mode] Auto-logged in as: " + currentUser.getName());
        }
    }
    
    // Save Everything
    public void saveAll() {
        System.out.println("Saving...");
        saver.saveEmployees(employees);
        saver.saveModels(models);
        saver.saveStock(stockList);
        saver.saveAttendance(attendanceList);
        saver.saveSales(salesList);
        System.out.println("Saved.");
    }

    // Feature: Stock Count
    public void doStockCount(Scanner scan) {
        if (currentUser == null) {
            System.out.println("Error: No user found. (Login system not ready)");
            return;
        }

        System.out.println("1. Morning Stock Count");
        System.out.println("2. Night Stock Count");
        System.out.print("Select count type: ");
        String countChoice = scan.nextLine().trim();
        String countType = countChoice.equals("2") ? "Night Stock Count" : "Morning Stock Count";

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("hh:mm a");

        String outlet = resolveOutletForUser(scan);
        System.out.println("=== " + countType + " ===");
        System.out.println("Date: " + today);
        System.out.println("Time: " + now.format(timeFmt).toLowerCase());
        System.out.println("Outlet: " + outlet);
        
        // MODIFIED: Iterate through stockList directly instead of models
        boolean hasStock = false;
        int totalChecked = 0;
        int mismatches = 0;
        for (Stock s : stockList) {
            
            // Filter: Only process items belonging to the current user's outlet
            if (s.getOutletCode().equalsIgnoreCase(outlet)) {
                hasStock = true;
                System.out.print("Count for " + s.getModelName() + " (System: " + s.getQuantity() + "): ");
                
                if (scan.hasNextInt()) {
                    int input = scan.nextInt();
                    // scan.nextLine(); // (Optional) Consumes the newline left over
                    
                    totalChecked++;
                    if (input == s.getQuantity()) {
                        System.out.println("Correct.");
                    } else {
                        mismatches++;
                        System.out.println("Mismatch! Diff: " + (input - s.getQuantity()));
                    }
                    logStockCount(today.toString(), now.format(timeFmt).toLowerCase(),
                        outlet, countType, s.getModelName(), s.getQuantity(), input);
                } else {
                    System.out.println("Invalid input."); 
                    scan.next(); // Consume bad input to prevent infinite loop
                }
            }
        }
        
        if (!hasStock) {
            System.out.println("No stock records found for outlet: " + outlet);
        }
        
        System.out.println("Total Models Checked: " + totalChecked);
        System.out.println("Tally Correct: " + (totalChecked - mismatches));
        System.out.println("Mismatches: " + mismatches);
        if (mismatches > 0) {
            System.out.println("Warning: Please verify stock.");
        } else {
            System.out.println("Stock tally correct.");
        }
        System.out.println(countType + " completed.");
    }
    
    // Feature: Stock In / Out
    public void doTransfer(Scanner scan) {
        if (currentUser == null) return;

        System.out.println("1. Stock In (Receive)");
        System.out.println("2. Stock Out (Send)");
        System.out.print("Choice: ");
        
        int choice = 0;
        if (scan.hasNextInt()) choice = scan.nextInt();
        scan.nextLine(); 
        
        String type = (choice == 1) ? "Stock In" : "Stock Out";
        String myOutlet = resolveOutletForUser(scan);
        String otherOutlet = "";
        
        System.out.print("From/To Outlet: ");
        otherOutlet = scan.nextLine();
        
        List<String> rModels = new ArrayList<>();
        List<Integer> rQtys = new ArrayList<>();
        
        while (true) {
            System.out.print("Model Name: ");
            String name = scan.nextLine();
            
            Stock s = findStock(name, myOutlet);
            
            // Create new stock record if receiving new item
            if (s == null && choice == 1) {
                s = new Stock(name, myOutlet, 0);
                stockList.add(s);
            }
            
            if (s != null) {
                System.out.print("Qty: ");
                int qty = scan.nextInt();
                scan.nextLine();
                
                if (choice == 1) { 
                    s.setQuantity(s.getQuantity() + qty);
                } else { 
                    if (s.getQuantity() >= qty) s.setQuantity(s.getQuantity() - qty);
                    else { System.out.println("Not enough stock!"); continue; }
                }
                rModels.add(name);
                rQtys.add(qty);
                System.out.println("Updated.");
            } else {
                System.out.println("Model not found.");
            }
            
            System.out.print("More items? (y/n): ");
            String ans = scan.nextLine();
            if (!ans.equalsIgnoreCase("y")) break;
        }
        
        if (rModels.size() > 0) {
            if (choice == 1) printer.generateStockReceipt(type, otherOutlet, myOutlet, currentUser.getName(), rModels, rQtys);
            else printer.generateStockReceipt(type, myOutlet, otherOutlet, currentUser.getName(), rModels, rQtys);
        }
    }

    private Stock findStock(String name, String outlet) {
        for (Stock s : stockList) {
            if (s.getModelName().equals(name) && s.getOutletCode().equals(outlet)) {
                return s;
            }
        }
        return null;
    }

    public int[] stockCountGui(String outlet, String countType, String[] models, int[] counts, int size) {
        int totalChecked = 0;
        int mismatches = 0;
        String date = LocalDate.now().toString();
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")).toLowerCase();

        for (int i = 0; i < stockList.size(); i++) {
            Stock s = stockList.get(i);
            if (s.getOutletCode().equalsIgnoreCase(outlet)) {
                totalChecked++;
                int idx = indexOfModel(models, size, s.getModelName());
                if (idx >= 0) {
                    int counted = counts[idx];
                    if (counted != s.getQuantity()) mismatches++;
                    logStockCount(date, time, outlet, countType, s.getModelName(), s.getQuantity(), counted);
                }
            }
        }
        return new int[]{totalChecked, mismatches};
    }

    public boolean transferStockGui(String type, String myOutlet, String otherOutlet,
                                    String[] models, int[] qtys, int size, String employeeName) {
        return transferStockGuiMessage(type, myOutlet, otherOutlet, models, qtys, size, employeeName) == null;
    }

    public String transferStockGuiMessage(String type, String myOutlet, String otherOutlet,
                                          String[] models, int[] qtys, int size, String employeeName) {
        if (!"Stock In".equalsIgnoreCase(type) && !"Stock Out".equalsIgnoreCase(type)) return "Invalid transaction type.";
        if (models == null || qtys == null || size <= 0) return "No items provided.";
        if (myOutlet == null || otherOutlet == null) return "Outlet code missing.";
        if (myOutlet.equalsIgnoreCase(otherOutlet)) return "From and To outlet cannot be the same.";

        boolean isHQ = otherOutlet.equalsIgnoreCase("HQ") || otherOutlet.equalsIgnoreCase("Service Center");
        if ("Stock Out".equalsIgnoreCase(type) && isHQ) return "Stock Out cannot be sent to HQ (Service Center).";

        // validation
        if ("Stock Out".equalsIgnoreCase(type)) {
            // sending from myOutlet to otherOutlet
            for (int i = 0; i < size; i++) {
                Stock from = findStock(models[i], myOutlet);
                if (from == null) return "Model not found in outlet " + myOutlet + ": " + models[i];
                if (from.getQuantity() < qtys[i]) {
                    return "Not enough stock in " + myOutlet + " for " + models[i] +
                            " (have " + from.getQuantity() + ", need " + qtys[i] + ").";
                }
            }
        } else {
            // Stock In: receiving into myOutlet from otherOutlet or HQ
            if (!isHQ) {
                for (int i = 0; i < size; i++) {
                    Stock from = findStock(models[i], otherOutlet);
                    if (from == null) return "Model not found in outlet " + otherOutlet + ": " + models[i];
                    if (from.getQuantity() < qtys[i]) {
                        return "Not enough stock in " + otherOutlet + " for " + models[i] +
                                " (have " + from.getQuantity() + ", need " + qtys[i] + ").";
                    }
                }
            }
        }

        // apply updates
        for (int i = 0; i < size; i++) {
            String modelName = models[i];
            int qty = qtys[i];

            if ("Stock Out".equalsIgnoreCase(type)) {
                // decrease myOutlet, increase otherOutlet
                Stock from = findStock(modelName, myOutlet);
                from.setQuantity(from.getQuantity() - qty);

                Stock to = findStock(modelName, otherOutlet);
                if (to == null) {
                    to = new Stock(modelName, otherOutlet, 0);
                    stockList.add(to);
                }
                to.setQuantity(to.getQuantity() + qty);
            } else {
                // Stock In: increase myOutlet
                Stock to = findStock(modelName, myOutlet);
                if (to == null) {
                    to = new Stock(modelName, myOutlet, 0);
                    stockList.add(to);
                }
                to.setQuantity(to.getQuantity() + qty);

                // if coming from another outlet, decrease that outlet
                if (!isHQ) {
                    Stock from = findStock(modelName, otherOutlet);
                    from.setQuantity(from.getQuantity() - qty);
                }
            }
        }

        saver.saveStock(stockList);

        String fromOutlet = "Stock In".equalsIgnoreCase(type) ? otherOutlet : myOutlet;
        String toOutlet = "Stock In".equalsIgnoreCase(type) ? myOutlet : otherOutlet;
        printer.generateStockReceipt(type, formatOutletLabel(fromOutlet), formatOutletLabel(toOutlet), employeeName, models, qtys, size);
        return null;
    }

    private String formatOutletLabel(String code) {
        if (code == null) return "";
        if (code.equalsIgnoreCase("HQ") || code.equalsIgnoreCase("Service Center")) {
            return "HQ (Service Center)";
        }
        return code;
    }

    private int indexOfModel(String[] models, int size, String name) {
        for (int i = 0; i < size; i++) {
            if (models[i] != null && models[i].equalsIgnoreCase(name)) return i;
        }
        return -1;
    }

    private void logStockCount(String date, String time, String outlet, String countType,
                               String model, int systemQty, int countedQty) {
        try {
            ensureStockCountHeader();
            PrintWriter pw = new PrintWriter(new FileWriter("stock_count.csv", true));
            int diff = countedQty - systemQty;
            pw.println(date + "," + time + "," + outlet + "," + countType + "," + model + "," + systemQty + "," + countedQty + "," + diff);
            pw.close();
        } catch (IOException ignored) {}
    }

    private void ensureStockCountHeader() throws IOException {
        File f = new File("stock_count.csv");
        if (!f.exists() || f.length() == 0) {
            PrintWriter pw = new PrintWriter(new FileWriter(f));
            pw.println("Date,Time,OutletCode,CountType,ModelName,SystemQty,CountedQty,Diff");
            pw.close();
        }
    }

    // For GUI stock table: return Model, Price, Qty for one outlet
    public Object[][] getStockTableForOutlet(String outletCode) {
        int count = 0;
        for (Stock s : stockList) {
            if (s.getOutletCode().equalsIgnoreCase(outletCode)) count++;
        }

        Object[][] out = new Object[count][3];
        int idx = 0;
        for (Stock s : stockList) {
            if (s.getOutletCode().equalsIgnoreCase(outletCode)) {
                double price = findModelPrice(s.getModelName());
                out[idx][0] = s.getModelName();
                out[idx][1] = String.format(java.util.Locale.US, "%.2f", price);
                out[idx][2] = s.getQuantity();
                idx++;
            }
        }
        return out;
    }

    // For GUI model list
    public String[] listAllModels() {
        String[] out = new String[models.size()];
        for (int i = 0; i < models.size(); i++) {
            out[i] = models.get(i).getName();
        }
        return out;
    }

    private double findModelPrice(String modelName) {
        for (Model m : models) {
            if (m.getName().equalsIgnoreCase(modelName)) return m.getUnitPrice();
        }
        return 0.0;
    }

    public double getModelPrice(String modelName) {
        return findModelPrice(modelName);
    }
    
    // For Teammate's Login Module
    public void setCurrentUser(Employee e) {
        this.currentUser = e;
    }
    
    public Employee getCurrentUser() {
        return currentUser;
    }
}

