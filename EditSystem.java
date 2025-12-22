import java.util.*;
import java.io.IOException;
import java.time.LocalDateTime;

public class EditSystem
{
    public void EditStockInfo(List<SaleRecord> saleRecords)
    {
        Scanner input = new Scanner(System.in);
        System.out.println("=== Edit Item Quantity in a Sale ===");
        System.out.print("Enter Customer Name to find the sale: ");
        String customerName = input.nextLine();
        System.out.print("Enter Model Name to edit: ");
        String editModelName = input.nextLine();

        boolean recordFound = false;
        for (SaleRecord saleRecord : saleRecords) {
            if (saleRecord.customerName.equalsIgnoreCase(customerName)) {
                recordFound = true;
                for (ItemPurchased item : saleRecord.item) {
                    if (item.model.equalsIgnoreCase(editModelName)) {
                        System.out.println("Current Quantity for " + item.model + " in this sale: " + item.quantity);
                        
                        // Show breakdown by location
                        System.out.println("Location Breakdown:");
                        String[] locationNames = {
                            "Kuala Lumpur City Centre", "MidValley", "Sunway Velocity", "IOI City Mall", "Lalaport",
                            "Kuala Lumpur East Mall", "Nu Sentral", "Pavillion Kuala Lumpur", "1 Utama", "MyTown"
                        };
                        for(int i=0; i<10; i++) {
                            if(item.location.get(i) > 0) {
                                System.out.println(locationNames[i] + " (C" + (60+i) + "): " + item.location.get(i));
                            }
                        }
                        
                        System.out.println("Select Location to edit:");
                        int locationIndex = -1;
                        while (locationIndex < 0 || locationIndex > 9) {
                            for(int i=0; i<10; i++) {
                                System.out.println((i+1) + ". " + locationNames[i] + " (C" + (60+i) + ")");
                            }
                            System.out.print("Enter selection (1-10): ");
                            if (input.hasNextInt()) {
                                locationIndex = input.nextInt() - 1;
                            }
                            input.nextLine();
                        }
                        
                        System.out.println("Current Quantity at " + locationNames[locationIndex] + " (C" + (60+locationIndex) + "): " + item.location.get(locationIndex));
                        System.out.print("Enter New Quantity for this location: ");
                        int newLocQty = input.nextInt();
                        input.nextLine();

                        System.out.print("Confirm Update? (Y/N): ");
                        if (input.nextLine().equalsIgnoreCase("Y"))
                        {
                            int oldLocQty = item.location.get(locationIndex);
                            int delta = newLocQty - oldLocQty;
                            
                            item.location.set(locationIndex, newLocQty);
                            item.quantity = item.quantity - oldLocQty + newLocQty;
                            
                            System.out.println("Quantity updated successfully in the record.");
                            
                            try {
                                if (delta != 0) {
                                    List<Integer> deltaLocs = new ArrayList<>(Collections.nCopies(10, 0));
                                    deltaLocs.set(locationIndex, delta);
                                    ItemPurchased deltaItem = new ItemPurchased(item.model, delta, item.price, deltaLocs);
                                    
                                    List<ItemPurchased> deltaItems = new ArrayList<>();
                                    deltaItems.add(deltaItem);
                                    
                                    SaleRecord deltaRecord = new SaleRecord(LocalDateTime.now(), "Delta", deltaItems, "None", 0, "None");
                                    
                                    SalesSystem.saveToModelCsv(deltaRecord);
                                    SalesSystem.saveToStockCsv(deltaRecord);
                                }
                                
                                rewriteSalesData(saleRecords);
                                
                            } catch (IOException e) {
                                System.out.println("Error updating files: " + e.getMessage());
                            }

                        }
                        return;
                    }
                }
            }
        }
        if (!recordFound) {
            System.out.println("No sales record found for that customer.");
        } else {
            System.out.println("Model not found in that customer's sales record.");
        }
    }

