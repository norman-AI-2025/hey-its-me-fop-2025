package extrafeatures;

import extrafeatures.Models.EmployeeInfo;
import extrafeatures.Models.SaleItem;
import java.io.*;
import java.time.LocalDate;

public class TeammateAPI {

    private allAttendanceLog attendanceLog;
    private SystemManager systemManager;
    private SearchSystem searchSystem = new SearchSystem();
    private EditSystem editSystem = new EditSystem();

    public TeammateAPI() {
        systemManager = new SystemManager();
        reloadAttendanceData();
    }

    public void reloadSystemData() {
        systemManager = new SystemManager();
    }

    private void reloadAttendanceData() {
        attendanceLog = new allAttendanceLog();
        attendanceLog.loadEmployees(FilePathHelper.resolveReadPath("employees.csv"));
        attendanceLog.loadAttendanceLog(FilePathHelper.resolveReadPath("attendance.csv"));
    }

    public EmployeeInfo login(String employeeId, String password) throws Exception {
        User user = LoginSystem.login(employeeId, password);
        if (user == null) return null;

        String outlet = findOutletForEmployee(user.getID());
        boolean isManager = "manager".equalsIgnoreCase(user.getRole());
        return new EmployeeInfo(user.getID(), user.getUsername(), user.getRole(), outlet, isManager);
    }

    public boolean registerEmployee(String id, String name, String role, String password, String outlet) throws Exception {
        if (id == null || name == null || role == null || password == null) return false;
        if (id.trim().length() == 0 || name.trim().length() == 0 || role.trim().length() == 0 || password.trim().length() == 0) return false;
        if (employeeIdExists(id.trim())) return false;

        UserManager.addUserWithOutlet(id.trim(), name.trim(), role.trim(), password.trim(), outlet);
        reloadAttendanceData();
        return true;
    }

    public void clockIn(EmployeeInfo emp) throws Exception {
        if (emp == null) return;
        AttendanceLog log = attendanceLog.clockInGui(emp.id);
        if (log == null) throw new Exception("Clock in failed.");
    }

    public double clockOut(EmployeeInfo emp) throws Exception {
        if (emp == null) return 0.0;
        AttendanceLog log = attendanceLog.clockOutGui(emp.id);
        if (log == null) throw new Exception("No active clock-in record found.");
        return log.gettotal_hours_worked();
    }

    public Object[][] getStockTable(String outletCode) throws Exception {
        return systemManager.getStockTableForOutlet(outletCode);
    }

    public String[] listAllModels() throws Exception {
        return systemManager.listAllModels();
    }

    public double getModelPrice(String modelName) throws Exception {
        return systemManager.getModelPrice(modelName);
    }

    // ---------- Reports / Analytics ----------
    public Object[][] loadSalesHistory() throws Exception {
        return Reports.loadSalesTable();
    }

    public Object[][] filterSalesByDate(Object[][] rows, String from, String to) throws Exception {
        return Reports.filterByDate(rows, from, to);
    }

    public void sortSalesHistory(Object[][] rows, String key, boolean asc) {
        Reports.bubbleSort(rows, key, asc);
    }

    public double sumSalesAmount(Object[][] rows) {
        return Reports.sumAmount(rows);
    }

    public String[] analyticsSummary() throws Exception {
        return Reports.analyticsSummary();
    }

    public Object[][] employeePerformance(String from, String to) throws Exception {
        return Reports.employeePerformance(from, to);
    }

    // ---------- ExtraStore / Email ----------
    public void initExtraStore() throws Exception {
        ExtraStore.initEmailOnly();
    }

    public String newExtraSaleIdForToday() throws Exception {
        return ExtraStore.newSaleIdForToday();
    }

    public void logExtraSale(EmployeeInfo emp, String saleId, String customer, String method,
                             SaleItem[] items, String receiptPath) throws Exception {
        ExtraStore.logSale(emp, saleId, customer, method, items, receiptPath);
    }

