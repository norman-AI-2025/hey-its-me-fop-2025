package goldenhourstoresystem;

public class Sales {
    private String salesID;
    private String date;
    private String customerName;
    private String modelName;
    private int quantity;
    private double totalPrice;

    public Sales(String salesID, String date, String customerName, String modelName, int quantity, double totalPrice) {
        this.salesID = salesID;
        this.date = date;
        this.customerName = customerName;
        this.modelName = modelName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public String getSalesID() { 
       return salesID; 
    }
    
    public String getDate() { 
       return date; 
    
    }
    public String getCustomerName() { 
       return customerName; 
    
    }
    public String getModelName() { 
       return modelName; 
    }
    
    public int getQuantity() { 
       return quantity; 
    }
    
    public double getTotalPrice() { 
       return totalPrice; 
    }
    
    public String toCSV() {
        return salesID + "," + date + "," + customerName + "," + modelName + "," + quantity + "," + totalPrice;
    }
}
