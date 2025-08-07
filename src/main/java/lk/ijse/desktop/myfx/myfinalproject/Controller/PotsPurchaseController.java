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
import lk.ijse.desktop.myfx.myfinalproject.Dto.PotsPurchaseDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.PotsPurchaseModel;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOFactory;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOTypes;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.PotsPurchaseBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PotsPurchaseController implements Initializable {
    public AnchorPane getAncPotsPurchase(){
        return null;
    }

    @FXML
    private AnchorPane ancPotsPurchase;

    @FXML
    private TableColumn<PotsPurchaseDto, String> colDate;

    @FXML
    private TableColumn<PotsPurchaseDto, String> colId;

    @FXML
    private TableColumn<PotsPurchaseDto, Integer> colPotsSize;

    @FXML
    private TableColumn<PotsPurchaseDto, Double> colPrice;

    @FXML
    private TableColumn<PotsPurchaseDto, Integer> colQuantity;

    @FXML
    private TableView<PotsPurchaseDto> tblPotsPurchase;

    @FXML
    private TextField txtDate;

    @FXML
    private Label lblId;

    @FXML
    private ComboBox<Integer> comPotsSize;

    @FXML
    private TextField txtQuantity;

    @FXML
    private TextField txtUnitPrice;

    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;

    private final String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
    private final String quantityPattern = "^[1-9]\\d*$";
    private final String unitPricePattern = "^\\d+(\\.\\d{1,2})?$";

    private final PotsPurchaseBO potsPurchaseBO = BOFactory.getInstance().getBO(BOTypes.POTS_PURCHASE);


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        setupFieldListeners();
        try {
            loadNextId();
            loadPotsSize();
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
        txtDate.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtQuantity.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtUnitPrice.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        comPotsSize.valueProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        tblPotsPurchase.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
    }

    private void updateButtonStates() {
        if (btnSave == null || btnUpdate == null || btnDelete == null || btnClear == null) {
            return;
        }

        boolean isAnyFieldEmpty = txtDate.getText().isEmpty() ||
                txtQuantity.getText().isEmpty() ||
                txtUnitPrice.getText().isEmpty() ||
                comPotsSize.getValue() == null;

        boolean isValid = isValidInputs(false);

        PotsPurchaseDto selectedItem = tblPotsPurchase.getSelectionModel().getSelectedItem();

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

    private void loadPotsSize() throws SQLException {
        List<Integer> potsSizes = potsPurchaseBO.getAllPotsSizesForPurchase();
        ObservableList<Integer> observableList = FXCollections.observableArrayList(potsSizes);
        comPotsSize.setItems(observableList);
    }

    private void loadNextId () throws SQLException {
        String id = potsPurchaseBO.getNextPotsPurchaseId();
        lblId.setText(id);
    }

    private void clearFields() throws SQLException {
        lblId.setText("");
        comPotsSize.getSelectionModel().clearSelection();
        txtDate.clear();
        txtQuantity.clear();
        txtUnitPrice.clear();
        resetValidationStyles();

        loadNextId();
        loadTable();
        tblPotsPurchase.getSelectionModel().clearSelection();
        updateButtonStates();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("purchaseId"));
        colPotsSize.setCellValueFactory(new PropertyValueFactory<>("potsSize"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    private void loadTable() {
        try {
            List<PotsPurchaseDto> potsPurchaseDtos = potsPurchaseBO.getAllPotsPurchases();
            tblPotsPurchase.setItems(FXCollections.observableArrayList(potsPurchaseDtos));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error Loading Data", "Error loading pots purchase data into table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isValidInputs(boolean showDialog) {
        boolean isPotsSizeSelected = comPotsSize.getValue() != null && comPotsSize.getValue() > 0;
        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidQuantity = txtQuantity.getText().matches(quantityPattern);
        boolean isValidUnitPrice = txtUnitPrice.getText().matches(unitPricePattern);

        if (showDialog) {
            if (!isPotsSizeSelected) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Please select a Pots Size.");
                return false;
            }
            if (!isValidDate) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Date must be in YYYY-MM-DD format.");
                return false;
            }
            if (!isValidQuantity) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Quantity must be a positive integer.");
                return false;
            }
            if (!isValidUnitPrice) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Unit Price must be a valid number (e.g., 12.50).");
                return false;
            }
        }
        return isPotsSizeSelected && isValidDate && isValidQuantity && isValidUnitPrice;
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

        if (tblPotsPurchase.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a pots purchase record to delete from the table.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Pots Purchase");
        alert.setContentText("Are you sure you want to delete this pots purchase record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = potsPurchaseBO.deletePotsPurchase(id);
                if (isDeleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Deletion Successful", "Pots Purchase record deleted successfully!");
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Failed to delete pots purchase record.");
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

        try {
            int potsSize = comPotsSize.getValue();
            int quantity = Integer.parseInt(txtQuantity.getText());
            double unitPrice = Double.parseDouble(txtUnitPrice.getText());

            PotsPurchaseDto potsPurchaseDto = new PotsPurchaseDto(
                    lblId.getText(),
                    potsSize,
                    txtDate.getText(),
                    quantity,
                    unitPrice
            );

            potsPurchaseBO.savePotsPurchase(potsPurchaseDto);
            showAlert(Alert.AlertType.INFORMATION, "Save Successful", "Pots Purchase saved successfully!");
            clearFields();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid number format for Quantity or Unit Price.");
        } catch (DuplicateException e) {
            showAlert(Alert.AlertType.ERROR, "Save Failed", e.getMessage());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save pots purchase due to a database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void btnUpdateOnAction(ActionEvent event) {
        if (!isValidInputs(true)) {
            applyValidationStyles();
            return;
        }

        if (tblPotsPurchase.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a pots purchase record from the table to update.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Update Pots Purchase");
        alert.setContentText("Are you sure you want to update this pots purchase record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int potsSize = comPotsSize.getValue();
                int quantity = Integer.parseInt(txtQuantity.getText());
                double unitPrice = Double.parseDouble(txtUnitPrice.getText());

                PotsPurchaseDto potsPurchaseDto = new PotsPurchaseDto(
                        lblId.getText(),
                        potsSize,
                        txtDate.getText(),
                        quantity,
                        unitPrice
                );

                potsPurchaseBO.updatePotsPurchase(potsPurchaseDto);
                showAlert(Alert.AlertType.INFORMATION, "Update Successful", "Pots Purchase updated successfully!");
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
        PotsPurchaseDto potsPurchaseDto = tblPotsPurchase.getSelectionModel().getSelectedItem();
        if (potsPurchaseDto != null) {
            lblId.setText(potsPurchaseDto.getPurchaseId());
            comPotsSize.setValue(potsPurchaseDto.getPotsSize());
            txtDate.setText(potsPurchaseDto.getDate());
            txtQuantity.setText(String.valueOf(potsPurchaseDto.getQuantity()));
            txtUnitPrice.setText(String.valueOf(potsPurchaseDto.getPrice()));
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
            ancPotsPurchase.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(path));

            anchorPane.prefWidthProperty().bind(ancPotsPurchase.widthProperty());
            anchorPane.prefHeightProperty().bind(ancPotsPurchase.heightProperty());
            ancPotsPurchase.getChildren().add(anchorPane);
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

    public void comPotsSizeOnAction(ActionEvent actionEvent) {
        Integer selectedPotsSize = comPotsSize.getValue();
        if (selectedPotsSize != null && selectedPotsSize > 0) {
            comPotsSize.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comPotsSize.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
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

    public void txtUnitPriceChange(KeyEvent keyEvent) {
        String unitPrice = txtUnitPrice.getText();
        boolean isValid = unitPrice.matches(unitPricePattern);
        if (isValid) {
            txtUnitPrice.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtUnitPrice.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    private void applyValidationStyles() {
        comPotsSizeOnAction(null);
        txtDateChange(null);
        txtQuantityChange(null);
        txtUnitPriceChange(null);
    }

    private void resetValidationStyles() {
        comPotsSize.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtDate.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtQuantity.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtUnitPrice.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
    }
}