import java.util.List;
import java.util.*;

public class SearchSystem{

    SalesSystem salesSystem = new SalesSystem();


    // FIX: Renamed parameter to 'saleRecords' to match its usage.
    public void searchStockInfo(List<SaleRecord> saleRecords)
    {
        // FIX: Corrected null check to use 'saleRecords'.
        if (saleRecords == null || saleRecords.isEmpty()) {
            System.out.println("Cannot search: No sales records provided.");
            return;
        }

        Scanner input = new Scanner(System.in);
        System.out.println("=== Search Stock Information ===");
        System.out.print("Search Model Name: ");
        String searchModelName = input.nextLine();
        System.out.println("Searching...");

        boolean modelFound = false;
        // FIX: Loop through the list of records.
        for (SaleRecord saleRecord : saleRecords) {
            // FIX: Loop through the items within each record.
            for (ItemPurchased item : saleRecord.item) {
                if (item.model.equalsIgnoreCase(searchModelName)) {
                    System.out.println("Model: " + item.model);
                    System.out.println("Unit Price: RM" + item.price);
                    System.out.println("Quantity in this sale: " + item.quantity);
                    modelFound = true;
                }
            }
        }

        if (!modelFound) {
            System.out.println("Item not found in any sales record.");
        }
    }

    public void searchSalesInfo(List<SaleRecord> saleRecords)
    {
        // FIX: Corrected null check to use 'saleRecords'.
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
        // FIX: Loop through all sale records to find matches.
        for (SaleRecord saleRecord : saleRecords) {
            if (saleRecord.customerName.equalsIgnoreCase(searchCustomerName)) {
                System.out.println("--- Sales Record Found ---");
                System.out.println(saleRecord.toString());
                System.out.println("--------------------------");
                recordFound = true;
            }
        }

        if (!recordFound) {
            System.out.println("Sales record not found for the given keyword.");
        }
    }




}