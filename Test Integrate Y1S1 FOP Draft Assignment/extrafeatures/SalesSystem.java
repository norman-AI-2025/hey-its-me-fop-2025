package extrafeatures;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SalesSystem {

    /* ================= ENTRY POINT ================= */
    public static void SalesSystem(String employeeId) throws Exception {
        Scanner input = new Scanner(System.in);
        List<SaleRecord> salesList;

        try {
            salesList = readRecords();
        } catch (Exception e) {
            salesList = new ArrayList<>();
        }

        Map<String, String> employeeNames = loadEmployeeNames();
        String employeeName = employeeNames.getOrDefault(employeeId, "Unknown");

        System.out.println("Welcome to Sales System, " + employeeName);

        SearchSystem searchSystem = new SearchSystem();
        EditSystem editSystem = new EditSystem();

        int choice;
        do {
            System.out.println("\n=== Sales System Menu ===");
            System.out.println("1. Record New Sale");
            System.out.println("2. Search Stock Info");
            System.out.println("3. Search Sales Info");
            System.out.println("4. Edit Sale Info");
            System.out.println("5. Edit Stock Info");
            System.out.println("6. Edit Stock Count Info");
            System.out.print("Select action (-1 to exit): ");

            choice = input.nextInt();
            input.nextLine();

            switch (choice) {
                case 1 -> {
                    SaleRecord newSale = recordNewSale(input, employeeId, employeeName);
                    if (newSale != null) {
                        salesList.add(newSale);
                        appendSale(newSale);
                    }
                }
                case 2 -> searchSystem.searchStockInfo();
                case 3 -> searchSystem.searchSalesInfo(salesList);
                case 4 -> editSystem.EditSalesInfo(salesList);
                case 5 -> editSystem.EditStockInfo();
                case 6 -> editSystem.EditStockCountInfo();
            }
        } while (choice != -1);
    }

    /* ================= RECORD NEW SALE ================= */
    private static SaleRecord recordNewSale(Scanner input, String employeeId, String employeeName) {
        try {
            System.out.print("Customer Name: ");
            String customerName = input.nextLine();

            System.out.print("Model Name: ");
            String modelName = input.nextLine();

            System.out.print("Quantity: ");
            int quantity = input.nextInt();
            input.nextLine();

            // Outlet selection (1–10)
            String[] outletCodes = {
                "C60","C61","C62","C63","C64",
                "C65","C66","C67","C68","C69"
            };

            String[] outletNames = {
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

            int outletIndex = -1;
            while (outletIndex < 0 || outletIndex > 9) {
                System.out.println("Select Outlet:");
                for (int i = 0; i < 10; i++) {
                    System.out.println((i + 1) + ". " + outletNames[i] + " (" + outletCodes[i] + ")");
                }
                System.out.print("Enter choice (1-10): ");
                outletIndex = input.nextInt() - 1;
                input.nextLine();
            }

            String outletCode = outletCodes[outletIndex];

            System.out.print("Payment Method: ");
            String paymentMethod = input.nextLine();

            double price = getModelPrice(modelName);
            double totalPrice = price * quantity;

            String saleId = generateSaleId();
            LocalDate date = LocalDate.now();
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")).toLowerCase();

            SaleRecord record = new SaleRecord(
                saleId,
                date,
                time,
                customerName,
                modelName,
                quantity,
                totalPrice,
                employeeId,
                paymentMethod,
                outletCode
            );

            updateStock(modelName, outletCode, -quantity);
            ReceiptGenerator printer = new ReceiptGenerator();
            printer.generateSalesReceipt(customerName, modelName, quantity, totalPrice, paymentMethod, employeeName);
            System.out.println("Receipt generated: sales_" + date + ".txt");
            return record;

        } catch (Exception e) {
            System.out.println("Error recording sale: " + e.getMessage());
            return null;
        }
    }

    /* ================= FILE HELPERS ================= */
    private static void appendSale(SaleRecord record) throws IOException {
        ensureSalesHeader();
        try (PrintWriter pw = new PrintWriter(new FileWriter("sales_data.csv", true))) {
            pw.println(record.toCsvString());
        }
    }

    // GUI helper (array-based, no Scanner)
    public static SaleRecord[] recordSaleItems(String employeeId, String employeeName, String customerName,
                                               String paymentMethod, String outletCode,
                                               String[] modelNames, int[] quantities, int size) throws Exception {
        if (size <= 0) return new SaleRecord[0];

        String saleId = generateSaleId();
        LocalDate date = LocalDate.now();
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")).toLowerCase();

        SaleRecord[] records = new SaleRecord[size];
        double[] unitPrices = new double[size];

        for (int i = 0; i < size; i++) {
            double price = getModelPrice(modelNames[i]);
            unitPrices[i] = price;
            double totalPrice = price * quantities[i];

            SaleRecord record = new SaleRecord(
                saleId,
                date,
                time,
                customerName,
                modelNames[i],
                quantities[i],
                totalPrice,
                employeeId,
                paymentMethod,
                outletCode
            );
            updateStock(modelNames[i], outletCode, -quantities[i]);
            records[i] = record;
        }

        for (int i = 0; i < size; i++) appendSale(records[i]);

        ReceiptGenerator printer = new ReceiptGenerator();
        printer.generateSalesReceipt(customerName, modelNames, quantities, unitPrices, size, paymentMethod, employeeName);
        System.out.println("Receipt generated: sales_" + date + ".txt");

        return records;
    }

    private static List<SaleRecord> readRecords() throws IOException {
        List<SaleRecord> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("sales_data.csv"))) {
            br.readLine(); // header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().length() == 0) continue;
                list.add(SaleRecord.fromCsv(line));
            }
        }
        return list;
    }

    private static void ensureSalesHeader() throws IOException {
        File f = new File("sales_data.csv");
        if (!f.exists() || f.length() == 0) {
            PrintWriter pw = new PrintWriter(new FileWriter(f));
            pw.println("SalesID,Date,Time,CustomerName,ModelName,Quantity,TotalPrice,EmployeeID,PaymentMethod");
            pw.close();
            return;
        }
        BufferedReader br = new BufferedReader(new FileReader(f));
        String header = br.readLine();
        br.close();
        if (header == null || header.toLowerCase().indexOf("time") < 0) {
            File temp = new File("sales_tmp_header.csv");
            PrintWriter pw = new PrintWriter(new FileWriter(temp));
            pw.println("SalesID,Date,Time,CustomerName,ModelName,Quantity,TotalPrice,EmployeeID,PaymentMethod");
            BufferedReader br2 = new BufferedReader(new FileReader(f));
            br2.readLine();
            String line;
            while ((line = br2.readLine()) != null) {
                pw.println(line);
            }
            br2.close();
            pw.close();
            f.delete();
            temp.renameTo(f);
        }
    }

    /* ================= UTILITIES ================= */
    private static String generateSaleId() throws IOException {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("sales_data.csv"))) {
            br.readLine();
            while (br.readLine() != null) count++;
        }
        return String.format("S%04d", count + 1);
    }

    private static double getModelPrice(String modelName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("model.csv"))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p[0].equalsIgnoreCase(modelName)) {
                    return Double.parseDouble(p[1]);
                }
            }
        }
        throw new IOException("Model not found");
    }

    private static void updateStock(String modelName, String outletCode, int delta) throws IOException {
        File input = new File("stock.csv");
        File temp = new File("stock_tmp.csv");

        try (
            BufferedReader br = new BufferedReader(new FileReader(input));
            PrintWriter pw = new PrintWriter(new FileWriter(temp))
        ) {
            pw.println(br.readLine());
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p[0].equalsIgnoreCase(modelName) && p[1].equals(outletCode)) {
                    int qty = Integer.parseInt(p[2]) + delta;
                    pw.println(p[0] + "," + p[1] + "," + qty);
                } else {
                    pw.println(line);
                }
            }
        }
        input.delete();
        temp.renameTo(input);
    }

    /* ================= EMPLOYEE ================= */
    public static Map<String, String> loadEmployeeNames() {
        Map<String, String> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("employees.csv"))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                map.put(p[0], p[1]);
            }
        } catch (IOException ignored) {}
        return map;
    }
}

