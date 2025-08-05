package lk.ijse.desktop.myfx.myfinalproject.Controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import lk.ijse.desktop.myfx.myfinalproject.Dto.BuffaloDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOFactory;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOTypes;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.BuffaloBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.InUseException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;


public class BuffaloController implements Initializable {

    @FXML private TableColumn<BuffaloDto, Integer> colAge;
    @FXML private TableColumn<BuffaloDto, String> colGender;
    @FXML private TableColumn<BuffaloDto, String> colHealth;
    @FXML private TableColumn<BuffaloDto, String> colID;
    @FXML private TableColumn<BuffaloDto, Double> colMilk;
    @FXML private TableView<BuffaloDto> tblBuffalo;
    @FXML private TextField txtAge;
    @FXML private Label lblId;
    @FXML private ComboBox<String> comGender;
    @FXML private TextField txtHealth;
    @FXML private TextField txtMilkProduction;


    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;


    private final String milkPattern = "^\\d+(\\.\\d+)?$";
    private final String agePattern = "^\\d+$";
    private final String healthPattern = "^[A-Za-z ]+$";

    private final BuffaloBO buffaloBO = BOFactory.getInstance().getBO(BOTypes.BUFFALO);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        setupFieldListeners();

        try {
            loadNextId();
            loadBuffaloGender();
            loadTable();
            updateButtonStates();
            resetValidationStyles();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize BuffaloController", e);
        }
    }

    private void setupTableColumns() {
        colID.setCellValueFactory(new PropertyValueFactory<>("buffaloID"));
        colMilk.setCellValueFactory(new PropertyValueFactory<>("milkProduction"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        colHealth.setCellValueFactory(new PropertyValueFactory<>("healthStatus"));
    }

    private void loadTable() {
        try {
            List<BuffaloDto> buffaloDtos = buffaloBO.getAllBuffaloes();
            tblBuffalo.setItems(FXCollections.observableArrayList(buffaloDtos));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading buffalo data into table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupFieldListeners() {
        txtMilkProduction.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtAge.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtHealth.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        comGender.valueProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());

        tblBuffalo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
    }

    private void updateButtonStates() {
        if (btnSave == null || btnUpdate == null || btnDelete == null || btnClear == null) {
            return;
        }

        boolean isAnyFieldEmpty = txtMilkProduction.getText().isEmpty() ||
                txtAge.getText().isEmpty() ||
                txtHealth.getText().isEmpty() ||
                comGender.getValue() == null || comGender.getValue().isEmpty();

        boolean isValid = isValidInputs(false);

        BuffaloDto selectedItem = tblBuffalo.getSelectionModel().getSelectedItem();

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
        String nextId = buffaloBO.getNextBuffaloId();
        lblId.setText(nextId);
    }

    private void clearFields() throws SQLException {
        txtAge.clear();
        txtMilkProduction.clear();
        comGender.setValue(null);
        txtHealth.clear();
        resetValidationStyles();

        loadNextId();
        loadTable();
        tblBuffalo.getSelectionModel().clearSelection();
        updateButtonStates();
    }

    private void loadBuffaloGender() throws Exception {
        List<String> genderList = buffaloBO.getAllBuffaloGender();
        ObservableList<String> genderObservableList = FXCollections.observableArrayList(genderList);
        comGender.setItems(genderObservableList);
    }

    private boolean isValidInputs(boolean showDialog) {
        boolean isValidMilk = txtMilkProduction.getText().matches(milkPattern);
        boolean isValidAge = txtAge.getText().matches(agePattern);
        boolean isValidHealth = txtHealth.getText().matches(healthPattern);
        boolean isGenderSelected = comGender.getValue() != null && !comGender.getValue().isEmpty();

        if (showDialog) {
            if (!isValidMilk) {
                showAlert(Alert.AlertType.WARNING, "Milk Production must be a valid number (e.g., 10.50).");
                return false;
            }
            if (!isValidAge) {
                showAlert(Alert.AlertType.WARNING, "Age must be a whole number.");
                return false;
            }
            if (!isValidHealth) {
                showAlert(Alert.AlertType.WARNING, "Health Status should contain only letters and spaces.");
                return false;
            }
            if (!isGenderSelected) {
                showAlert(Alert.AlertType.WARNING, "Please select a Gender.");
                return false;
            }
        }
        return isValidMilk && isValidAge && isValidHealth && isGenderSelected;
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
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFields();
    }

    @FXML
    public void btnDeleteOnAction(ActionEvent event) {
        String id = lblId.getText();

        if (id == null || id.isEmpty() || id.equals("BUF001") || tblBuffalo.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a buffalo from the table to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Buffalo");
        alert.setContentText("Are you sure you want to delete this buffalo?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = buffaloBO.deleteBuffalo(id);
                if (isDeleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Buffalo has been deleted successfully!");
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed to delete buffalo.");
                }
            } catch (NotFoundException | InUseException e) {
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
            double milkProduction = Double.parseDouble(txtMilkProduction.getText());
            int age = Integer.parseInt(txtAge.getText());
            BuffaloDto buffaloDto = new BuffaloDto(
                    lblId.getText(),
                    milkProduction,
                    comGender.getValue(),
                    age,
                    txtHealth.getText()
            );

            buffaloBO.saveBuffalo(buffaloDto);
            showAlert(Alert.AlertType.INFORMATION, "Buffalo Saved Successfully!");
            clearFields();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid number format for Milk Production or Age.");
        } catch (DuplicateException e) {
            showAlert(Alert.AlertType.ERROR, e.getMessage());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "An error occurred while saving buffalo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void btnUpdateOnAction(ActionEvent event) {
        if (!isValidInputs(true)) {
            applyValidationStyles();
            return;
        }

        if (tblBuffalo.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a buffalo from the table to update.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Update Buffalo");
        alert.setContentText("Are you sure you want to update buffalo?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                double milkProduction = Double.parseDouble(txtMilkProduction.getText());
                int age = Integer.parseInt(txtAge.getText());
                BuffaloDto buffaloDto = new BuffaloDto(
                        lblId.getText(),
                        milkProduction,
                        comGender.getValue(),
                        age,
                        txtHealth.getText()
                );

                buffaloBO.updateBuffalo(buffaloDto);
                showAlert(Alert.AlertType.INFORMATION, "Buffalo Updated Successfully!");
                clearFields();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid number format for Milk Production or Age.");
            } catch (NotFoundException e) {
                showAlert(Alert.AlertType.ERROR, e.getMessage());
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "An error occurred during update: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void tableOnClick(MouseEvent mouseEvent) {
        BuffaloDto buffaloDto = tblBuffalo.getSelectionModel().getSelectedItem();
        if (buffaloDto != null) {
            lblId.setText(buffaloDto.getBuffaloID());
            txtMilkProduction.setText(String.valueOf(buffaloDto.getMilkProduction()));
            comGender.setValue(buffaloDto.getGender());
            txtAge.setText(String.valueOf(buffaloDto.getAge()));
            txtHealth.setText(buffaloDto.getHealthStatus());
            resetValidationStyles();
            updateButtonStates();
        }
    }

    public void txtMilkChange(KeyEvent keyEvent) {
        String milk = txtMilkProduction.getText();
        if (milk.matches(milkPattern)) {
            txtMilkProduction.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtMilkProduction.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void txtAgeChange(KeyEvent keyEvent) {
        String age = txtAge.getText();
        if (age.matches(agePattern)) {
            txtAge.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtAge.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void txtHealthChange(KeyEvent keyEvent) {
        String health = txtHealth.getText();
        if (health.matches(healthPattern)) {
            txtHealth.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtHealth.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void comGenderOnAction(ActionEvent actionEvent) {
        String selectedGender = comGender.getValue();
        if (selectedGender != null && !selectedGender.isEmpty()) {
            comGender.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comGender.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    private void applyValidationStyles() {
        txtMilkChange(null);
        txtAgeChange(null);
        txtHealthChange(null);
        comGenderOnAction(null);
    }

    private void resetValidationStyles() {
        txtMilkProduction.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtAge.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtHealth.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        comGender.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
    }
}