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
import lk.ijse.desktop.myfx.myfinalproject.DBConnection.DBConnection;
import lk.ijse.desktop.myfx.myfinalproject.Dto.CustomerDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.CustomerModel;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOFactory;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOTypes;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.CustomerBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CustomerController implements Initializable {

    @FXML
    private TableColumn<CustomerDto, String> colAddress;

    @FXML
    private TableColumn<CustomerDto, String> colCustId;

    @FXML
    private TableColumn<CustomerDto, String> colName;

    @FXML
    private TableColumn<CustomerDto, String> colNumber;

    @FXML
    private TableView<CustomerDto> tblCustomer;

    @FXML
    private TextField txtAddress;

    @FXML
    private Label lblId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtNumber;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnUpdate;

    private final String namePattern = "^[A-Za-z ]+$";
    private final String numberPattern = "^0\\d{9}$";

    private ObservableList<CustomerDto> data = FXCollections.observableArrayList();
    private final CustomerBO customerBO = BOFactory.getInstance().getBO(BOTypes.CUSTOMER);

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFields();
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String id = lblId.getText();

        if (id == null || id.isEmpty() || id.equals("Auto Generated")) {
            new Alert(Alert.AlertType.WARNING, "Please select a customer to delete from the table.").show();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Customer");
        alert.setContentText("Are you sure you want to delete this customer?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDelete = new CustomerModel().deleteCustomer(new CustomerDto(id, null, null, null));
                if (isDelete) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "Customer has been deleted successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete customer.").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "An error occurred during deletion: " + e.getMessage()).show();
            }
        }
    }

    private void loadNextId() throws SQLException{
        CustomerModel customerModel = new CustomerModel();
        String nextId = customerModel.getNextId();
        lblId.setText(nextId);
    }

    @FXML
    public void btnSaveOnAction(ActionEvent event) throws ClassNotFoundException, SQLException {
        String name = txtName.getText();
        String address = txtAddress.getText();
        String number = txtNumber.getText();

        boolean isValidName = name.matches(namePattern);
        boolean isValidNumber = number.matches(numberPattern);

        if (isValidName && isValidNumber) {
            CustomerDto customerDto = new CustomerDto(lblId.getText(), name, address, number);
            try {
                customerBO.saveCustomer(customerDto);
                new Alert(Alert.AlertType.INFORMATION, "Customer has been saved successfully!").show();
               clearFields();
            }catch (DuplicateException e){
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure Customer Name (letters only) and Contact Number (10 digits starting with 0) are valid.").show();
            applyValidationStyles();
        }
    }

    private void clearFields() throws SQLException {
        lblId.setText("");
        txtName.setText("");
        txtAddress.setText("");
        txtNumber.setText("");
        resetValidationStyles();

        loadNextId();
        loadTable();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadNextId();
            setupTableColumns();
            clearFields();
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing controller: " + e.getMessage(), e);
        }
    }

    private void setupTableColumns(){
        colCustId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colNumber.setCellValueFactory(new PropertyValueFactory<>("customerNumber"));
    }

    private void loadTable(){
        try {
            CustomerModel customerModel = new CustomerModel();
            ArrayList<CustomerDto> customerDtos = customerModel.viewAllCustomer();
            if (customerDtos != null){
                ObservableList<CustomerDto> observableList = FXCollections.observableArrayList(customerDtos);
                tblCustomer.setItems(observableList);
            }else {
                tblCustomer.setItems(FXCollections.emptyObservableList());
            }
        }catch (Exception e){
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading customer data into table.").show();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        String name = txtName.getText();
        String address = txtAddress.getText();
        String number = txtNumber.getText();

        boolean isValidName = name.matches(namePattern);
        boolean isValidNumber = number.matches(numberPattern);

        if (isValidName && isValidNumber) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Update Customer");
            alert.setContentText("Are you sure you want to update this customer?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    CustomerDto customerDto = new CustomerDto(lblId.getText(), name, address, number);
                    boolean isUpdated = CustomerModel.updateCustomer(customerDto);
                    if (isUpdated) {
                        clearFields();
                        new Alert(Alert.AlertType.INFORMATION, "Customer Updated Successfully!").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to update customer.").show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "An error occurred during update: " + e.getMessage()).show();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure Customer Name (letters only) and Contact Number (10 digits starting with 0) are valid.").show();
            applyValidationStyles();
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
        }
    }

    public void txtNameChange(KeyEvent keyEvent) {
        String name = txtName.getText();
        boolean isValidName = name.matches(namePattern);
        if (isValidName) {
            txtName.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtName.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    public void txtNumberChange(KeyEvent keyEvent) {
        String number = txtNumber.getText();
        boolean isValidNumber = number.matches(numberPattern);
        if (isValidNumber) {
            txtNumber.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtNumber.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    public void CustomerReportOnAction(ActionEvent actionEvent) {
        CustomerDto customerDto = tblCustomer.getSelectionModel().getSelectedItem();

        if (customerDto == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a customer first!").show();
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
            new Alert(Alert.AlertType.ERROR, "Error in generating report: " + e.getMessage()).show();
            e.printStackTrace();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage()).show();
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