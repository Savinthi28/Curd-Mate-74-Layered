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
import lk.ijse.desktop.myfx.myfinalproject.Dto.DailyExpenseDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.DailyExpenseModel;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOFactory;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOTypes;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.DailyExpenseBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class DailyExpenseController implements Initializable {
    public AnchorPane getAncDailyExpense(){
        return null;
    }

    @FXML
    private AnchorPane ancDailyExpense;
    private String path;

    @FXML
    private TableColumn<DailyExpenseDto, Double> colAmount;

    @FXML
    private TableColumn<DailyExpenseDto, String> colDate;

    @FXML
    private TableColumn<DailyExpenseDto, String> colDescription;

    @FXML
    private TableColumn<DailyExpenseDto, Boolean> colExpense;

    @FXML
    private TableColumn<DailyExpenseDto, String> colId;

    @FXML
    private TableView<DailyExpenseDto> tblExpense;

    @FXML
    private TextField txtAmount;

    @FXML
    private TextField txtDate;

    @FXML
    private TextField txtDescription;

    @FXML
    private ComboBox<Boolean> comExpense;

    @FXML
    private Label lblId;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnUpdate;

    private final String amountPattern = "^\\d+(\\.\\d{1,2})?$";
    private final String descriptionPattern = "^[A-Za-z0-9 ,.'\\-]+$";
    private final String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";

    private final DailyExpenseBO dailyExpenseBO = BOFactory.getInstance().getBO(BOTypes.DAILY_EXPENSE);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        setupFieldListeners();
        try {
            loadNextId();
            loadExpenseCategories();
            loadTable();
            updateButtonStates();
            clearFields();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize DailyExpenseController", e);
        }
    }

    private void setupTableColumns(){
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colExpense.setCellValueFactory(new PropertyValueFactory<>("dailyExpense"));
    }

    private void loadTable(){
        try {
            List<DailyExpenseDto> dailyExpenseDtos = dailyExpenseBO.getAllDailyExpenses();
            tblExpense.setItems(FXCollections.observableArrayList(dailyExpenseDtos));
        }catch (SQLException e){
            showAlert(Alert.AlertType.ERROR, "Error loading daily expense data into table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupFieldListeners() {
        txtAmount.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtDescription.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtDate.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        comExpense.valueProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        tblExpense.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
    }

    private void updateButtonStates() {
        if (btnSave == null || btnUpdate == null || btnDelete == null || btnClear == null) {
            return;
        }

        boolean isAnyFieldEmpty = txtAmount.getText().isEmpty() ||
                txtDescription.getText().isEmpty() ||
                txtDate.getText().isEmpty() ||
                comExpense.getValue() == null;

        boolean isValid = isValidInputs(false);

        DailyExpenseDto selectedItem = tblExpense.getSelectionModel().getSelectedItem();

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
        String id = dailyExpenseBO.getNextDailyExpenseId();
        lblId.setText(id);
    }

    private void clearFields() throws SQLException {
        lblId.setText("");
        txtDate.clear();
        txtDescription.clear();
        txtAmount.clear();
        comExpense.getSelectionModel().clearSelection();
        resetValidationStyles();

        loadNextId();
        loadTable();
        tblExpense.getSelectionModel().clearSelection();
        updateButtonStates();
    }

    private void loadExpenseCategories() throws SQLException {
        List<Boolean> expenseCategories = dailyExpenseBO.getAllExpenseCategories();
        ObservableList<Boolean> data = FXCollections.observableArrayList(expenseCategories);
        if (data.isEmpty()) {
            data.addAll(true, false);
        }
        comExpense.setItems(data);
    }

    private boolean isValidInputs(boolean showDialog) {
        boolean isValidAmount = txtAmount.getText().matches(amountPattern);
        boolean isValidDescription = txtDescription.getText().matches(descriptionPattern);
        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isExpenseCategorySelected = comExpense.getValue() != null;

        if (showDialog) {
            if (!isValidAmount) {
                showAlert(Alert.AlertType.WARNING, "Amount must be a valid number (e.g., 1500.00).");
                return false;
            }
            if (!isValidDescription) {
                showAlert(Alert.AlertType.WARNING, "Description contains invalid characters. Use letters, numbers, spaces, commas, periods, apostrophes, and hyphens.");
                return false;
            }
            if (!isValidDate) {
                showAlert(Alert.AlertType.WARNING, "Date must be in YYYY-MM-DD format.");
                return false;
            }
            if (!isExpenseCategorySelected) {
                showAlert(Alert.AlertType.WARNING, "Please select an Expense Category (True/False).");
                return false;
            }
        }
        return isValidAmount && isValidDescription && isValidDate && isExpenseCategorySelected;
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

        if (id == null || id.isEmpty() || lblId.getText().equals("E001") || tblExpense.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a daily expense to delete from the table.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Daily Expense");
        alert.setContentText("Are you sure you want to delete this daily expense record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = dailyExpenseBO.deleteDailyExpense(id);
                if (isDeleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Daily expense record deleted successfully!");
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed to delete daily expense record.");
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
            double amount = Double.parseDouble(txtAmount.getText());
            boolean expense = comExpense.getValue();

            DailyExpenseDto dailyExpenseDto = new DailyExpenseDto(
                    lblId.getText(),
                    txtDate.getText(),
                    txtDescription.getText(),
                    amount,
                    expense
            );

            dailyExpenseBO.saveDailyExpense(dailyExpenseDto);
            showAlert(Alert.AlertType.INFORMATION, "Daily expense has been saved successfully!");
            clearFields();
        } catch (NumberFormatException e){
            showAlert(Alert.AlertType.ERROR, "Invalid number format for Amount.");
        } catch (SQLException | DuplicateException e){
            showAlert(Alert.AlertType.ERROR, "Failed to save daily expense: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        if (!isValidInputs(true)) {
            applyValidationStyles();
            return;
        }

        if (tblExpense.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a daily expense record from the table to update.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Update Daily Expense");
        alert.setContentText("Are you sure you want to update this daily expense record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                double amount = Double.parseDouble(txtAmount.getText());
                boolean expense = comExpense.getValue();

                DailyExpenseDto dailyExpenseDto = new DailyExpenseDto(
                        lblId.getText(),
                        txtDate.getText(),
                        txtDescription.getText(),
                        amount,
                        expense
                );

                dailyExpenseBO.updateDailyExpense(dailyExpenseDto);
                showAlert(Alert.AlertType.INFORMATION, "Daily expense updated successfully!");
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
        DailyExpenseDto dailyExpenseDto = tblExpense.getSelectionModel().getSelectedItem();
        if (dailyExpenseDto != null) {
            lblId.setText(dailyExpenseDto.getId());
            txtDate.setText(dailyExpenseDto.getDate());
            txtDescription.setText(dailyExpenseDto.getDescription());
            txtAmount.setText(String.valueOf(dailyExpenseDto.getAmount()));
            comExpense.setValue(dailyExpenseDto.isDailyExpense());
            resetValidationStyles();
            updateButtonStates();
        }
    }

    public void btnGoToIncomeOnAction(ActionEvent actionEvent) {
        navigateTo("/View/DailyIncomeView.fxml");
    }

    private void navigateTo(String path){
        try {
            ancDailyExpense.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(path));

            anchorPane.prefWidthProperty().bind(ancDailyExpense.widthProperty());
            anchorPane.prefHeightProperty().bind(ancDailyExpense.heightProperty());
            ancDailyExpense.getChildren().add(anchorPane);
        }catch (Exception e){
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage());
        }
    }

    public void btnGoToExpenseOnAction(ActionEvent actionEvent) {
        navigateTo("/View/DailyExpenseView.fxml");
    }

    public void comExpenseOnAction(ActionEvent actionEvent) {
        Boolean selectedExpense = comExpense.getValue();
        if (selectedExpense != null) {
            comExpense.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comExpense.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void txtDescriptionChange(KeyEvent keyEvent) {
        String description = txtDescription.getText();
        if (description.matches(descriptionPattern)) {
            txtDescription.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtDescription.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
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
        txtAmountChange(null);
        txtDescriptionChange(null);
        txtDateChange(null);
        comExpenseOnAction(null);
    }

    private void resetValidationStyles() {
        txtAmount.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtDescription.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtDate.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        comExpense.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
    }
}