package fop_assignment_2025;

import java.io.*;
import java.time.LocalDate;
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
            System.out.print("Select action (-1 to exit): ");

            choice = input.nextInt();
            input.nextLine();

            switch (choice) {
                case 1 -> {
                    SaleRecord newSale = recordNewSale(input, employeeId);
                    if (newSale != null) {
                        salesList.add(newSale);
                        appendSale(newSale);
                    }
                }
                case 2 -> searchSystem.searchStockInfo();
                case 3 -> searchSystem.searchSalesInfo(salesList);
                case 4 -> editSystem.EditSalesInfo(salesList);
                case 5 -> editSystem.EditStockInfo();
            }
        } while (choice != -1);
    }

    /* ================= RECORD NEW SALE ================= */
    private static SaleRecord recordNewSale(Scanner input, String employeeId) {
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

            SaleRecord record = new SaleRecord(
                saleId,
                date,
                customerName,
                modelName,
                quantity,
                totalPrice,
                employeeId,
                paymentMethod,
                outletCode
            );

            updateStock(modelName, outletCode, -quantity);
            return record;

        } catch (Exception e) {
            System.out.println("Error recording sale: " + e.getMessage());
            return null;
        }
    }

    /* ================= FILE HELPERS ================= */
    private static void appendSale(SaleRecord record) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter("sales_data.csv", true))) {
            pw.println(record.toCsvString());
        }
    }

    private static List<SaleRecord> readRecords() throws IOException {
        List<SaleRecord> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("sales_data.csv"))) {
            br.readLine(); // header
            String line;
            while ((line = br.readLine()) != null) {
                list.add(SaleRecord.fromCsv(line));
            }
        }
        return list;
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
        try (BufferedReader br = new BufferedReader(new FileReader("employee.csv"))) {
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
    public String customerName;
    public ItemPurchased item;
    public String employeeId;
    public String paymentMethod;

    public SaleRecord(String salesId, LocalDate date, String customerName,
                      String modelName, int quantity, double totalPrice,
                      String employeeId, String paymentMethod, String outletCode) {

        this.salesId = salesId;
        this.date = date;
        this.customerName = customerName;
        this.item = new ItemPurchased(modelName, quantity, totalPrice, outletCode);
        this.employeeId = employeeId;
        this.paymentMethod = paymentMethod;
    }

    public String toCsvString() {
        return salesId + "," +
               date.format(DateTimeFormatter.ISO_DATE) + "," +
               customerName + "," +
               item.modelName + "," +
               item.quantity + "," +
               item.totalPrice + "," +
               employeeId + "," +
               paymentMethod;
    }

    public static SaleRecord fromCsv(String line) {
        String[] p = line.split(",");
        return new SaleRecord(
                p[0],
                LocalDate.parse(p[1]),
                p[2],
                p[3],
                Integer.parseInt(p[4]),
                Double.parseDouble(p[5]),
                p[6],
                p[7],
                "UNKNOWN"
        );
    }
}