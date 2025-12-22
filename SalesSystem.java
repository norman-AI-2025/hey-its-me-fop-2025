import java.util.*;
import java.time.LocalDateTime;
import java.io.*;
import java.nio.*;

class ItemPurchased
{
    String model;
    int quantity;
    double price;
    List<Integer> location;

    public ItemPurchased(String model, int quantity, double price, int locationIndex)
    {
        this.model = model;
        this.quantity = quantity;
        this.price = price;
        this.location = new ArrayList<>(Collections.nCopies(10, 0));
        if (locationIndex >= 0 && locationIndex < 10) {
            this.location.set(locationIndex, quantity);
        }
    }
    
    public ItemPurchased(String model, int quantity, double price, List<Integer> locationQuantities)
    {
        this.model = model;
        this.quantity = quantity;
        this.price = price;
        this.location = new ArrayList<>(locationQuantities);
    }

    public double subtotal(int quantity, double price)
    {
        return (quantity * price);
    }
}

class Employee {
    String id;
    String name;

    public Employee(String id, String name) {
        this.id = id;
        this.name = name;
    }
}


class SaleRecord {
    LocalDateTime localDateTime;
    String customerName;
    List<ItemPurchased> item;
    String paymentMethod;
    double totalAmount;
    String employeeId;

    public SaleRecord(LocalDateTime localDateTime, String customerName, List<ItemPurchased> item, String paymentMethod, double totalAmount, String employeeId) {
        this.localDateTime = localDateTime;
        this.customerName = customerName;
        this.item = item;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.employeeId = employeeId;
    }

    public String toString(Map<String, String> employeeNames) {
        String date = localDateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String time = localDateTime.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
        String employeeName = employeeNames.getOrDefault(this.employeeId, "Unknown");

        StringBuilder receipt = new StringBuilder();
        receipt.append("Date: ").append(date).append(" Time: ").append(time).append("\n");
        receipt.append("Customer: ").append(customerName).append("\n");
        receipt.append("Item(s):\n");
        for (ItemPurchased items : item) {
            receipt.append("  - ").append(items.model).append(" Quantity: ").append(items.quantity).append("\n");
        }
        receipt.append("Total: RM").append(totalAmount).append("\n");
        receipt.append("Transaction Method: ").append(paymentMethod).append("\n");
        receipt.append("Employee: ").append(employeeName).append(" (").append(this.employeeId).append(")").append("\n");
        return receipt.toString();
    }

    public String toSalesCsvString()
    {
        StringBuilder csv = new StringBuilder();
        for (ItemPurchased items : this.item)
        {
            String date = this.localDateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            csv.append(date).append(",");
            csv.append(this.customerName).append(",");
            csv.append(items.model).append(",");
            csv.append(this.totalAmount).append(",");
            csv.append(this.paymentMethod).append(",");
            csv.append(this.employeeId).append("\n");
        }
        return csv.toString();
    }

    public String toModelCsvString()
    {
        StringBuilder csv = new StringBuilder();
        csv.append("Model,Price,C60,C61,C62,C63,C64,C65,C66,C67,C68,C69\n");
        for (ItemPurchased items : this.item)
        {
            csv.append(items.model).append(",");
            csv.append(items.price);
            for (int qty : items.location)
            {
                csv.append(",").append(qty);
            }
            csv.append("\n");
        }
        return csv.toString();
    }
}

public class SalesSystem
{

    public static String getValidPaymentMethod(String paymentMethod)
    {
        Scanner input = new Scanner(System.in);
         while (!(paymentMethod.equalsIgnoreCase("Credit card") || paymentMethod.equalsIgnoreCase("Cash") || paymentMethod.equalsIgnoreCase("Check") || paymentMethod.equalsIgnoreCase("e-wallet")))
         {
             System.out.println("Invalid Payment Method");
             System.out.print("Enter transaction method: ");
             paymentMethod = input.nextLine();
         }
         return paymentMethod;
    }

