package extrafeatures;

import java.io.*;
import java.util.*;

public class SearchSystem {
    private static final Scanner INPUT = new Scanner(System.in);

    private double findUnitPrice(String modelName) {
        try (BufferedReader br = new BufferedReader(new FileReader("model.csv"))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 2 && p[0].trim().equalsIgnoreCase(modelName)) {
                    try { return Double.parseDouble(p[1].trim()); } catch (Exception ignored) {}
                }
            }
        } catch (IOException ignored) {}
        return 0.0;
    }

    private String outletLabel(String code) {
        String name = "";
        try (BufferedReader br = new BufferedReader(new FileReader("outlet.csv"))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 2 && p[0].trim().equalsIgnoreCase(code)) {
                    name = p[1].trim();
                    break;
                }
            }
        } catch (IOException ignored) {}
        if (name.length() == 0) return code;
        return code + " (" + name + ")";
    }

    private String findEmployeeName(String id) {
        try (BufferedReader br = new BufferedReader(new FileReader("employees.csv"))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length >= 2 && p[0].trim().equalsIgnoreCase(id)) {
                    return p[1].trim();
                }
            }
        } catch (IOException ignored) {}
        return "";
    }

    public String searchStockInfoText(String model) {
        String text = "=== Search Stock Information ===\n";
        text += "Model: " + model + "\n";
        double price = findUnitPrice(model);
        text += "Unit Price: RM " + String.format(java.util.Locale.US, "%.2f", price) + "\n";
        text += "Stock by Outlet:\n";

        boolean found = false;
        try (BufferedReader br = new BufferedReader(new FileReader("stock.csv"))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length >= 3 && p[0].equalsIgnoreCase(model)) {
                    found = true;
                    text += outletLabel(p[1]) + ": " + p[2] + "  ";
                }
            }
        } catch (IOException e) {
            return "Error reading stock file.";
        }
        if (!found) text += "Model not found in stock.";
        return text;
    }

    public String searchSalesInfoText(String mode, String query) {
        String text = "=== Search Sales Information ===\n";
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader("sales_data.csv"))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().length() == 0) continue;
                String[] p = line.split(",", -1);
                if (p.length < 6) continue;

                String date = p[1].trim();
                String time = "N/A";
                String customer = p[2].trim();
                String model = p[3].trim();
                String qty = p[4].trim();
                String total = p[5].trim();
                String employee = "N/A";
                String method = "N/A";

                if (p.length >= 9) {
                    time = p[2].trim();
                    customer = p[3].trim();
                    model = p[4].trim();
                    qty = p[5].trim();
                    total = p[6].trim();
                    employee = p[7].trim();
                    method = p[8].trim();
                } else if (p.length >= 8) {
                    customer = p[2].trim();
                    model = p[3].trim();
                    qty = p[4].trim();
                    total = p[5].trim();
                    employee = p[6].trim();
                    method = p[7].trim();
                }

                boolean match = false;
                if ("DATE".equals(mode)) match = date.equalsIgnoreCase(query);
                else if ("CUSTOMER".equals(mode)) match = customer.equalsIgnoreCase(query);
                else if ("MODEL".equals(mode)) match = model.equalsIgnoreCase(query);

                if (match) {
                    found = true;
                    String empName = findEmployeeName(employee);
                    if (empName.length() == 0) empName = employee;
                    text += "Date: " + date + "  Time: " + time + "\n";
                    text += "Customer: " + customer + "\n";
                    text += "Item(s): " + model + " Quantity: " + qty + "\n";
                    text += "Total: RM " + total + "\n";
                    text += "Transaction Method: " + method + "\n";
                    text += "Employee: " + empName + "\n";
                    text += "Status: Transaction verified.\n\n";
                }
            }
        } catch (IOException e) {
            return "Error reading sales file.";
        }

        if (!found) text += "No sales record found.";
        return text;
    }

    /* ================= SEARCH STOCK ================= */
    public void searchStockInfo() {
        System.out.print("Search Model Name: ");
        String model = INPUT.nextLine();

        System.out.println("Searching...");
        System.out.println("\n=== Search Stock Information ===");
        System.out.println("Model: " + model);
        double price = findUnitPrice(model);
        System.out.println("Unit Price: RM " + String.format(java.util.Locale.US, "%.2f", price));
        System.out.println("Stock by Outlet:");
        try (BufferedReader br = new BufferedReader(new FileReader("stock.csv"))) {
            br.readLine();
            boolean found = false;

            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 3 && p[0].equalsIgnoreCase(model)) {
                    found = true;
                    System.out.println(outletLabel(p[1]) + ": " + p[2]);
                }
            }

            if (!found) System.out.println("Model not found in stock.");

        } catch (IOException e) {
            System.out.println("Error reading stock file.");
        }
    }

    /* ================= SEARCH SALES ================= */
    public void searchSalesInfo(List<SaleRecord> salesList) {
        System.out.println("Search by:");
        System.out.println("1. Date (YYYY-MM-DD)");
        System.out.println("2. Customer Name");
        System.out.println("3. Model Name");
        System.out.print("Enter choice: ");
        String choice = INPUT.nextLine();
        System.out.print("Enter search text: ");
        String query = INPUT.nextLine();

        boolean found = false;
        for (SaleRecord s : salesList) {
            boolean match = false;
            if (choice.equals("1")) {
                match = s.date.toString().equalsIgnoreCase(query);
            } else if (choice.equals("2")) {
                match = s.customerName.equalsIgnoreCase(query);
            } else if (choice.equals("3")) {
                match = s.item.modelName.equalsIgnoreCase(query);
            }

            if (match) {
                found = true;
                String empName = findEmployeeName(s.employeeId);
                if (empName.length() == 0) empName = s.employeeId;
                System.out.println("\nSales Record Found:");
                System.out.println("Date: " + s.date + "  Time: " + s.time);
                System.out.println("Customer: " + s.customerName);
                System.out.println("Item(s): " + s.item.modelName + " Quantity: " + s.item.quantity);
                System.out.println("Total: RM " + s.item.totalPrice);
                System.out.println("Transaction Method: " + s.paymentMethod);
                System.out.println("Employee: " + empName);
                System.out.println("Status: Transaction verified.");
            }
        }

        if (!found) System.out.println("No sales record found.");
    }
}
