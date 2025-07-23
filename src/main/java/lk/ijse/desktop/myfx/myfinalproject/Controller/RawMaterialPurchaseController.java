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

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class RawMaterialPurchaseController implements Initializable {
    public AnchorPane getAncRawMaterialPurchase() {
        return null;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadNextId();
            loadSupplierId();
            setupTableColumns();
            clearFields();
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing controller: " + e.getMessage(), e);
        }
    }

    private void loadSupplierId() throws SQLException {
        ArrayList<String> supplierIds = RawMaterialPurchaseModel.getAllSupplierId();
        ObservableList<String> observableList = FXCollections.observableArrayList(supplierIds);
        comSupplierId.setItems(observableList);
    }

    @FXML
    private AnchorPane ancRawMaterial;
    private String path;

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

    private final String materialNamePattern = "^[a-zA-Z0-9 ]+$";
    private final String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
    private final String quantityPattern = "^[1-9]\\d*(\\s*(kg|g|ml|L|pcs|units))?$";
    private final String pricePattern = "^\\d+(\\.\\d{1,2})?$";

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFields();
    }

    @FXML
    public void btnDeleteOnAction(ActionEvent event) {
        String id = lblId.getText();

        if (id == null || id.isEmpty() || id.equals("Auto Generated")) {
            new Alert(Alert.AlertType.WARNING, "Please select a raw material purchase record to delete from the table.").show();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Raw Material Purchase");
        alert.setContentText("Are you sure you want to delete this raw material purchase record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = new RawMaterialPurchaseModel().deleteRawMaterialPurchase(new RawMaterialPurchaseDto(id, null, null, null, 0, 0.0));
                if (isDeleted) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "Raw Material Purchase record deleted successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete raw material purchase record.").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "An error occurred during deletion: " + e.getMessage()).show();
            }
        }
    }

    private void loadNextId() throws SQLException {
        RawMaterialPurchaseModel rawMaterialPurchaseModel = new RawMaterialPurchaseModel();
        String id = rawMaterialPurchaseModel.getNextId();
        lblId.setText(id);
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {

        boolean isSupplierIdSelected = comSupplierId.getValue() != null && !comSupplierId.getValue().isEmpty();
        boolean isValidMaterialName = txtMaterialName.getText().matches(materialNamePattern);
        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidQuantity = txtQuantity.getText().matches(quantityPattern);
        boolean isValidPrice = txtPrice.getText().matches(pricePattern);

        if (isSupplierIdSelected && isValidMaterialName && isValidDate && isValidQuantity && isValidPrice) {
            try {
                String quantityText = txtQuantity.getText().replaceAll("[^\\d.]", "");
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

                RawMaterialPurchaseModel rawMaterialPurchaseModel = new RawMaterialPurchaseModel();
                boolean isSaved = rawMaterialPurchaseModel.saveRawMaterialPurchase(rawMaterialPurchaseDto);
                if(isSaved){
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "Raw Material Purchase saved successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save raw material purchase.").show();
                }
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Invalid number format for Quantity or Unit Price.").show();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to save raw material purchase due to a database error: " + e.getMessage()).show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly (Material Name: alphanumeric, Date: YYYY-MM-DD, Quantity: positive integer with optional unit, Unit Price: e.g., 2500.00).").show();
            applyValidationStyles();
        }
    }

    private void clearFields() throws SQLException {
        lblId.setText("");
        comSupplierId.getSelectionModel().clearSelection();
        txtMaterialName.setText("");
        txtDate.setText("");
        txtQuantity.setText("");
        txtPrice.setText("");
        resetValidationStyles();

        loadNextId();
        loadTable();
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
            RawMaterialPurchaseModel rawMaterialPurchaseModel = new RawMaterialPurchaseModel();
            ArrayList<RawMaterialPurchaseDto> rawMaterialPurchaseDtos = rawMaterialPurchaseModel.viewAllRawMaterialPurchase();
            if(rawMaterialPurchaseDtos != null){
                ObservableList<RawMaterialPurchaseDto> list = FXCollections.observableArrayList(rawMaterialPurchaseDtos);
                tblRawMaterialPurchase.setItems(list);
            } else {
                tblRawMaterialPurchase.setItems(FXCollections.emptyObservableList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading raw material purchase data into table.").show();
        }
    }

    @FXML
    public void btnUpdateOnAction(ActionEvent event) {

        boolean isSupplierIdSelected = comSupplierId.getValue() != null && !comSupplierId.getValue().isEmpty();
        boolean isValidMaterialName = txtMaterialName.getText().matches(materialNamePattern);
        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidQuantity = txtQuantity.getText().matches(quantityPattern);
        boolean isValidPrice = txtPrice.getText().matches(pricePattern);

        if (isSupplierIdSelected && isValidMaterialName && isValidDate && isValidQuantity && isValidPrice) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Update Raw Material Purchase");
            alert.setContentText("Are you sure you want to update this raw material purchase record?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    String quantityText = txtQuantity.getText().replaceAll("[^\\d.]", "");
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

                    boolean isUpdated = RawMaterialPurchaseModel.updateRawMaterialPurchase(rawMaterialPurchaseDto);
                    if (isUpdated) {
                        clearFields();
                        new Alert(Alert.AlertType.INFORMATION, "Raw Material Purchase updated successfully!").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to update raw material purchase.").show();
                    }
                } catch (NumberFormatException e) {
                    new Alert(Alert.AlertType.ERROR, "Invalid number format for Quantity or Unit Price.").show();
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "An error occurred during update: " + e.getMessage()).show();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly (Material Name: alphanumeric, Date: YYYY-MM-DD, Quantity: positive integer with optional unit, Unit Price: e.g., 2500.00).").show();
            applyValidationStyles();
        }
    }

    public void tableOnClick(MouseEvent mouseEvent) {
        RawMaterialPurchaseDto rawMaterialPurchaseDto = tblRawMaterialPurchase.getSelectionModel().getSelectedItem();
        if(rawMaterialPurchaseDto != null){
            lblId.setText(rawMaterialPurchaseDto.getPurchaseId());
            comSupplierId.setValue(rawMaterialPurchaseDto.getSupplierId());
            txtMaterialName.setText(rawMaterialPurchaseDto.getMaterialName());
            txtDate.setText(String.valueOf(rawMaterialPurchaseDto.getDate()));
            txtQuantity.setText(String.valueOf(rawMaterialPurchaseDto.getQuantity()));
            txtPrice.setText(String.valueOf(rawMaterialPurchaseDto.getUnitPrice()));
            resetValidationStyles();
        }
    }

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

    public void comSupplierIdOnAction(ActionEvent actionEvent) {
        String selectedSupplierId = comSupplierId.getValue();
        if (selectedSupplierId != null && !selectedSupplierId.isEmpty()) {
            comSupplierId.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comSupplierId.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    public void txtMaterialNameChange(KeyEvent keyEvent) {
        String materialName = txtMaterialName.getText();
        boolean isValid = materialName.matches(materialNamePattern);
        if (isValid) {
            txtMaterialName.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtMaterialName.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
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

    public void txtQuantityChange(KeyEvent keyEvent) {
        String quantity = txtQuantity.getText();
        boolean isValid = quantity.matches(quantityPattern);
        if (isValid) {
            txtQuantity.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtQuantity.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    public void txtPriceChange(KeyEvent keyEvent) {
        String price = txtPrice.getText();
        boolean isValid = price.matches(pricePattern);
        if (isValid) {
            txtPrice.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtPrice.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
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