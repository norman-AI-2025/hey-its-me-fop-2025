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

public class LoginSystem {

    public static User login(String ID, String password) throws IOException {
        List<User> users = UserManager.loadUsers();

        for (User u : users) {
            // Check if username and password match
            if (u.getID().equals(ID) && u.getPassword().equals(password)) {
                return u; // Return the found user object
            }
        }
        return null; // Login failed
    }
}

