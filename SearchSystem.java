package fop_assignment_2025;

import java.io.*;
import java.util.*;

public class SearchSystem {

    /* ================= SEARCH STOCK ================= */
    public void searchStockInfo() {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter Model Name: ");
        String model = input.nextLine();

        System.out.println("\n=== Stock Availability ===");
        try (BufferedReader br = new BufferedReader(new FileReader("stock.csv"))) {
            br.readLine();
            boolean found = false;

            while (true) {
                String line = br.readLine();
                if (line == null) break;

                String[] p = line.split(",");
                if (p[0].equalsIgnoreCase(model)) {
                    found = true;
                    System.out.println(p[1] + " → Quantity: " + p[2]);
                }
            }

            if (!found) System.out.println("Model not found in stock.");

        } catch (IOException e) {
            System.out.println("Error reading stock file.");
        }
    }

    /* ================= SEARCH SALES ================= */
    public void searchSalesInfo(List<SaleRecord> salesList) {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter Customer Name: ");
        String name = input.nextLine();

        boolean found = false;
        for (SaleRecord s : salesList) {
            if (s.customerName.equalsIgnoreCase(name)) {
                found = true;
                System.out.println("\nSales ID: " + s.salesId);
                System.out.println("Date: " + s.date);
                System.out.println("Model: " + s.item.modelName);
                System.out.println("Outlet: " + s.item.outletCode);
                System.out.println("Quantity: " + s.item.quantity);
                System.out.println("Total: RM " + s.item.totalPrice);
                System.out.println("Payment: " + s.paymentMethod);
                System.out.println("Employee ID: " + s.employeeId);
            }
        }

        if (!found) System.out.println("No sales record found.");
    }
}
