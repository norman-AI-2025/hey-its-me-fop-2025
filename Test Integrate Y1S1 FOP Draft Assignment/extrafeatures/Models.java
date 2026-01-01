package extrafeatures;

public class Models {

    public static class EmployeeInfo {
        public String id, name, role, outletCode;
        public boolean isManager;

        public EmployeeInfo(String id, String name, String role, String outletCode, boolean isManager) {
            this.id = id;
            this.name = name;
            this.role = role;
            this.outletCode = outletCode;
            this.isManager = isManager;
        }
    }

    public static class SaleItem {
        public String model;
        public int qty;
        public double unitPrice;

        public SaleItem(String model, int qty, double unitPrice) {
            this.model = model;
            this.qty = qty;
            this.unitPrice = unitPrice;
        }

        public double lineTotal() { return unitPrice * qty; }
    }
}