    public void logStockCount(String countType, String outlet, String model, int systemQty, int countedQty) throws Exception {
        ExtraStore.logStockCount(countType, outlet, model, systemQty, countedQty);
    }

    public boolean updateStockCount(String date, String outlet, String countType, String model, int newQty) throws Exception {
        return editSystem.editStockCount(date, outlet, countType, model, newQty);
    }

    public void saveEmailSettings(String from, String to, String appPass) throws Exception {
        ExtraStore.saveEmailSettings(from, to, appPass);
    }

    public String[] loadEmailSettings() throws Exception {
        return ExtraStore.loadEmailSettings();
    }

    public boolean alreadySentToday(String date) throws Exception {
        return ExtraStore.alreadySentToday(date);
    }

    public void sendDailyReport(String date, String from, String appPass, String to) throws Exception {
        EmailerGmail.sendDailyReport(date, from, appPass, to);
    }

    public String recordSale(EmployeeInfo emp, String customer, String paymentMethod, SaleItem[] items) throws Exception {
        if (emp == null) throw new Exception("No employee logged in.");
        if (items == null || items.length == 0) throw new Exception("No items.");

        String[] modelNames = new String[items.length];
        int[] quantities = new int[items.length];
        for (int i = 0; i < items.length; i++) {
            SaleItem it = items[i];
            double price = findUnitPrice(it.model);
            it.unitPrice = price;
            modelNames[i] = it.model;
            quantities[i] = it.qty;
        }

        SalesSystem.recordSaleItems(emp.id, emp.name, customer, paymentMethod, emp.outletCode, modelNames, quantities, items.length);
        return "receipts/sales_" + LocalDate.now().toString() + ".txt";
    }

    public int[] stockCount(String outletCode, String countType, String[] models, int[] counts, int size) throws Exception {
        SystemManager manager = new SystemManager();
        return manager.stockCountGui(outletCode, countType, models, counts, size);
    }

    public boolean transferStock(String type, String myOutlet, String otherOutlet,
                                 String[] models, int[] qtys, int size, String employeeName) throws Exception {
        return transferStockMessage(type, myOutlet, otherOutlet, models, qtys, size, employeeName) == null;
    }

    public String transferStockMessage(String type, String myOutlet, String otherOutlet,
                                       String[] models, int[] qtys, int size, String employeeName) throws Exception {
        reloadSystemData();
        return systemManager.transferStockGuiMessage(type, myOutlet, otherOutlet, models, qtys, size, employeeName);
    }

    public String searchStock(String model) throws Exception {
        return searchSystem.searchStockInfoText(model);
    }

    public String searchSales(String mode, String query) throws Exception {
        return searchSystem.searchSalesInfoText(mode, query);
    }

    public boolean editStock(String model, String outlet, int newQty) throws Exception {
        return editSystem.editStock(model, outlet, newQty);
    }

    public boolean editSales(String date, String customer, String field, String newValue) throws Exception {
        return editSystem.editSales(date, customer, field, newValue);
    }

    private boolean employeeIdExists(String id) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(FilePathHelper.resolveReadPath("employees.csv")))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length > 0 && p[0].trim().equalsIgnoreCase(id)) return true;
            }
        }
        return false;
    }

    private String findOutletForEmployee(String employeeId) {
        try (BufferedReader br = new BufferedReader(new FileReader(FilePathHelper.resolveReadPath("employees.csv")))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 5 && p[0].trim().equalsIgnoreCase(employeeId)) {
                    return p[4].trim();
                }
            }
        } catch (IOException ignored) {}
        return "N/A";
    }

    private double findUnitPrice(String modelName) {
        try (BufferedReader br = new BufferedReader(new FileReader(FilePathHelper.resolveReadPath("model.csv")))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 2 && p[0].equalsIgnoreCase(modelName)) {
                    try { return Double.parseDouble(p[1]); } catch (Exception ignored) {}
                }
            }
        } catch (IOException ignored) {}
        return 0.0;
    }
}
