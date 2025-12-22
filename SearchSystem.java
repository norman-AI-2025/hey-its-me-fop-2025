import java.util.List;
import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SearchSystem{

    public void searchStockInfo() {
        Scanner input = new Scanner(System.in);
        System.out.println("=== Search Stock Information ===");
        System.out.print("Search Model Name: ");
        String searchModelName = input.nextLine();
        System.out.println("Searching...");

        String fileName = "stock.csv";
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("Stock file (stock.csv) not found.");
            return;
        }

        boolean modelFound = false;
        int totalQuantity = 0;
        List<String> foundLocations = new ArrayList<>();

        try (Scanner fileScanner = new Scanner(file)) {
            if (fileScanner.hasNextLine()) {
                fileScanner.nextLine(); // Skip header
            }
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[0].equalsIgnoreCase(searchModelName)) {
                    modelFound = true;
                    String outlet = parts[1];
                    int quantity = Integer.parseInt(parts[2]);
                    totalQuantity += quantity;
                    foundLocations.add("  - Outlet: " + outlet + ", Quantity: " + quantity);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading stock file: " + e.getMessage());
            return;
        }

        if (modelFound) {
            System.out.println("Stock Information for Model: " + searchModelName);
            for (String locationInfo : foundLocations) {
                System.out.println(locationInfo);
            }
            System.out.println("Total Quantity across all outlets: " + totalQuantity);
        } else {
            System.out.println("Model not found in stock.");
        }
    }

    public void searchSalesInfo(List<SaleRecord> saleRecords)
    {
        if (saleRecords == null || saleRecords.isEmpty()) {
            System.out.println("Cannot search: No sales records provided.");
            return;
        }

        Scanner input = new Scanner(System.in);
        System.out.println("=== Search Sales Information ===");
        System.out.print("Search keyword (Customer Name): ");
        String searchCustomerName = input.nextLine();
        System.out.println("Searching...");

        boolean recordFound = false;
        try {
            Map<String, String> employeeNames = SalesSystem.loadEmployeeNames();
            for (SaleRecord saleRecord : saleRecords) {
                if (saleRecord.customerName.equalsIgnoreCase(searchCustomerName)) {
                    System.out.println("--- Sales Record Found ---");
                    System.out.println(saleRecord.toString(employeeNames));
                    System.out.println("--------------------------");
                    recordFound = true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading employee data: " + e.getMessage());
        }


        if (!recordFound) {
            System.out.println("Sales record not found for the given keyword.");
        }
    }
}
