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

