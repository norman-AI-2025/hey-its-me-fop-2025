package extrafeatures;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

import extrafeatures.Models.EmployeeInfo;
import extrafeatures.Models.SaleItem;

public class ExtraStore {

    public static final String DATA_DIR = "data";
    public static final String RECEIPTS_DIR = "receipts";

    public static final String ATT_CSV = DATA_DIR + "/extra_attendance.csv";
    public static final String SALES_CSV = DATA_DIR + "/extra_sales.csv";
    public static final String ITEMS_CSV = DATA_DIR + "/extra_sales_items.csv";
    public static final String EMAIL_LOG = DATA_DIR + "/extra_email_log.txt";
    public static final String EMAIL_SETTINGS = DATA_DIR + "/extra_email_settings.csv";
    public static final String STOCK_COUNT_CSV = DATA_DIR + "/extra_stock_count.csv";

    public static void init() throws IOException {
        ensureDir(DATA_DIR);
        ensureDir(RECEIPTS_DIR);

        ensureFileWithHeader(ATT_CSV, "Date,EmployeeID,EmployeeName,OutletCode,ClockIn,ClockOut,TotalHours");
        ensureFileWithHeader(SALES_CSV, "SaleID,Date,Time,EmployeeID,EmployeeName,OutletCode,CustomerName,PaymentMethod,TotalAmount,ReceiptFile");
        ensureFileWithHeader(ITEMS_CSV, "SaleID,Model,Qty,UnitPrice,LineTotal");
        ensureFile(EMAIL_LOG);
        ensureFileWithHeader(EMAIL_SETTINGS, "FromGmail,ToEmail,AppPassword");
        ensureFileWithHeader(STOCK_COUNT_CSV, "Date,Time,OutletCode,CountType,ModelName,SystemQty,CountedQty,Diff");
    }

    public static void initEmailOnly() throws IOException {
        ensureDir(DATA_DIR);
        ensureDir(RECEIPTS_DIR);
        ensureFile(EMAIL_LOG);
        ensureFileWithHeader(EMAIL_SETTINGS, "FromGmail,ToEmail,AppPassword");
    }

    // ---------- Email settings ----------
    public static void saveEmailSettings(String from, String to, String appPass) throws IOException {
        initEmailOnly();
        BufferedWriter bw = new BufferedWriter(new FileWriter(EMAIL_SETTINGS, false));
        bw.write("FromGmail,ToEmail,AppPassword"); bw.newLine();
        bw.write(from + "," + to + "," + appPass); bw.newLine();
        bw.close();
    }

    public static String[] loadEmailSettings() throws IOException {
        initEmailOnly();
        BufferedReader br = new BufferedReader(new FileReader(EMAIL_SETTINGS));
        br.readLine(); // header
        String line = br.readLine();
        br.close();
        if (line == null) return null;
        String[] p = split(line);
        if (p.length < 3) return null;
        return new String[]{p[0].trim(), p[1].trim(), p[2].trim()};
    }

    public static void logEmail(String date, String toEmail, double total, String receipt, String status) throws IOException {
        initEmailOnly();
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        appendLine(EMAIL_LOG, "[" + now + "] To=" + toEmail + " Date=" + date + " Total=" + fmt2(total) +
                " Receipt=" + receipt + " Status=" + status);
    }

