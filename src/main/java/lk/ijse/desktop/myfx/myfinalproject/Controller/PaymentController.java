package lk.ijse.desktop.myfx.myfinalproject.Controller;

import javafx.application.Platform;
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
import lk.ijse.desktop.myfx.myfinalproject.bo.BOFactory;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOTypes;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.PaymentBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;

    private final String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
    private final String amountPattern = "^\\d+(\\.\\d{1,2})?$";

    private final PaymentBO paymentBO = BOFactory.getInstance().getBO(BOTypes.PAYMENT);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        setupFieldListeners();
        try {
            loadNextId();
            loadOrderId();
            loadCustomerId();
            loadPaymentMethod();
            loadTable();
            updateButtonStates();
            clearFields();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error initializing controller: " + e.getMessage(), e);
        }
    }

    private void setupTableColumns() {
        colPaymentId.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colMethod.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
    }

    private void loadOrderId() throws SQLException {
        List<String> orderIds = paymentBO.getAllOrderIds();
        ObservableList<String> orders = FXCollections.observableArrayList(orderIds);
        comOrderId.setItems(orders);
    }

    private void loadCustomerId() throws SQLException {
        List<String> customerIds = paymentBO.getAllCustomerIds();
        ObservableList<String> customers = FXCollections.observableArrayList(customerIds);
        comCustomerId.setItems(customers);
    }

    private void loadPaymentMethod() throws SQLException {
        List<String> paymentMethods = paymentBO.getAllPaymentMethods();
        ObservableList<String> payments = FXCollections.observableArrayList(paymentMethods);
        comPaymentMethod.setItems(payments);
    }

    private void loadNextId() throws SQLException {
        String id = paymentBO.getNextPaymentId();
        lblId.setText(id);
    }

    private void loadTable() {
        try {
            List<PaymentDto> paymentDtos = paymentBO.getAllPayments();
            tblPayment.setItems(FXCollections.observableArrayList(paymentDtos));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading payment data into table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupFieldListeners() {
        txtDate.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtAmount.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        comOrderId.valueProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        comCustomerId.valueProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        comPaymentMethod.valueProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        tblPayment.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
    }

    private void updateButtonStates() {
        if (btnSave == null || btnUpdate == null || btnDelete == null || btnClear == null) {
            return;
        }

        boolean isAnyFieldEmpty = txtDate.getText().isEmpty() ||
                txtAmount.getText().isEmpty() ||
                comOrderId.getValue() == null || comOrderId.getValue().isEmpty() ||
                comCustomerId.getValue() == null || comCustomerId.getValue().isEmpty() ||
                comPaymentMethod.getValue() == null || comPaymentMethod.getValue().isEmpty();

        boolean isValid = isValidInputs(false);

        PaymentDto selectedItem = tblPayment.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            btnSave.setDisable(isAnyFieldEmpty || !isValid);
            btnUpdate.setDisable(true);
            btnDelete.setDisable(true);
        } else {
            btnSave.setDisable(true);
            btnUpdate.setDisable(isAnyFieldEmpty || !isValid);
            btnDelete.setDisable(false);
        }
        btnClear.setDisable(false);
    }

    private void clearFields() throws SQLException {
        lblId.setText("");
        comOrderId.getSelectionModel().clearSelection();
        comCustomerId.getSelectionModel().clearSelection();
        txtDate.clear();
        comPaymentMethod.getSelectionModel().clearSelection();
        txtAmount.clear();
        resetValidationStyles();

        loadNextId();
        loadTable();
        tblPayment.getSelectionModel().clearSelection();
        updateButtonStates();
    }

    private boolean isValidInputs(boolean showDialog) {
        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidAmount = txtAmount.getText().matches(amountPattern);
        boolean isOrderIdSelected = comOrderId.getValue() != null && !comOrderId.getValue().isEmpty();
        boolean isCustomerIdSelected = comCustomerId.getValue() != null && !comCustomerId.getValue().isEmpty();
        boolean isPaymentMethodSelected = comPaymentMethod.getValue() != null && !comPaymentMethod.getValue().isEmpty();

        if (showDialog) {
            if (!isValidDate) {
                showAlert(Alert.AlertType.WARNING, "Date must be in YYYY-MM-DD format (e.g., 2025-07-31).");
                return false;
            }
            if (!isValidAmount) {
                showAlert(Alert.AlertType.WARNING, "Amount must be a valid number (e.g., 5000.00 or 123).");
                return false;
            }
            if (!isOrderIdSelected) {
                showAlert(Alert.AlertType.WARNING, "Please select an Order ID.");
                return false;
            }
            if (!isCustomerIdSelected) {
                showAlert(Alert.AlertType.WARNING, "Please select a Customer ID.");
                return false;
            }
            if (!isPaymentMethodSelected) {
                showAlert(Alert.AlertType.WARNING, "Please select a Payment Method.");
                return false;
            }
        }
        return isValidDate && isValidAmount && isOrderIdSelected && isCustomerIdSelected && isPaymentMethodSelected;
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(alertType.name().replace("_", " "));
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFields();
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String id = lblId.getText();

        if (id == null || id.isEmpty() || lblId.getText().equals("P001") || tblPayment.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a payment record to delete from the table.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Payment");
        alert.setContentText("Are you sure you want to delete this payment record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = paymentBO.deletePayment(id);
                if (isDeleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Payment record deleted successfully!");
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed to delete payment record.");
                }
            } catch (NotFoundException e) {
                showAlert(Alert.AlertType.ERROR, e.getMessage());
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "An error occurred during deletion: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        if (!isValidInputs(true)) {
            applyValidationStyles();
            return;
        }

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

            paymentBO.savePayment(paymentDto);
            showAlert(Alert.AlertType.INFORMATION, "Payment has been saved successfully!");
            clearFields();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid number format for Payment Amount.");
        } catch (SQLException | DuplicateException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to save payment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void btnUpdateOnAction(ActionEvent event) {
        if (!isValidInputs(true)) {
            applyValidationStyles();
            return;
        }

        if (tblPayment.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a payment record from the table to update.");
            return;
        }

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

                paymentBO.updatePayment(paymentDto);
                showAlert(Alert.AlertType.INFORMATION, "Payment has been updated successfully!");
                clearFields();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid number format for Payment Amount.");
            } catch (SQLException | NotFoundException e) {
                showAlert(Alert.AlertType.ERROR, "An error occurred during update: " + e.getMessage());
                e.printStackTrace();
            }
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
            updateButtonStates();
        }
    }

    public void comOrderIdOnAction(ActionEvent actionEvent) {
        if (comOrderId.getValue() != null && !comOrderId.getValue().isEmpty()) {
            comOrderId.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comOrderId.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void comCustomerIdOnAction(ActionEvent actionEvent) {
        if (comCustomerId.getValue() != null && !comCustomerId.getValue().isEmpty()) {
            comCustomerId.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comCustomerId.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void comPaymentMethodOnAction(ActionEvent actionEvent) {
        if (comPaymentMethod.getValue() != null && !comPaymentMethod.getValue().isEmpty()) {
            comPaymentMethod.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comPaymentMethod.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void txtAmountChange(KeyEvent keyEvent) {
        String amount = txtAmount.getText();
        if (amount.matches(amountPattern)) {
            txtAmount.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtAmount.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void txtDateChange(KeyEvent keyEvent) {
        String date = txtDate.getText();
        if (date.matches(datePattern)) {
            txtDate.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtDate.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
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