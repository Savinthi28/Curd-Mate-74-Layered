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
import lk.ijse.desktop.myfx.myfinalproject.Dto.QualityCheckDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.QualityCheckModel;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOFactory;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOTypes;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.QualityCheckBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class QualityCheckController implements Initializable {

    public AnchorPane getAncQualityCheck(){
        return null;
    }

    @FXML
    private AnchorPane ancQualityCheck;

    @FXML
    private TableColumn<QualityCheckDto, String> colAppearance;

    @FXML
    private TableColumn<QualityCheckDto, String> colCheckId;

    @FXML
    private TableColumn<QualityCheckDto, String> colCollectionId;

    @FXML
    private TableColumn<QualityCheckDto, String> colDate;

    @FXML
    private TableColumn<QualityCheckDto, Double> colFatContent;

    @FXML
    private TableColumn<QualityCheckDto, String> colNotes;

    @FXML
    private TableColumn<QualityCheckDto, Double> colTemperature;

    @FXML
    private TableView<QualityCheckDto> tblQualityCheck;

    @FXML
    private TextField txtAppearance;

    @FXML
    private Label lblId;

    @FXML
    private ComboBox<String> comCollectionId;

    @FXML
    private TextField txtDate;

    @FXML
    private TextField txtFatContent;

    @FXML
    private TextField txtNotes;

    @FXML
    private TextField txtTemperature;

    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;

    private final String doublePattern = "^\\d+(\\.\\d{1,2})?$";
    private final String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
    private final String notesPattern = "^[a-zA-Z0-9.,\\- ]{0,255}$";

    private final QualityCheckBO qualityCheckBO = BOFactory.getInstance().getBO(BOTypes.QUALITY_CHECK);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        setupFieldListeners();
        try {
            loadNextId();
            loadCollectionIds();
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
        txtAppearance.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtFatContent.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtTemperature.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtDate.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtNotes.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        comCollectionId.valueProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        tblQualityCheck.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
    }

    private void updateButtonStates() {
        if (btnSave == null || btnUpdate == null || btnDelete == null || btnClear == null) {
            return;
        }

        boolean isAnyFieldEmpty = txtAppearance.getText().isEmpty() ||
                txtFatContent.getText().isEmpty() ||
                txtTemperature.getText().isEmpty() ||
                txtDate.getText().isEmpty() ||
                txtNotes.getText().isEmpty() ||
                comCollectionId.getValue() == null || comCollectionId.getValue().isEmpty();

        boolean isValid = isValidInputs(false);

        QualityCheckDto selectedItem = tblQualityCheck.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
//            btnSave.setDisable(isAnyFieldEmpty || !isValid);
            btnUpdate.setDisable(true);
            btnDelete.setDisable(true);
        } else {
            btnSave.setDisable(false);
            btnUpdate.setDisable(isAnyFieldEmpty || !isValid);
            btnDelete.setDisable(false);
        }
        btnClear.setDisable(false);
    }

    private void loadCollectionIds() throws SQLException {
        List<String> collectionIds = qualityCheckBO.getAllCollectionIds();
        ObservableList<String> observableList = FXCollections.observableArrayList(collectionIds);
        comCollectionId.setItems(observableList);
    }

    private void loadNextId() throws SQLException {
        String id = qualityCheckBO.getNextQualityCheckId();
        lblId.setText(id);
    }

    private void clearFields() throws SQLException {
        lblId.setText("");
        comCollectionId.getSelectionModel().clearSelection();
        txtAppearance.clear();
        txtFatContent.clear();
        txtTemperature.clear();
        txtDate.clear();
        txtNotes.clear();
        resetValidationStyles();

        loadNextId();
        loadTable();
        tblQualityCheck.getSelectionModel().clearSelection();
        updateButtonStates();
    }

    private void setupTableColumns() {
        colCheckId.setCellValueFactory(new PropertyValueFactory<>("checkId"));
        colCollectionId.setCellValueFactory(new PropertyValueFactory<>("collectionId"));
        colAppearance.setCellValueFactory(new PropertyValueFactory<>("appearance"));
        colFatContent.setCellValueFactory(new PropertyValueFactory<>("fatContent"));
        colTemperature.setCellValueFactory(new PropertyValueFactory<>("temperature"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));
    }

    private void loadTable() {
        try {
            List<QualityCheckDto> qualityCheckDtos = qualityCheckBO.getAllQualityChecks();
            tblQualityCheck.setItems(FXCollections.observableArrayList(qualityCheckDtos));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error Loading Data", "Error loading quality check data into table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isValidInputs(boolean showDialog) {
        boolean isCollectionIdSelected = comCollectionId.getValue() != null && !comCollectionId.getValue().isEmpty();
        boolean isValidFatContent = txtFatContent.getText().matches(doublePattern);
        boolean isValidTemperature = txtTemperature.getText().matches(doublePattern);
        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidNotes = txtNotes.getText().matches(notesPattern);

        if (showDialog) {
            if (!isCollectionIdSelected) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Please select a Collection ID.");
                return false;
            }
            if (!isValidFatContent) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Fat Content must be a valid number (e.g., 3.50).");
                return false;
            }
            if (!isValidTemperature) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Temperature must be a valid number (e.g., 4.0).");
                return false;
            }
            if (!isValidDate) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Date must be in YYYY-MM-DD format.");
                return false;
            }
            if (!isValidNotes) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Notes can contain alphanumeric characters, spaces, and common punctuation (.,-). Max 255 characters.");
                return false;
            }
        }
        return isCollectionIdSelected && isValidFatContent && isValidTemperature && isValidDate && isValidNotes;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void autoGenerateFieldValues() {
        if (txtAppearance.getText().isEmpty()) {
            txtAppearance.setText(new Random().nextBoolean() ? "Normal" : "Clotted");
        }

        if (txtFatContent.getText().isEmpty() || !txtFatContent.getText().matches(doublePattern)) {
            double fatContent = 3.5 + (new Random().nextDouble() * 0.5 - 0.25);
            txtFatContent.setText(String.format("%.2f", fatContent));
        }

        if (txtTemperature.getText().isEmpty() || !txtTemperature.getText().matches(doublePattern)) {
            double temperature = 4.0 + (new Random().nextDouble() * 2.0 - 1.0);
            txtTemperature.setText(String.format("%.1f", temperature));
        }

        if (txtDate.getText().isEmpty() || !txtDate.getText().matches(datePattern)) {
            txtDate.setText(LocalDate.now().toString());
        }

        if (txtNotes.getText().isEmpty()) {
            txtNotes.setText("Auto-generated quality check.");
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFields();
    }

    @FXML
    public void btnDeleteOnAction(ActionEvent event) {
        String id = lblId.getText();

        if (tblQualityCheck.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a quality check record to delete from the table.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Quality Check");
        alert.setContentText("Are you sure you want to delete this quality check record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = qualityCheckBO.deleteQualityCheck(id);
                if (isDeleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Deletion Successful", "Quality Check record deleted successfully!");
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Failed to delete quality check record.");
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
        autoGenerateFieldValues();

        if (!isValidInputs(true)) {
            applyValidationStyles();
            return;
        }

        try {
            QualityCheckDto qualityCheckDto = new QualityCheckDto(
                    lblId.getText(),
                    comCollectionId.getValue(),
                    txtAppearance.getText(),
                    Double.parseDouble(txtFatContent.getText()),
                    Double.parseDouble(txtTemperature.getText()),
                    txtDate.getText(),
                    txtNotes.getText()
            );

            qualityCheckBO.saveQualityCheck(qualityCheckDto);
            showAlert(Alert.AlertType.INFORMATION, "Save Successful", "Quality Check saved successfully!");
            clearFields();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid number format in Fat Content or Temperature fields. Please check the values after auto-generation.");
        } catch (DuplicateException e) {
            showAlert(Alert.AlertType.ERROR, "Save Failed", e.getMessage());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save quality check due to a database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void btnUpdateOnAction(ActionEvent event) {
        if (!isValidInputs(true)) {
            applyValidationStyles();
            return;
        }

        if (tblQualityCheck.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a quality check record from the table to update.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Update Quality Check");
        alert.setContentText("Are you sure you want to update this quality check record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                QualityCheckDto qualityCheckDto = new QualityCheckDto(
                        lblId.getText(),
                        comCollectionId.getValue(),
                        txtAppearance.getText(),
                        Double.parseDouble(txtFatContent.getText()),
                        Double.parseDouble(txtTemperature.getText()),
                        txtDate.getText(),
                        txtNotes.getText()
                );

                qualityCheckBO.updateQualityCheck(qualityCheckDto);
                showAlert(Alert.AlertType.INFORMATION, "Update Successful", "Quality Check updated successfully!");
                clearFields();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid number format in Fat Content or Temperature fields.");
            } catch (NotFoundException e) {
                showAlert(Alert.AlertType.ERROR, "Update Failed", e.getMessage());
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred during update: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void tableOnClick(MouseEvent mouseEvent) {
        QualityCheckDto qualityCheckDto = tblQualityCheck.getSelectionModel().getSelectedItem();
        if (qualityCheckDto != null) {
            lblId.setText(qualityCheckDto.getCheckId());
            comCollectionId.setValue(qualityCheckDto.getCollectionId());
            txtAppearance.setText(qualityCheckDto.getAppearance());
            txtFatContent.setText(String.valueOf(qualityCheckDto.getFatContent()));
            txtTemperature.setText(String.valueOf(qualityCheckDto.getTemperature()));
            txtDate.setText(String.valueOf(qualityCheckDto.getDate()));
            txtNotes.setText(String.valueOf(qualityCheckDto.getNotes()));
            resetValidationStyles();
            updateButtonStates();
        }
    }

    @FXML
    public void btnGoToMilkCollectionOnAction(ActionEvent actionEvent) {
        navigateTo("/View/MilkCollectionView.fxml");
    }

    private void navigateTo(String path){
        try {
            ancQualityCheck.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(path));

            anchorPane.prefWidthProperty().bind(ancQualityCheck.widthProperty());
            anchorPane.prefHeightProperty().bind(ancQualityCheck.heightProperty());
            ancQualityCheck.getChildren().add(anchorPane);
        }catch (Exception e){
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Something went wrong: " + e.getMessage());
        }
    }

    @FXML
    public void btnGoToMilkStorageOnAction(ActionEvent actionEvent) {
        navigateTo("/View/MilkStorageView.fxml");
    }

    @FXML
    public void btnGoToQualityCheckOnAction(ActionEvent actionEvent) {
        navigateTo("/View/QualityCheckView.fxml");
    }

    public void comCollectionIdOnAction(ActionEvent actionEvent) {
        String selectedCollectionId = comCollectionId.getValue();
        if (selectedCollectionId != null && !selectedCollectionId.isEmpty()) {
            comCollectionId.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comCollectionId.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void txtFatContentChange(KeyEvent keyEvent) {
        String fatContent = txtFatContent.getText();
        boolean isValid = fatContent.matches(doublePattern);
        if (isValid) {
            txtFatContent.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtFatContent.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void txtTemperatureChange(KeyEvent keyEvent) {
        String temperature = txtTemperature.getText();
        boolean isValid = temperature.matches(doublePattern);
        if (isValid) {
            txtTemperature.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtTemperature.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
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

    public void txtNotesChange(KeyEvent keyEvent) {
        String notes = txtNotes.getText();
        boolean isValid = notes.matches(notesPattern);
        if (isValid) {
            txtNotes.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtNotes.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    private void applyValidationStyles() {
        comCollectionIdOnAction(null);
        txtFatContentChange(null);
        txtTemperatureChange(null);
        txtDateChange(null);
        txtNotesChange(null);
    }

    private void resetValidationStyles() {
        comCollectionId.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtFatContent.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtTemperature.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtDate.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtNotes.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
    }
}