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

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadNextId();
            loadUserId();
            setupTableColumns();
            clearFields();
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing controller: " + e.getMessage(), e);
        }
    }

    private void loadUserId() throws SQLException {
        ArrayList<String> userIds = ReportsModel.getAllUserId();
        ObservableList<String> users = FXCollections.observableArrayList(userIds);
        comUserId.setItems(users);
    }

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

    private final String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
    private final String typePattern = "^[a-zA-Z0-9 ]{2,50}$";
    private final String generatedByPattern = "^[a-zA-Z ]{2,100}$";

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFields();
    }

    @FXML
    public void btnDeleteOnAction(ActionEvent event) {
        String idToDelete = lblId.getText();
        if (idToDelete == null || idToDelete.isEmpty() || idToDelete.equals("Auto Generated")) {
            new Alert(Alert.AlertType.WARNING, "Please select a Report from the table or enter an ID to delete.").show();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Report");
        alert.setContentText("Are you sure you want to delete this report?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = ReportsModel.deleteReports(idToDelete);
                if (isDeleted) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "Report Deleted Successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete Report.").show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error deleting Report: " + e.getMessage()).show();
            }
        }
    }

    private void loadNextId() throws SQLException {
        ReportsModel reportsModel = new ReportsModel();
        String id = reportsModel.getNextId();
        lblId.setText(id);
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {

        boolean isUserIdSelected = comUserId.getValue() != null && !comUserId.getValue().isEmpty();
        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidType = txtType.getText().matches(typePattern);
        boolean isValidGeneratedBy = txtGenerateBy.getText().matches(generatedByPattern);

        if (isUserIdSelected && isValidDate && isValidType && isValidGeneratedBy) {
            ReportsDto reportsDto = new ReportsDto(
                    lblId.getText(),
                    txtDate.getText(),
                    comUserId.getValue(),
                    txtType.getText(),
                    txtGenerateBy.getText()
            );

            try {
                ReportsModel reportsModel = new ReportsModel();
                boolean isSaved = reportsModel.saveReport(reportsDto);
                if (isSaved) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "Report has been saved successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Report could not be saved.").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Report could not be saved due to an error: " + e.getMessage()).show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly. (Date: YYYY-MM-DD, Report Type: alphanumeric, Generated By: alphabetic).").show();
            applyValidationStyles();
        }
    }

    private void clearFields() throws SQLException {
        lblId.setText("");
        txtDate.setText("");
        comUserId.getSelectionModel().clearSelection();
        txtType.setText("");
        txtGenerateBy.setText("");
        resetValidationStyles();

        loadNextId();
        loadTable();
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
            ReportsModel reportsModel = new ReportsModel();
            ArrayList<ReportsDto> reportsDtos = reportsModel.viewAllReports();
            if (reportsDtos != null) {
                ObservableList<ReportsDto> List = FXCollections.observableArrayList(reportsDtos);
                tblReports.setItems(List);
            } else {
                tblReports.setItems(FXCollections.emptyObservableList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading report data into table.").show();
        }
    }

    @FXML
    public void btnUpdateOnAction(ActionEvent event) {

        boolean isUserIdSelected = comUserId.getValue() != null && !comUserId.getValue().isEmpty();
        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidType = txtType.getText().matches(typePattern);
        boolean isValidGeneratedBy = txtGenerateBy.getText().matches(generatedByPattern);

        if (isUserIdSelected && isValidDate && isValidType && isValidGeneratedBy) {
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
                    boolean isUpdated = ReportsModel.updateReports(reportsDto);
                    if (isUpdated) {
                        clearFields();
                        new Alert(Alert.AlertType.INFORMATION, "Report has been updated successfully!").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Report could not be updated.").show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Report could not be updated due to an error: " + e.getMessage()).show();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly (Date: YYYY-MM-DD, Report Type: alphanumeric, Generated By: alphabetic).").show();
            applyValidationStyles();
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
        }
    }

    public void comUserIdOnAction(ActionEvent actionEvent) {
        String selectedUserId = comUserId.getValue();
        if (selectedUserId != null && !selectedUserId.isEmpty()) {
            comUserId.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comUserId.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
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

    public void txtTypeChange(KeyEvent keyEvent) {
        String type = txtType.getText();
        boolean isValid = type.matches(typePattern);
        if (isValid) {
            txtType.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtType.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    public void txtGenerateByChange(KeyEvent keyEvent) {
        String generatedBy = txtGenerateBy.getText();
        boolean isValid = generatedBy.matches(generatedByPattern);
        if (isValid) {
            txtGenerateBy.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtGenerateBy.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
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