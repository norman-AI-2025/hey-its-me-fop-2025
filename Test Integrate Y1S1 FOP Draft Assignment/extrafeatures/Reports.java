package extrafeatures;

import java.io.*;
import java.time.LocalDate;

public class Reports {
    private static String[] empIds;
    private static String[] empNames;

    // row: {Date, Time, SaleID, Customer, EmployeeName, Amount, Method}
    public static Object[][] loadSalesTable() throws Exception {
        String salesPath = FilePathHelper.resolveReadPath("sales_data.csv");
        int totalLines = countDataLines(salesPath);
        if (totalLines <= 0) return new Object[0][7];

        ensureEmployeeLookup();

        Object[][] temp = new Object[totalLines][7];
        int n = 0;

        BufferedReader br = new BufferedReader(new FileReader(salesPath));
        br.readLine(); // header
        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().length() == 0) continue;
            String[] p = line.split(",", -1);

            String saleId = "";
            String date = "";
            String time = "N/A";
            String customer = "";
            String totalStr = "";
            double total = 0.0;
            String employeeId = "";
            String method = "N/A";

            if (p.length >= 9) {
                saleId = p[0].trim();
                date = p[1].trim();
                time = p[2].trim();
                customer = p[3].trim();
                totalStr = p[6].trim();
                employeeId = p[7].trim();
                method = p[8].trim();
            } else if (p.length >= 8) {
                saleId = p[0].trim();
                date = p[1].trim();
                customer = p[2].trim();
                totalStr = p[5].trim();
                employeeId = p[6].trim();
                method = p[7].trim();
            } else if (p.length >= 6) {
                saleId = p[0].trim();
                date = p[1].trim();
                customer = p[2].trim();
                totalStr = p[5].trim();
            } else {
                continue;
            }

            if (date.length() == 0) continue;
            total = parseDouble(totalStr);
            String employeeName = findEmployeeName(employeeId);
            if (employeeName.length() == 0) employeeName = employeeId;

