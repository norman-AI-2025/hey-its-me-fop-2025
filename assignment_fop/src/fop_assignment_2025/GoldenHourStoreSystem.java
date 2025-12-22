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

public class GoldenHourStoreSystem {

    public static void main(String[] args) {
        System.out.println("=== GOLDEN HOUR SYSTEM (STOCK MODULE) ===");
        
        SystemManager sys = new SystemManager();
        Scanner scan = new Scanner(System.in);
        
        boolean loop = true;
        while (loop) {
            // No Login Screen here. Assume user is already logged in (Mock).
            System.out.println("\nMENU:");
            System.out.println("1. Stock Count");
            System.out.println("2. Stock Transfer (In/Out)");
            System.out.println("3. Save & Exit");
            System.out.print("> ");
            
            if (scan.hasNextInt()) {
                int input = scan.nextInt();
                scan.nextLine(); // Clear buffer
                
                if (input == 1) {
                    sys.doStockCount(scan);
                } else if (input == 2) {
                    sys.doTransfer(scan);
                } else if (input == 3) {
                    sys.saveAll();
                    loop = false;
                } else {
                    System.out.println("Invalid.");
                }
            } else {
                System.out.println("Please enter a number.");
                scan.next();
            }
        }
        System.out.println("System Closed.");
    }
}

