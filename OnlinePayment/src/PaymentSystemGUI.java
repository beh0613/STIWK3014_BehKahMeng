import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PaymentSystemGUI extends JFrame {

    private static double balance = 1000.00;
    private static final Lock balanceLock = new ReentrantLock();

    private JTextField userField, amountField;
    private JTextArea outputArea;

    public PaymentSystemGUI() {
        setTitle("Real-Time Online Payment System");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Make a Payment"));

        inputPanel.add(new JLabel("User Name:"));
        userField = new JTextField();
        inputPanel.add(userField);

        inputPanel.add(new JLabel("Amount ($):"));
        amountField = new JTextField();
        inputPanel.add(amountField);

        JButton payButton = new JButton("Make Payment");
        JButton bulkPayButton = new JButton("Simulate 10 Users Paying");
        JButton clearButton = new JButton("Clear Log");
        inputPanel.add(payButton);
        inputPanel.add(bulkPayButton);
        inputPanel.add(clearButton);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        payButton.addActionListener(e -> {
            String user = userField.getText().trim();
            String amountText = amountField.getText().trim();

            if (user.isEmpty() || amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter user and amount.");
                return;
            }

            try {
                double amount = Double.parseDouble(amountText);
                Thread paymentThread = new Thread(() -> processPayment(user, amount));
                paymentThread.start();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount.");
            }
        });

        bulkPayButton.addActionListener(e -> simulateMultipleUsers());

        clearButton.addActionListener(e -> outputArea.setText(""));
    }

    private void processPayment(String user, double amount) {
        appendOutput(user + " is attempting to pay $" + amount);

        try {
            Thread.sleep(500);  // Simulate processing delay
        } catch (InterruptedException e) {
            appendOutput(user + " transaction interrupted.");
        }

        balanceLock.lock();
        try {
            if (balance >= amount) {
                balance -= amount;
                appendOutput("✅ " + user + " paid $" + amount + " | Remaining balance: $" + balance);
            } else {
                appendOutput("❌ " + user + ": Insufficient funds.");
            }
        } finally {
            balanceLock.unlock();
        }
    }

    private void simulateMultipleUsers() {
        appendOutput("Starting simulation: 10 users paying concurrently...");

        List<Thread> threads = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            String userName = "User" + i;
            double amount = 120.0; // each tries to pay $120

            Thread t = new Thread(() -> processPayment(userName, amount));
            threads.add(t);
            t.start();
        }

        // Join all threads to wait for completion
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                appendOutput("Thread interrupted while waiting.");
            }
        }

        appendOutput("All user payments processed.");
    }

    private void appendOutput(String message) {
        SwingUtilities.invokeLater(() -> outputArea.append(message + "\n"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PaymentSystemGUI().setVisible(true));
    }
}
