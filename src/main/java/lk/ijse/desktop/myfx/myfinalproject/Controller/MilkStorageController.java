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
import lk.ijse.desktop.myfx.myfinalproject.Dto.MilkStorageDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.MilkStorageModel;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOFactory;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOTypes;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.MilkStorageBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MilkStorageController implements Initializable {

    public AnchorPane getAncMilkStorage() {
        return ancMilkStorage;
    }

    @FXML
    private AnchorPane ancMilkStorage;

    @FXML
    private TableColumn<MilkStorageDto, String> colCollectionId;

    @FXML
    private TableColumn<MilkStorageDto, String> colDate;

    @FXML
    private TableColumn<MilkStorageDto, Time> colDuration;

    @FXML
    private TableColumn<MilkStorageDto, String> colStorageId;

    @FXML
    private TableColumn<MilkStorageDto, Double> colTemperature;

    @FXML
    private TableView<MilkStorageDto> tblMilkStorage;

    @FXML
    private ComboBox<String> comMilkStorage;

    @FXML
    private TextField txtDate;

    @FXML
    private TextField txtDuration;

    @FXML
    private Label lblId;

    @FXML
    private TextField txtTemperature;

    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;

    private final String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
    private final String durationPattern = "^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$";
    private final String temperaturePattern = "^-?\\d+(\\.\\d{1,2})?$";

    private final MilkStorageBO milkStorageBO = BOFactory.getInstance().getBO(BOTypes.MILK_STORAGE);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        setupFieldListeners();
        try {
            loadNextId();
            loadMilkStorageCollectionIds();
            loadTable();
            updateButtonStates();
            clearField();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error initializing controller: " + e.getMessage(), e);
        }
    }

    private void setupTableColumns() {
        colStorageId.setCellValueFactory(new PropertyValueFactory<>("storageId"));
        colCollectionId.setCellValueFactory(new PropertyValueFactory<>("collectionId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        colTemperature.setCellValueFactory(new PropertyValueFactory<>("temperature"));
    }

    private void loadMilkStorageCollectionIds() throws SQLException {
        List<String> collectionIds = milkStorageBO.getAllCollectionIds();
        ObservableList<String> observableList = FXCollections.observableArrayList(collectionIds);
        comMilkStorage.setItems(observableList);
    }

    private void loadTable() {
        try {
            List<MilkStorageDto> milkStorageDtos = milkStorageBO.getAllMilkStorages();
            tblMilkStorage.setItems(FXCollections.observableArrayList(milkStorageDtos));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading milk storage data into table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupFieldListeners() {
        txtDate.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtDuration.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtTemperature.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        comMilkStorage.valueProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        tblMilkStorage.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
    }

    private void updateButtonStates() {
        if (btnSave == null || btnUpdate == null || btnDelete == null || btnClear == null) {
            return;
        }

        boolean isAnyFieldEmpty = txtDate.getText().isEmpty() ||
                txtDuration.getText().isEmpty() ||
                txtTemperature.getText().isEmpty() ||
                comMilkStorage.getValue() == null || comMilkStorage.getValue().isEmpty();

        boolean isValid = isValidInputs(false);

        MilkStorageDto selectedItem = tblMilkStorage.getSelectionModel().getSelectedItem();

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
        String id = milkStorageBO.getNextMilkStorageId();
        lblId.setText(id);
    }

    private void clearField() throws SQLException {
        lblId.setText("");
        comMilkStorage.getSelectionModel().clearSelection();
        txtDate.clear();
        txtDuration.clear();
        txtTemperature.clear();
        resetValidationStyles();

        loadNextId();
        loadTable();
        tblMilkStorage.getSelectionModel().clearSelection();
        updateButtonStates();
    }

    private boolean isValidInputs(boolean showDialog) {
        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidDuration = txtDuration.getText().matches(durationPattern);
        boolean isValidTemperature = txtTemperature.getText().matches(temperaturePattern);
        boolean isCollectionIdSelected = comMilkStorage.getValue() != null && !comMilkStorage.getValue().isEmpty();

        if (showDialog) {
            if (!isValidDate) {
                showAlert(Alert.AlertType.WARNING, "Date must be in YYYY-MM-DD format (e.g., 2025-07-31).");
                return false;
            }
            if (!isValidDuration) {
                showAlert(Alert.AlertType.WARNING, "Duration must be in HH:MM:SS format (e.g., 01:30:00).");
                return false;
            }
            if (!isValidTemperature) {
                showAlert(Alert.AlertType.WARNING, "Temperature must be a valid number (e.g., 4.5 or -2.0).");
                return false;
            }
            if (!isCollectionIdSelected) {
                showAlert(Alert.AlertType.WARNING, "Please select a Collection ID.");
                return false;
            }
        }
        return isValidDate && isValidDuration && isValidTemperature && isCollectionIdSelected;
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

    private void navigateTo(String path){
        try {
            ancMilkStorage.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(path));

            anchorPane.prefWidthProperty().bind(ancMilkStorage.widthProperty());
            anchorPane.prefHeightProperty().bind(ancMilkStorage.heightProperty());
            ancMilkStorage.getChildren().add(anchorPane);
        }catch (Exception e){
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage());
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearField();
    }

    @FXML
    public void btnDeleteOnAction(ActionEvent event) {
        String id = lblId.getText();

        if (id == null || id.isEmpty() || lblId.getText().equals("MSI001") || tblMilkStorage.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a milk storage record to delete from the table.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Milk Storage");
        alert.setContentText("Are you sure you want to delete this milk storage record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = milkStorageBO.deleteMilkStorage(id);
                if (isDeleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Milk Storage record deleted successfully!");
                    clearField();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed to delete milk storage record.");
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
    void btnSaveOnAction(ActionEvent event) {
        if (!isValidInputs(true)) {
            applyValidationStyles();
            return;
        }

        try {
            Time duration = Time.valueOf(txtDuration.getText());
            double temperature = Double.parseDouble(txtTemperature.getText());
            MilkStorageDto milkStorageDto = new MilkStorageDto(
                    lblId.getText(),
                    comMilkStorage.getValue(),
                    txtDate.getText(),
                    duration,
                    temperature
            );

            milkStorageBO.saveMilkStorage(milkStorageDto);
            showAlert(Alert.AlertType.INFORMATION, "Milk Storage has been saved successfully!");
            clearField();
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid format for Duration or Temperature: " + e.getMessage());
        } catch (SQLException | DuplicateException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to save milk storage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        if (!isValidInputs(true)) {
            applyValidationStyles();
            return;
        }

        if (tblMilkStorage.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a milk storage record from the table to update.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Update Milk Storage");
        alert.setContentText("Are you sure you want to update this milk storage record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Time duration = Time.valueOf(txtDuration.getText());
                double temperature = Double.parseDouble(txtTemperature.getText());
                MilkStorageDto milkStorageDto = new MilkStorageDto(
                        lblId.getText(),
                        comMilkStorage.getValue(),
                        txtDate.getText(),
                        duration,
                        temperature
                );

                milkStorageBO.updateMilkStorage(milkStorageDto);
                showAlert(Alert.AlertType.INFORMATION, "Milk Storage has been updated successfully!");
                clearField();
            } catch (IllegalArgumentException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid format for Duration or Temperature: " + e.getMessage());
            } catch (SQLException | NotFoundException e) {
                showAlert(Alert.AlertType.ERROR, "An error occurred during update: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void tableOnClick(MouseEvent mouseEvent) {
        MilkStorageDto milkStorageDto = tblMilkStorage.getSelectionModel().getSelectedItem();
        if (milkStorageDto != null) {
            lblId.setText(milkStorageDto.getStorageId());
            comMilkStorage.setValue(milkStorageDto.getCollectionId());
            txtDate.setText(milkStorageDto.getDate());
            txtDuration.setText(milkStorageDto.getDuration().toString());
            txtTemperature.setText(String.valueOf(milkStorageDto.getTemperature()));
            resetValidationStyles();
            updateButtonStates();
        }
    }

    public void btnGoToMilkCollectionOnAction(ActionEvent actionEvent) {
        navigateTo("/View/MilkCollectionView.fxml");
    }

    public void btnGoToMilkStorageOnAction(ActionEvent actionEvent) {
        navigateTo("/View/MilkStorageView.fxml");
    }

    public void btnGoToQualityCheckOnAction(ActionEvent actionEvent) {
        navigateTo("/View/QualityCheckView.fxml");
    }

    public void comMilkStorageOnAction(ActionEvent actionEvent) {
        String selectedMilkStorage = comMilkStorage.getValue();
        if (selectedMilkStorage != null && !selectedMilkStorage.isEmpty()) {
            comMilkStorage.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comMilkStorage.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void txtDurationChange(KeyEvent keyEvent) {
        String duration = txtDuration.getText();
        if (duration.matches(durationPattern)) {
            txtDuration.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtDuration.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void txtTemperatureChange(KeyEvent keyEvent) {
        String temperature = txtTemperature.getText();
        if (temperature.matches(temperaturePattern)) {
            txtTemperature.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtTemperature.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
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
        txtDurationChange(null);
        txtTemperatureChange(null);
        comMilkStorageOnAction(null);
    }

    private void resetValidationStyles() {
        txtDate.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtDuration.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtTemperature.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        comMilkStorage.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
    }
}