    public void EditSalesInfo(List<SaleRecord> saleRecords)
    {
        if (saleRecords == null || saleRecords.isEmpty()) {
            System.out.println("Cannot edit: No sales records provided.");
            return;
        }
        Scanner input = new Scanner(System.in);

        System.out.println("=== Edit Sales Information === ");
        System.out.print("Enter Customer Name to find record: ");
        String editCustomerName = input.nextLine();

        SaleRecord recordToEdit = null;
        for (SaleRecord saleRecord : saleRecords) {
            if (saleRecord.customerName.equalsIgnoreCase(editCustomerName)) {
                recordToEdit = saleRecord;
                break;
            }
        }

        if (recordToEdit == null) {
            System.out.println("Sales Record Not Found.");
            return;
        }

        System.out.println("Sales Record Found:");
        ItemPurchased firstItem = recordToEdit.item.get(0);

        System.out.println("Model: " + firstItem.model + "   Quantity: " + firstItem.quantity);
        System.out.println("Total: RM" + recordToEdit.totalAmount);
        System.out.println("Transaction Method: " + recordToEdit.paymentMethod);

        System.out.println("Select number to edit:");
        System.out.println("1.Name 2.Model 3.Quantity 4.Total 5.Transaction Method");
        System.out.print(">");
        int choice = input.nextInt();
        input.nextLine(); // Consume the newline character

        switch (choice)
        {
            case 1 -> {
                System.out.print("Enter New Name: ");
                String newName = input.nextLine();
                System.out.print("Confirm Update? (Y/N): ");
                if (input.nextLine().equalsIgnoreCase("Y")) {
                    recordToEdit.customerName = newName;
                    rewriteSalesData(saleRecords);
                }
            }
            case 2 -> {
                System.out.print("Enter New Model Name: ");
                String newModel = input.nextLine();
                System.out.print("Confirm Update? (Y/N) :");
                if (input.nextLine().equalsIgnoreCase("Y"))
                {
                    recordToEdit.item.get(0).model = newModel;
                    rewriteSalesData(saleRecords);
                }
            }
            case 3 -> {
                System.out.println("Current Total Quantity: " + recordToEdit.item.get(0).quantity);
                System.out.println("Location Breakdown:");
                String[] locationNames = {
                    "Kuala Lumpur City Centre", "MidValley", "Sunway Velocity", "IOI City Mall", "Lalaport",
                    "Kuala Lumpur East Mall", "Nu Sentral", "Pavillion Kuala Lumpur", "1 Utama", "MyTown"
                };
                for(int i=0; i<10; i++) {
                    if(recordToEdit.item.get(0).location.get(i) > 0) {
                        System.out.println(locationNames[i] + " (C" + (60+i) + "): " + recordToEdit.item.get(0).location.get(i));
                    }
                }
                
                System.out.println("Select Location to edit:");
                int locationIndex = -1;
                while (locationIndex < 0 || locationIndex > 9) {
                    for(int i=0; i<10; i++) {
                        System.out.println((i+1) + ". " + locationNames[i] + " (C" + (60+i) + ")");
                    }
                    System.out.print("Enter selection (1-10): ");
                    if (input.hasNextInt()) {
                        locationIndex = input.nextInt() - 1;
                    }
                    input.nextLine();
                }
                
                System.out.print("Enter New Quantity for " + locationNames[locationIndex] + " (C" + (60+locationIndex) + "): ");
                int newQuantity = input.nextInt();
                input.nextLine(); // Consume newline
                System.out.print("Confirm Update? (Y/N): ");
                if (input.nextLine().equalsIgnoreCase("Y")) {
                    int oldLocQty = recordToEdit.item.get(0).location.get(locationIndex);
                    int delta = newQuantity - oldLocQty;
                    
                    recordToEdit.item.get(0).location.set(locationIndex, newQuantity);
                    recordToEdit.item.get(0).quantity = recordToEdit.item.get(0).quantity - oldLocQty + newQuantity;
                    
                    try {
                        if (delta != 0) {
                            List<Integer> deltaLocs = new ArrayList<>(Collections.nCopies(10, 0));
                            deltaLocs.set(locationIndex, delta);
                            ItemPurchased deltaItem = new ItemPurchased(recordToEdit.item.get(0).model, delta, recordToEdit.item.get(0).price, deltaLocs);
                            List<ItemPurchased> deltaItems = new ArrayList<>();
                            deltaItems.add(deltaItem);
                            SaleRecord deltaRecord = new SaleRecord(LocalDateTime.now(), "Delta", deltaItems, "None", 0, "None");
                            
                            SalesSystem.saveToModelCsv(deltaRecord);
                            SalesSystem.saveToStockCsv(deltaRecord);
                        }
                        rewriteSalesData(saleRecords);
                    } catch (IOException e) {
                        System.out.println("Error updating files: " + e.getMessage());
                    }
                }
            }
            case 4 -> {
                System.out.print("Enter New Total: ");
                double newTotal = input.nextDouble();
                input.nextLine(); // Consume newline
                System.out.print("Confirm Update? (Y/N): ");
                if (input.nextLine().equalsIgnoreCase("Y")) {
                    recordToEdit.totalAmount = newTotal;
                    rewriteSalesData(saleRecords);
                }
            }
            case 5 -> {
                System.out.print("Enter New Transaction Method: ");
                String newTransactionMethod = input.nextLine();
                System.out.print("Confirm Update? (Y/N): ");
                if (input.nextLine().equalsIgnoreCase("Y")) {
                    recordToEdit.paymentMethod = newTransactionMethod;
                    rewriteSalesData(saleRecords);
                }
            }
        }

        System.out.println("Sales information updated successfully.");
    }
    
    private void rewriteSalesData(List<SaleRecord> saleRecords) {
        try (java.io.FileWriter writer = new java.io.FileWriter("sales_data.csv")) {
            writer.write("Date,CustomerName,Model,Quantity,TotalAmount,PaymentMethod\n");
            for (SaleRecord record : saleRecords) {
                writer.write(record.toSalesCsvString());
            }
        } catch (IOException e) {
            System.out.println("Error rewriting sales data: " + e.getMessage());
        }
    }
}
