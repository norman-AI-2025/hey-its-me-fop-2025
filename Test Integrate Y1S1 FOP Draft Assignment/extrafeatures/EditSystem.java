package extrafeatures;

import java.io.*;
import java.util.*;

public class EditSystem {
    private static final Scanner INPUT = new Scanner(System.in);

    /* ================= EDIT SALES ================= */
    public void EditSalesInfo(List<SaleRecord> salesList) {
        System.out.print("Enter Transaction Date (YYYY-MM-DD): ");
        String dateStr = INPUT.nextLine();
        System.out.print("Enter Customer Name: ");
        String custName = INPUT.nextLine();

        SaleRecord record = null;
        for (SaleRecord s : salesList) {
            if (s.date.toString().equalsIgnoreCase(dateStr) &&
                s.customerName.equalsIgnoreCase(custName)) {
                record = s;
                break;
            }
        }

        if (record == null) {
            System.out.println("Sales record not found.");
            return;
        }

        System.out.println("\n1. Customer Name");
        System.out.println("2. Model Name");
        System.out.println("3. Quantity");
        System.out.println("4. Total Price");
        System.out.println("5. Transaction Method");
        System.out.print("Select field to edit: ");

        int choice = INPUT.nextInt();
        INPUT.nextLine();

        switch (choice) {
            case 1 -> {
                System.out.print("New Customer Name: ");
                record.customerName = INPUT.nextLine();
            }
            case 2 -> {
                System.out.print("New Model Name: ");
                record.item.modelName = INPUT.nextLine();
            }
            case 3 -> {
                System.out.print("New Quantity: ");
                record.item.quantity = INPUT.nextInt();
                INPUT.nextLine();
            }
            case 4 -> {
                System.out.print("New Total Price: ");
                record.item.totalPrice = INPUT.nextDouble();
                INPUT.nextLine();
            }
            case 5 -> {
                System.out.print("New Payment Method: ");
                record.paymentMethod = INPUT.nextLine();
            }
            default -> System.out.println("Invalid option.");
        }

        rewriteSales(salesList);
        System.out.println("Sales record updated.");
    }

