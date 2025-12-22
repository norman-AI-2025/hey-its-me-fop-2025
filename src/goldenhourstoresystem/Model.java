package goldenhourstoresystem;
/* represents a watch product*/
public class Model {
    
    // 1. Attributes
    private String name; 
    private double Price; 
    
    public Model(String modelName, double Price) {
        this.name = modelName;
        this.Price = Price;
    }

    // 2. Getter Methods
    // Used to retrieve info when we display stock or calculate sales total.
    
    public String getName() {
        return name;
    }

    public double getUnitPrice() {
        return Price;
    }
    
    // 3. toString Method
    // Useful for testing: prints "DW2300-4 (RM349.0)"
    public String toString() {
        return name + " (RM" + Price + ")";
    }
}