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
import lk.ijse.desktop.myfx.myfinalproject.Dto.PotsInventoryDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.PotsInventoryModel;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOFactory;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOTypes;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.PotsInventoryBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PotsInventoryController implements Initializable {

    @FXML
    private AnchorPane ancPotsInventory;

    @FXML
    private TableColumn<PotsInventoryDto, String> colCondition;

    @FXML
    private TableColumn<PotsInventoryDto, String> colId;

    @FXML
    private TableColumn<PotsInventoryDto, Integer> colPotsSize;

    @FXML
    private TableColumn<PotsInventoryDto, Integer> colQuantity;

    @FXML
    private TableView<PotsInventoryDto> tblPotsInventory;

    @FXML
    private TextField txtCondition;

    @FXML
    private Label lblId;

    @FXML
    private ComboBox<Integer> comPotsSize;

    @FXML
    private TextField txtQuantity;

    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;

    private final String quantityPattern = "^\\d+$";
    private final String conditionPattern = "^[a-zA-Z0-9 ]{3,50}$";

    private final PotsInventoryBO potsInventoryBO = BOFactory.getInstance().getBO(BOTypes.POTS_INVENTORY);

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
            showAlert(Alert.AlertType.ERROR, "Initialization Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error initializing controller: " + e.getMessage(), e);
        }
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPotsSize.setCellValueFactory(new PropertyValueFactory<>("potsSize"));
        colCondition.setCellValueFactory(new PropertyValueFactory<>("condition"));
    }

    private void loadPotsSize() throws SQLException {
        List<Integer> potsSizes = potsInventoryBO.getAllPotsSizes();
        ObservableList<Integer> observableList = FXCollections.observableArrayList(potsSizes);
        comPotsSize.setItems(observableList);
    }

    private void loadNextId() throws SQLException {
        String id = potsInventoryBO.getNextPotsInventoryId();
        lblId.setText(id);
    }

    private void loadTable() {
        try {
            List<PotsInventoryDto> potsInventoryDtos = potsInventoryBO.getAllPotsInventory();
            tblPotsInventory.setItems(FXCollections.observableArrayList(potsInventoryDtos));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading pots inventory data into table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupFieldListeners() {
        txtQuantity.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtCondition.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        comPotsSize.valueProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        tblPotsInventory.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
    }

    private void updateButtonStates() {
        if (btnSave == null || btnUpdate == null || btnDelete == null || btnClear == null) {
            return;
        }

        boolean isAnyFieldEmpty = txtQuantity.getText().isEmpty() ||
                txtCondition.getText().isEmpty() ||
                comPotsSize.getValue() == null;

        boolean isValid = isValidInputs(false);

        PotsInventoryDto selectedItem = tblPotsInventory.getSelectionModel().getSelectedItem();

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
        txtQuantity.clear();
        comPotsSize.getSelectionModel().clearSelection();
        txtCondition.clear();
        resetValidationStyles();

        loadNextId();
        loadTable();
        tblPotsInventory.getSelectionModel().clearSelection();
        updateButtonStates();
    }

    private boolean isValidInputs(boolean showDialog) {
        boolean isValidQuantity = txtQuantity.getText().matches(quantityPattern);
        boolean isValidCondition = txtCondition.getText().matches(conditionPattern);
        boolean isPotsSizeSelected = comPotsSize.getValue() != null && comPotsSize.getValue() > 0;

        if (showDialog) {
            if (!isValidQuantity) {
                showAlert(Alert.AlertType.WARNING, "Quantity must be a positive integer.");
                return false;
            }
            if (!isValidCondition) {
                showAlert(Alert.AlertType.WARNING, "Condition must be 3-50 alphanumeric characters.");
                return false;
            }
            if (!isPotsSizeSelected) {
                showAlert(Alert.AlertType.WARNING, "Please select a Pots Size.");
                return false;
            }
        }
        return isValidQuantity && isValidCondition && isPotsSizeSelected;
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType.name().replace("_", " "));
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

        if (id == null || id.isEmpty() || lblId.getText().equals("PI001") || tblPotsInventory.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a pots inventory record to delete from the table.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Pots Inventory");
        alert.setContentText("Are you sure you want to delete this pots inventory record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = potsInventoryBO.deletePotsInventory(id);
                if (isDeleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Pots inventory record deleted successfully!");
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed to delete pots inventory record.");
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
    public void btnSaveOnAction(ActionEvent event) {
        if (!isValidInputs(true)) {
            applyValidationStyles();
            return;
        }

        try {
            int quantity = Integer.parseInt(txtQuantity.getText());
            int potsSize = comPotsSize.getValue();
            PotsInventoryDto potsInventoryDto = new PotsInventoryDto(
                    lblId.getText(),
                    quantity,
                    potsSize,
                    txtCondition.getText()
            );

            potsInventoryBO.savePotsInventory(potsInventoryDto);
            showAlert(Alert.AlertType.INFORMATION, "Pots Inventory has been saved successfully!");
            clearFields();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid number format for Quantity.");
        } catch (SQLException | DuplicateException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to save pots inventory: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void btnUpdateOnAction(ActionEvent event) {
        if (!isValidInputs(true)) {
            applyValidationStyles();
            return;
        }

        if (tblPotsInventory.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a pots inventory record from the table to update.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Update Pots Inventory");
        alert.setContentText("Are you sure you want to update this pots inventory record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int quantity = Integer.parseInt(txtQuantity.getText());
                int potsSize = comPotsSize.getValue();
                PotsInventoryDto potsInventoryDto = new PotsInventoryDto(
                        lblId.getText(),
                        quantity,
                        potsSize,
                        txtCondition.getText()
                );

                potsInventoryBO.updatePotsInventory(potsInventoryDto);
                showAlert(Alert.AlertType.INFORMATION, "Pots inventory has been updated successfully!");
                clearFields();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid number format for Quantity.");
            } catch (SQLException | NotFoundException e) {
                showAlert(Alert.AlertType.ERROR, "An error occurred during update: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void tableOnClick(MouseEvent mouseEvent) {
        PotsInventoryDto potsInventoryDto = tblPotsInventory.getSelectionModel().getSelectedItem();
        if (potsInventoryDto != null) {
            lblId.setText(potsInventoryDto.getId());
            txtQuantity.setText(String.valueOf(potsInventoryDto.getQuantity()));
            comPotsSize.setValue(potsInventoryDto.getPotsSize());
            txtCondition.setText(potsInventoryDto.getCondition());
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
            ancPotsInventory.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(path));

            anchorPane.prefWidthProperty().bind(ancPotsInventory.widthProperty());
            anchorPane.prefHeightProperty().bind(ancPotsInventory.heightProperty());
            ancPotsInventory.getChildren().add(anchorPane);
        }catch (Exception e){
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage());
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

    public void txtConditionChange(KeyEvent keyEvent) {
        String condition = txtCondition.getText();
        boolean isValid = condition.matches(conditionPattern);
        if (isValid) {
            txtCondition.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtCondition.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    private void applyValidationStyles() {
        txtQuantityChange(null);
        txtConditionChange(null);
        comPotsSizeOnAction(null);
    }

    private void resetValidationStyles() {
        txtQuantity.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtCondition.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        comPotsSize.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
    }
}