class ItemPurchased {
    public String modelName;
    public int quantity;
    public double totalPrice;
    public String outletCode;

    public ItemPurchased(String modelName, int quantity, double totalPrice, String outletCode) {
        this.modelName = modelName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.outletCode = outletCode;
    }
}

class SaleRecord {
    public String salesId;
    public LocalDate date;
    public String time;
    public String customerName;
    public ItemPurchased item;
    public String employeeId;
    public String paymentMethod;

    public SaleRecord(String salesId, LocalDate date, String time, String customerName,
                      String modelName, int quantity, double totalPrice,
                      String employeeId, String paymentMethod, String outletCode) {
        this.salesId = salesId;
        this.date = date;
        this.time = time;
        this.customerName = customerName;
        this.item = new ItemPurchased(modelName, quantity, totalPrice, outletCode);
        this.employeeId = employeeId;
        this.paymentMethod = paymentMethod;
    }

    public String toCsvString() {
        return salesId + "," + date + "," + time + "," + customerName + "," +
            item.modelName + "," + item.quantity + "," + item.totalPrice + "," +
            employeeId + "," + paymentMethod;
    }

    public static SaleRecord fromCsv(String line) {
        String[] p = line.split(",", -1);
        String salesId = (p.length > 0) ? p[0].trim() : "";
        String dateStr = (p.length > 1) ? p[1].trim() : "1970-01-01";
        String time = (p.length > 2) ? p[2].trim() : "N/A";
        String customer = (p.length > 3) ? p[3].trim() : "";
        String model = (p.length > 4) ? p[4].trim() : "";
        int qty = (p.length > 5) ? parseIntSafe(p[5].trim()) : 0;
        double total = (p.length > 6) ? parseDoubleSafe(p[6].trim()) : 0.0;
        String employeeId = (p.length > 7) ? p[7].trim() : "";
        String method = (p.length > 8) ? p[8].trim() : "";
        LocalDate date = LocalDate.parse(dateStr);
        return new SaleRecord(salesId, date, time, customer, model, qty, total, employeeId, method, "");
    }

    private static int parseIntSafe(String value) {
        try { return Integer.parseInt(value); } catch (Exception e) { return 0; }
    }

    private static double parseDoubleSafe(String value) {
        try { return Double.parseDouble(value); } catch (Exception e) { return 0.0; }
    }
}