    /* ================= EDIT STOCK ================= */
    public void EditStockInfo() {
        System.out.print("Enter Model Name: ");
        String model = INPUT.nextLine();

        System.out.println("""
        C60 Kuala Lumpur City Centre
        C61 MidValley
        C62 Sunway Velocity
        C63 IOI City Mall
        C64 Lalaport
        C65 Kuala Lumpur East Mall
        C66 Nu Sentral
        C67 Pavillion Kuala Lumpur
        C68 1 Utama
        C69 MyTown
        """);

        System.out.print("Enter Outlet Code: ");
        String outlet = INPUT.nextLine();

        int currentQty = -1;
        try (BufferedReader br = new BufferedReader(new FileReader("stock.csv"))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p[0].equalsIgnoreCase(model) && p[1].equalsIgnoreCase(outlet)) {
                    try { currentQty = Integer.parseInt(p[2].trim()); } catch (Exception ignored) {}
                    break;
                }
            }
        } catch (IOException ignored) {}

        if (currentQty >= 0) {
            System.out.println("Current Stock: " + currentQty);
        }

        System.out.print("Enter New Quantity: ");
        int newQty = INPUT.nextInt();
        INPUT.nextLine();

        File inputFile = new File("stock.csv");
        File tempFile = new File("stock_tmp.csv");

        try (
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile))
        ) {
            pw.println(br.readLine());

            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p[0].equalsIgnoreCase(model) && p[1].equalsIgnoreCase(outlet)) {
                    pw.println(model + "," + outlet + "," + newQty);
                } else {
                    pw.println(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error updating stock.");
            return;
        }

        inputFile.delete();
        tempFile.renameTo(inputFile);
        System.out.println("Stock updated successfully.");
    }

    /* ================= EDIT STOCK COUNT ================= */
    public void EditStockCountInfo() {
        System.out.print("Enter Date (YYYY-MM-DD): ");
        String date = INPUT.nextLine();

        System.out.print("Enter Outlet Code: ");
        String outlet = INPUT.nextLine();

        System.out.println("1. Morning Stock Count");
        System.out.println("2. Night Stock Count");
        System.out.print("Select count type: ");
        String choice = INPUT.nextLine();
        String countType = choice.equals("2") ? "Night Stock Count" : "Morning Stock Count";

        System.out.print("Enter Model Name: ");
        String model = INPUT.nextLine();

        System.out.print("Enter New Counted Quantity: ");
        int newQty = INPUT.nextInt();

        File inputFile = new File("stock_count.csv");
        File tempFile = new File("stock_count_tmp.csv");
        boolean updated = false;

        try (
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile))
        ) {
            String header = br.readLine();
            if (header == null || header.trim().length() == 0) {
                header = "Date,Time,OutletCode,CountType,ModelName,SystemQty,CountedQty,Diff";
            }
            pw.println(header);

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().length() == 0) continue;
                String[] p = line.split(",", -1);
                if (!updated && p.length >= 7 &&
                    p[0].trim().equalsIgnoreCase(date) &&
                    p[2].trim().equalsIgnoreCase(outlet) &&
                    p[3].trim().equalsIgnoreCase(countType) &&
                    p[4].trim().equalsIgnoreCase(model)) {
                    int systemQty = 0;
                    try { systemQty = Integer.parseInt(p[5].trim()); } catch (Exception ignored) {}
                    int diff = newQty - systemQty;
                    String time = p[1].trim();
                    pw.println(date + "," + time + "," + outlet + "," + countType + "," + model + "," + systemQty + "," + newQty + "," + diff);
                    updated = true;
                } else {
                    pw.println(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error updating stock count.");
            return;
        }

        inputFile.delete();
        tempFile.renameTo(inputFile);
        System.out.println(updated ? "Stock count updated successfully." : "Stock count record not found.");
    }

    public boolean editStock(String model, String outlet, int newQty) {
        File inputFile = new File("stock.csv");
        File tempFile = new File("stock_tmp.csv");
        boolean updated = false;

        try (
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile))
        ) {
            pw.println(br.readLine());
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length >= 3 &&
                    p[0].trim().equalsIgnoreCase(model) &&
                    p[1].trim().equalsIgnoreCase(outlet)) {
                    pw.println(model + "," + outlet + "," + newQty);
                    updated = true;
                } else {
                    pw.println(line);
                }
            }
        } catch (IOException e) {
            return false;
        }

        inputFile.delete();
        tempFile.renameTo(inputFile);
        return updated;
    }

    public boolean editSales(String date, String customer, String field, String newValue) {
        File inputFile = new File("sales_data.csv");
        File tempFile = new File("sales_tmp.csv");
        boolean updated = false;

        try (
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile))
        ) {
            String header = br.readLine();
            if (header == null || header.trim().length() == 0) {
                header = "SalesID,Date,Time,CustomerName,ModelName,Quantity,TotalPrice,EmployeeID,PaymentMethod";
            }
            pw.println(header);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().length() == 0) continue;
                String[] p = line.split(",", -1);
                String[] row = normalizeSalesRow(p);
                if (!updated &&
                    row[1].trim().equalsIgnoreCase(date) &&
                    row[3].trim().equalsIgnoreCase(customer)) {
                    if ("Customer Name".equals(field)) row[3] = newValue;
                    else if ("Model Name".equals(field)) row[4] = newValue;
                    else if ("Quantity".equals(field)) row[5] = newValue;
                    else if ("Total Price".equals(field)) row[6] = newValue;
                    else if ("Payment Method".equals(field)) row[8] = newValue;
                    updated = true;
                    pw.println(joinCsv(row, row.length));
                } else {
                    pw.println(line);
                }
            }
        } catch (IOException e) {
            return false;
        }

        inputFile.delete();
        tempFile.renameTo(inputFile);
        return updated;
    }

    public boolean editStockCount(String date, String outlet, String countType, String model, int newQty) {
        File inputFile = new File("stock_count.csv");
        File tempFile = new File("stock_count_tmp.csv");
        boolean updated = false;

        try (
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile))
        ) {
            String header = br.readLine();
            if (header == null || header.trim().length() == 0) {
                header = "Date,Time,OutletCode,CountType,ModelName,SystemQty,CountedQty,Diff";
            }
            pw.println(header);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().length() == 0) continue;
                String[] p = line.split(",", -1);
                if (!updated && p.length >= 7 &&
                    p[0].trim().equalsIgnoreCase(date) &&
                    p[2].trim().equalsIgnoreCase(outlet) &&
                    p[3].trim().equalsIgnoreCase(countType) &&
                    p[4].trim().equalsIgnoreCase(model)) {
                    int systemQty = 0;
                    try { systemQty = Integer.parseInt(p[5].trim()); } catch (Exception ignored) {}
                    int diff = newQty - systemQty;
                    String time = p[1].trim();
                    pw.println(date + "," + time + "," + outlet + "," + countType + "," + model + "," + systemQty + "," + newQty + "," + diff);
                    updated = true;
                } else {
                    pw.println(line);
                }
            }
        } catch (IOException e) {
            return false;
        }

        inputFile.delete();
        tempFile.renameTo(inputFile);
        return updated;
    }

    private String[] normalizeSalesRow(String[] p) {
        if (p.length >= 9) return p;
        String[] out = new String[9];
        for (int i = 0; i < out.length; i++) out[i] = "";

        if (p.length >= 8) {
            out[0] = p[0];
            out[1] = p[1];
            out[2] = "N/A";
            out[3] = p[2];
            out[4] = p[3];
            out[5] = p[4];
            out[6] = p[5];
            out[7] = p[6];
            out[8] = p[7];
            return out;
        }
        if (p.length >= 6) {
            out[0] = p[0];
            out[1] = p[1];
            out[2] = "N/A";
            out[3] = p[2];
            out[4] = p[3];
            out[5] = p[4];
            out[6] = p[5];
            out[7] = "N/A";
            out[8] = "N/A";
            return out;
        }
        return p;
    }

    private String joinCsv(String[] p, int len) {
        if (len <= 0) return "";
        String out = p[0];
        for (int i = 1; i < len; i++) {
            out += "," + p[i];
        }
        return out;
    }

    private void rewriteSales(List<SaleRecord> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("sales_data.csv"))) {
            pw.println("SalesID,Date,Time,CustomerName,ModelName,Quantity,TotalPrice,EmployeeID,PaymentMethod");
            for (SaleRecord s : list) pw.println(s.toCsvString());
        } catch (IOException e) {
            System.out.println("Error rewriting sales file.");
        }
    }
}
