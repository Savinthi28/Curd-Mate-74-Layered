package lk.ijse.desktop.myfx.myfinalproject.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.desktop.myfx.myfinalproject.Dto.SupplierDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.SupplierModel;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOFactory;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOTypes;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.SupplierBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SupplierController implements Initializable {
    public AnchorPane getAncSupplier(){
        return null;
    }

    @FXML
    private AnchorPane ancSupplier;
    private String path;
    @FXML
    private TableColumn<SupplierDto, String> colAddress;

    @FXML
    private TableColumn<SupplierDto, String> colId;

    @FXML
    private TableColumn<SupplierDto, String> colName;

    @FXML
    private TableColumn<SupplierDto, String> colNumber;

    @FXML
    private TableView<SupplierDto> tblSupplier;

    @FXML
    private TextField txtAddress;

    @FXML
    private Label lblId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtNumber;

    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;


    private final String namePattern = "^[a-zA-Z ]{2,50}$";
    private final String contactNumberPattern = "^(?:0|94|\\+94)?(7(0|1|2|4|5|6|7|8)\\d{7}|\\d{9,10})$";
    private final String addressPattern = "^[a-zA-Z0-9.,\\- ]{5,100}$";

    private final SupplierBO supplierBO = BOFactory.getInstance().getBO(BOTypes.SUPPLIER);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        setupFieldListeners();
        try {
            loadNextId();
            loadTable();
            clearFields();
            updateButtonStates();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Error initializing controller: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error initializing controller: " + e.getMessage(), e);
        }
    }

    private void setupFieldListeners() {
        txtName.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtNumber.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtAddress.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        tblSupplier.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
    }

    private void updateButtonStates() {
        if (btnSave == null || btnUpdate == null || btnDelete == null || btnClear == null) {
            return;
        }

        boolean isAnyFieldEmpty = txtName.getText().isEmpty() ||
                txtNumber.getText().isEmpty() ||
                txtAddress.getText().isEmpty();

        boolean isValid = isValidInputs(false);

        SupplierDto selectedItem = tblSupplier.getSelectionModel().getSelectedItem();

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

    private void loadNextId() throws SQLException {
        String id = supplierBO.getNextSupplierId();
        lblId.setText(id);
    }

    private void clearFields() throws SQLException {
        lblId.setText("");
        txtAddress.clear();
        txtName.clear();
        txtNumber.clear();
        resetValidationStyles();

        loadNextId();
        loadTable();
        tblSupplier.getSelectionModel().clearSelection();
        updateButtonStates();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        colNumber.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
    }

    private void loadTable() {
        try {
            List<SupplierDto> supplierDtos = supplierBO.getAllSuppliers();
            tblSupplier.setItems(FXCollections.observableArrayList(supplierDtos));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error Loading Data", "Error loading supplier data into table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isValidInputs(boolean showDialog) {
        boolean isValidName = txtName.getText().matches(namePattern);
        boolean isValidNumber = txtNumber.getText().matches(contactNumberPattern);
        boolean isValidAddress = txtAddress.getText().matches(addressPattern);

        if (showDialog) {
            if (!isValidName) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Name must be alphabetic (2-50 characters) and can contain spaces.");
                return false;
            }
            if (!isValidNumber) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Contact Number must be a valid 10-digit phone number (e.g., 07x-xxxxxxx).");
                return false;
            }
            if (!isValidAddress) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Address must be alphanumeric (5-100 characters) and can contain .,-");
                return false;
            }
        }
        return isValidName && isValidNumber && isValidAddress;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFields();
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String idToDelete = lblId.getText();

        if (tblSupplier.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a supplier record from the table to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Supplier");
        alert.setContentText("Are you sure you want to delete this supplier?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = supplierBO.deleteSupplier(idToDelete);
                if (isDeleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Deletion Successful", "Supplier Deleted Successfully!");
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Failed to delete supplier.");
                }
            } catch (NotFoundException e) {
                showAlert(Alert.AlertType.ERROR, "Deletion Failed", e.getMessage());
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred during deletion: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void btnSaveOnAction(ActionEvent event) {
        if (!isValidInputs(true)) {
            applyValidationStyles();
            return;
        }

        SupplierDto supplierDto = new SupplierDto(
                lblId.getText(),
                txtName.getText(),
                txtNumber.getText(),
                txtAddress.getText()
        );

        try {
            supplierBO.saveSupplier(supplierDto);
            showAlert(Alert.AlertType.INFORMATION, "Save Successful", "Supplier has been saved successfully!");
            clearFields();
        } catch (DuplicateException e) {
            showAlert(Alert.AlertType.ERROR, "Save Failed", e.getMessage());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred during saving: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void btnUpdateOnAction(ActionEvent event) {
        if (!isValidInputs(true)) {
            applyValidationStyles();
            return;
        }

        if (tblSupplier.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a supplier record from the table to update.");
            return;
        }

        SupplierDto supplierDto = new SupplierDto(
                lblId.getText(),
                txtName.getText(),
                txtNumber.getText(),
                txtAddress.getText()
        );

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Update Supplier");
        alert.setContentText("Are you sure you want to update this supplier?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                supplierBO.updateSupplier(supplierDto);
                showAlert(Alert.AlertType.INFORMATION, "Update Successful", "Supplier has been updated successfully!");
                clearFields();
            } catch (NotFoundException e) {
                showAlert(Alert.AlertType.ERROR, "Update Failed", e.getMessage());
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred during update: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void tableOnClick(MouseEvent mouseEvent) {
        SupplierDto supplierDto = tblSupplier.getSelectionModel().getSelectedItem();
        if (supplierDto != null) {
            lblId.setText(supplierDto.getSupplierId());
            txtName.setText(supplierDto.getSupplierName());
            txtNumber.setText(supplierDto.getContactNumber());
            txtAddress.setText(supplierDto.getAddress());
            resetValidationStyles();
            updateButtonStates();
        }
    }

    public void btnGoToSupplierOnAction(ActionEvent actionEvent) {

    }

    private void navigateTo(String path){
        try {
            AnchorPane newPane = FXMLLoader.load(getClass().getResource(path));
            ancSupplier.getChildren().setAll(newPane);
            newPane.prefWidthProperty().bind(ancSupplier.widthProperty());
            newPane.prefHeightProperty().bind(ancSupplier.heightProperty());
        }catch (Exception e){
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the view: " + e.getMessage());
        }
    }

    public void btnGoToPotsInventoryOnAction(ActionEvent actionEvent) {
        navigateTo("/View/PotsInventoryView.fxml");
    }

    public void btnGoToPotsPurchaseOnAction(ActionEvent actionEvent) {
        navigateTo("/View/PotsPurchaseView.fxml");
    }

    public void btnGoToRawMaterialOnAction(ActionEvent actionEvent) {
        navigateTo("/View/RawMaterialPurchaseView.fxml");
    }

    public void txtNameChange(KeyEvent keyEvent) {
        String name = txtName.getText();
        boolean isValid = name.matches(namePattern);
        if (isValid) {
            txtName.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtName.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void txtNumberChange(KeyEvent keyEvent) {
        String number = txtNumber.getText();
        boolean isValid = number.matches(contactNumberPattern);
        if (isValid) {
            txtNumber.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtNumber.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void txtAddressChange(KeyEvent keyEvent) {
        String address = txtAddress.getText();
        boolean isValid = address.matches(addressPattern);
        if (isValid) {
            txtAddress.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtAddress.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    private void applyValidationStyles() {
        txtNameChange(null);
        txtNumberChange(null);
        txtAddressChange(null);
    }

    private void resetValidationStyles() {
        txtName.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtNumber.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtAddress.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
    }
}