            temp[n][0] = date;
            temp[n][1] = time;
            temp[n][2] = saleId;
            temp[n][3] = customer;
            temp[n][4] = employeeName;
            temp[n][5] = total;
            temp[n][6] = method;
            n++;
        }
        br.close();

        Object[][] out = new Object[n][7];
        for (int i = 0; i < n; i++) out[i] = temp[i];
        return out;
    }

    public static Object[][] filterByDate(Object[][] rows, String from, String to) throws Exception {
        LocalDate a = LocalDate.parse(from);
        LocalDate b = LocalDate.parse(to);

        Object[][] temp = new Object[rows.length][7];
        int n = 0;

        for (int i = 0; i < rows.length; i++) {
            LocalDate d = LocalDate.parse(rows[i][0].toString());
            boolean ok = (d.isEqual(a) || d.isAfter(a)) && (d.isEqual(b) || d.isBefore(b));
            if (ok) temp[n++] = rows[i];
        }

        Object[][] out = new Object[n][7];
        for (int i = 0; i < n; i++) out[i] = temp[i];
        return out;
    }

    public static void bubbleSort(Object[][] rows, String key, boolean asc) {
        for (int i = 0; i < rows.length - 1; i++) {
            for (int j = 0; j < rows.length - i - 1; j++) {
                if (needSwap(rows[j], rows[j+1], key, asc)) {
                    Object[] t = rows[j]; rows[j] = rows[j+1]; rows[j+1] = t;
                }
            }
        }
    }

    private static boolean needSwap(Object[] a, Object[] b, String key, boolean asc) {
        int cmp = 0;
        if ("DATE".equals(key)) {
            String x = a[0].toString() + " " + a[1].toString();
            String y = b[0].toString() + " " + b[1].toString();
            cmp = x.compareTo(y);
        } else if ("AMOUNT".equals(key)) {
            double x = parseDouble(a[5].toString());
            double y = parseDouble(b[5].toString());
            cmp = (x<y)?-1:(x>y?1:0);
        } else { // CUSTOMER
            cmp = a[3].toString().compareToIgnoreCase(b[3].toString());
        }
        return asc ? (cmp > 0) : (cmp < 0);
    }

    public static double sumAmount(Object[][] rows) {
        double s = 0;
        for (int i = 0; i < rows.length; i++) s += parseDouble(rows[i][5].toString());
        return s;
    }

    // ---------- Analytics ----------
    public static String[] analyticsSummary() throws Exception {
        Object[][] sales = loadSalesTable();
        String today = LocalDate.now().toString();

        double totalToday = 0;
        for (int i = 0; i < sales.length; i++)
            if (today.equals(sales[i][0].toString()))
                totalToday += parseDouble(sales[i][5].toString());

        double totalWeek = totalThisWeek(sales);
        double avg7 = totalLastNDays(sales, 7) / 7.0;

        return new String[]{
                "Total Sales Today: RM " + fmt2(totalToday),
                "Total Sales This Week: RM " + fmt2(totalWeek),
                "Average Daily Revenue (Last 7 days): RM " + fmt2(avg7)
        };
    }

    private static double totalThisWeek(Object[][] sales) {
        LocalDate today = LocalDate.now();
        int dow = today.getDayOfWeek().getValue(); // Mon=1..Sun=7
        LocalDate mon = today.minusDays(dow - 1);
        LocalDate sun = mon.plusDays(6);

        double sum = 0;
        for (int i = 0; i < sales.length; i++) {
            LocalDate d = LocalDate.parse(sales[i][0].toString());
            boolean ok = (d.isEqual(mon) || d.isAfter(mon)) && (d.isEqual(sun) || d.isBefore(sun));
            if (ok) sum += parseDouble(sales[i][5].toString());
        }
        return sum;
    }

    private static double totalLastNDays(Object[][] sales, int n) {
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(n - 1);

        double sum = 0;
        for (int i = 0; i < sales.length; i++) {
            LocalDate d = LocalDate.parse(sales[i][0].toString());
            boolean ok = (d.isEqual(start) || d.isAfter(start)) && (d.isEqual(today) || d.isBefore(today));
            if (ok) sum += parseDouble(sales[i][5].toString());
        }
        return sum;
    }

    // ---------- Employee performance (manager-only in GUI) ----------
    public static Object[][] employeePerformance(String from, String to) throws Exception {
        Object[][] sales = loadSalesTable();
        Object[][] filtered = filterByDate(sales, from, to);

        String[] emp = new String[5000];
        double[] total = new double[5000];
        int[] tx = new int[5000];
        int size = 0;

        for (int i = 0; i < filtered.length; i++) {
            String name = filtered[i][4].toString();
            double amt = parseDouble(filtered[i][5].toString());

            int idx = indexOf(emp, size, name);
            if (idx == -1) { emp[size]=name; total[size]=amt; tx[size]=1; size++; }
            else { total[idx]+=amt; tx[idx]+=1; }
        }

        // sort desc by total (bubble)
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                if (total[j] < total[j+1]) {
                    swap(emp, j, j+1);
                    swap(total, j, j+1);
                    swap(tx, j, j+1);
                }
            }
        }

        Object[][] out = new Object[size][3];
        for (int i = 0; i < size; i++) {
            out[i][0] = emp[i];
            out[i][1] = fmt2(total[i]);
            out[i][2] = tx[i];
        }
        return out;
    }

    private static int indexOf(String[] arr, int size, String key) {
        for (int i = 0; i < size; i++) if (key.equals(arr[i])) return i;
        return -1;
    }

    private static void swap(String[] a, int i, int j){ String t=a[i]; a[i]=a[j]; a[j]=t; }
    private static void swap(double[] a, int i, int j){ double t=a[i]; a[i]=a[j]; a[j]=t; }
    private static void swap(int[] a, int i, int j){ int t=a[i]; a[i]=a[j]; a[j]=t; }

    private static double parseDouble(String s){ try { return Double.parseDouble(s.trim()); } catch(Exception e){ return 0.0; } }
    private static String fmt2(double x){ return String.format(java.util.Locale.US,"%.2f", x); }

    private static int countDataLines(String path) throws Exception {
        File f = new File(path);
        if (!f.exists()) return 0;
        BufferedReader br = new BufferedReader(new FileReader(f));
        br.readLine(); // header
        int count = 0;
        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().length() == 0) continue;
            count++;
        }
        br.close();
        return count;
    }

    private static void ensureEmployeeLookup() throws Exception {
        if (empIds != null && empNames != null) return;
        String path = FilePathHelper.resolveReadPath("employees.csv");
        int total = countDataLines(path);
        if (total <= 0) {
            empIds = new String[0];
            empNames = new String[0];
            return;
        }
        empIds = new String[total];
        empNames = new String[total];

        BufferedReader br = new BufferedReader(new FileReader(path));
        br.readLine(); // header
        String line;
        int n = 0;
        while ((line = br.readLine()) != null) {
            if (line.trim().length() == 0) continue;
            String[] p = line.split(",", -1);
            if (p.length < 2) continue;
            empIds[n] = p[0].trim();
            empNames[n] = p[1].trim();
            n++;
        }
        br.close();

        if (n < empIds.length) {
            String[] ids = new String[n];
            String[] names = new String[n];
            for (int i = 0; i < n; i++) {
                ids[i] = empIds[i];
                names[i] = empNames[i];
            }
            empIds = ids;
            empNames = names;
        }
    }

    private static String findEmployeeName(String id) {
        if (id == null) return "";
        if (empIds == null || empNames == null) return "";
        for (int i = 0; i < empIds.length; i++) {
            if (empIds[i] != null && empIds[i].equalsIgnoreCase(id)) return empNames[i];
        }
        return "";
    }
}
