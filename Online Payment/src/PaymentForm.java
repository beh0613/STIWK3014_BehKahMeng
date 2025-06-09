

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class PaymentForm extends Application {
    @Override
    public void start(Stage stage) {
        Label cardLabel = new Label("Card Number:");
        TextField cardField = new TextField();

        Label amountLabel = new Label("Amount:");
        TextField amountField = new TextField();

        Label ipLabel = new Label("Recipient IP:");
        TextField ipField = new TextField();

        Button payButton = new Button("Pay Now");
        Label statusLabel = new Label();

        payButton.setOnAction(e -> {
            String card = cardField.getText();
            String amount = amountField.getText();
            String ip = ipField.getText();

            statusLabel.setText("Processing payment...");

            // Start a payment task in a new thread (simulated)
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // simulate delay
                    statusLabel.setText("Payment Successful to IP: " + ip);
                } catch (InterruptedException ex) {
                    statusLabel.setText("Payment Failed");
                }
            }).start();
        });

        VBox layout = new VBox(10, cardLabel, cardField, amountLabel, amountField, ipLabel, ipField, payButton, statusLabel);
        layout.setStyle("-fx-padding: 20px; -fx-font-size: 14px;");
        Scene scene = new Scene(layout, 400, 300);

        stage.setScene(scene);
        stage.setTitle("Online Payment System");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

