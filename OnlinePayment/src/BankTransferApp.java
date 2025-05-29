import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class BankTransferApp extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private java.util.List<String> transactions = new ArrayList<>();
    private Map<String, Double> accountBalances = new HashMap<>();

    private JTextField accountNumberField;
    private JLabel balanceLabel;
    private JComboBox<String> fromBankCombo;
    private JComboBox<String> toBankCombo;
    private JTextField amountField;

    private JList<String> receiptList;

    public BankTransferApp() {
        setTitle("💸 Bank Transfer System");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        accountBalances.put("12345", 1500.0);
        accountBalances.put("23456", 800.0);
        accountBalances.put("34567", 3000.0);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createMainMenuPanel(), "MainMenu");
        mainPanel.add(createTransferMoneyPanel(), "TransferMoney");
        mainPanel.add(createTransactionReceiptPanel(), "TransactionReceipt");

        add(mainPanel);
        cardLayout.show(mainPanel, "MainMenu");
    }

    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(42, 87, 141)); // deep blue background

        JLabel label = new JLabel("Welcome to Bank Transfer System", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(Color.WHITE);
        label.setBorder(new EmptyBorder(30, 0, 30, 0));
        panel.add(label, BorderLayout.NORTH);

        JButton btnTransfer = styledButton("💰 Transfer Money");
        JButton btnViewReceipt = styledButton("📄 View Transaction Receipts");

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(42, 87, 141));
        btnPanel.add(btnTransfer);
        btnPanel.add(btnViewReceipt);
        panel.add(btnPanel, BorderLayout.CENTER);

        btnTransfer.addActionListener(e -> cardLayout.show(mainPanel, "TransferMoney"));
        btnViewReceipt.addActionListener(e -> {
            updateReceiptList();
            cardLayout.show(mainPanel, "TransactionReceipt");
        });

        return panel;
    }

    private JButton styledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(70, 130, 180)); // steel blue
        btn.setFocusPainted(false);
        btn.setBorder(new RoundedBorder(10));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(220, 45));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(100, 149, 237)); // lighter blue on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(70, 130, 180));
            }
        });

        return btn;
    }

    private JPanel createTransferMoneyPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255)); // AliceBlue background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblTitle = new JLabel("Transfer Money");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(25, 25, 112)); // Midnight Blue
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 1;

        // Account Number
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(createLabel("Your Account Number:"), gbc);
        gbc.gridx = 1;
        accountNumberField = createTextField(15);
        panel.add(accountNumberField, gbc);

        // Update balance when account number field loses focus
        accountNumberField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                updateBalanceLabel();
            }
        });

        // From Bank
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(createLabel("From Bank:"), gbc);
        gbc.gridx = 1;
        fromBankCombo = new JComboBox<>(new String[]{"Bank A", "Bank B", "Bank C"});
        styleComboBox(fromBankCombo);
        panel.add(fromBankCombo, gbc);

        // To Bank
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(createLabel("To Bank:"), gbc);
        gbc.gridx = 1;
        toBankCombo = new JComboBox<>(new String[]{"Bank A", "Bank B", "Bank C"});
        styleComboBox(toBankCombo);
        panel.add(toBankCombo, gbc);

        // Amount
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(createLabel("Amount ($):"), gbc);
        gbc.gridx = 1;
        amountField = createTextField(10);
        panel.add(amountField, gbc);

        // Balance Label
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(createLabel("Current Balance:"), gbc);
        gbc.gridx = 1;
        balanceLabel = new JLabel("$0.00");
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        balanceLabel.setForeground(new Color(34, 139, 34)); // Forest Green
        panel.add(balanceLabel, gbc);

        // Buttons
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        JButton btnSend = styledButton("Send Transfer");
        JButton btnBack = styledButton("Back");
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setBackground(new Color(240, 248, 255));
        btnPanel.add(btnSend);
        btnPanel.add(btnBack);
        panel.add(btnPanel, gbc);

        btnSend.addActionListener(e -> processTransfer());
        btnBack.addActionListener(e -> {
            clearTransferForm();
            cardLayout.show(mainPanel, "MainMenu");
        });

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(new Color(25, 25, 112));
        return label;
    }

    private JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tf.setBorder(new RoundedBorder(8));
        return tf;
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(new RoundedBorder(8));
        comboBox.setPreferredSize(new Dimension(180, 30));
    }

    private void updateBalanceLabel() {
        String accNum = accountNumberField.getText().trim();
        Double balance = accountBalances.get(accNum);
        if (balance == null) {
            balanceLabel.setText("Account not found");
            balanceLabel.setForeground(Color.RED);
        } else {
            balanceLabel.setText(String.format("$%.2f", balance));
            balanceLabel.setForeground(new Color(34, 139, 34)); // Forest Green
        }
    }

    private void processTransfer() {
        String accNum = accountNumberField.getText().trim();
        if (!accountBalances.containsKey(accNum)) {
            showErrorDialog("Account number not found.");
            return;
        }

        String fromBank = (String) fromBankCombo.getSelectedItem();
        String toBank = (String) toBankCombo.getSelectedItem();
        String amountText = amountField.getText().trim();

        if (fromBank.equals(toBank)) {
            showErrorDialog("From Bank and To Bank cannot be the same.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                showErrorDialog("Amount must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            showErrorDialog("Invalid amount.");
            return;
        }

        double currentBalance = accountBalances.get(accNum);
        if (currentBalance < amount) {
            showErrorDialog("Insufficient balance.");
            return;
        }

        accountBalances.put(accNum, currentBalance - amount);
        updateBalanceLabel();

        String receipt = String.format("Transferred $%.2f from %s (%s) to %s", amount, fromBank, accNum, toBank);
        transactions.add(receipt);
        showInfoDialog("Transaction Successful!\n" + receipt);
        clearTransferForm();
        cardLayout.show(mainPanel, "MainMenu");
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfoDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearTransferForm() {
        accountNumberField.setText("");
        amountField.setText("");
        fromBankCombo.setSelectedIndex(0);
        toBankCombo.setSelectedIndex(0);
        balanceLabel.setText("$0.00");
        balanceLabel.setForeground(new Color(34, 139, 34));
    }

    private JPanel createTransactionReceiptPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 248, 220)); // Cornsilk background

        JLabel label = new JLabel("Transaction Receipts", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setBorder(new EmptyBorder(20, 0, 20, 0));
        label.setForeground(new Color(139, 69, 19)); // SaddleBrown
        panel.add(label, BorderLayout.NORTH);

        receiptList = new JList<>();
        receiptList.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(receiptList);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton btnBack = styledButton("Back");
        btnBack.setPreferredSize(new Dimension(100, 40));
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(255, 248, 220));
        btnPanel.add(btnBack);
        panel.add(btnPanel, BorderLayout.SOUTH);

        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "MainMenu"));

        return panel;
    }

    private void updateReceiptList() {
        receiptList.setListData(transactions.toArray(new String[0]));
    }

    // Rounded border class for inputs and buttons
    class RoundedBorder extends AbstractBorder {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(70, 130, 180));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, radius, radius);
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 1, this.radius + 1);
        }

        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = this.radius + 1;
            return insets;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BankTransferApp app = new BankTransferApp();
            app.setVisible(true);
        });
    }
}