    public static SaleRecord recordNewSale(Scanner input, String employeeId) throws IOException
    {

        // getting the Data and the Time locally
        LocalDateTime localDateTime = LocalDateTime.now();
        String date = localDateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String time = localDateTime.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));

        // main function in the record New Sale System
        System.out.println("Date: " + date);
        System.out.println("Time: " + time);
        System.out.print("Customer Name: ");
        String customerName = input.nextLine();
        System.out.println("Item(s) Purchased: ");

        // way to study arraylist first to continue
        List<ItemPurchased> itemPurchased = new ArrayList<>();

        // declare and initialize variables
        double subtotal = 0;
        String moreItems ;

        //loop for items purchased
        do {
            System.out.print("Enter Model: ");
            String model = input.nextLine();
            System.out.print("Enter Quantity: ");
            int quantity = input.nextInt();
            System.out.print("Unit Price: ");
            double price = input.nextDouble();

            input.nextLine();

            int locationIndex = -1;
            String[] locationNames = {
                "Kuala Lumpur City Centre",
                "MidValley",
                "Sunway Velocity",
                "IOI City Mall",
                "Lalaport",
                "Kuala Lumpur East Mall",
                "Nu Sentral",
                "Pavillion Kuala Lumpur",
                "1 Utama",
                "MyTown"
            };

            // selection for location stock
            while (locationIndex < 0 || locationIndex > 9) {
                System.out.println("Select Location:");
                for(int i=0; i<10; i++) {
                    System.out.println((i+1) + ". " + locationNames[i] + " (C" + (60+i) + ")");
                }
                System.out.print("Enter selection (1-10): ");
                if (input.hasNextInt()) {
                    locationIndex = input.nextInt() - 1;
                }
                input.nextLine();
                if (locationIndex < 0 || locationIndex > 9) {
                    System.out.println("Invalid selection. Please try again.");
                }
            }

            itemPurchased.add(new ItemPurchased(model,quantity,price,locationIndex));
            subtotal += (double)(price * quantity);

            System.out.print("Are there more items purchased? (Y/N) : ");
            moreItems = input.nextLine();

        }while(moreItems.equalsIgnoreCase("Y"));

        // payment method

        // Get a valid payment method using the helper function
        System.out.print("Enter transaction method: ");
        String transactionMethod = getValidPaymentMethod(input.nextLine());

        System.out.println("Subtotal: "+ subtotal);
        System.out.println();

        // To record the Sales, pass the list of items
        SaleRecord newSale = new SaleRecord(localDateTime, customerName, itemPurchased, transactionMethod, subtotal, employeeId);

        System.out.println("Transaction successful.");
        System.out.println("Sale recorded successfully.");
        saveToSalesCsv(newSale);

        // Update the model csv
        System.out.println("Model quantities updated successfully.");
        saveToModelCsv(newSale);
        
        // Update the stock csv
        System.out.println("Stock quantities updated successfully.");
        saveToStockCsv(newSale);

        generateReceipt(newSale);

        return newSale;
    }

    public static void generateReceipt(SaleRecord saleRecord) throws IOException
    {
        System.out.println("Receipt generated: sales_" + saleRecord.localDateTime.toLocalDate() + ".txt");
        String filename = "Sales_" + saleRecord.localDateTime.toLocalDate() + ".txt";
        
        Map<String, String> employeeNames = loadEmployeeNames();

        try (FileWriter writer = new FileWriter(filename,true))
        {
            writer.write("============================================== \n");
            writer.write(saleRecord.toString(employeeNames));
            writer.write("============================================== \n");
        }
    }

    public static void saveToSalesCsv(SaleRecord saleRecord)
    {
        String fileName = "sales_data.csv";

        try(FileWriter writer = new FileWriter(fileName,true))
        {
            writer.write(saleRecord.toSalesCsvString());
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public static void saveToModelCsv(SaleRecord saleRecord) throws IOException
    {
        String fileName = "model.csv";
        Map<String, List<Integer>> modelStock = new LinkedHashMap<>();
        Map<String, Double> modelPrices = new HashMap<>();
        
        File file = new File(fileName);
        if (file.exists()) {
            try (Scanner scanner = new Scanner(file)) {
                if (scanner.hasNextLine()) {
                    String header = scanner.nextLine();
                    if (header.startsWith("Model,Price,C60")) {
                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            if (line.trim().isEmpty()) continue;
                            String[] parts = line.split(",");
                            if (parts.length >= 12) {
                                String m = parts[0];
                                double p = Double.parseDouble(parts[1]);
                                List<Integer> qtys = new ArrayList<>();
                                for(int i=0; i<10; i++) qtys.add(Integer.parseInt(parts[i+2]));
                                modelStock.put(m, qtys);
                                modelPrices.put(m, p);
                            }
                        }
                    } else {
                        // Handle old format or no header by re-reading
                    }
                }
            }
            
            // Re-read for migration if map is empty
            if (modelStock.isEmpty()) {
                 try (Scanner scanner = new Scanner(file)) {
                     while (scanner.hasNextLine()) {
                         String line = scanner.nextLine();
                         if (line.startsWith("Model,Price")) continue; 
                         String[] parts = line.split(",");
                         if (parts.length == 3) {
                             String m = parts[0];
                             double p = Double.parseDouble(parts[1]);
                             int q = Integer.parseInt(parts[2]);
                             List<Integer> qtys = new ArrayList<>(Collections.nCopies(10, 0));
                             qtys.set(0, q); // Default to C60
                             modelStock.put(m, qtys);
                             modelPrices.put(m, p);
                         }
                     }
                 }
            }
        }

        // Update with new sale
        for (ItemPurchased item : saleRecord.item) {
            modelStock.putIfAbsent(item.model, new ArrayList<>(Collections.nCopies(10, 0)));
            modelPrices.put(item.model, item.price);
            
            List<Integer> currentStock = modelStock.get(item.model);
            for(int i=0; i<10; i++) {
                int qtyToAdd = item.location.get(i);
                if (qtyToAdd > 0) {
                    currentStock.set(i, currentStock.get(i) + qtyToAdd);
                }
            }
        }

        // Write back
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("Model,Price,C60,C61,C62,C63,C64,C65,C66,C67,C68,C69");
            for (Map.Entry<String, List<Integer>> entry : modelStock.entrySet()) {
                writer.print(entry.getKey() + "," + modelPrices.get(entry.getKey()));
                for(int qty : entry.getValue()) {
                    writer.print("," + qty);
                }
                writer.println();
            }
        }
    }
    
    public static void saveToStockCsv(SaleRecord saleRecord) throws IOException {
        String fileName = "stock.csv";
        Map<String, Integer> stock = new LinkedHashMap<>();
        
        File file = new File(fileName);
        if (file.exists()) {
            try (Scanner scanner = new Scanner(file)) {
                if (scanner.hasNextLine()) scanner.nextLine(); // Skip header
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.trim().isEmpty()) continue;
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        String key = parts[0] + "," + parts[1]; // ModelName,OutletCode
                        stock.put(key, Integer.parseInt(parts[2]));
                    }
                }
            }
        }

        // Update with new sale
        for (ItemPurchased item : saleRecord.item) {
            for (int i = 0; i < 10; i++) {
                int qtySold = item.location.get(i);
                if (qtySold > 0) {
                    String outletCode = "C" + (60 + i);
                    String key = item.model + "," + outletCode;
                    int currentStock = stock.getOrDefault(key, 0);
                    stock.put(key, currentStock - qtySold); // Subtracting stock
                }
            }
        }

        // Write back
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("ModelName,OutletCode,Quantity");
            for (Map.Entry<String, Integer> entry : stock.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }
        }
    }
    
    public static List<SaleRecord> readRecords() throws IOException {
        List<SaleRecord> salesList = new ArrayList<>();
        Map<String, Double> modelPrices = new HashMap<>();
        Map<String, List<Integer>> modelLocations = new HashMap<>();
        String salesFile = "sales_data.csv";
        String modelFile = "model.csv";


        // Step 1: Read model prices and locations into a map for quick lookup
        File mFile = new File(modelFile);
        if (mFile.exists()) {
             try (Scanner modelScanner = new Scanner(new FileInputStream(modelFile))) {
                 while (modelScanner.hasNextLine()) {
                    String line = modelScanner.nextLine();
                    if (line.trim().isEmpty()) continue;
                    if (line.startsWith("Model,Price")) continue; // Skip header lines
                    
                    String[] parts = line.split(",");
                    if (parts.length >= 12) {
                        String modelName = parts[0];
                        double price = Double.parseDouble(parts[1]);
                        modelPrices.put(modelName, price);
                        
                        List<Integer> locs = modelLocations.getOrDefault(modelName, new ArrayList<>(Collections.nCopies(10, 0)));
                        for(int i=0; i<10; i++) {
                            locs.set(i, locs.get(i) + Integer.parseInt(parts[i+2]));
                        }
                        modelLocations.put(modelName, locs);
                    } else if (parts.length >= 3) {
                         // Old format fallback
                        try {
                            String modelName = parts[0];
                            double price = Double.parseDouble(parts[1]);
                            int qty = Integer.parseInt(parts[2]);
                            modelPrices.put(modelName, price);
                            
                            List<Integer> locs = modelLocations.getOrDefault(modelName, new ArrayList<>(Collections.nCopies(10, 0)));
                            locs.set(0, locs.get(0) + qty); // Add to C60
                            modelLocations.put(modelName, locs);
                        } catch (NumberFormatException e) {}
                    }
                }
            }
        }

        // Step 2: Read the sales data and build the SaleRecord objects
        File sFile = new File(salesFile);
        if (sFile.exists()) {
            try (Scanner salesScanner = new Scanner(new FileInputStream(salesFile))) {
                while (salesScanner.hasNextLine()) {
                    String line = salesScanner.nextLine();
                    if (line.startsWith("Date")) continue; // Skip header

                    String[] parts = line.split(",");
                    if (parts.length >= 5) {
                        try {
                            LocalDateTime dt;
                            try {
                                dt = LocalDateTime.parse(parts[0]);
                            } catch (Exception e) {
                                // Fallback for date only format
                                dt = java.time.LocalDate.parse(parts[0]).atStartOfDay();
                            }
                            
                            String customerName = parts[1];
                            String model = parts[2];
                            double totalAmount = Double.parseDouble(parts[3]);
                            String paymentMethod = parts[4];
                            String employeeId = (parts.length > 5) ? parts[5] : "Unknown";
                            
                            int quantity = 1; // Default quantity to 1 as it's not in the file
                            double price = modelPrices.getOrDefault(model, 0.0);
        
                            // Use the location info from model.csv (aggregated)
                            List<Integer> locs = modelLocations.getOrDefault(model, new ArrayList<>(Collections.nCopies(10, 0)));
                            
                            ItemPurchased item = new ItemPurchased(model, quantity, price, locs);
                            List<ItemPurchased> items = new ArrayList<>();
                            items.add(item);
        
                            salesList.add(new SaleRecord(dt, customerName, items, paymentMethod, totalAmount, employeeId));
                        } catch (Exception e) {
                            // Ignore malformed lines
                        }
                    }
                }
            }
        }
        return salesList;
    }
    
    public static Map<String, String> loadEmployeeNames() throws IOException {
        Map<String, String> employeeNames = new HashMap<>();
        File file = new File("employees.csv");
        if (file.exists()) {
            try (Scanner scanner = new Scanner(file)) {
                if (scanner.hasNextLine()) scanner.nextLine(); // Skip header
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        employeeNames.put(parts[0], parts[1]);
                    }
                }
            }
        }
        return employeeNames;
    }

    public static void SalesSystem(String employeeId)
    {
        try {

            Scanner input = new Scanner(System.in);

            List<SaleRecord> saleRecords = readRecords();
            Map<String, String> employeeNames = loadEmployeeNames();
            String employeeName = employeeNames.getOrDefault(employeeId, "Unknown User");

            System.out.println("Welcome to Sales System, " + employeeName);

            int choice;

            SearchSystem searchSystem = new SearchSystem();
            EditSystem editSystem = new EditSystem();

            do {
                System.out.println("1. Record New Sale");
                System.out.println("2. Search Stock Info");
                System.out.println("3. Search Sales Info");
                System.out.println("4. Edit Sale Info");
                System.out.println("5. Edit Stock Info");
                System.out.print("Select the action you want to perform (-1 to exit): ");
                choice = input.nextInt();
                input.nextLine();

                switch (choice) {
                    case 1 -> {
                        System.out.println();
                        SaleRecord newSale = recordNewSale(input, employeeId);
                        saleRecords.add(newSale);
                    }
                    case 2 -> {
                        System.out.println();
                        searchSystem.searchStockInfo();
                    }
                    case 3 -> {
                        System.out.println();
                        searchSystem.searchSalesInfo(saleRecords);
                    }
                    case 4 -> {
                        System.out.println();
                        editSystem.EditSalesInfo(saleRecords);
                    }
                    case 5 -> {
                        System.out.println();
                        editSystem.EditStockInfo(saleRecords);
                    }
                }
            } while (choice != -1);

        }catch (Exception e)
        {
            System.out.println(e.getMessage()+" Something wrong happening!!");
        }
    }

    public static void main(String[] args) throws Exception
    {
        // Simplified login
        Scanner input = new Scanner(System.in);
        System.out.print("Enter Employee ID to login: ");
        String employeeId = input.nextLine();
        
        SalesSystem(employeeId);
    }
}