    public static boolean alreadySentToday(String date) throws IOException {
        initEmailOnly();
        BufferedReader br = new BufferedReader(new FileReader(EMAIL_LOG));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains("Date=" + date) && line.contains("Status=SENT")) {
                br.close();
                return true;
            }
        }
        br.close();
        return false;
    }

    // ---------- Attendance ----------
    public static void logClockIn(EmployeeInfo emp) throws IOException {
        init();
        String date = LocalDate.now().toString();
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        appendLine(ATT_CSV, date + "," + emp.id + "," + emp.name + "," + emp.outletCode + "," + time + ",,");
    }

    public static void logClockOut(EmployeeInfo emp, double hours) throws IOException {
        init();
        String date = LocalDate.now().toString();
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        appendLine(ATT_CSV, date + "," + emp.id + "," + emp.name + "," + emp.outletCode + ",," + time + "," + fmt2(hours));
    }

    // ---------- Sales ----------
    public static String newSaleIdForToday() throws IOException {
        init();
        String date = LocalDate.now().toString();
        int count = 0;

        BufferedReader br = new BufferedReader(new FileReader(SALES_CSV));
        br.readLine();
        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().length() == 0) continue;
            String[] p = split(line);
            if (p.length > 1 && date.equals(p[1].trim())) count++;
        }
        br.close();

        return "XS" + date.replace("-", "") + "-" + String.format("%04d", count + 1);
    }

    public static void logSale(EmployeeInfo emp, String saleId, String customer, String method,
                              SaleItem[] items, String receiptPath) throws IOException {
        init();
        String date = LocalDate.now().toString();
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        double total = 0.0;
        for (int i = 0; i < items.length; i++) total += items[i].lineTotal();

        if (receiptPath == null || receiptPath.trim().length() == 0) {
            receiptPath = RECEIPTS_DIR + "/sales_" + date + ".txt";
        }

        appendLine(SALES_CSV,
                saleId + "," + date + "," + time + "," + emp.id + "," + emp.name + "," + emp.outletCode + "," +
                        customer + "," + method + "," + fmt2(total) + "," + receiptPath);

        for (int i = 0; i < items.length; i++) {
            SaleItem it = items[i];
            appendLine(ITEMS_CSV,
                    saleId + "," + it.model + "," + it.qty + "," + fmt2(it.unitPrice) + "," + fmt2(it.lineTotal()));
        }

        ensureDailyReceipt(date);
        appendReceipt(date, saleId, emp, customer, method, items, total);
    }

    public static String getReceiptPathForDate(String date) throws IOException {
        init();
        BufferedReader br = new BufferedReader(new FileReader(SALES_CSV));
        br.readLine();
        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().length() == 0) continue;
            String[] p = split(line);
            if (p.length >= 10 && date.equals(p[1].trim())) { br.close(); return p[9].trim(); }
        }
        br.close();
        return RECEIPTS_DIR + "/sales_" + date + ".txt";
    }

    // ---------- Stock count ----------
    public static void logStockCount(String countType, String outlet, String model, int systemQty, int countedQty) throws IOException {
        init();
        String date = LocalDate.now().toString();
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        int diff = countedQty - systemQty;
        appendLine(STOCK_COUNT_CSV, date + "," + time + "," + outlet + "," + countType + "," + model + "," + systemQty + "," + countedQty + "," + diff);
    }

    public static boolean updateStockCount(String date, String outlet, String countType, String model, int newCounted) throws IOException {
        init();
        File input = new File(STOCK_COUNT_CSV);
        File temp = new File(DATA_DIR + "/extra_stock_count_tmp.csv");
        boolean updated = false;

        BufferedReader br = new BufferedReader(new FileReader(input));
        PrintWriter pw = new PrintWriter(new FileWriter(temp));
        String header = br.readLine();
        if (header == null || header.trim().length() == 0) {
            header = "Date,Time,OutletCode,CountType,ModelName,SystemQty,CountedQty,Diff";
        }
        pw.println(header);

        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().length() == 0) continue;
            String[] p = split(line);
            if (!updated && p.length >= 7 &&
                p[0].trim().equalsIgnoreCase(date) &&
                p[2].trim().equalsIgnoreCase(outlet) &&
                p[3].trim().equalsIgnoreCase(countType) &&
                p[4].trim().equalsIgnoreCase(model)) {

                int systemQty = parseIntSafe(p[5]);
                int diff = newCounted - systemQty;
                String time = (p.length > 1) ? p[1].trim() : "";
                pw.println(date + "," + time + "," + outlet + "," + countType + "," + model + "," + systemQty + "," + newCounted + "," + diff);
                updated = true;
            } else {
                pw.println(line);
            }
        }

        br.close();
        pw.close();
        input.delete();
        temp.renameTo(input);
        return updated;
    }

    // ---------- Receipt ----------
    private static void ensureDailyReceipt(String date) throws IOException {
        String path = RECEIPTS_DIR + "/sales_" + date + ".txt";
        File f = new File(path);
        if (!f.exists()) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write("=== DAILY SALES RECEIPT (" + date + ") ===");
            bw.newLine();
            bw.close();
        }
    }

    private static void appendReceipt(String date, String saleId, EmployeeInfo emp,
                                      String customer, String method, SaleItem[] items, double total) throws IOException {
        String path = RECEIPTS_DIR + "/sales_" + date + ".txt";
        BufferedWriter bw = new BufferedWriter(new FileWriter(path, true));
        bw.newLine();
        bw.write("SaleID: " + saleId); bw.newLine();
        bw.write("Date/Time: " + date + " " + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")).toLowerCase()); bw.newLine();
        bw.write("Employee: " + emp.name + " (" + emp.id + ") Outlet: " + emp.outletCode); bw.newLine();
        bw.write("Customer: " + customer); bw.newLine();
        bw.write("Payment: " + method); bw.newLine();
        bw.write("Items:"); bw.newLine();
        for (int i = 0; i < items.length; i++) {
            bw.write(" - " + items[i].model + " x" + items[i].qty + " @ " + fmt2(items[i].unitPrice));
            bw.newLine();
        }
        bw.write("TOTAL: " + fmt2(total)); bw.newLine();
        bw.write("----------------------------------"); bw.newLine();
        bw.close();
    }

    // ---------- File helpers ----------
    private static void ensureDir(String dir) throws IOException {
        File d = new File(dir);
        if (!d.exists() && !d.mkdirs()) throw new IOException("Cannot create dir: " + dir);
    }

    private static void ensureFile(String path) throws IOException {
        File f = new File(path);
        if (!f.exists() && !f.createNewFile()) throw new IOException("Cannot create file: " + path);
    }

    private static void ensureFileWithHeader(String path, String header) throws IOException {
        File f = new File(path);
        if (!f.exists()) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write(header); bw.newLine();
            bw.close();
        }
    }

    private static void appendLine(String path, String line) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path, true));
        bw.write(line); bw.newLine();
        bw.close();
    }

    public static String[] split(String line) { return line.split(",", -1); }
    public static String fmt2(double x) { return String.format(java.util.Locale.US, "%.2f", x); }
    private static int parseIntSafe(String s) { try { return Integer.parseInt(s.trim()); } catch(Exception e){ return 0; } }
}
