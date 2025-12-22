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

        String outlet = resolveOutletForUser(scan);
        System.out.println("=== STOCK COUNT (" + outlet + ") ===");
        
        for (Model m : models) {
            Stock s = findStock(m.getName(), outlet);
            
            if (s != null) {
                System.out.print("Count for " + m.getName() + " (System: " + s.getQuantity() + "): ");
                if (scan.hasNextInt()) {
                    int input = scan.nextInt();
                    // scanner.nextLine(); // Optional clean up
                    if (input == s.getQuantity()) System.out.println("Correct.");
                    else System.out.println("Mismatch! Diff: " + (input - s.getQuantity()));
                } else {
                    System.out.println("Invalid input."); 
                    scan.next(); // Consume bad input
                }
            }
        }
        System.out.println("Done.");
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
    
    // For Teammate's Login Module
    public void setCurrentUser(Employee e) {
        this.currentUser = e;
    }
    
    public Employee getCurrentUser() {
        return currentUser;
    }
}

