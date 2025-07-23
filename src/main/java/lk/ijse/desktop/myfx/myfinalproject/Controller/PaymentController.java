package lk.ijse.desktop.myfx.myfinalproject.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import lk.ijse.desktop.myfx.myfinalproject.Dto.PaymentDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.PaymentModel;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class PaymentController implements Initializable {

    @FXML
    private TableColumn<PaymentDto, Double> colAmount;

    @FXML
    private TableColumn<PaymentDto, String> colCustomerId;

    @FXML
    private TableColumn<PaymentDto, String> colDate;

    @FXML
    private TableColumn<PaymentDto, String> colMethod;

    @FXML
    private TableColumn<PaymentDto, String> colOrderId;

    @FXML
    private TableColumn<PaymentDto, String> colPaymentId;

    @FXML
    private TableView<PaymentDto> tblPayment;

    @FXML
    private TextField txtAmount;

    @FXML
    private ComboBox<String> comCustomerId;

    @FXML
    private ComboBox<String> comOrderId;

    @FXML
    private ComboBox<String> comPaymentMethod;

    @FXML
    private TextField txtDate;

    @FXML
    private Label lblId;

    private final String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
    private final String amountPattern = "^\\d+(\\.\\d{1,2})?$";

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFields();
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String id = lblId.getText();

        if (id == null || id.isEmpty() || id.equals("Auto Generated")) {
            new Alert(Alert.AlertType.WARNING, "Please select a payment record to delete from the table.").show();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Payment");
        alert.setContentText("Are you sure you want to delete this payment record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = new PaymentModel().deletePayment(new PaymentDto(id, null, null, null, null, 0.0));
                if (isDeleted) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "Payment record deleted successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete payment record.").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "An error occurred during deletion: " + e.getMessage()).show();
            }
        }
    }

    private void loadNextId() throws SQLException {
        PaymentModel paymentModel = new PaymentModel();
        String id = paymentModel.getNextId();
        lblId.setText(id);
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {

        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidAmount = txtAmount.getText().matches(amountPattern);
        boolean isOrderIdSelected = comOrderId.getValue() != null && !comOrderId.getValue().isEmpty();
        boolean isCustomerIdSelected = comCustomerId.getValue() != null && !comCustomerId.getValue().isEmpty();
        boolean isPaymentMethodSelected = comPaymentMethod.getValue() != null && !comPaymentMethod.getValue().isEmpty();

        if (isValidDate && isValidAmount && isOrderIdSelected && isCustomerIdSelected && isPaymentMethodSelected) {
            try {
                double amount = Double.parseDouble(txtAmount.getText());
                PaymentDto paymentDto = new PaymentDto(
                        lblId.getText(),
                        comOrderId.getValue(),
                        comCustomerId.getValue(),
                        txtDate.getText(),
                        comPaymentMethod.getValue(),
                        amount
                );

                PaymentModel paymentModel = new PaymentModel();
                boolean isSaved = paymentModel.savePayment(paymentDto);
                if (isSaved) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "Payment has been saved successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save payment.").show();
                }
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Invalid number format for Payment Amount.").show();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to save payment due to a database error: " + e.getMessage()).show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly (Date: YYYY-MM-DD, Amount: e.g., 5000.00).").show();
            applyValidationStyles();
        }
    }

    private void clearFields() throws SQLException {
        lblId.setText("");
        comOrderId.getSelectionModel().clearSelection();
        comCustomerId.getSelectionModel().clearSelection();
        txtDate.setText("");
        comPaymentMethod.getSelectionModel().clearSelection();
        txtAmount.setText("");
        resetValidationStyles();

        loadNextId();
        loadTable();
    }

    private void setupTableColumns() {
        colPaymentId.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colMethod.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
    }

    private void loadTable() {
        try {
            PaymentModel paymentModel = new PaymentModel();
            ArrayList<PaymentDto> paymentDtos = paymentModel.viewAllPayment();
            if (paymentDtos != null) {
                ObservableList<PaymentDto> payments = FXCollections.observableArrayList(paymentDtos);
                tblPayment.setItems(payments);
            } else {
                tblPayment.setItems(FXCollections.emptyObservableList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading payment data into table.").show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadNextId();
            loadOrderId();
            loadCustomerId();
            loadPaymentMethod();
            setupTableColumns();
            clearFields();
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing controller: " + e.getMessage(), e);
        }
    }

    private void loadCustomerId() throws SQLException {
        ArrayList<String> customerIds = PaymentModel.getAllCustomerId();
        ObservableList<String> customers = FXCollections.observableArrayList(customerIds);
        comCustomerId.setItems(customers);
    }

    private void loadOrderId() throws SQLException {
        ArrayList<String> orderId = PaymentModel.getAllOrderId();
        ObservableList<String> orders = FXCollections.observableArrayList(orderId);
        comOrderId.setItems(orders);
    }

    private void loadPaymentMethod() throws SQLException {
        ArrayList<String> paymentMethods = PaymentModel.getAllPaymentMethod();
        ObservableList<String> payments = FXCollections.observableArrayList(paymentMethods);
        comPaymentMethod.setItems(payments);
    }

    @FXML
    public void btnUpdateOnAction(ActionEvent event) {

        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidAmount = txtAmount.getText().matches(amountPattern);
        boolean isOrderIdSelected = comOrderId.getValue() != null && !comOrderId.getValue().isEmpty();
        boolean isCustomerIdSelected = comCustomerId.getValue() != null && !comCustomerId.getValue().isEmpty();
        boolean isPaymentMethodSelected = comPaymentMethod.getValue() != null && !comPaymentMethod.getValue().isEmpty();

        if (isValidDate && isValidAmount && isOrderIdSelected && isCustomerIdSelected && isPaymentMethodSelected) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Update Payment");
            alert.setContentText("Are you sure you want to update this payment record?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    double amount = Double.parseDouble(txtAmount.getText());
                    PaymentDto paymentDto = new PaymentDto(
                            lblId.getText(),
                            comOrderId.getValue(),
                            comCustomerId.getValue(),
                            txtDate.getText(),
                            comPaymentMethod.getValue(),
                            amount
                    );

                    boolean isUpdated = PaymentModel.updatePayment(paymentDto);
                    if (isUpdated) {
                        clearFields();
                        new Alert(Alert.AlertType.INFORMATION, "Payment has been updated successfully!").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to update payment.").show();
                    }
                } catch (NumberFormatException e) {
                    new Alert(Alert.AlertType.ERROR, "Invalid number format for Payment Amount.").show();
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "An error occurred during update: " + e.getMessage()).show();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly (Date: YYYY-MM-DD, Amount: e.g., 5000.00).").show();
            applyValidationStyles();
        }
    }

    public void tableOnClick(MouseEvent mouseEvent) {
        PaymentDto paymentDto = tblPayment.getSelectionModel().getSelectedItem();
        if (paymentDto != null) {
            lblId.setText(paymentDto.getPaymentId());
            comOrderId.setValue(paymentDto.getOrderId());
            comCustomerId.setValue(paymentDto.getCustomerId());
            txtDate.setText(paymentDto.getDate());
            comPaymentMethod.setValue(paymentDto.getPaymentMethod());
            txtAmount.setText(String.valueOf(paymentDto.getAmount()));
            resetValidationStyles();
        }
    }

    public void comOrderIdOnAction(ActionEvent actionEvent) {
        if (comOrderId.getValue() != null && !comOrderId.getValue().isEmpty()) {
            comOrderId.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comOrderId.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    public void comCustomerIdOnAction(ActionEvent actionEvent) {
        if (comCustomerId.getValue() != null && !comCustomerId.getValue().isEmpty()) {
            comCustomerId.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comCustomerId.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    public void comPaymentMethodOnAction(ActionEvent actionEvent) {
        if (comPaymentMethod.getValue() != null && !comPaymentMethod.getValue().isEmpty()) {
            comPaymentMethod.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comPaymentMethod.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    public void txtAmountChange(KeyEvent keyEvent) {
        String amount = txtAmount.getText();
        boolean isValid = amount.matches(amountPattern);
        if (isValid) {
            txtAmount.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtAmount.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    public void txtDateChange(KeyEvent keyEvent) {
        String date = txtDate.getText();
        boolean isValid = date.matches(datePattern);
        if (isValid) {
            txtDate.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtDate.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    private void applyValidationStyles() {
        txtDateChange(null);
        txtAmountChange(null);
        comOrderIdOnAction(null);
        comCustomerIdOnAction(null);
        comPaymentMethodOnAction(null);
    }

    private void resetValidationStyles() {
        txtDate.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtAmount.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        comOrderId.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        comCustomerId.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        comPaymentMethod.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
    }
}