package extrafeatures;

import extrafeatures.Models.EmployeeInfo;
import extrafeatures.Models.SaleItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.io.*;

public class ExtraFrame extends JFrame {

    private CardLayout card = new CardLayout();
    private JPanel root = new JPanel(card);

    private TeammateAPI api = new TeammateAPI();
    private EmployeeInfo user = null;

    // Login
    private JTextField idField = new JTextField(16);
    private JPasswordField pwField = new JPasswordField(16);
    private JLabel loginStatus = new JLabel(" ");

    // Main labels
    private JLabel welcome = new JLabel(" ");
    private JLabel autoEmailStatus = new JLabel("Auto email: -");

    // Tabs
    private JTabbedPane tabs;
    private JPanel registerTabPanel;
    private JPanel perfTabPanel;
    private JPanel emailTabPanel;

    // Attendance
    private JLabel attStatus = new JLabel(" ");
    private JTextArea attendanceArea = new JTextArea(10, 60);

    // Profile
    private JLabel profileName = new JLabel("-");
    private JLabel profileId = new JLabel("-");
    private JLabel profileRole = new JLabel("-");
    private JLabel profileOutlet = new JLabel("-");

    // Registration (manager only)
    private JTextField regIdField = new JTextField(12);
    private JTextField regNameField = new JTextField(16);
    private JPasswordField regPassField = new JPasswordField(12);
    private JComboBox<String> regRoleBox = new JComboBox<String>(new String[]{"Part-time","Full-time","Manager"});
    private JTextField regOutletField = new JTextField(8);
    private JLabel regStatus = new JLabel(" ");

    // Stock
    private DefaultTableModel stockModel = new DefaultTableModel(new Object[]{"Model","Price","Qty"}, 0) {
        public boolean isCellEditable(int r,int c){ 
            return false; 
        }
    };

    private JLabel transferStatus = new JLabel(" ");
    private JTextArea transferArea = new JTextArea(10, 60);

    // Stock count
    private JLabel countStatus = new JLabel(" ");
    private JTextArea stockCountArea = new JTextArea(12, 60);

    // Sales
    private JTextField customerField = new JTextField(16);
    private JComboBox<String> payBox = new JComboBox<String>(new String[]{"Cash","Debit/Credit","E-Wallet"});
    private JComboBox<String> modelBox = new JComboBox<String>();
    private JSpinner qtySpin = new JSpinner(new SpinnerNumberModel(1,1,999,1));
    private JLabel totalLabel = new JLabel("Total: RM 0.00");
    private DefaultTableModel cartModel = new DefaultTableModel(new Object[]{"Model","Qty","UnitPrice"}, 0) {
        public boolean isCellEditable(int r,int c){ return false; }
    };

    // History
    private JTextField fromField = new JTextField(10);
    private JTextField toField = new JTextField(10);
    private JComboBox<String> sortBox = new JComboBox<String>(new String[]{
            "Date Asc","Date Desc","Amount Asc","Amount Desc","Customer A-Z","Customer Z-A"
    });
    private JLabel rangeTotal = new JLabel("Total Sales: RM -");
    private DefaultTableModel histModel = new DefaultTableModel(
            new Object[]{"Date","Time","SaleID","Customer","Employee","Amount","Method"}, 0
    ){
        public boolean isCellEditable(int r,int c){ return false; }
    };

    // Search
    private JTextField searchField = new JTextField(16);
    private JComboBox<String> searchType = new JComboBox<String>(new String[]{"Stock Information","Sales Information"});
    private JTextArea searchArea = new JTextArea(8, 45);

    // Edit
    private JLabel editStatus = new JLabel(" ");

    // Analytics
    private JTextArea analyticsArea = new JTextArea(8, 45);

    // Performance
    private JTextField perfFrom = new JTextField(10);
    private JTextField perfTo = new JTextField(10);
    private DefaultTableModel perfModel = new DefaultTableModel(new Object[]{"Employee","Total Sales","Transactions"}, 0){
        public boolean isCellEditable(int r,int c){ return false; }
    };

    // Email
    private JTextField emailDate = new JTextField(10);
    private JTextField fromGmail = new JTextField(22);
    private JPasswordField appPass = new JPasswordField(22);
    private JTextField toEmail = new JTextField(22);
    private JTextArea emailPreview = new JTextArea(8, 45);

    // Auto email timer
    private javax.swing.Timer autoTimer;

    public ExtraFrame() {
        super("Extra Features (GUI + CSV + Email + Analytics)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 760);
        setLocationRelativeTo(null);

        try { api.initExtraStore(); } catch(Exception ignored){}
        try { new DataLoader(); } catch(Exception ignored){}

        root.add(buildLoginPanel(), "LOGIN");
        root.add(buildMainPanel(), "MAIN");
        setContentPane(root);

        showLogin();
        startAutoEmailTimer();
    }

    private JPanel buildLoginPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel("=== Employee Login ===");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        c.gridx=0; c.gridy=0; c.gridwidth=2; p.add(title, c);

        c.gridwidth=1;
        c.gridx=0; c.gridy=1; p.add(new JLabel("User ID:"), c);
        c.gridx=1; p.add(idField, c);

        c.gridx=0; c.gridy=2; p.add(new JLabel("Password:"), c);
        c.gridx=1; p.add(pwField, c);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(new LoginAction());
        pwField.addActionListener(new LoginAction());
        c.gridx=1; c.gridy=3; c.anchor = GridBagConstraints.EAST; p.add(loginBtn, c);

        loginStatus.setForeground(new Color(150,0,0));
        c.gridx=0; c.gridy=4; c.gridwidth=2; c.anchor = GridBagConstraints.WEST; p.add(loginStatus, c);

        return p;
    }

    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        welcome.setFont(welcome.getFont().deriveFont(Font.BOLD, 14f));
        top.add(welcome, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        autoEmailStatus.setForeground(new Color(0,80,0));
        right.add(autoEmailStatus);

        JButton logout = new JButton("Logout");
        logout.addActionListener(new LogoutAction());
        right.add(logout);
        top.add(right, BorderLayout.EAST);

        tabs = new JTabbedPane();
        tabs.addTab("Profile", buildProfileTab());
        tabs.addTab("Attendance", buildAttendanceTab());
        tabs.addTab("Stock View", buildStockTab());
        tabs.addTab("Stock Count", buildStockCountTab());
        tabs.addTab("Stock Transfer", buildStockTransferTab());
        tabs.addTab("Record Sale", buildSalesTab());
        registerTabPanel = buildRegisterTab();
        tabs.addTab("Register Employee", registerTabPanel);
        tabs.addTab("Search", buildSearchTab());
        tabs.addTab("Edit", buildEditTab());
        tabs.addTab("Sales History", buildHistoryTab());
        tabs.addTab("Analytics", buildAnalyticsTab());
        emailTabPanel = buildEmailTab();
        tabs.addTab("Auto Email (Gmail)", emailTabPanel);
        perfTabPanel = buildPerfTab();
        tabs.addTab("Performance (Manager)", perfTabPanel);

        main.add(top, BorderLayout.NORTH);
        main.add(tabs, BorderLayout.CENTER);
        return main;
    }

