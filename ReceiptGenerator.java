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

public class ReceiptGenerator {

    //Generate Stock Transfer Receipt (Stock In / Stock Out)
    public void generateStockReceipt(String type, String fromOutlet, String toOutlet, String employeeName, List<String> models, List<Integer> quantities) {
        
        // 1. Determine Filename based on TODAY'S date
        // Example: "data/receipts_2025-11-29.txt"
        LocalDate today = ZonedDateTime.now(ZoneId.of("Asia/Kuala_Lumpur")).toLocalDate();
        String filename = "receipts_" + today.toString() + ".txt";
        
        // 2. Get current timestamp for the receipt content
        // Example: "2025-11-29 14:30:05"
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = now.format(formatter);
        
        // Calculate total items
        int totalQuantity = 0;
        for (int qty : quantities) {
            totalQuantity += qty;
        }

        // 3. Write to file
        // It adds new text to the end of the file instead of deleting old content.
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename, true))) {
            
            pw.println("=== STOCK TRANSACTION RECEIPT (" + type + ") ===");
            pw.println("Date/Time: " + timestamp);
            pw.println("From: " + fromOutlet);
            pw.println("To:   " + toOutlet);
            pw.println("Staff: " + employeeName);
            pw.println("--- Items List ---");
            
            // Loop through the lists to print each item
            for (int i = 0; i < models.size(); i++) {
                pw.println("- " + models.get(i) + " (Qty: " + quantities.get(i) + ")");
            }
            
            pw.println("--------------------------------");
            pw.println("Total Quantity: " + totalQuantity);
            pw.println("--------------------------------\n"); // Extra newlines for separation
            
            System.out.println(" [Receipt] Generated successfully: " + filename);
            
        } catch (IOException e) {
            System.err.println(" ! Error generating receipt: " + e.getMessage());
        }
    }
    
    //Generate Sales Receipt (Optional helper for Sales System)

    public void generateSalesReceipt(String customerName, String modelName, int quantity, double totalMetrics, String paymentMethod, String employeeName) {
        
        LocalDate today = ZonedDateTime.now(ZoneId.of("Asia/Kuala_Lumpur")).toLocalDate();
        String filename = "data/sales_receipts_" + today.toString() + ".txt";
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = now.format(formatter);
        
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename, true))) {
            pw.println("=== SALES RECEIPT ===");
            pw.println("Date: " + timestamp);
            pw.println("Customer: " + customerName);
            pw.println("Item: " + modelName + " x" + quantity);
            pw.println("Total: RM" + totalMetrics);
            pw.println("Payment: " + paymentMethod);
            pw.println("Served by: " + employeeName);
            pw.println("=====================\n");
            
            System.out.println(" [Receipt] Sales receipt generated.");
        } catch (IOException e) {
            System.err.println(" ! Error generating sales receipt: " + e.getMessage());
        }
    }
}

