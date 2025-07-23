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
import lk.ijse.desktop.myfx.myfinalproject.Dto.BuffaloDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.BuffaloModel;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;


public class BuffaloController implements Initializable {

    @FXML
    private TableColumn<BuffaloDto, Integer> colAge;

    @FXML
    private TableColumn<BuffaloDto, String> colGender;

    @FXML
    private TableColumn<BuffaloDto, String> colHealth;

    @FXML
    private TableColumn<BuffaloDto, String> colID;

    @FXML
    private TableColumn<BuffaloDto, Double> colMilk;

    @FXML
    private TableView<BuffaloDto> tblBuffalo;

    @FXML
    private TextField txtAge;

    @FXML
    private Label lblId;

    @FXML
    private ComboBox<String> comGender;

    @FXML
    private TextField txtHealth;

    @FXML
    private TextField txtMilkProduction;

    private final String milkPattern = "^\\d+(\\.\\d+)?$";
    private final String agePattern = "^\\d+$";
    private final String healthPattern = "^[A-Za-z ]+$";

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFields();
    }

    @FXML
    public void btnDeleteOnAction(ActionEvent event) {
        String id = lblId.getText();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Buffalo");
        alert.setContentText("Are you sure you want to delete this buffalo?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDelete = new BuffaloModel().deleteBuffalo(new BuffaloDto(id));
                if (isDelete) {
                    clearFields();
                    loadTable();
                    new Alert(Alert.AlertType.INFORMATION, "Buffalo Deleted").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Buffalo Not Deleted").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error deleting buffalo.").show();
            }
        }
    }

    private void loadNextId() throws SQLException {
        BuffaloModel buffaloModel = new BuffaloModel();
        String nextId = buffaloModel.getNextId();
        lblId.setText(nextId);
    }

    @FXML
    public void btnSaveOnAction(ActionEvent event) throws ClassNotFoundException, SQLException {

        boolean isValidMilk = txtMilkProduction.getText().matches(milkPattern);
        boolean isValidAge = txtAge.getText().matches(agePattern);
        boolean isValidHealth = txtHealth.getText().matches(healthPattern);
        boolean isGenderSelected = comGender.getValue() != null && !comGender.getValue().isEmpty();

        if (isValidMilk && isValidAge && isValidHealth && isGenderSelected) {
            try {
                double milkProduction = Double.parseDouble(txtMilkProduction.getText());
                int age = Integer.parseInt(txtAge.getText());
                BuffaloDto buffaloDto = new BuffaloDto(lblId.getText(), milkProduction, comGender.getValue(), age, txtHealth.getText());

                BuffaloModel buffaloModel = new BuffaloModel();
                boolean isSave = buffaloModel.saveBuffalo(buffaloDto);
                if (isSave) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "Buffalo has been saved successfully").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save buffalo").show();
                }
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Invalid number format for Milk Production or Age.").show();

            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to save buffalo due to a database error.").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are valid and gender is selected.").show();
            applyValidationStyles();
        }
    }

    private void clearFields() throws SQLException {
        txtAge.setText("");
        txtMilkProduction.setText("");
        comGender.setValue(null);
        txtHealth.setText("");
        resetValidationStyles();

        loadNextId();
        loadTable();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadNextId();
            loadBuffaloGender();
            clearFields();
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing controller: " + e.getMessage(), e);
        }
        setupTableColumns();
    }

    private void loadBuffaloGender() throws SQLException {
        ArrayList<String> genderList = BuffaloModel.getAllBuffaloGender();
        ObservableList<String> genderObservableList = FXCollections.observableArrayList(genderList);
        comGender.setItems(genderObservableList);
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
            BuffaloModel buffaloModel = new BuffaloModel();
            ArrayList<BuffaloDto> buffaloDtos = buffaloModel.viewAllBuffalo();
            if (buffaloDtos != null) {
                ObservableList<BuffaloDto> data = FXCollections.observableArrayList(buffaloDtos);
                tblBuffalo.setItems(data);
            } else {
                tblBuffalo.setItems(FXCollections.emptyObservableList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading buffalo data into table.").show();
        }
    }

    @FXML
    public void btnUpdateOnAction(ActionEvent event) {

        boolean isValidMilk = txtMilkProduction.getText().matches(milkPattern);
        boolean isValidAge = txtAge.getText().matches(agePattern);
        boolean isValidHealth = txtHealth.getText().matches(healthPattern);
        boolean isGenderSelected = comGender.getValue() != null && !comGender.getValue().isEmpty();


        if (isValidMilk && isValidAge && isValidHealth && isGenderSelected) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Update Buffalo");
            alert.setContentText("Are you sure you want to update buffalo?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    double milkProduction = Double.parseDouble(txtMilkProduction.getText());
                    int age = Integer.parseInt(txtAge.getText());
                    BuffaloDto buffaloDto = new BuffaloDto(lblId.getText(), milkProduction, comGender.getValue(), age, txtHealth.getText());

                    boolean isUpdated = BuffaloModel.updateBuffalo(buffaloDto);
                    if (isUpdated) {
                        clearFields();
                        new Alert(Alert.AlertType.INFORMATION, "Buffalo has been updated successfully").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to update buffalo").show();
                    }
                } catch (NumberFormatException e) {
                    new Alert(Alert.AlertType.ERROR, "Invalid number format for Milk Production or Age.").show();
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Failed to update buffalo due to a database error.").show();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are valid and gender is selected.").show();
            applyValidationStyles();
        }
    }

    public void tableOnClick(MouseEvent mouseEvent) {
        BuffaloDto buffaloDto = tblBuffalo.getSelectionModel().getSelectedItem();
        if (buffaloDto != null) {
            lblId.setText(buffaloDto.getBuffaloID());
            txtMilkProduction.setText(String.valueOf(buffaloDto.getMilkProduction()));
            comGender.setValue(buffaloDto.getGender());
            txtAge.setText(String.valueOf(buffaloDto.getAge()));
            txtHealth.setText(String.valueOf(buffaloDto.getHealthStatus()));
            resetValidationStyles();
        }
    }

    public void txtMilkChange(KeyEvent keyEvent) {
        String milk = txtMilkProduction.getText();
        boolean isValidMilk = milk.matches(milkPattern);
        if (isValidMilk) {
            txtMilkProduction.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtMilkProduction.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    public void txtAgeChange(KeyEvent keyEvent) {
        String age = txtAge.getText();
        boolean isValidAge = age.matches(agePattern);
        if (isValidAge) {
            txtAge.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtAge.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    public void txtHealthChange(KeyEvent keyEvent) {
        String health = txtHealth.getText();
        boolean isValidHealth = health.matches(healthPattern);
        if (isValidHealth) {
            txtHealth.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtHealth.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    public void comGenderOnAction(ActionEvent actionEvent) {
        String selectedGender = comGender.getValue();
        if (selectedGender != null && !selectedGender.isEmpty()) {
            comGender.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comGender.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
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