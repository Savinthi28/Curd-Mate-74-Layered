package lk.ijse.desktop.myfx.myfinalproject.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import lk.ijse.desktop.myfx.myfinalproject.Dto.ReportsDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.ReportsModel;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOFactory;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOTypes;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.ReportBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ReportController implements Initializable {

    @FXML
    private TableColumn<ReportsDto, String> colDate;

    @FXML
    private TableColumn<ReportsDto, String> colGenerateBy;

    @FXML
    private TableColumn<ReportsDto, String> colReportId;

    @FXML
    private TableColumn<ReportsDto, String> colType;

    @FXML
    private TableColumn<ReportsDto, String> colUserId;

    @FXML
    private TableView<ReportsDto> tblReports;

    @FXML
    private TextField txtDate;

    @FXML
    private TextField txtGenerateBy;

    @FXML
    private Label lblId;

    @FXML
    private TextField txtType;

    @FXML
    private ComboBox<String> comUserId;

    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;

    private final String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
    private final String typePattern = "^[a-zA-Z0-9 ]{2,50}$";
    private final String generatedByPattern = "^[a-zA-Z ]{2,100}$";

    private final ReportBO reportBO = BOFactory.getInstance().getBO(BOTypes.REPORT);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        setupFieldListeners();
        try {
            loadNextId();
            loadUserIds();
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
        txtType.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtGenerateBy.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        comUserId.valueProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        tblReports.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
    }

    private void updateButtonStates() {
        if (btnSave == null || btnUpdate == null || btnDelete == null || btnClear == null) {
            return;
        }

        boolean isAnyFieldEmpty = txtDate.getText().isEmpty() ||
                txtType.getText().isEmpty() ||
                txtGenerateBy.getText().isEmpty() ||
                comUserId.getValue() == null || comUserId.getValue().isEmpty();

        boolean isValid = isValidInputs(false);

        ReportsDto selectedItem = tblReports.getSelectionModel().getSelectedItem();

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

    private void loadUserIds() throws SQLException {
        List<String> userIds = reportBO.getAllUserIds();
        ObservableList<String> users = FXCollections.observableArrayList(userIds);
        comUserId.setItems(users);
    }

    private void loadNextId() throws SQLException {
        String id = reportBO.getNextReportId();
        lblId.setText(id);
    }

    private void clearFields() throws SQLException {
        lblId.setText("");
        txtDate.clear();
        comUserId.getSelectionModel().clearSelection();
        txtType.clear();
        txtGenerateBy.clear();
        resetValidationStyles();

        loadNextId();
        loadTable();
        tblReports.getSelectionModel().clearSelection();
        updateButtonStates();
    }

    private void setupTableColumns() {
        colReportId.setCellValueFactory(new PropertyValueFactory<>("reportId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colType.setCellValueFactory(new PropertyValueFactory<>("reportType"));
        colGenerateBy.setCellValueFactory(new PropertyValueFactory<>("generateBy"));
    }

    private void loadTable() {
        try {
            List<ReportsDto> reportsDtos = reportBO.getAllReports();
            tblReports.setItems(FXCollections.observableArrayList(reportsDtos));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error Loading Data", "Error loading report data into table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isValidInputs(boolean showDialog) {
        boolean isUserIdSelected = comUserId.getValue() != null && !comUserId.getValue().isEmpty();
        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidType = txtType.getText().matches(typePattern);
        boolean isValidGeneratedBy = txtGenerateBy.getText().matches(generatedByPattern);

        if (showDialog) {
            if (!isUserIdSelected) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Please select a User ID.");
                return false;
            }
            if (!isValidDate) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Date must be in YYYY-MM-DD format.");
                return false;
            }
            if (!isValidType) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Report Type must be alphanumeric (2-50 characters).");
                return false;
            }
            if (!isValidGeneratedBy) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Generated By must be alphabetic (2-100 characters) and can contain spaces.");
                return false;
            }
        }
        return isUserIdSelected && isValidDate && isValidType && isValidGeneratedBy;
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

        if (tblReports.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a Report from the table to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Report");
        alert.setContentText("Are you sure you want to delete this report?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = reportBO.deleteReport(idToDelete);
                if (isDeleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Deletion Successful", "Report Deleted Successfully!");
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Failed to delete Report.");
                }
            } catch (NotFoundException e) {
                showAlert(Alert.AlertType.ERROR, "Deletion Failed", e.getMessage());
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Error deleting Report: " + e.getMessage());
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

        ReportsDto reportsDto = new ReportsDto(
                lblId.getText(),
                txtDate.getText(),
                comUserId.getValue(),
                txtType.getText(),
                txtGenerateBy.getText()
        );

        try {
            reportBO.saveReport(reportsDto);
            showAlert(Alert.AlertType.INFORMATION, "Save Successful", "Report has been saved successfully!");
            clearFields();
        } catch (DuplicateException e) {
            showAlert(Alert.AlertType.ERROR, "Save Failed", e.getMessage());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Report could not be saved due to an error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void btnUpdateOnAction(ActionEvent event) {
        if (!isValidInputs(true)) {
            applyValidationStyles();
            return;
        }

        if (tblReports.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a Report from the table to update.");
            return;
        }

        ReportsDto reportsDto = new ReportsDto(
                lblId.getText(),
                txtDate.getText(),
                comUserId.getValue(),
                txtType.getText(),
                txtGenerateBy.getText()
        );

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Update Report");
        alert.setContentText("Are you sure you want to update this report?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reportBO.updateReport(reportsDto);
                showAlert(Alert.AlertType.INFORMATION, "Update Successful", "Report has been updated successfully!");
                clearFields();
            } catch (NotFoundException e) {
                showAlert(Alert.AlertType.ERROR, "Update Failed", e.getMessage());
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Report could not be updated due to an error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void tableOnClick(MouseEvent mouseEvent) {
        ReportsDto reportsDto = tblReports.getSelectionModel().getSelectedItem();
        if (reportsDto != null) {
            lblId.setText(reportsDto.getReportId());
            txtDate.setText(reportsDto.getDate());
            comUserId.setValue(reportsDto.getUserId());
            txtType.setText(reportsDto.getReportType());
            txtGenerateBy.setText(reportsDto.getGenerateBy());
            resetValidationStyles();
            updateButtonStates();
        }
    }

    public void comUserIdOnAction(ActionEvent actionEvent) {
        String selectedUserId = comUserId.getValue();
        if (selectedUserId != null && !selectedUserId.isEmpty()) {
            comUserId.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comUserId.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
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

    public void txtTypeChange(KeyEvent keyEvent) {
        String type = txtType.getText();
        boolean isValid = type.matches(typePattern);
        if (isValid) {
            txtType.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtType.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void txtGenerateByChange(KeyEvent keyEvent) {
        String generatedBy = txtGenerateBy.getText();
        boolean isValid = generatedBy.matches(generatedByPattern);
        if (isValid) {
            txtGenerateBy.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtGenerateBy.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    private void applyValidationStyles() {
        comUserIdOnAction(null);
        txtDateChange(null);
        txtTypeChange(null);
        txtGenerateByChange(null);
    }

    private void resetValidationStyles() {
        comUserId.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtDate.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtType.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtGenerateBy.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
    }
}