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
import lk.ijse.desktop.myfx.myfinalproject.DBConnection.DBConnection;
import lk.ijse.desktop.myfx.myfinalproject.Dto.CustomerDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOFactory;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOTypes;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.CustomerBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.InUseException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {

    @FXML private TableColumn<CustomerDto, String> colAddress;
    @FXML private TableColumn<CustomerDto, String> colCustId;
    @FXML private TableColumn<CustomerDto, String> colName;
    @FXML private TableColumn<CustomerDto, String> colNumber;
    @FXML private TableView<CustomerDto> tblCustomer;
    @FXML private TextField txtAddress;
    @FXML private Label lblId;
    @FXML private TextField txtName;
    @FXML private TextField txtNumber;
    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;

    private final String namePattern = "^[A-Za-z ]+$";
    private final String numberPattern = "^0\\d{9}$";

    private final CustomerBO customerBO = BOFactory.getInstance().getBO(BOTypes.CUSTOMER);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        setupFieldListeners();
        try {
            loadNextId();
            loadTable();
            updateButtonStates();
            resetValidationStyles();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize CustomerController", e);
        }
    }

    private void setupTableColumns() {
        colCustId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colNumber.setCellValueFactory(new PropertyValueFactory<>("customerNumber"));
    }

    private void loadTable() {
        try {
            List<CustomerDto> customerDtos = customerBO.getAllCustomer();
            tblCustomer.setItems(FXCollections.observableArrayList(customerDtos));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading customer data into table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupFieldListeners() {
        txtName.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtAddress.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtNumber.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        tblCustomer.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
    }

    private void updateButtonStates() {
        boolean isAnyFieldEmpty = txtName.getText().isEmpty() ||
                txtAddress.getText().isEmpty() ||
                txtNumber.getText().isEmpty();

        boolean isValid = isValidInputs(false);

        CustomerDto selectedItem = tblCustomer.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            btnSave.setDisable(isAnyFieldEmpty || !isValid);
            btnUpdate.setDisable(true);
            btnDelete.setDisable(true);
        } else {
            btnSave.setDisable(true);
            btnUpdate.setDisable(isAnyFieldEmpty || !isValid);
            btnDelete.setDisable(false);
        }
    }

    private void loadNextId() throws SQLException {
        String nextId = customerBO.getNextId();
        lblId.setText(nextId);
    }

    private void clearFields() throws SQLException {
        lblId.setText("");
        txtName.clear();
        txtAddress.clear();
        txtNumber.clear();
        resetValidationStyles();

        loadNextId();
        loadTable();
        tblCustomer.getSelectionModel().clearSelection();
        updateButtonStates();
    }

    private boolean isValidInputs(boolean showDialog) {
        boolean isValidName = txtName.getText().matches(namePattern);
        boolean isValidNumber = txtNumber.getText().matches(numberPattern);
        boolean isAddressEmpty = txtAddress.getText().isEmpty();

        if (isAddressEmpty) {
            if (showDialog) showAlert(Alert.AlertType.WARNING, "Address cannot be empty.");
            return false;
        }
        if (!isValidName) {
            if (showDialog) showAlert(Alert.AlertType.WARNING, "Customer Name should contain only letters.");
            return false;
        }
        if (!isValidNumber) {
            if (showDialog) showAlert(Alert.AlertType.WARNING, "Contact Number should be 10 digits and start with 0.");
            return false;
        }

        return true;
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

        if (id == null || id.isEmpty() || id.equals("Auto Generated")) {
            showAlert(Alert.AlertType.WARNING, "Please select a customer to delete from the table.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Customer");
        alert.setContentText("Are you sure you want to delete this customer?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = customerBO.deleteCustomer(id);
                if (isDeleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Customer has been deleted successfully!");
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed to delete customer.");
                }
            } catch (NotFoundException | InUseException e) {
                showAlert(Alert.AlertType.ERROR, e.getMessage());
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "An error occurred during deletion: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void btnSaveOnAction(ActionEvent event) {
        if (!isValidInputs(true)) {
            return;
        }

        CustomerDto customerDto = new CustomerDto(
                lblId.getText(),
                txtName.getText(),
                txtAddress.getText(),
                txtNumber.getText()
        );

        try {
            customerBO.saveCustomer(customerDto);
            showAlert(Alert.AlertType.INFORMATION, "Customer Saved Successfully!");
            clearFields();
        } catch (DuplicateException e) {
            showAlert(Alert.AlertType.ERROR, e.getMessage());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "An error occurred while saving customer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        if (!isValidInputs(true)) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Update Customer");
        alert.setContentText("Are you sure you want to update this customer?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            CustomerDto customerDto = new CustomerDto(
                    lblId.getText(),
                    txtName.getText(),
                    txtAddress.getText(),
                    txtNumber.getText()
            );
            try {
                customerBO.updateCustomer(customerDto);
                showAlert(Alert.AlertType.INFORMATION, "Customer Updated Successfully!");
                clearFields();
            } catch (NotFoundException e) {
                showAlert(Alert.AlertType.ERROR, e.getMessage());
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "An error occurred during update: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void tableOnClick(MouseEvent mouseEvent) {
        CustomerDto customerDto = tblCustomer.getSelectionModel().getSelectedItem();
        if (customerDto != null) {
            lblId.setText(customerDto.getCustomerId());
            txtName.setText(customerDto.getCustomerName());
            txtAddress.setText(customerDto.getAddress());
            txtNumber.setText(customerDto.getCustomerNumber());
            resetValidationStyles();
            updateButtonStates();
        }
    }

    public void txtNameChange(KeyEvent keyEvent) {
        String name = txtName.getText();
        if (name.matches(namePattern)) {
            txtName.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtName.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void txtNumberChange(KeyEvent keyEvent) {
        String number = txtNumber.getText();
        if (number.matches(numberPattern)) {
            txtNumber.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtNumber.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void CustomerReportOnAction(ActionEvent actionEvent) {
        CustomerDto customerDto = tblCustomer.getSelectionModel().getSelectedItem();

        if (customerDto == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a customer first!");
            return;
        }

        try {
            JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("/report/CustomerOrderDetailsReport.jrxml"));
            Connection connection = DBConnection.getInstance().getConnection();

            Map<String, Object> parameters = new HashMap<>();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = LocalDate.now().format(formatter);

            parameters.put("P_Date", formattedDate);
            parameters.put("P_Customer_ID", customerDto.getCustomerId());

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException e) {
            showAlert(Alert.AlertType.ERROR, "Error in generating report: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error during report generation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void applyValidationStyles() {
        txtNameChange(null);
        txtNumberChange(null);
    }

    private void resetValidationStyles() {
        txtName.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtNumber.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
    }
}