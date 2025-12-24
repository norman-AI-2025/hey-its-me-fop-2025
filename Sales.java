package fop_assignment_2025;

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