    // ---------------- Tabs ----------------
    private JPanel buildAttendanceTab() {
        JPanel p = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        JButton inBtn = new JButton("Clock In");
        JButton outBtn = new JButton("Clock Out");

        inBtn.addActionListener(new ClockInAction());
        outBtn.addActionListener(new ClockOutAction());

        attStatus.setForeground(new Color(0,90,0));
        top.add(inBtn); top.add(outBtn); top.add(attStatus);

        attendanceArea.setEditable(false);
        attendanceArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(attendanceArea), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildStockTab() {
        JPanel p = new JPanel(new BorderLayout());
        JTable t = new JTable(stockModel);

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(new RefreshStockAction());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(refresh);

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(t), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildProfileTab() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel("Profile");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        p.add(title, c);

        c.gridwidth = 1;
        addProfileRow(p, c, 1, "Name:", profileName);
        addProfileRow(p, c, 2, "Employee ID:", profileId);
        addProfileRow(p, c, 3, "Role:", profileRole);
        addProfileRow(p, c, 4, "Outlet:", profileOutlet);

        c.gridx = 0; c.gridy = 5; c.weighty = 1; c.fill = GridBagConstraints.VERTICAL;
        p.add(new JLabel(" "), c);

        return p;
    }

    private void addProfileRow(JPanel p, GridBagConstraints c, int row, String label, JLabel value) {
        JLabel k = new JLabel(label);
        k.setFont(k.getFont().deriveFont(Font.BOLD));
        c.gridx = 0; c.gridy = row; c.weightx = 0; c.fill = GridBagConstraints.NONE;
        p.add(k, c);

        c.gridx = 1; c.gridy = row; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        p.add(value, c);
    }

    private JPanel buildStockCountTab() {
        JPanel p = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        JButton morningBtn = new JButton("Morning Count");
        JButton nightBtn = new JButton("Night Count");

        morningBtn.addActionListener(e -> doStockCount("Morning Stock Count"));
        nightBtn.addActionListener(e -> doStockCount("Night Stock Count"));

        countStatus.setForeground(new Color(0,90,0));
        top.add(morningBtn);
        top.add(nightBtn);
        top.add(countStatus);

        stockCountArea.setEditable(false);
        stockCountArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(stockCountArea), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildStockTransferTab() {
        JPanel p = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        JButton inBtn = new JButton("Stock In");
        JButton outBtn = new JButton("Stock Out");

        inBtn.addActionListener(e -> doStockTransfer("Stock In"));
        outBtn.addActionListener(e -> doStockTransfer("Stock Out"));

        transferStatus.setForeground(new Color(0,90,0));
        top.add(inBtn);
        top.add(outBtn);
        top.add(transferStatus);

        transferArea.setEditable(false);
        transferArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(transferArea), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildSalesTab() {
        JPanel p = new JPanel(new BorderLayout());

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        form.add(new JLabel("Customer:")); form.add(customerField);
        form.add(new JLabel("Payment:")); form.add(payBox);
        form.add(new JLabel("Model:")); form.add(modelBox);
        form.add(new JLabel("Qty:")); form.add(qtySpin);

        JButton add = new JButton("Add Item");
        JButton remove = new JButton("Remove Selected");
        JButton confirm = new JButton("Confirm Sale");
        add.addActionListener(new AddItemAction());
        remove.addActionListener(new RemoveItemAction());
        confirm.addActionListener(new ConfirmSaleAction());

        form.add(add); form.add(remove); form.add(confirm);
        form.add(totalLabel);

        JTable cart = new JTable(cartModel);

        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(cart), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildHistoryTab() {
        JPanel p = new JPanel(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        top.add(new JLabel("From:")); top.add(fromField);
        top.add(new JLabel("To:")); top.add(toField);
        top.add(new JLabel("Sort:")); top.add(sortBox);

        JButton apply = new JButton("Apply");
        apply.addActionListener(new ApplyHistoryAction());
        top.add(apply);
        top.add(rangeTotal);

        JTable t = new JTable(histModel);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(t), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildAnalyticsTab() {
        JPanel p = new JPanel(new BorderLayout());
        analyticsArea.setEditable(false);
        analyticsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(new RefreshAnalyticsAction());
        p.add(new JScrollPane(analyticsArea), BorderLayout.CENTER);
        p.add(refresh, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildEmailTab() {
        JPanel p = new JPanel(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(6,2,8,8));
        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        form.add(new JLabel("Report Date (YYYY-MM-DD):")); form.add(emailDate);
        form.add(new JLabel("From Gmail:")); form.add(fromGmail);
        form.add(new JLabel("Gmail App Password:")); form.add(appPass);
        form.add(new JLabel("To Email (proof):")); form.add(toEmail);

        JButton save = new JButton("Save Settings");
        JButton send = new JButton("Send Now");
        JButton disable = new JButton("Disable Auto Email");
        save.addActionListener(new SaveEmailSettingsAction());
        send.addActionListener(new SendEmailNowAction());
        disable.addActionListener(new DisableEmailSettingsAction());

        form.add(save); form.add(send);
        form.add(disable); form.add(new JLabel(" "));

        emailPreview.setEditable(false);
        emailPreview.setFont(new Font("Monospaced", Font.PLAIN, 13));

        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(emailPreview), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildPerfTab() {
        JPanel p = new JPanel(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        top.add(new JLabel("From:")); top.add(perfFrom);
        top.add(new JLabel("To:")); top.add(perfTo);

        JButton gen = new JButton("Generate");
        gen.addActionListener(new GeneratePerfAction());
        top.add(gen);

        JTable t = new JTable(perfModel);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(t), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildSearchTab() {
        JPanel p = new JPanel(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        top.add(new JLabel("Search:"));
        top.add(searchField);
        top.add(searchType);
        JButton go = new JButton("Search");
        go.addActionListener(new SearchAction());
        top.add(go);

        searchArea.setEditable(false);
        searchArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(searchArea), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildEditTab() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        JButton editStockBtn = new JButton("Edit Stock");
        JButton editCountBtn = new JButton("Edit Stock Count");
        JButton editSalesBtn = new JButton("Edit Sales");

        editStockBtn.addActionListener(e -> editStockDialog());
        editCountBtn.addActionListener(e -> editStockCountDialog());
        editSalesBtn.addActionListener(e -> editSalesDialog());

        editStatus.setForeground(new Color(0,90,0));
        p.add(editStockBtn);
        p.add(editCountBtn);
        p.add(editSalesBtn);
        p.add(editStatus);
        return p;
    }

    private JPanel buildRegisterTab() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.WEST;

        c.gridx=0; c.gridy=0; p.add(new JLabel("Employee ID:"), c);
        c.gridx=1; p.add(regIdField, c);

        c.gridx=0; c.gridy=1; p.add(new JLabel("Employee Name:"), c);
        c.gridx=1; p.add(regNameField, c);

        c.gridx=0; c.gridy=2; p.add(new JLabel("Password:"), c);
        c.gridx=1; p.add(regPassField, c);

        c.gridx=0; c.gridy=3; p.add(new JLabel("Role:"), c);
        c.gridx=1; p.add(regRoleBox, c);

        c.gridx=0; c.gridy=4; p.add(new JLabel("Outlet Code:"), c);
        c.gridx=1; p.add(regOutletField, c);

        JButton regBtn = new JButton("Register");
        regBtn.addActionListener(new RegisterEmployeeAction());
        c.gridx=1; c.gridy=5; c.anchor = GridBagConstraints.EAST; p.add(regBtn, c);

        regStatus.setForeground(new Color(150,0,0));
        c.gridx=0; c.gridy=6; c.gridwidth=2; c.anchor = GridBagConstraints.WEST; p.add(regStatus, c);

        return p;
    }

    // ---------------- Screen helpers ----------------
    private void resetUiState() {
        welcome.setText(" ");
        autoEmailStatus.setText("Auto email: -");

        profileName.setText("-");
        profileId.setText("-");
        profileRole.setText("-");
        profileOutlet.setText("-");

        attStatus.setText(" ");
        attendanceArea.setText("");
        transferStatus.setText(" ");
        transferArea.setText("");
        countStatus.setText(" ");
        stockCountArea.setText("");
        editStatus.setText(" ");
        analyticsArea.setText("");
        searchArea.setText("");
        emailPreview.setText("");

        customerField.setText("");
        cartModel.setRowCount(0);
        modelBox.removeAllItems();
        qtySpin.setValue(1);
        payBox.setSelectedIndex(0);

        searchField.setText("");
        searchType.setSelectedIndex(0);

        stockModel.setRowCount(0);
        histModel.setRowCount(0);
        rangeTotal.setText("Total Sales: RM -");
        fromField.setText("");
        toField.setText("");
        perfFrom.setText("");
        perfTo.setText("");
        perfModel.setRowCount(0);

        regIdField.setText("");
        regNameField.setText("");
        regPassField.setText("");
        regOutletField.setText("");
        regRoleBox.setSelectedIndex(0);
        regStatus.setText(" ");

        emailDate.setText("");
        fromGmail.setText("");
        appPass.setText("");
        toEmail.setText("");
    }

    private void showLogin() {
        resetUiState();
        card.show(root, "LOGIN");
        idField.setText("");
        pwField.setText("");
        loginStatus.setText(" ");
    }

    private void showMain() {
        card.show(root, "MAIN");
        if (tabs != null) tabs.setSelectedIndex(0);
        updateRoleTabs();
        refreshAll();
    }

    private void refreshAll() {
        if (user != null) {
            welcome.setText("Welcome, " + user.name + " (" + user.outletCode + ")");
            profileName.setText(user.name);
            profileId.setText(user.id);
            profileRole.setText(user.role);
            profileOutlet.setText(user.outletCode);
        }

        // defaults
        String today = LocalDate.now().toString();
        if (fromField.getText().trim().length()==0) fromField.setText(today);
        if (toField.getText().trim().length()==0) toField.setText(today);
        if (perfFrom.getText().trim().length()==0) perfFrom.setText(today);
        if (perfTo.getText().trim().length()==0) perfTo.setText(today);
        if (emailDate.getText().trim().length()==0) emailDate.setText(today);

        refreshModels();
        refreshStock();
        refreshAnalytics();
        loadEmailSettingsToUI();
        refreshAutoEmailStatus();
    }

    private void updateRoleTabs() {
        if (tabs == null) return;
        boolean isManager = (user != null && user.isManager);
        updateTabVisibility(registerTabPanel, "Register Employee", isManager, 5);
        updateTabVisibility(emailTabPanel, "Auto Email (Gmail)", isManager, 11);
        updateTabVisibility(perfTabPanel, "Performance (Manager)", isManager, tabs.getTabCount());
        autoEmailStatus.setVisible(isManager);
    }

    private void refreshAutoEmailStatus() {
        if (user == null || !user.isManager) {
            autoEmailStatus.setVisible(false);
            return;
        }

        autoEmailStatus.setVisible(true);
        try {
            String today = LocalDate.now().toString();
            if (api.alreadySentToday(today)) {
                autoEmailStatus.setText("Auto email: SENT today");
                return;
            }

            String[] s = api.loadEmailSettings();
            boolean configured = (s != null
                    && s[0] != null && s[0].trim().length() > 0
                    && s[1] != null && s[1].trim().length() > 0
                    && s[2] != null && s[2].trim().length() > 0);

            if (!configured) autoEmailStatus.setText("Auto email: OFF (not set)");
            else autoEmailStatus.setText("Auto email: READY");
        } catch (Exception ex) {
            autoEmailStatus.setText("Auto email: error");
        }
    }

    private void updateTabVisibility(JPanel panel, String title, boolean show, int insertAt) {
        if (panel == null) return;
        int idx = tabs.indexOfComponent(panel);
        if (show) {
            if (idx == -1) {
                int pos = Math.min(insertAt, tabs.getTabCount());
                tabs.insertTab(title, null, panel, null, pos);
            }
        } else {
            if (idx != -1) tabs.removeTabAt(idx);
        }
    }

    private void refreshModels() {
        modelBox.removeAllItems();
        try {
            String[] models = api.listAllModels();
            for (int i = 0; i < models.length; i++) modelBox.addItem(models[i]);
        } catch(Exception ignored){}
    }

    private void refreshStock() {
        stockModel.setRowCount(0);
        if (user == null) return;
        try {
            api.reloadSystemData();
            Object[][] rows = api.getStockTable(user.outletCode);
            for (int i = 0; i < rows.length; i++) {
                Object[] r = rows[i];
                Object[] display = new Object[r.length];
                for (int j = 0; j < r.length; j++) display[j] = r[j];
                if (r.length > 1) display[1] = formatMoney(toDouble(r[1]));
                stockModel.addRow(display);
            }
        } catch(Exception ex) {
            // ok if not connected
        }
    }

    private void refreshAnalytics() {
        try {
            String[] lines = api.analyticsSummary();
            String text = "";
            for (int i = 0; i < lines.length; i++) {
                text += lines[i] + "\n";
            }
            analyticsArea.setText(text);
        } catch(Exception ex) {
            analyticsArea.setText("No analytics / error: " + ex.getMessage());
        }
    }

    private void loadEmailSettingsToUI() {
        try {
            String[] s = api.loadEmailSettings();
            if (s != null) {
                fromGmail.setText(s[0]);
                toEmail.setText(s[1]);
                appPass.setText(s[2]);
            }
        } catch(Exception ignored){}
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private int promptCountedQty(String title, String modelName, int systemQty) {
        while (true) {
            JTextField field = new JTextField(10);
            JPanel panel = new JPanel(new GridLayout(0, 1, 4, 4));
            panel.add(new JLabel(title));
            panel.add(new JLabel("Model: " + modelName));
            panel.add(new JLabel("System Qty: " + systemQty));
            panel.add(new JLabel("Enter Counted Qty:"));
            panel.add(field);

            int result = JOptionPane.showOptionDialog(
                this,
                panel,
                "Input",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"OK"},
                "OK"
            );
            if (result == JOptionPane.CLOSED_OPTION) return Integer.MIN_VALUE;

            String input = field.getText() == null ? "" : field.getText().trim();
            if (input.length() == 0) {
                JOptionPane.showMessageDialog(this, "Please enter a number for " + modelName + ".");
                continue;
            }
            try {
                return Integer.parseInt(input);
            } catch (Exception ignored) {
                JOptionPane.showMessageDialog(this, "Invalid input for " + modelName + ".");
            }
        }
    }

    // ---------------- Auto email timer ----------------
    private void startAutoEmailTimer() {
        autoTimer = new javax.swing.Timer(60_000, new AutoEmailTickAction()); // every minute
        autoTimer.setInitialDelay(5_000);
        autoTimer.start();
    }

    // ---------------- Actions (polymorphism via ActionListener) ----------------
    private class LoginAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String id = idField.getText().trim();
            String pw = new String(pwField.getPassword()).trim();
            if (id.length()==0 || pw.length()==0) { loginStatus.setText("Login Failed: Invalid User ID or Password."); return; }
            try {
                EmployeeInfo emp = api.login(id, pw);
                if (emp == null) { loginStatus.setText("Login Failed: Invalid User ID or Password."); return; }
                user = emp;
                loginStatus.setText("Login Successful!");
                showMain();
            } catch(Exception ex) {
                loginStatus.setText("Login Failed: Invalid User ID or Password.");
            }
        }
    }

    private class LogoutAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            user = null;
            showLogin();
        }
    }

    private class ClockInAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                api.clockIn(user);
                attStatus.setText("Clock-in recorded.");
                String date = LocalDate.now().toString();
                String time = getAttendanceTime(user.id, date, false);
                String outlet = outletLabel(user.outletCode);
                String msg = "=== Attendance Clock In ===\n" +
                        "Employee ID: " + user.id + "\n" +
                        "Name: " + user.name + "\n" +
                        "Outlet: " + outlet + "\n\n" +
                        "Clock In Successful!\n" +
                        "Date: " + date + "\n" +
                        "Time: " + formatTimeDisplay(time) + "\n";
                attendanceArea.setText(msg);
            } catch(Exception ex) { showError(ex.getMessage()); }
        }
    }

    private class ClockOutAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                double hours = api.clockOut(user);
                attStatus.setText("Clock-out recorded. Hours: " + String.format(java.util.Locale.US,"%.2f",hours));
                String date = LocalDate.now().toString();
                String time = getAttendanceTime(user.id, date, true);
                String outlet = outletLabel(user.outletCode);
                String msg = "=== Attendance Clock Out ===\n" +
                        "Employee ID: " + user.id + "\n" +
                        "Name: " + user.name + "\n" +
                        "Outlet: " + outlet + "\n\n" +
                        "Clock Out Successful!\n" +
                        "Date: " + date + "\n" +
                        "Time: " + formatTimeDisplay(time) + "\n" +
                        "Total Hours Worked: " + String.format(java.util.Locale.US,"%.1f", hours) + " hours\n";
                attendanceArea.setText(msg);
            } catch(Exception ex) { showError(ex.getMessage()); }
        }
    }

    private class RefreshStockAction implements ActionListener {
        public void actionPerformed(ActionEvent e) { refreshStock(); }
    }

    private class AddItemAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String m = (String)modelBox.getSelectedItem();
            int q = (Integer)qtySpin.getValue();
            if (m == null || m.trim().length()==0) { showError("Select a model."); return; }
            double price = 0.0;
            try { price = api.getModelPrice(m); } catch(Exception ignored){}
                cartModel.addRow(new Object[]{m, q, formatMoney(price)});
                updateCartTotal();
            }
        }

    private class RemoveItemAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int r = cartModel.getRowCount();
            if (r <= 0) return;
            // remove last row (simple; avoids needing JTable selectedRow explanation)
            cartModel.removeRow(r - 1);
            updateCartTotal();
        }
    }

    private class ConfirmSaleAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                String cust = customerField.getText().trim();
                if (cust.length()==0) throw new Exception("Customer name required.");
                if (cartModel.getRowCount()==0) throw new Exception("Add at least 1 item.");

                SaleItem[] items = new SaleItem[cartModel.getRowCount()];
                for (int i = 0; i < cartModel.getRowCount(); i++) {
                    String m = cartModel.getValueAt(i,0).toString();
                    int q = Integer.parseInt(cartModel.getValueAt(i,1).toString());
                    double price = parseMoney(cartModel.getValueAt(i,2).toString());
                    items[i] = new SaleItem(m, q, price);
                }
 
                // check stock availability (avoid negative stock)
                for (int i = 0; i < cartModel.getRowCount(); i++) {
                    String m = cartModel.getValueAt(i,0).toString();
                    boolean first = true;
                    for (int k = 0; k < i; k++) {
                        String prev = cartModel.getValueAt(k,0).toString();
                        if (prev.equalsIgnoreCase(m)) { first = false; break; }
                    }
                    if (!first) continue;
 
                    int totalQty = 0;
                    for (int j = 0; j < cartModel.getRowCount(); j++) {
                        String mm = cartModel.getValueAt(j,0).toString();
                        if (mm.equalsIgnoreCase(m)) {
                            totalQty += Integer.parseInt(cartModel.getValueAt(j,1).toString());
                        }
                    }
 
                    int available = getStockQty(m, user.outletCode);
                    if (available < 0) throw new Exception("Stock not found for " + m + ".");
                    if (totalQty > available) {
                        throw new Exception("Not enough stock for " + m + ". Available: " + available);
                    }
                }
 
                String receiptPath = api.recordSale(user, cust, (String)payBox.getSelectedItem(), items);

                JOptionPane.showMessageDialog(ExtraFrame.this, "Sale recorded.\nReceipt: " + receiptPath);
                customerField.setText("");
                cartModel.setRowCount(0);
                updateCartTotal();

                refreshAnalytics();
                refreshStock();
            } catch(Exception ex) { showError(ex.getMessage()); }
        }
    }

    private class ApplyHistoryAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                Object[][] rows = api.loadSalesHistory();
                Object[][] filtered = api.filterSalesByDate(rows, fromField.getText().trim(), toField.getText().trim());

                String s = (String)sortBox.getSelectedItem();
                if ("Date Asc".equals(s)) api.sortSalesHistory(filtered, "DATE", true);
                else if ("Date Desc".equals(s)) api.sortSalesHistory(filtered, "DATE", false);
                else if ("Amount Asc".equals(s)) api.sortSalesHistory(filtered, "AMOUNT", true);
                else if ("Amount Desc".equals(s)) api.sortSalesHistory(filtered, "AMOUNT", false);
                else if ("Customer A-Z".equals(s)) api.sortSalesHistory(filtered, "CUSTOMER", true);
                else api.sortSalesHistory(filtered, "CUSTOMER", false);

                double sum = api.sumSalesAmount(filtered);
                histModel.setRowCount(0);
                for (int i = 0; i < filtered.length; i++) {
                    Object[] r = filtered[i];
                    Object[] display = new Object[r.length];
                    for (int j = 0; j < r.length; j++) display[j] = r[j];
                    if (r.length > 5) display[5] = formatMoney(toDouble(r[5]));
                    histModel.addRow(display);
                }
                rangeTotal.setText("Total Sales: RM " + String.format(java.util.Locale.US,"%.2f", sum));
            } catch(Exception ex) { showError(ex.getMessage()); }
        }
    }

    private class RefreshAnalyticsAction implements ActionListener {
        public void actionPerformed(ActionEvent e) { refreshAnalytics(); }
    }

    private class GeneratePerfAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                if (user == null || !user.isManager) throw new Exception("Access denied: manager only.");
                Object[][] rows = api.employeePerformance(perfFrom.getText().trim(), perfTo.getText().trim());
                perfModel.setRowCount(0);
                for (int i = 0; i < rows.length; i++) {
                    Object[] r = rows[i];
                    Object[] display = new Object[r.length];
                    for (int j = 0; j < r.length; j++) display[j] = r[j];
                    if (r.length > 1) display[1] = formatMoney(toDouble(r[1]));
                    perfModel.addRow(display);
                }
            } catch(Exception ex) { showError(ex.getMessage()); }
        }
    }

    private class SaveEmailSettingsAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                String f = fromGmail.getText().trim();
                String t = toEmail.getText().trim();
                String p = new String(appPass.getPassword()).trim();
                if (f.length()==0 || t.length()==0 || p.length()==0) throw new Exception("Fill all email fields.");
                api.saveEmailSettings(f, t, p);
                JOptionPane.showMessageDialog(ExtraFrame.this, "Saved email settings.");
                refreshAutoEmailStatus();
            } catch(Exception ex) { showError(ex.getMessage()); }
        }
    }

    private class SendEmailNowAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                String date = emailDate.getText().trim();
                String f = fromGmail.getText().trim();
                String t = toEmail.getText().trim();
                String p = new String(appPass.getPassword()).trim();

                api.sendDailyReport(date, f, p, t);
                emailPreview.setText("SENT ✅\nDate: "+date+"\nTo: "+t+"\nSee proof: data/extra_email_log.txt");
                refreshAutoEmailStatus();
            } catch(Exception ex) { showError(ex.getMessage()); }
        }
    }

    private class DisableEmailSettingsAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                api.saveEmailSettings("", "", "");
                fromGmail.setText("");
                toEmail.setText("");
                appPass.setText("");
                JOptionPane.showMessageDialog(ExtraFrame.this, "Auto email disabled.");
                refreshAutoEmailStatus();
            } catch(Exception ex) { showError(ex.getMessage()); }
        }
    }

    private class AutoEmailTickAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                if (user == null || !user.isManager) return;
                // auto window: 21:50 - 21:59 (before 10pm)
                LocalTime now = LocalTime.now();
                if (now.isBefore(LocalTime.of(21, 50)) || now.isAfter(LocalTime.of(21, 59))) return;

                String date = LocalDate.now().toString();
                if (api.alreadySentToday(date)) {
                    autoEmailStatus.setText("Auto email: already sent today.");
                    return;
                }

                String[] s = api.loadEmailSettings();
                if (s == null) return;
                if (s[0].length()==0 || s[1].length()==0 || s[2].length()==0) return;

                api.sendDailyReport(date, s[0], s[2], s[1]);
                autoEmailStatus.setText("Auto email: SENT at " + now.toString());
            } catch(Exception ex) {
                autoEmailStatus.setText("Auto email failed: " + ex.getMessage());
            }
        }
    }

    private class SearchAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String q = searchField.getText().trim();
            if (q.length() == 0) return;
            String type = searchType.getSelectedItem().toString();

            if (type.startsWith("Stock")) {
                try {
                    String text = api.searchStock(q);
                    searchArea.setText(formatStockByOutletLines(text));
                } catch(Exception ex) { showError(ex.getMessage()); }
            } else {
                try {
                    String text = api.searchSales("DATE", q);
                    if (text != null && text.endsWith("No sales record found.")) {
                        text = api.searchSales("CUSTOMER", q);
                    }
                    if (text != null && text.endsWith("No sales record found.")) {
                        text = api.searchSales("MODEL", q);
                    }
                    searchArea.setText(text);
                } catch(Exception ex) { showError(ex.getMessage()); }
            }
        }
    }

    private class RegisterEmployeeAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                if (user == null || !user.isManager) {
                    regStatus.setText("Access denied: manager only.");
                    return;
                }
                String id = regIdField.getText().trim();
                String name = regNameField.getText().trim();
                String pass = new String(regPassField.getPassword()).trim();
                String role = regRoleBox.getSelectedItem().toString().trim();
                String outlet = regOutletField.getText().trim();

                if (id.length()==0 || name.length()==0 || pass.length()==0) {
                    regStatus.setText("Please fill all fields.");
                    return;
                }
                if (!isKnownOutletCode(outlet)) {
                    regStatus.setText("Invalid outlet code.");
                    return;
                }
                boolean ok = api.registerEmployee(id, name, role, pass, outlet);
                if (!ok) {
                    regStatus.setText("Error: User ID already exists.");
                    return;
                }
                regStatus.setText("Employee successfully registered!");

                regIdField.setText("");
                regNameField.setText("");
                regPassField.setText("");
                regRoleBox.setSelectedIndex(0);
                regOutletField.setText("");
            } catch(Exception ex) {
                regStatus.setText("Error registering employee.");
            }
        }
    }

    private void doStockCount(String title) {
        if (user == null) return;
        try {
            Object[][] rows = api.getStockTable(user.outletCode);
            String[] models = new String[rows.length];
            int[] counts = new int[rows.length];
            int[] systemQtys = new int[rows.length];
            int size = 0;
            int mismatches = 0;

            String date = LocalDate.now().toString();
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")).toLowerCase();
            String text = "=== " + title + " ===\n\n" +
                    "Date: " + date + "\n\n" +
                    "Time: " + formatTimeDisplay(time) + "\n\n";
            stockCountArea.setText(text);

            for (int i = 0; i < rows.length; i++) {
                String modelName = rows[i][0].toString();
                int systemQty = Integer.parseInt(rows[i][2].toString());
                int counted = promptCountedQty(title, modelName, systemQty);
                if (counted == Integer.MIN_VALUE) {
                    countStatus.setText(title + " canceled.");
                    return;
                }

                models[size] = modelName;
                counts[size] = counted;
                systemQtys[size] = systemQty;
                size++;

                text += "Model: " + modelName + " - Counted: " + counted + "\n\n";
                text += "Store Record: " + systemQty + "\n\n";
                if (counted == systemQty) {
                    text += "Stock tally correct.\n\n";
                } else {
                    mismatches++;
                    int diff = counted - systemQty;
                    if (diff < 0) diff = -diff;
                    text += "! Mismatch detected (" + diff + " unit difference)\n\n";
                }
                stockCountArea.setText(text);
            }

            int[] result = api.stockCount(user.outletCode, title, models, counts, size);
            int totalChecked = result[0];
            int tallyCorrect = totalChecked - mismatches;
            String completed = title.replace("Stock Count", "stock count") + " completed.";

            text += "Total Models Checked: " + totalChecked + "\n\n";
            text += "Tally Correct: " + tallyCorrect + "\n\n";
            text += "Mismatches: " + mismatches + "\n\n";
            text += completed + "\n";
            if (mismatches > 0) text += "Warning: Please verify stock.\n";

            stockCountArea.setText(text);
            countStatus.setText(title + " completed.");
        } catch (Exception ex) {
            showError("Stock count error: " + ex.getMessage());
        }
    }

    private void doStockTransfer(String type) {
        if (user == null) return;
        String myOutlet = user.outletCode;
        String otherOutlet = JOptionPane.showInputDialog(this, "Enter other outlet code (e.g., C60 or HQ):");
        if (otherOutlet == null || otherOutlet.trim().length() == 0) return;
        otherOutlet = otherOutlet.trim();
        if (!isValidOutletInput(otherOutlet)) {
            showError("Invalid outlet. Use C60–C69 or HQ / Service Center.");
            return;
        }

        int capacity = 8;
        String[] models = new String[capacity];
        int[] qtys = new int[capacity];
        int size = 0;

        while (true) {
            String modelName = JOptionPane.showInputDialog(this, "Enter Model Name (Cancel to stop):");
            if (modelName == null || modelName.trim().length() == 0) break;
            modelName = modelName.trim();
            if (!isValidModelName(modelName)) {
                showError("Invalid model name. Please use a model from model.csv.");
                continue;
            }

            String qtyStr = JOptionPane.showInputDialog(this, "Enter Quantity:");
            if (qtyStr == null || qtyStr.trim().length() == 0) break;
            int qty;
            try { qty = Integer.parseInt(qtyStr.trim()); }
            catch (Exception e) { JOptionPane.showMessageDialog(this, "Invalid quantity."); continue; }
            if (qty <= 0) { JOptionPane.showMessageDialog(this, "Quantity must be greater than 0."); continue; }

            if (size >= capacity) {
                capacity = capacity * 2;
                String[] newModels = new String[capacity];
                int[] newQtys = new int[capacity];
                System.arraycopy(models, 0, newModels, 0, size);
                System.arraycopy(qtys, 0, newQtys, 0, size);
                models = newModels;
                qtys = newQtys;
            }
            models[size] = modelName;
            qtys[size] = qty;
            size++;
        }

        if (size == 0) return;
        try {
            String err = api.transferStockMessage(type, myOutlet, otherOutlet, models, qtys, size, user.name);
            if (err != null) {
                transferStatus.setText("Transfer failed.");
                showError(err);
                return;
            }

            String date = LocalDate.now().toString();
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")).toLowerCase();
            String fromLabel = "Stock In".equalsIgnoreCase(type) ? otherOutlet : myOutlet;
            String toLabel = "Stock In".equalsIgnoreCase(type) ? myOutlet : otherOutlet;

            String text = "=== " + type + " ===\n\n";
            text += "Date: " + date + "\n\n";
            text += "Time: " + formatTimeDisplay(time) + "\n\n";
            text += "From: " + outletLabel(fromLabel) + "\n";
            text += "To:   " + outletLabel(toLabel) + "\n\n";
            text += "Models:\n";

            int totalQty = 0;
            for (int i = 0; i < size; i++) {
                text += " - " + models[i] + " (Quantity: " + qtys[i] + ")\n";
                totalQty += qtys[i];
            }
            text += "\nTotal Quantity: " + totalQty + "\n\n";
            text += "Model quantities updated successfully.\n";
            text += type + " recorded.\n";
            text += "Receipt generated: receipts/receipts_" + date + ".txt\n";

            transferArea.setText(text);
            transferStatus.setText(type + " recorded.");
            refreshStock();
        } catch (Exception ex) {
            showError("Transfer error: " + ex.getMessage());
        }
    }

 

 

    private void editStockDialog() {
        String model = JOptionPane.showInputDialog(this, "Enter Model Name:");
        if (model == null || model.trim().length() == 0) return;
        model = model.trim();
        if (!isValidModelName(model)) { showError("Invalid model name."); return; }
        String outlet = JOptionPane.showInputDialog(this, "Enter Outlet Code:");
        if (outlet == null || outlet.trim().length() == 0) return;
        outlet = outlet.trim();
        if (!isValidOutletInput(outlet)) { showError("Invalid outlet code."); return; }

        int currentQty = getStockQty(model, outlet);
        if (currentQty < 0) { showError("Stock record not found."); return; }
        JOptionPane.showMessageDialog(this,
            "=== Edit Stock Information ===\n" +
            "Model: " + model + "\n" +
            "Current Stock: " + currentQty
        );

        String newQtyStr = JOptionPane.showInputDialog(this, "Enter New Stock Value:");
        if (newQtyStr == null || newQtyStr.trim().length() == 0) return;
        int newQty;
        try { newQty = Integer.parseInt(newQtyStr.trim()); }
        catch (Exception e) { showError("Invalid quantity."); return; }

        int confirm = JOptionPane.showConfirmDialog(this, "Confirm update?", "Confirm",
            JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = api.editStock(model, outlet, newQty);
            editStatus.setText(ok ? "Stock information updated successfully." : "Stock record not found.");
            refreshStock();
        } catch (Exception e) {
            showError("Error updating stock.");
        }
    }

    private void editStockCountDialog() {
        String date = JOptionPane.showInputDialog(this, "Enter Date (YYYY-MM-DD):");
        if (date == null || date.trim().length() == 0) return;
        date = date.trim();

        String outlet = JOptionPane.showInputDialog(this, "Enter Outlet Code:");
        if (outlet == null || outlet.trim().length() == 0) return;
        outlet = outlet.trim();

        String[] types = {"Morning Stock Count", "Night Stock Count"};
        Object t = JOptionPane.showInputDialog(this, "Select Count Type:", "Edit Stock Count",
            JOptionPane.PLAIN_MESSAGE, null, types, types[0]);
        if (t == null) return;
        String type = t.toString();

        String model = JOptionPane.showInputDialog(this, "Enter Model Name:");
        if (model == null || model.trim().length() == 0) return;
        model = model.trim();
        if (!isValidModelName(model)) { showError("Invalid model name."); return; }

        String[] rec = findStockCountRecord(date, outlet, type, model);
        if (rec == null) { showError("Stock count record not found."); return; }
        JOptionPane.showMessageDialog(this,
            "Stock Count Record Found:\n" +
            "Date: " + date + "  Time: " + rec[0] + "\n" +
            "System Qty: " + rec[1] + "\n" +
            "Counted Qty: " + rec[2]
        );

        String newQtyStr = JOptionPane.showInputDialog(this, "Enter New Counted Quantity:");
        if (newQtyStr == null || newQtyStr.trim().length() == 0) return;
        int newQty;
        try { newQty = Integer.parseInt(newQtyStr.trim()); }
        catch (Exception e) { showError("Invalid quantity."); return; }

        int confirm = JOptionPane.showConfirmDialog(this, "Confirm update?", "Confirm",
            JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = api.updateStockCount(date, outlet, type, model, newQty);
            editStatus.setText(ok ? "Stock count updated successfully." : "Stock count record not found.");
        } catch (Exception e) {
            showError("Error updating stock count.");
        }
    }

    private void editSalesDialog() {
        String date = JOptionPane.showInputDialog(this, "Enter Transaction Date (YYYY-MM-DD):");
        if (date == null || date.trim().length() == 0) return;
        String customer = JOptionPane.showInputDialog(this, "Enter Customer Name:");
        if (customer == null || customer.trim().length() == 0) return;

        String[] rec = findSalesRecord(date.trim(), customer.trim());
        if (rec == null) { showError("Sales record not found."); return; }
        JOptionPane.showMessageDialog(this,
            "Sales Record Found:\n" +
            "Date: " + rec[0] + "  Time: " + rec[1] + "\n" +
            "Model: " + rec[2] + "  Quantity: " + rec[3] + "\n" +
            "Total: " + formatMoney(parseMoney(rec[4])) + "\n" +
            "Transaction Method: " + rec[5]
        );

        String[] choices = {"Customer Name","Model Name","Quantity","Total Price","Payment Method"};
        Object choice = JOptionPane.showInputDialog(this, "Select field to edit:", "Edit Sales",
            JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);
        if (choice == null) return;

        String newVal = JOptionPane.showInputDialog(this, "Enter New Value:");
        if (newVal == null || newVal.trim().length() == 0) return;
        newVal = newVal.trim();

        int confirm = JOptionPane.showConfirmDialog(this, "Confirm update?", "Confirm",
            JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = api.editSales(date.trim(), customer.trim(), choice.toString(), newVal);
            editStatus.setText(ok ? "Sales information updated successfully." : "Sales record not found.");
        } catch (Exception e) {
            showError("Error updating sales.");
        }
    }

    private boolean isValidOutletInput(String outlet) {
        if (outlet == null) return false;
        String s = outlet.trim();
        if (s.equalsIgnoreCase("HQ") || s.equalsIgnoreCase("Service Center")) return true;
        return outletLabel(s).indexOf("(") > 0;
    }

    private boolean isKnownOutletCode(String outlet) {
        if (outlet == null) return false;
        String code = outlet.trim();
        if (code.length() == 0) return false;
        try (BufferedReader br = new BufferedReader(new FileReader(FilePathHelper.resolveReadPath("outlet.csv")))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().length() == 0) continue;
                String[] p = line.split(",", -1);
                if (p.length >= 1 && p[0].trim().equalsIgnoreCase(code)) return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    private boolean isValidModelName(String modelName) {
        if (modelName == null) return false;
        String name = modelName.trim();
        if (name.length() == 0) return false;
        try {
            String[] models = api.listAllModels();
            for (int i = 0; i < models.length; i++) {
                if (models[i] != null && models[i].equalsIgnoreCase(name)) return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    private String formatStockByOutletLines(String text) {
        if (text == null) return null;
        String key = "Stock by Outlet:";
        int idx = text.indexOf(key);
        if (idx < 0) return text;

        int start = idx + key.length();
        String head = text.substring(0, start);
        String tail = text.substring(start);

        // trim leading spaces/newlines from the outlet list
        while (tail.startsWith("\r") || tail.startsWith("\n") || tail.startsWith(" ")) {
            tail = tail.substring(1);
        }
        tail = tail.replace("  ", "\n");
        return head + "\n" + tail;
    }

    private void updateCartTotal() {
        double sum = 0.0;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            int qty = 0;
            try { qty = Integer.parseInt(cartModel.getValueAt(i,1).toString()); } catch(Exception ignored){}
            double price = parseMoney(cartModel.getValueAt(i,2).toString());
            sum += price * qty;
        }
        totalLabel.setText("Total: " + formatMoney(sum));
    }

    private String formatMoney(double value) {
        return "RM " + String.format(java.util.Locale.US, "%.2f", value);
    }

    private double parseMoney(String text) {
        if (text == null) return 0.0;
        String s = text.trim();
        if (s.startsWith("RM")) s = s.substring(2).trim();
        s = s.replace(",", "");
        try { return Double.parseDouble(s); } catch(Exception ignored) { return 0.0; }
    }

    private double toDouble(Object value) {
        if (value == null) return 0.0;
        if (value instanceof Number) return ((Number)value).doubleValue();
        return parseMoney(value.toString());
    }
 
    private int getStockQty(String modelName, String outletCode) {
        if (modelName == null || outletCode == null) return -1;
        String model = modelName.trim();
        String outlet = outletCode.trim();
        if (model.length() == 0 || outlet.length() == 0) return -1;
        try (BufferedReader br = new BufferedReader(new FileReader(FilePathHelper.resolveReadPath("stock.csv")))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().length() == 0) continue;
                String[] p = line.split(",", -1);
                if (p.length >= 3 &&
                    p[0].trim().equalsIgnoreCase(model) &&
                    p[1].trim().equalsIgnoreCase(outlet)) {
                    try { return Integer.parseInt(p[2].trim()); } catch (Exception ignored) { return -1; }
                }
            }
        } catch (Exception ignored) {}
        return -1;
    }

    private String[] findSalesRecord(String date, String customer) {
        if (date == null || customer == null) return null;
        String d = date.trim();
        String c = customer.trim();
        if (d.length() == 0 || c.length() == 0) return null;

        try (BufferedReader br = new BufferedReader(new FileReader(FilePathHelper.resolveReadPath("sales_data.csv")))) {
            br.readLine(); // header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().length() == 0) continue;
                String[] p = line.split(",", -1);

                String rowDate = "";
                String rowTime = "N/A";
                String rowCustomer = "";
                String rowModel = "";
                String rowQty = "";
                String rowTotal = "";
                String rowMethod = "N/A";

                if (p.length >= 9) {
                    rowDate = p[1].trim();
                    rowTime = p[2].trim();
                    rowCustomer = p[3].trim();
                    rowModel = p[4].trim();
                    rowQty = p[5].trim();
                    rowTotal = p[6].trim();
                    rowMethod = p[8].trim();
                } else if (p.length >= 8) {
                    rowDate = p[1].trim();
                    rowCustomer = p[2].trim();
                    rowModel = p[3].trim();
                    rowQty = p[4].trim();
                    rowTotal = p[5].trim();
                    rowMethod = p[7].trim();
                } else if (p.length >= 6) {
                    rowDate = p[1].trim();
                    rowCustomer = p[2].trim();
                    rowModel = p[3].trim();
                    rowQty = p[4].trim();
                    rowTotal = p[5].trim();
                } else {
                    continue;
                }

                if (rowDate.equalsIgnoreCase(d) && rowCustomer.equalsIgnoreCase(c)) {
                    return new String[]{rowDate, rowTime, rowModel, rowQty, rowTotal, rowMethod};
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private String[] findStockCountRecord(String date, String outlet, String countType, String model) {
        if (date == null || outlet == null || countType == null || model == null) return null;
        String d = date.trim();
        String o = outlet.trim();
        String t = countType.trim();
        String m = model.trim();
        if (d.length() == 0 || o.length() == 0 || t.length() == 0 || m.length() == 0) return null;

        String path = FilePathHelper.resolveReadPath("stock_count.csv");
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine(); // header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().length() == 0) continue;
                String[] p = line.split(",", -1);
                if (p.length >= 7 &&
                    d.equalsIgnoreCase(p[0].trim()) &&
                    o.equalsIgnoreCase(p[2].trim()) &&
                    t.equalsIgnoreCase(p[3].trim()) &&
                    m.equalsIgnoreCase(p[4].trim())) {
                    String time = (p.length > 1) ? p[1].trim() : "N/A";
                    String systemQty = (p.length > 5) ? p[5].trim() : "0";
                    String countedQty = (p.length > 6) ? p[6].trim() : "0";
                    String diff = (p.length > 7) ? p[7].trim() : "";
                    return new String[]{time, systemQty, countedQty, diff};
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private String getAttendanceTime(String employeeId, String date, boolean clockOut) {
        String time = "";
        try (BufferedReader br = new BufferedReader(new FileReader(FilePathHelper.resolveReadPath("attendance.csv")))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().length() == 0) continue;
                String[] p = line.split(",", -1);
                if (p.length >= 3 &&
                    p[0].trim().equalsIgnoreCase(employeeId) &&
                    p[1].trim().equalsIgnoreCase(date)) {
                    String t = clockOut ? (p.length > 3 ? p[3].trim() : "") : p[2].trim();
                    if (t.length() > 0) time = t;
                }
            }
        } catch (Exception ignored) {}
        if (time.length() == 0) time = "N/A";
        return time;
    }

    private String formatTimeDisplay(String time) {
        if (time == null) return "N/A";
        String s = time.trim().toLowerCase();
        if (s.endsWith("am")) s = s.substring(0, s.length() - 2) + "a.m.";
        else if (s.endsWith("pm")) s = s.substring(0, s.length() - 2) + "p.m.";
        return s;
    }

    private String outletLabel(String code) {
        if (code != null) {
            if (code.equalsIgnoreCase("HQ") || code.equalsIgnoreCase("Service Center")) {
                return "HQ (Service Center)";
            }
        }
        String name = "";
        try (BufferedReader br = new BufferedReader(new FileReader(FilePathHelper.resolveReadPath("outlet.csv")))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length >= 2 && p[0].trim().equalsIgnoreCase(code)) {
                    name = p[1].trim();
                    break;
                }
            }
        } catch (Exception ignored) {}
        if (name.length() == 0) return code;
        return code + " (" + name + ")";
    }

}
