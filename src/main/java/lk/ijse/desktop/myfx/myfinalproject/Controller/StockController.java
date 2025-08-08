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
import lk.ijse.desktop.myfx.myfinalproject.Dto.StockDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.StockModel;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOFactory;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOTypes;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.StockBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class StockController implements Initializable {

    @FXML
    private AnchorPane ancStock;

    @FXML
    private TableColumn<StockDto, String> colDate;

    @FXML
    private TableColumn<StockDto, String> colProductionId;

    @FXML
    private TableColumn<StockDto, Integer> colQuantity;

    @FXML
    private TableColumn<StockDto, String> colStockId;

    @FXML
    private TableColumn<StockDto, String> colStockType;

    @FXML
    private TableView<StockDto> tblStock;

    @FXML
    private TextField txtDate;

    @FXML
    private ComboBox<String> comProductionId;

    @FXML
    private TextField txtQuantity;

    @FXML
    private Label lblId;

    @FXML
    private TextField txtStockType;

    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;

    private final String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
    private final String quantityPattern = "^[1-9]\\d*$";
    private final String stockTypePattern = "^[a-zA-Z ]{2,50}$";

    private final StockBO stockBO = BOFactory.getInstance().getBO(BOTypes.STOCK);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        setupFieldListeners();
        try {
            loadNextId();
            loadProductionIds();
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
        txtStockType.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        comProductionId.valueProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        tblStock.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
    }

    private void updateButtonStates() {
        if (btnSave == null || btnUpdate == null || btnDelete == null || btnClear == null) {
            return;
        }

        boolean isAnyFieldEmpty = txtDate.getText().isEmpty() ||
                txtQuantity.getText().isEmpty() ||
                txtStockType.getText().isEmpty() ||
                comProductionId.getValue() == null || comProductionId.getValue().isEmpty();

        boolean isValid = isValidInputs(false);

        StockDto selectedItem = tblStock.getSelectionModel().getSelectedItem();

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

    private void loadProductionIds() throws SQLException {
        List<String> productionIds = stockBO.getAllProductionIds();
        ObservableList<String> observableList = FXCollections.observableArrayList(productionIds);
        comProductionId.setItems(observableList);
    }

    private void loadNextId() throws SQLException {
        String id = stockBO.getNextStockId();
        lblId.setText(id);
    }

    private void clearFields() throws SQLException {
        lblId.setText("");
        comProductionId.getSelectionModel().clearSelection();
        txtDate.clear();
        txtQuantity.clear();
        txtStockType.clear();
        resetValidationStyles();

        loadNextId();
        loadTable();
        tblStock.getSelectionModel().clearSelection();
        updateButtonStates();
    }

    private void setupTableColumns() {
        colStockId.setCellValueFactory(new PropertyValueFactory<>("stockId"));
        colProductionId.setCellValueFactory(new PropertyValueFactory<>("productionId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colStockType.setCellValueFactory(new PropertyValueFactory<>("stockType"));
    }

    private void loadTable() {
        try {
            List<StockDto> stockDtos = stockBO.getAllStock();
            tblStock.setItems(FXCollections.observableArrayList(stockDtos));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error Loading Data", "Error loading stock data into table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isValidInputs(boolean showDialog) {
        boolean isProductionIdSelected = comProductionId.getValue() != null && !comProductionId.getValue().isEmpty();
        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidQuantity = txtQuantity.getText().matches(quantityPattern);
        boolean isValidStockType = txtStockType.getText().matches(stockTypePattern);

        if (showDialog) {
            if (!isProductionIdSelected) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Please select a Production ID.");
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
            if (!isValidStockType) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Stock Type must be alphabetic (2-50 characters) and can contain spaces.");
                return false;
            }
        }
        return isProductionIdSelected && isValidDate && isValidQuantity && isValidStockType;
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
        String idToDelete = lblId.getText();

        if (tblStock.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a stock record from the table to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Stock");
        alert.setContentText("Are you sure you want to delete this stock record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = stockBO.deleteStock(idToDelete);
                if (isDeleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Deletion Successful", "Stock Deleted Successfully!");
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Failed to delete stock.");
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
            int quantity = Integer.parseInt(txtQuantity.getText());
            StockDto stockDto = new StockDto(
                    lblId.getText(),
                    comProductionId.getValue(),
                    txtDate.getText(),
                    quantity,
                    txtStockType.getText()
            );

            stockBO.saveStock(stockDto);
            showAlert(Alert.AlertType.INFORMATION, "Save Successful", "Stock added successfully!");
            clearFields();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid number format for Quantity.");
        } catch (DuplicateException e) {
            showAlert(Alert.AlertType.ERROR, "Save Failed", e.getMessage());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Something went wrong: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void btnUpdateOnAction(ActionEvent event) {
        if (!isValidInputs(true)) {
            applyValidationStyles();
            return;
        }

        if (tblStock.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a stock record from the table to update.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Update Stock");
        alert.setContentText("Are you sure you want to update this stock record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int quantity = Integer.parseInt(txtQuantity.getText());
                StockDto stockDto = new StockDto(
                        lblId.getText(),
                        comProductionId.getValue(),
                        txtDate.getText(),
                        quantity,
                        txtStockType.getText()
                );

                stockBO.updateStock(stockDto);
                showAlert(Alert.AlertType.INFORMATION, "Update Successful", "Stock updated successfully!");
                clearFields();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid number format for Quantity.");
            } catch (NotFoundException e) {
                showAlert(Alert.AlertType.ERROR, "Update Failed", e.getMessage());
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred during update: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void tableOnClick(MouseEvent mouseEvent) {
        StockDto stockDto = tblStock.getSelectionModel().getSelectedItem();
        if (stockDto != null) {
            lblId.setText(stockDto.getStockId());
            comProductionId.setValue(stockDto.getProductionId());
            txtDate.setText(stockDto.getDate());
            txtQuantity.setText(String.valueOf(stockDto.getQuantity()));
            txtStockType.setText(stockDto.getStockType());
            resetValidationStyles();
            updateButtonStates();
        }
    }

    public void btnGoToCurdProduOnAction(ActionEvent actionEvent) {
        navigateTo("/View/CurdProductionView.fxml");
    }

    private void navigateTo(String path){
        try {
            AnchorPane newPane = FXMLLoader.load(getClass().getResource(path));
            ancStock.getChildren().setAll(newPane);
            newPane.prefWidthProperty().bind(ancStock.widthProperty());
            newPane.prefHeightProperty().bind(ancStock.heightProperty());
        }catch (Exception e){
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the view: " + e.getMessage());
        }
    }

    public void btnGoToStockOnAction(ActionEvent actionEvent) {

    }

    public void comProductionIdOnAction(ActionEvent actionEvent) {
        String selectedProductionId = comProductionId.getValue();
        if (selectedProductionId != null && !selectedProductionId.isEmpty()) {
            comProductionId.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comProductionId.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
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

    public void txtStockTypeChange(KeyEvent keyEvent) {
        String stockType = txtStockType.getText();
        boolean isValid = stockType.matches(stockTypePattern);
        if (isValid) {
            txtStockType.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtStockType.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    private void applyValidationStyles() {
        comProductionIdOnAction(null);
        txtDateChange(null);
        txtQuantityChange(null);
        txtStockTypeChange(null);
    }

    private void resetValidationStyles() {
        comProductionId.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtDate.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtQuantity.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtStockType.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
    }
}