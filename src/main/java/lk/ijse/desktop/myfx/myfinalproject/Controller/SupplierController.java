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

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
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

    private final String namePattern = "^[a-zA-Z ]{2,50}$";
    private final String contactNumberPattern = "^(?:0|94|\\+94)?(7(0|1|2|4|5|6|7|8)\\d{7}|\\d{9,10})$";
    private final String addressPattern = "^[a-zA-Z0-9.,\\- ]{5,100}$";

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFields();
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String id = lblId.getText();

        if (id == null || id.isEmpty() || id.equals("Auto Generated")) {
            new Alert(Alert.AlertType.WARNING, "Please select a supplier from the table to delete.").show();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Supplier");
        alert.setContentText("Are you sure you want to delete this supplier?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = new SupplierModel().deleteSupplier(new SupplierDto(id, null, null, null));
                if (isDeleted) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "Supplier Deleted Successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete supplier.").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "An error occurred during deletion: " + e.getMessage()).show();
            }
        }
    }

    private void loadNextId() throws SQLException {
        SupplierModel supplierModel = new SupplierModel();
        String id = supplierModel.getNextId();
        lblId.setText(id);
    }

    @FXML
    public void btnSaveOnAction(ActionEvent event) {

        boolean isValidName = txtName.getText().matches(namePattern);
        boolean isValidNumber = txtNumber.getText().matches(contactNumberPattern);
        boolean isValidAddress = txtAddress.getText().matches(addressPattern);

        if (isValidName && isValidNumber && isValidAddress) {
            SupplierDto supplierDto = new SupplierDto(lblId.getText(), txtName.getText(), txtNumber.getText(), txtAddress.getText());

            try {
                SupplierModel supplierModel = new SupplierModel();
                boolean isSaved = supplierModel.saveSupplier(supplierDto);
                if (isSaved) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "Supplier has been saved successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save supplier.").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "An error occurred during saving: " + e.getMessage()).show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly (Name: alphabetic, Contact No: 10 digits starting with 07, Address: alphanumeric).").show();
            applyValidationStyles();
        }
    }

    private void clearFields() throws SQLException {
        lblId.setText("");
        txtAddress.setText("");
        txtName.setText("");
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

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        colNumber.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
    }

    private void loadTable() {
        try {
            SupplierModel supplierModel = new SupplierModel();
            ArrayList<SupplierDto> supplierDtos = supplierModel.viewAllSupplier();
            if (supplierDtos != null) {
                ObservableList<SupplierDto> supplierList = FXCollections.observableArrayList(supplierDtos);
                tblSupplier.setItems(supplierList);
            } else {
                tblSupplier.setItems(FXCollections.emptyObservableList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading supplier data into table.").show();
        }
    }

    @FXML
    public void btnUpdateOnAction(ActionEvent event) {

        boolean isValidName = txtName.getText().matches(namePattern);
        boolean isValidNumber = txtNumber.getText().matches(contactNumberPattern);
        boolean isValidAddress = txtAddress.getText().matches(addressPattern);

        if (isValidName && isValidNumber && isValidAddress) {
            SupplierDto supplierDto = new SupplierDto(lblId.getText(), txtName.getText(), txtNumber.getText(), txtAddress.getText());

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Update Supplier");
            alert.setContentText("Are you sure you want to update this supplier?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    boolean isUpdated = SupplierModel.updateSupplier(supplierDto);
                    if (isUpdated) {
                        clearFields();
                        new Alert(Alert.AlertType.INFORMATION, "Supplier has been updated successfully!").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to update supplier.").show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "An error occurred during update: " + e.getMessage()).show();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly (Name: alphabetic, Contact No: 10 digits starting with 07, Address: alphanumeric).").show();
            applyValidationStyles();
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
        }
    }

    public void btnGoToSupplierOnAction(ActionEvent actionEvent) {
        navigateTo("/View/SupplierView.fxml");
    }

    private void navigateTo(String path){
        try {
            ancSupplier.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(path));

            anchorPane.prefWidthProperty().bind(ancSupplier.widthProperty());
            anchorPane.prefHeightProperty().bind(ancSupplier.heightProperty());
            ancSupplier.getChildren().add(anchorPane);
        }catch (Exception e){
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage(), ButtonType.OK).show();
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
    }

    public void txtNumberChange(KeyEvent keyEvent) {
        String number = txtNumber.getText();
        boolean isValid = number.matches(contactNumberPattern);
        if (isValid) {
            txtNumber.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtNumber.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    public void txtAddressChange(KeyEvent keyEvent) {
        String address = txtAddress.getText();
        boolean isValid = address.matches(addressPattern);
        if (isValid) {
            txtAddress.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtAddress.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
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