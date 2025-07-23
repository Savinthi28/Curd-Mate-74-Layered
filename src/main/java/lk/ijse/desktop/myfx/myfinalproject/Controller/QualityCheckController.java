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

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;

public class QualityCheckController implements Initializable {

    public AnchorPane getAncQualityCheck(){
        return null;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadNextId();
            loadQualityCollectionId();
            setupTableColumns();
            clearFields();
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing controller: " + e.getMessage(), e);
        }
    }

    private void loadQualityCollectionId() throws SQLException {
        ArrayList<String> collectionId = QualityCheckModel.getAllQualityCollectionId();
        ObservableList<String> observableList = FXCollections.observableArrayList(collectionId);
        comCollectionId.setItems(observableList);
    }


    @FXML
    private AnchorPane ancQualityCheck;
    private String path;

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

    private final String doublePattern = "^\\d+(\\.\\d{1,2})?$";
    private final String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
    private final String notesPattern = "^[a-zA-Z0-9.,\\- ]{0,255}$";

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFields();
    }

    @FXML
    public void btnDeleteOnAction(ActionEvent event) {
        String id = lblId.getText();

        if (id == null || id.isEmpty() || id.equals("Auto Generated")) {
            new Alert(Alert.AlertType.WARNING, "Please select a quality check record to delete from the table.").show();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Quality Check");
        alert.setContentText("Are you sure you want to delete this quality check record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = new QualityCheckModel().deleteQualityCheck(new QualityCheckDto(id, null, null, 0.0, 0.0, null, null));
                if (isDeleted) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "Quality Check record deleted successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete quality check record.").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "An error occurred during deletion: " + e.getMessage()).show();
            }
        }
    }

    private void loadNextId() throws SQLException {
        QualityCheckModel qualityCheckModel = new QualityCheckModel();
        String id = qualityCheckModel.getNextId();
        lblId.setText(id);
    }

    @FXML
    public void btnSaveOnAction(ActionEvent event) {
        String autoAppearance = txtAppearance.getText().isEmpty() ? (new Random().nextBoolean() ? "Normal" : "Clotted") : txtAppearance.getText();
        txtAppearance.setText(autoAppearance);

        double fatContent;
        try {
            fatContent = txtFatContent.getText().isEmpty() ? 3.5 + (new Random().nextDouble() * 0.5 - 0.25) : Double.parseDouble(txtFatContent.getText());
            fatContent = Math.round(fatContent * 100.0) / 100.0;
        } catch (NumberFormatException e) {
            fatContent = 3.5 + (new Random().nextDouble() * 0.5 - 0.25);
            fatContent = Math.round(fatContent * 100.0) / 100.0;
            new Alert(Alert.AlertType.WARNING, "Invalid Fat Content input. Auto-generating value.").show();
        }
        txtFatContent.setText(String.valueOf(fatContent));

        double temperature;
        try {
            temperature = txtTemperature.getText().isEmpty() ? 4.0 + (new Random().nextDouble() * 2.0 - 1.0) : Double.parseDouble(txtTemperature.getText());
            temperature = Math.round(temperature * 10.0) / 10.0;
        } catch (NumberFormatException e) {
            temperature = 4.0 + (new Random().nextDouble() * 2.0 - 1.0);
            temperature = Math.round(temperature * 10.0) / 10.0;
            new Alert(Alert.AlertType.WARNING, "Invalid Temperature input. Auto-generating value.").show();
        }
        txtTemperature.setText(String.valueOf(temperature));

        String date = txtDate.getText().isEmpty() ? LocalDate.now().toString() : txtDate.getText();
        txtDate.setText(date);

        String notes = txtNotes.getText().isEmpty() ? "Auto-generated quality check." : txtNotes.getText();
        txtNotes.setText(notes);

        boolean isCollectionIdSelected = comCollectionId.getValue() != null && !comCollectionId.getValue().isEmpty();
        boolean isValidFatContent = txtFatContent.getText().matches(doublePattern);
        boolean isValidTemperature = txtTemperature.getText().matches(doublePattern);
        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidNotes = txtNotes.getText().matches(notesPattern);


        if (isCollectionIdSelected && isValidFatContent && isValidTemperature && isValidDate && isValidNotes) {
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

                QualityCheckModel qualityCheckModel = new QualityCheckModel();
                boolean isSaved = qualityCheckModel.saveQualityCheck(qualityCheckDto);
                if (isSaved) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "Quality Check saved successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save quality check.").show();
                }
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Invalid number format in Fat Content or Temperature fields.").show();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to save quality check due to a database error: " + e.getMessage()).show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly. (Fat Content/Temperature: numbers, Date: YYYY-MM-DD, Notes: alphanumeric with common punctuation).").show();
            applyValidationStyles();
        }
    }

    private void clearFields() throws SQLException {
        lblId.setText("");
        comCollectionId.getSelectionModel().clearSelection();
        txtAppearance.setText("");
        txtFatContent.setText("");
        txtTemperature.setText("");
        txtDate.setText("");
        txtNotes.setText("");
        resetValidationStyles();

        loadNextId();
        loadTable();
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
            QualityCheckModel qualityCheckModel = new QualityCheckModel();
            ArrayList<QualityCheckDto> qualityCheckDtos = qualityCheckModel.viewAllQualityCheck();
            if (qualityCheckDtos != null) {
                ObservableList<QualityCheckDto> observableList = FXCollections.observableArrayList(qualityCheckDtos);
                tblQualityCheck.setItems(observableList);
            } else {
                tblQualityCheck.setItems(FXCollections.emptyObservableList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading quality check data into table.").show();
        }
    }

    @FXML
    public void btnUpdateOnAction(ActionEvent event) {

        boolean isCollectionIdSelected = comCollectionId.getValue() != null && !comCollectionId.getValue().isEmpty();
        boolean isValidFatContent = txtFatContent.getText().matches(doublePattern);
        boolean isValidTemperature = txtTemperature.getText().matches(doublePattern);
        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidNotes = txtNotes.getText().matches(notesPattern);

        if (isCollectionIdSelected && isValidFatContent && isValidTemperature && isValidDate && isValidNotes) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Update Quality Check");
            alert.setContentText("Are you sure you want to update this quality check record?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    double fatContent = Double.parseDouble(txtFatContent.getText());
                    double temperature = Double.parseDouble(txtTemperature.getText());
                    QualityCheckDto qualityCheckDto = new QualityCheckDto(
                            lblId.getText(),
                            comCollectionId.getValue(),
                            txtAppearance.getText(),
                            fatContent,
                            temperature,
                            txtDate.getText(),
                            txtNotes.getText()
                    );

                    boolean isUpdated = QualityCheckModel.updateQualityCheck(qualityCheckDto);
                    if (isUpdated) {
                        clearFields();
                        new Alert(Alert.AlertType.INFORMATION, "Quality Check updated successfully!").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to update quality check.").show();
                    }
                } catch (NumberFormatException e) {
                    new Alert(Alert.AlertType.ERROR, "Invalid number format in Fat Content or Temperature fields.").show();
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "An error occurred during update: " + e.getMessage()).show();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly (Fat Content/Temperature: numbers, Date: YYYY-MM-DD, Notes: alphanumeric with common punctuation).").show();
            applyValidationStyles();
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
        }
    }

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
            new Alert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage(), ButtonType.OK).show();
        }
    }
    public void btnGoToMilkStorageOnAction(ActionEvent actionEvent) {
        navigateTo("/View/MilkStorageView.fxml");
    }

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
    }

    public void txtFatContentChange(KeyEvent keyEvent) {
        String fatContent = txtFatContent.getText();
        boolean isValid = fatContent.matches(doublePattern);
        if (isValid) {
            txtFatContent.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtFatContent.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    public void txtTemperatureChange(KeyEvent keyEvent) {
        String temperature = txtTemperature.getText();
        boolean isValid = temperature.matches(doublePattern);
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

    public void txtNotesChange(KeyEvent keyEvent) {
        String notes = txtNotes.getText();
        boolean isValid = notes.matches(notesPattern);
        if (isValid) {
            txtNotes.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtNotes.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
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