package goldenhourstoresystem;
//records stock at a specific outlet
public class Stock {
    
   // 1. Attributes
   //private
   private String modelName;  
   private String outletCode;  
   private int quantity;      
    
   // 2. Constructor,used to initialize objects
   public Stock(String modelName, String outletCode, int quantity) {
      this.modelName = modelName;
      this.outletCode = outletCode;
      this.quantity = quantity;
   }

   // 3. Getters
   public String getModelName() {
      return modelName;
   }

   public String getOutletCode() {
      return outletCode;
   }

   public int getQuantity() {
      return quantity;
   }

   // 4. Setter for Quantity
   // This allows us to update stock levels during Stock In/Out or Sales.
   public void setQuantity(int quantity) {
      this.quantity = quantity;
   }
    
    
   public String toString() {
      return modelName + " in " + outletCode + ": " + quantity;
   }
}