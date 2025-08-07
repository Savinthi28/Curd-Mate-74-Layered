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
import lk.ijse.desktop.myfx.myfinalproject.Dto.DailyIncomeDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.DailyIncomeModel;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOFactory;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOTypes;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.DailyIncomeBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class DailyIncomeController implements Initializable {
    public AnchorPane getAncDailyIncome(){
        return null;
    }

    @FXML
    private AnchorPane ancDailyIncome;
    private String path;

    @FXML
    private TableColumn<DailyIncomeDto, Double> colAmount;

    @FXML
    private TableColumn<DailyIncomeDto, String> colDate;

    @FXML
    private TableColumn<DailyIncomeDto, String> colDescription;

    @FXML
    private TableColumn<DailyIncomeDto, String> colId;

    @FXML
    private TableColumn<DailyIncomeDto, String> colName;

    @FXML
    private TableView<DailyIncomeDto> tblIncome;

    @FXML
    private TextField txtAmount;

    @FXML
    private TextField txtDate;

    @FXML
    private ComboBox<String> comDescription;

    @FXML
    private Label lblId;

    @FXML
    private TextField txtName;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnUpdate;


    private final String namePattern = "^[A-Za-z ]+$";
    private final String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
    private final String amountPattern = "^\\d+(\\.\\d{1,2})?$";

    private final DailyIncomeBO dailyIncomeBO = BOFactory.getInstance().getBO(BOTypes.DAILY_INCOME);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        setupFieldListeners();
        try {
            loadNextId();
            loadIncomeDescription();
            loadTable();
            updateButtonStates();
            clearFields();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize DailyIncomeController", e);
        }
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
    }

    private void loadTable() {
        try {
            List<DailyIncomeDto> dailyIncomeDtos = dailyIncomeBO.getAllDailyIncomes();
            tblIncome.setItems(FXCollections.observableArrayList(dailyIncomeDtos));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading daily income data into table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupFieldListeners() {
        txtName.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtDate.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtAmount.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        comDescription.valueProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        tblIncome.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
    }

    private void updateButtonStates() {
        if (btnSave == null || btnUpdate == null || btnDelete == null || btnClear == null) {
            return;
        }

        boolean isAnyFieldEmpty = txtName.getText().isEmpty() ||
                txtDate.getText().isEmpty() ||
                txtAmount.getText().isEmpty() ||
                comDescription.getValue() == null || comDescription.getValue().isEmpty();

        boolean isValid = isValidInputs(false);

        DailyIncomeDto selectedItem = tblIncome.getSelectionModel().getSelectedItem();

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
        String id = dailyIncomeBO.getNextDailyIncomeId();
        lblId.setText(id);
    }

    private void clearFields() throws SQLException {
        lblId.setText("");
        txtName.clear();
        txtDate.clear();
        comDescription.getSelectionModel().clearSelection();
        txtAmount.clear();
        resetValidationStyles();

        loadNextId();
        loadTable();
        tblIncome.getSelectionModel().clearSelection();
        updateButtonStates();
    }

    private void loadIncomeDescription() throws SQLException {
        List<String> incomeDescription = dailyIncomeBO.getAllIncomeDescriptions();
        ObservableList<String> data = FXCollections.observableArrayList(incomeDescription);
        comDescription.setItems(data);
    }

    private boolean isValidInputs(boolean showDialog) {
        boolean isValidName = txtName.getText().matches(namePattern);
        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidAmount = txtAmount.getText().matches(amountPattern);
        boolean isDescriptionSelected = comDescription.getValue() != null && !comDescription.getValue().isEmpty();

        if (showDialog) {
            if (!isValidName) {
                showAlert(Alert.AlertType.WARNING, "Customer Name must contain only letters and spaces.");
                return false;
            }
            if (!isValidDate) {
                showAlert(Alert.AlertType.WARNING, "Date must be in YYYY-MM-DD format.");
                return false;
            }
            if (!isValidAmount) {
                showAlert(Alert.AlertType.WARNING, "Amount must be a valid number (e.g., 15000.00).");
                return false;
            }
            if (!isDescriptionSelected) {
                showAlert(Alert.AlertType.WARNING, "Please select a Description.");
                return false;
            }
        }
        return isValidName && isValidDate && isValidAmount && isDescriptionSelected;
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
    void btnDeleteOnAction(ActionEvent event) {
        String id = lblId.getText();

        if (id == null || id.isEmpty() || lblId.getText().equals("DI001") || tblIncome.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a daily income record to delete from the table.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Daily Income");
        alert.setContentText("Are you sure you want to delete this daily income record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = dailyIncomeBO.deleteDailyIncome(id);
                if (isDeleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Daily income record deleted successfully!");
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed to delete daily income record.");
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
            double amount = Double.parseDouble(txtAmount.getText());

            DailyIncomeDto dailyIncomeDto = new DailyIncomeDto(
                    lblId.getText(),
                    txtName.getText(),
                    txtDate.getText(),
                    comDescription.getValue(),
                    amount
            );

            dailyIncomeBO.saveDailyIncome(dailyIncomeDto);
            showAlert(Alert.AlertType.INFORMATION, "Income has been saved successfully!");
            clearFields();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid number format for Amount.");
        } catch (SQLException | DuplicateException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to save income: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        if (!isValidInputs(true)) {
            applyValidationStyles();
            return;
        }

        if (tblIncome.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a daily income record from the table to update.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Update Daily Income");
        alert.setContentText("Are you sure you want to update this daily income record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                double amount = Double.parseDouble(txtAmount.getText());

                DailyIncomeDto dailyIncomeDto = new DailyIncomeDto(
                        lblId.getText(),
                        txtName.getText(),
                        txtDate.getText(),
                        comDescription.getValue(),
                        amount
                );

                dailyIncomeBO.updateDailyIncome(dailyIncomeDto);
                showAlert(Alert.AlertType.INFORMATION, "Updated Successfully!");
                clearFields();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid number format for Amount.");
            } catch (SQLException | NotFoundException e) {
                showAlert(Alert.AlertType.ERROR, "An error occurred during update: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void tableOnClick(MouseEvent mouseEvent) {
        DailyIncomeDto dailyIncomeDto = tblIncome.getSelectionModel().getSelectedItem();
        if (dailyIncomeDto != null) {
            lblId.setText(dailyIncomeDto.getId());
            txtName.setText(dailyIncomeDto.getCustomerName());
            txtDate.setText(dailyIncomeDto.getDate());
            comDescription.setValue(dailyIncomeDto.getDescription());
            txtAmount.setText(String.valueOf(dailyIncomeDto.getAmount()));
            resetValidationStyles();
            updateButtonStates();
        }
    }

    public void btnGoToIncomeOnAction(ActionEvent actionEvent) {
        navigateTo("/View/DailyIncomeView.fxml");
    }

    private void navigateTo(String path){
        try {
            ancDailyIncome.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(path));

            anchorPane.prefWidthProperty().bind(ancDailyIncome.widthProperty());
            anchorPane.prefHeightProperty().bind(ancDailyIncome.heightProperty());
            ancDailyIncome.getChildren().add(anchorPane);
        }catch (Exception e){
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Something went wrong during navigation: " + e.getMessage());
        }
    }

    public void btnGoToExpenseOnAction(ActionEvent actionEvent) {
        navigateTo("/View/DailyExpenseView.fxml");
    }

    public void comDescriptionOnAction(ActionEvent actionEvent) {
        String selectedDescription = comDescription.getValue();
        if (selectedDescription != null && !selectedDescription.isEmpty()) {
            comDescription.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comDescription.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void txtNameChange(KeyEvent keyEvent) {
        String name = txtName.getText();
        if (name.matches(namePattern)) {
            txtName.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtName.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void txtAmountChange(KeyEvent keyEvent) {
        String amount = txtAmount.getText();
        if (amount.matches(amountPattern)) {
            txtAmount.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtAmount.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
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
        txtNameChange(null);
        txtDateChange(null);
        txtAmountChange(null);
        comDescriptionOnAction(null);
    }

    private void resetValidationStyles() {
        txtName.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtDate.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtAmount.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        comDescription.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
    }
}