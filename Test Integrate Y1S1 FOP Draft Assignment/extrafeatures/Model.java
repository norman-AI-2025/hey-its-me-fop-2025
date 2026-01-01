package extrafeatures;

import java.io.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

