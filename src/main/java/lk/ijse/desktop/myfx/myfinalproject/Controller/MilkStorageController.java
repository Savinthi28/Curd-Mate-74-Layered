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
import lk.ijse.desktop.myfx.myfinalproject.Dto.MilkStorageDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.MilkStorageModel;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class MilkStorageController implements Initializable {

    public AnchorPane getAncMilkStorage() {
        return null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadNextId();
            loadMilkStorageCollectionIds();
            setupTableColumns();
            clearField();
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing controller: " + e.getMessage(), e);
        }
    }

    private void loadMilkStorageCollectionIds() throws SQLException {
        ArrayList<String> milkStorageCollectionIds = MilkStorageModel.getAllMilkStorage();
        ObservableList<String> observableList = FXCollections.observableArrayList(milkStorageCollectionIds);
        comMilkStorage.setItems(observableList);
    }

    @FXML
    private AnchorPane ancMilkStorage;
    private String path;
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

    private final String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
    private final String durationPattern = "^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$";
    private final String temperaturePattern = "^-?\\d+(\\.\\d{1,2})?$";

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearField();
    }

    @FXML
    public void btnDeleteOnAction(ActionEvent event) {
        String id = lblId.getText();

        if (id == null || id.isEmpty() || id.equals("Auto Generated")) {
            new Alert(Alert.AlertType.WARNING, "Please select a milk storage record to delete from the table.").show();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Milk Storage");
        alert.setContentText("Are you sure you want to delete this milk storage record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = new MilkStorageModel().deleteMilkStorage(new MilkStorageDto(id, (Integer) null, null, null, 0.0));
                if (isDeleted) {
                    clearField();
                    new Alert(Alert.AlertType.INFORMATION, "Milk Storage record deleted successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete milk storage record.").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "An error occurred during deletion: " + e.getMessage()).show();
            }
        }
    }

    private void loadNextId() throws SQLException {
        MilkStorageModel milkStorageModel = new MilkStorageModel();
        String id = milkStorageModel.getNextId();
        lblId.setText(id);
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidDuration = txtDuration.getText().matches(durationPattern);
        boolean isValidTemperature = txtTemperature.getText().matches(temperaturePattern);
        boolean isCollectionIdSelected = comMilkStorage.getValue() != null && !comMilkStorage.getValue().isEmpty();

        if (isValidDate && isValidDuration && isValidTemperature && isCollectionIdSelected) {
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

                MilkStorageModel milkStorageModel = new MilkStorageModel();
                boolean isSaved = milkStorageModel.saveMilkStorage(milkStorageDto);
                if (isSaved) {
                    clearField();
                    new Alert(Alert.AlertType.INFORMATION, "Milk Storage has been saved successfully").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save milk storage.").show();
                }
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Invalid number format for Temperature or invalid time format for Duration.").show();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to save milk storage due to a database error.").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly (Date: YYYY-MM-DD, Duration: HH:MM:SS, Temperature: e.g., 4.5).").show();
            applyValidationStyles();
        }
    }

    private void clearField() throws SQLException {
        lblId.setText("");
        comMilkStorage.getSelectionModel().clearSelection();
        txtDate.setText("");
        txtDuration.setText("");
        txtTemperature.setText("");
        resetValidationStyles();

        loadNextId();
        loadTable();
    }

    private void setupTableColumns() {
        colStorageId.setCellValueFactory(new PropertyValueFactory<>("storageId"));
        colCollectionId.setCellValueFactory(new PropertyValueFactory<>("collectionId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        colTemperature.setCellValueFactory(new PropertyValueFactory<>("temperature"));
    }

    private void loadTable() {
        try {
            MilkStorageModel milkStorageModel = new MilkStorageModel();
            ArrayList<MilkStorageDto> milkStorageDtos = milkStorageModel.viewAllMilkStorage();
            if (milkStorageDtos != null) {
                ObservableList<MilkStorageDto> list = FXCollections.observableArrayList(milkStorageDtos);
                tblMilkStorage.setItems(list);
            } else {
                tblMilkStorage.setItems(FXCollections.emptyObservableList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading milk storage data into table.").show();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {

        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidDuration = txtDuration.getText().matches(durationPattern);
        boolean isValidTemperature = txtTemperature.getText().matches(temperaturePattern);
        boolean isCollectionIdSelected = comMilkStorage.getValue() != null && !comMilkStorage.getValue().isEmpty();

        if (isValidDate && isValidDuration && isValidTemperature && isCollectionIdSelected) {
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

                    boolean isUpdated = MilkStorageModel.updateMilkStorage(milkStorageDto);
                    if (isUpdated) {
                        clearField();
                        new Alert(Alert.AlertType.INFORMATION, "Milk Storage has been updated successfully").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to update milk storage.").show();
                    }
                } catch (NumberFormatException e) {
                    new Alert(Alert.AlertType.ERROR, "Invalid number format for Temperature or invalid time format for Duration.").show();
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "An error occurred during update: " + e.getMessage()).show();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly (Date: YYYY-MM-DD, Duration: HH:MM:SS, Temperature: e.g., 4.5).").show();
            applyValidationStyles();
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
        }
    }

    public void btnGoToMilkCollectionOnAction(ActionEvent actionEvent) {
        navigateTo("/View/MilkCollectionView.fxml");
    }

    private void navigateTo(String path) {
        try {
            ancMilkStorage.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(path));

            anchorPane.prefWidthProperty().bind(ancMilkStorage.widthProperty());
            anchorPane.prefHeightProperty().bind(ancMilkStorage.heightProperty());
            ancMilkStorage.getChildren().add(anchorPane);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage(), ButtonType.OK).show();
        }
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
    }

    public void txtDurationChange(KeyEvent keyEvent) {
        String duration = txtDuration.getText();
        boolean isValid = duration.matches(durationPattern);
        if (isValid) {
            txtDuration.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtDuration.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    public void txtTemperatureChange(KeyEvent keyEvent) {
        String temperature = txtTemperature.getText();
        boolean isValid = temperature.matches(temperaturePattern);
        if (isValid) {
            txtTemperature.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtTemperature.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
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