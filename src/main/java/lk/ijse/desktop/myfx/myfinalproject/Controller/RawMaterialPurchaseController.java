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
import lk.ijse.desktop.myfx.myfinalproject.Dto.RawMaterialPurchaseDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.RawMaterialPurchaseModel;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOFactory;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOTypes;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.RawMaterialPurchaseBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class RawMaterialPurchaseController implements Initializable {
    public AnchorPane getAncRawMaterialPurchase() {
        return null;
    }

    @FXML
    private AnchorPane ancRawMaterial;

    @FXML
    private TableColumn<RawMaterialPurchaseDto, String> colDate;

    @FXML
    private TableColumn<RawMaterialPurchaseDto, String> colMaterialName;

    @FXML
    private TableColumn<RawMaterialPurchaseDto, Double> colPrice;

    @FXML
    private TableColumn<RawMaterialPurchaseDto, String> colPurchaseId;

    @FXML
    private TableColumn<RawMaterialPurchaseDto, Integer> colQuantity;

    @FXML
    private TableColumn<RawMaterialPurchaseDto, String> colSupplierId;

    @FXML
    private TableView<RawMaterialPurchaseDto> tblRawMaterialPurchase;

    @FXML
    private TextField txtDate;

    @FXML
    private TextField txtMaterialName;

    @FXML
    private TextField txtPrice;

    @FXML
    private Label lblId;

    @FXML
    private TextField txtQuantity;

    @FXML
    private ComboBox<String> comSupplierId;

    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;

    private final String materialNamePattern = "^[a-zA-Z0-9 ]+$";
    private final String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
    private final String quantityPattern = "^[1-9]\\d*(\\s*(kg|g|ml|L|pcs|units))?$";
    private final String pricePattern = "^\\d+(\\.\\d{1,2})?$";

    private final RawMaterialPurchaseBO rawMaterialPurchaseBO = BOFactory.getInstance().getBO(BOTypes.RAW_MATERIAL_PURCHASE);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        setupFieldListeners();
        try {
            loadNextId();
            loadSupplierIds();
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
        txtMaterialName.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtDate.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtQuantity.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtPrice.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        comSupplierId.valueProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        tblRawMaterialPurchase.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
    }

    private void updateButtonStates() {
        if (btnSave == null || btnUpdate == null || btnDelete == null || btnClear == null) {
            return;
        }

        boolean isAnyFieldEmpty = txtMaterialName.getText().isEmpty() ||
                txtDate.getText().isEmpty() ||
                txtQuantity.getText().isEmpty() ||
                txtPrice.getText().isEmpty() ||
                comSupplierId.getValue() == null || comSupplierId.getValue().isEmpty();

        boolean isValid = isValidInputs(false);

        RawMaterialPurchaseDto selectedItem = tblRawMaterialPurchase.getSelectionModel().getSelectedItem();

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

    private void loadSupplierIds() throws SQLException {
        List<String> supplierIds = rawMaterialPurchaseBO.getAllSupplierIds();
        ObservableList<String> observableList = FXCollections.observableArrayList(supplierIds);
        comSupplierId.setItems(observableList);
    }

    private void loadNextId() throws SQLException {
        String id = rawMaterialPurchaseBO.getNextRawMaterialPurchaseId();
        lblId.setText(id);
    }

    private void clearFields() throws SQLException {
        lblId.setText("");
        comSupplierId.getSelectionModel().clearSelection();
        txtMaterialName.clear();
        txtDate.clear();
        txtQuantity.clear();
        txtPrice.clear();
        resetValidationStyles();

        loadNextId();
        loadTable();
        tblRawMaterialPurchase.getSelectionModel().clearSelection();
        updateButtonStates();
    }

    private void setupTableColumns() {
        colPurchaseId.setCellValueFactory(new PropertyValueFactory<>("purchaseId"));
        colSupplierId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colMaterialName.setCellValueFactory(new PropertyValueFactory<>("materialName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
    }

    private void loadTable() {
        try {
            List<RawMaterialPurchaseDto> rawMaterialPurchaseDtos = rawMaterialPurchaseBO.getAllRawMaterialPurchases();
            tblRawMaterialPurchase.setItems(FXCollections.observableArrayList(rawMaterialPurchaseDtos));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error Loading Data", "Error loading raw material purchase data into table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isValidInputs(boolean showDialog) {
        boolean isSupplierIdSelected = comSupplierId.getValue() != null && !comSupplierId.getValue().isEmpty();
        boolean isValidMaterialName = txtMaterialName.getText().matches(materialNamePattern);
        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidQuantity = txtQuantity.getText().matches(quantityPattern);
        boolean isValidPrice = txtPrice.getText().matches(pricePattern);

        if (showDialog) {
            if (!isSupplierIdSelected) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Please select a Supplier ID.");
                return false;
            }
            if (!isValidMaterialName) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Material Name must be alphanumeric and can contain spaces.");
                return false;
            }
            if (!isValidDate) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Date must be in YYYY-MM-DD format.");
                return false;
            }
            if (!isValidQuantity) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Quantity must be a positive integer with optional units (e.g., 10kg, 500g, 100pcs).");
                return false;
            }
            if (!isValidPrice) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Unit Price must be a valid number (e.g., 2500.00).");
                return false;
            }
        }
        return isSupplierIdSelected && isValidMaterialName && isValidDate && isValidQuantity && isValidPrice;
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
    public void btnDeleteOnAction(ActionEvent event) {
        String id = lblId.getText();

        if (tblRawMaterialPurchase.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a raw material purchase record to delete from the table.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Raw Material Purchase");
        alert.setContentText("Are you sure you want to delete this raw material purchase record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = rawMaterialPurchaseBO.deleteRawMaterialPurchase(id);
                if (isDeleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Deletion Successful", "Raw Material Purchase record deleted successfully!");
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Failed to delete raw material purchase record.");
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
    void btnSaveOnAction(ActionEvent event) {
        if (!isValidInputs(true)) {
            applyValidationStyles();
            return;
        }

        try {
            String quantityText = txtQuantity.getText().replaceAll("[^\\d]", "");
            int quantity = Integer.parseInt(quantityText);
            double price = Double.parseDouble(txtPrice.getText());

            RawMaterialPurchaseDto rawMaterialPurchaseDto = new RawMaterialPurchaseDto(
                    lblId.getText(),
                    comSupplierId.getValue(),
                    txtMaterialName.getText(),
                    txtDate.getText(),
                    quantity,
                    price
            );

            rawMaterialPurchaseBO.saveRawMaterialPurchase(rawMaterialPurchaseDto);
            showAlert(Alert.AlertType.INFORMATION, "Save Successful", "Raw Material Purchase saved successfully!");
            clearFields();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid number format for Quantity or Unit Price.");
        } catch (DuplicateException e) {
            showAlert(Alert.AlertType.ERROR, "Save Failed", e.getMessage());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save raw material purchase due to a database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void btnUpdateOnAction(ActionEvent event) {
        if (!isValidInputs(true)) {
            applyValidationStyles();
            return;
        }

        if (tblRawMaterialPurchase.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a raw material purchase record from the table to update.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Update Raw Material Purchase");
        alert.setContentText("Are you sure you want to update this raw material purchase record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                String quantityText = txtQuantity.getText().replaceAll("[^\\d]", "");
                int quantity = Integer.parseInt(quantityText);
                double price = Double.parseDouble(txtPrice.getText());

                RawMaterialPurchaseDto rawMaterialPurchaseDto = new RawMaterialPurchaseDto(
                        lblId.getText(),
                        comSupplierId.getValue(),
                        txtMaterialName.getText(),
                        txtDate.getText(),
                        quantity,
                        price
                );

                rawMaterialPurchaseBO.updateRawMaterialPurchase(rawMaterialPurchaseDto);
                showAlert(Alert.AlertType.INFORMATION, "Update Successful", "Raw Material Purchase updated successfully!");
                clearFields();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid number format for Quantity or Unit Price.");
            } catch (NotFoundException e) {
                showAlert(Alert.AlertType.ERROR, "Update Failed", e.getMessage());
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred during update: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void tableOnClick(MouseEvent mouseEvent) {
        RawMaterialPurchaseDto rawMaterialPurchaseDto = tblRawMaterialPurchase.getSelectionModel().getSelectedItem();
        if(rawMaterialPurchaseDto != null){
            lblId.setText(rawMaterialPurchaseDto.getPurchaseId());
            comSupplierId.setValue(rawMaterialPurchaseDto.getSupplierId());
            txtMaterialName.setText(rawMaterialPurchaseDto.getMaterialName());
            txtDate.setText(rawMaterialPurchaseDto.getDate());
            txtQuantity.setText(String.valueOf(rawMaterialPurchaseDto.getQuantity()));
            txtPrice.setText(String.valueOf(rawMaterialPurchaseDto.getUnitPrice()));
            resetValidationStyles();
            updateButtonStates();
        }
    }

    @FXML
    public void btnGoToSupplierOnAction(ActionEvent actionEvent) {
        navigateTo("/View/SupplierView.fxml");
    }

    private void navigateTo(String path){
        try {
            ancRawMaterial.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(path));

            anchorPane.prefWidthProperty().bind(ancRawMaterial.widthProperty());
            anchorPane.prefHeightProperty().bind(ancRawMaterial.heightProperty());
            ancRawMaterial.getChildren().add(anchorPane);
        }catch (Exception e){
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Something went wrong: " + e.getMessage());
        }
    }

    @FXML
    public void btnGoToPotsInventoryOnAction(ActionEvent actionEvent) {
        navigateTo("/View/PotsInventoryView.fxml");
    }

    @FXML
    public void btnGoToPotsPurchaseOnAction(ActionEvent actionEvent) {
        navigateTo("/View/PotsPurchaseView.fxml");
    }

    @FXML
    public void btnGoToRawMaterialOnAction(ActionEvent actionEvent) {
        navigateTo("/View/RawMaterialPurchaseView.fxml");
    }

    public void comSupplierIdOnAction(ActionEvent actionEvent) {
        String selectedSupplierId = comSupplierId.getValue();
        if (selectedSupplierId != null && !selectedSupplierId.isEmpty()) {
            comSupplierId.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comSupplierId.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void txtMaterialNameChange(KeyEvent keyEvent) {
        String materialName = txtMaterialName.getText();
        boolean isValid = materialName.matches(materialNamePattern);
        if (isValid) {
            txtMaterialName.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtMaterialName.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void txtDateChange(KeyEvent keyEvent) {
        String date = txtDate.getText();
        boolean isValid = date.matches(datePattern);
        if (isValid) {
            txtDate.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtDate.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void txtQuantityChange(KeyEvent keyEvent) {
        String quantity = txtQuantity.getText();
        boolean isValid = quantity.matches(quantityPattern);
        if (isValid) {
            txtQuantity.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtQuantity.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void txtPriceChange(KeyEvent keyEvent) {
        String price = txtPrice.getText();
        boolean isValid = price.matches(pricePattern);
        if (isValid) {
            txtPrice.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtPrice.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    private void applyValidationStyles() {
        comSupplierIdOnAction(null);
        txtMaterialNameChange(null);
        txtDateChange(null);
        txtQuantityChange(null);
        txtPriceChange(null);
    }

    private void resetValidationStyles() {
        comSupplierId.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtMaterialName.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtDate.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtQuantity.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtPrice.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
    }
}