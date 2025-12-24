package fop_assignment_2025;

import java.io.*;
import java.util.*;

public class EditSystem {

    /* ================= EDIT SALES ================= */
    public void EditSalesInfo(List<SaleRecord> salesList) {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter Sales ID to edit: ");
        String sid = input.nextLine();

        SaleRecord record = null;
        for (SaleRecord s : salesList) {
            if (s.salesId.equalsIgnoreCase(sid)) {
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
        System.out.println("4. Payment Method");
        System.out.print("Select field to edit: ");

        int choice = input.nextInt();
        input.nextLine();

        switch (choice) {
            case 1 -> {
                System.out.print("New Customer Name: ");
                record.customerName = input.nextLine();
            }
            case 2 -> {
                System.out.print("New Model Name: ");
                record.item.modelName = input.nextLine();
            }
            case 3 -> {
                System.out.print("New Quantity: ");
                record.item.quantity = input.nextInt();
                input.nextLine();
            }
            case 4 -> {
                System.out.print("New Payment Method: ");
                record.paymentMethod = input.nextLine();
            }
            default -> System.out.println("Invalid option.");
        }

        rewriteSales(salesList);
        System.out.println("Sales record updated.");
    }

    /* ================= EDIT STOCK ================= */
    public void EditStockInfo() {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter Model Name: ");
        String model = input.nextLine();

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
        String outlet = input.nextLine();

        System.out.print("Enter New Quantity: ");
        int newQty = input.nextInt();

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

    private void rewriteSales(List<SaleRecord> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("sales_data.csv"))) {
            pw.println("SalesID,Date,CustomerName,ModelName,Quantity,TotalPrice,EmployeeID,PaymentMethod");
            for (SaleRecord s : list) pw.println(s.toCsvString());
        } catch (IOException e) {
            System.out.println("Error rewriting sales file.");
        }
    }
}
