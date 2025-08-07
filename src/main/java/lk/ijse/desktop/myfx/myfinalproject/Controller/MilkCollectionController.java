package lk.ijse.desktop.myfx.myfinalproject.Controller;

import javafx.application.Platform;
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
import lk.ijse.desktop.myfx.myfinalproject.Dto.MilkCollectionDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.MilkCollectionModel;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOFactory;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOTypes;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.MilkCollectionBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MilkCollectionController implements Initializable {

    public AnchorPane getAncMilkCollection() {
        return ancMilkCollection;
    }

    @FXML
    private AnchorPane ancMilkCollection;

    @FXML
    private TableColumn<MilkCollectionDto, String> colBuffaloId;

    @FXML
    private TableColumn<MilkCollectionDto, String> colDate;

    @FXML
    private TableColumn<MilkCollectionDto, String> colId;

    @FXML
    private TableColumn<MilkCollectionDto, Double> colQuantity;

    @FXML
    private TableView<MilkCollectionDto> tblMilkCollection;

    @FXML
    private ComboBox<String> comMilkCollection;

    @FXML
    private TextField txtDate;

    @FXML
    private Label lblId;

    @FXML
    private TextField txtQuantity;

    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;


    private final String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
    private final String quantityPattern = "^\\d+(\\.\\d{1,2})?$";

    private final MilkCollectionBO milkCollectionBO = BOFactory.getInstance().getBO(BOTypes.MILK_COLLECTION);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        setupFieldListeners();
        try {
            loadNextId();
            loadMilkCollectionBuffaloId();
            loadTable();
            updateButtonStates();
            clearFields();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error initializing controller: " + e.getMessage(), e);
        }
    }

    private void setupTableColumns(){
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colBuffaloId.setCellValueFactory(new PropertyValueFactory<>("buffaloId"));
    }

    private void loadMilkCollectionBuffaloId() throws SQLException {
        List<String> buffaloIds = milkCollectionBO.getAllBuffaloIds();
        ObservableList<String> observableList = FXCollections.observableArrayList(buffaloIds);
        comMilkCollection.setItems(observableList);
    }

    private void loadTable() {
        try {
            List<MilkCollectionDto> milkCollectionDtos = milkCollectionBO.getAllMilkCollections();
            tblMilkCollection.setItems(FXCollections.observableArrayList(milkCollectionDtos));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading milk collection data into table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupFieldListeners() {
        txtDate.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtQuantity.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        comMilkCollection.valueProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        tblMilkCollection.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
    }

    private void updateButtonStates() {
        if (btnSave == null || btnUpdate == null || btnDelete == null || btnClear == null) {
            return;
        }

        boolean isAnyFieldEmpty = txtDate.getText().isEmpty() ||
                txtQuantity.getText().isEmpty() ||
                comMilkCollection.getValue() == null || comMilkCollection.getValue().isEmpty();

        boolean isValid = isValidInputs(false);

        MilkCollectionDto selectedItem = tblMilkCollection.getSelectionModel().getSelectedItem();

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

    private void loadNextId () throws SQLException {
        String nextId = milkCollectionBO.getNextMilkCollectionId();
        lblId.setText(nextId);
    }

    private void clearFields() throws SQLException {
        lblId.setText("");
        txtDate.clear();
        txtQuantity.clear();
        comMilkCollection.getSelectionModel().clearSelection();
        resetValidationStyles();

        loadNextId();
        loadTable();
        tblMilkCollection.getSelectionModel().clearSelection();
        updateButtonStates();
    }

    private boolean isValidInputs(boolean showDialog) {
        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidQuantity = txtQuantity.getText().matches(quantityPattern);
        boolean isBuffaloIdSelected = comMilkCollection.getValue() != null && !comMilkCollection.getValue().isEmpty();

        if (showDialog) {
            if (!isValidDate) {
                showAlert(Alert.AlertType.WARNING, "Date must be in YYYY-MM-DD format (e.g., 2025-07-31).");
                return false;
            }
            if (!isValidQuantity) {
                showAlert(Alert.AlertType.WARNING, "Quantity must be a valid number (e.g., 15.50).");
                return false;
            }
            if (!isBuffaloIdSelected) {
                showAlert(Alert.AlertType.WARNING, "Please select a Buffalo ID.");
                return false;
            }
        }
        return isValidDate && isValidQuantity && isBuffaloIdSelected;
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
    public void btnGoToMilkCollectionOnAction(ActionEvent actionEvent) {
        navigateTo("/View/MilkCollectionView.fxml");
    }

    private void navigateTo(String path){
        try {
            ancMilkCollection.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(path));

            anchorPane.prefWidthProperty().bind(ancMilkCollection.widthProperty());
            anchorPane.prefHeightProperty().bind(ancMilkCollection.heightProperty());
            ancMilkCollection.getChildren().add(anchorPane);
        }catch (Exception e){
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage());
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFields();
    }

    @FXML
    public void btnDeleteOnAction(ActionEvent event) {
        String id = lblId.getText();

        if (id == null || id.isEmpty() || lblId.getText().equals("MC001") || tblMilkCollection.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a milk collection record to delete from the table.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Milk Collection");
        alert.setContentText("Are you sure you want to delete this milk collection record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = milkCollectionBO.deleteMilkCollection(id);
                if (isDeleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Milk Collection record deleted successfully!");
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed to delete milk collection record.");
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
            double quantity = Double.parseDouble(txtQuantity.getText());
            MilkCollectionDto milkCollectionDto = new MilkCollectionDto(
                    lblId.getText(),
                    txtDate.getText(),
                    quantity,
                    comMilkCollection.getValue()
            );

            milkCollectionBO.saveMilkCollection(milkCollectionDto);
            showAlert(Alert.AlertType.INFORMATION, "Milk Collection has been saved successfully!");
            clearFields();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid number format for Quantity.");
        } catch (SQLException | DuplicateException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to save milk collection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void btnUpdateOnAction(ActionEvent event) {
        if (!isValidInputs(true)) {
            applyValidationStyles();
            return;
        }

        if (tblMilkCollection.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a milk collection record from the table to update.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Update Milk Collection");
        alert.setContentText("Are you sure you want to update this milk collection record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                double quantity = Double.parseDouble(txtQuantity.getText());
                MilkCollectionDto milkCollectionDto = new MilkCollectionDto(
                        lblId.getText(),
                        txtDate.getText(),
                        quantity,
                        comMilkCollection.getValue()
                );

                milkCollectionBO.updateMilkCollection(milkCollectionDto);
                showAlert(Alert.AlertType.INFORMATION, "Milk Collection has been updated successfully!");
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
        MilkCollectionDto milkCollectionDto = tblMilkCollection.getSelectionModel().getSelectedItem();
        if (milkCollectionDto != null) {
            lblId.setText(milkCollectionDto.getId());
            txtDate.setText(milkCollectionDto.getDate());
            txtQuantity.setText(String.valueOf(milkCollectionDto.getQuantity()));
            comMilkCollection.setValue(milkCollectionDto.getBuffaloId());
            resetValidationStyles();
            updateButtonStates();
        }
    }

    public void btnGoToMilkStorageOnAction(ActionEvent actionEvent) {
        navigateTo("/View/MilkStorageView.fxml");
    }

    public void btnGoToQualityCheckOnAction(ActionEvent actionEvent) {
        navigateTo("/View/QualityCheckView.fxml");
    }

    public void comMilkCollectionOnAction(ActionEvent actionEvent) {
        String selectedMilkCollection = comMilkCollection.getValue();
        if (selectedMilkCollection != null && !selectedMilkCollection.isEmpty()) {
            comMilkCollection.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comMilkCollection.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void txtQuantityChange(KeyEvent keyEvent) {
        String quantity = txtQuantity.getText();
        if (quantity.matches(quantityPattern)) {
            txtQuantity.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtQuantity.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
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
        txtQuantityChange(null);
        comMilkCollectionOnAction(null);
    }

    private void resetValidationStyles() {
        txtDate.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtQuantity.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        comMilkCollection.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
    }
}