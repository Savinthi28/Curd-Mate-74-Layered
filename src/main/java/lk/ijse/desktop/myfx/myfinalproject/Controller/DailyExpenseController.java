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
import lk.ijse.desktop.myfx.myfinalproject.Dto.DailyExpenseDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.DailyExpenseModel;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
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

    private final String amountPattern = "^\\d+(\\.\\d{1,2})?$";
    private final String descriptionPattern = "^[A-Za-z0-9 ,.'\\-]+$";
    private final String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFields();
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String id = lblId.getText();

        if (id == null || id.isEmpty() || id.equals("Auto Generated")) {
            new Alert(Alert.AlertType.WARNING, "Please select a daily expense to delete from the table.").show();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Daily Expense");
        alert.setContentText("Are you sure you want to delete this daily expense record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDelete = new DailyExpenseModel().deleteDailyExpense(new DailyExpenseDto(id, null, null, 0.0, false));
                if (isDelete) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "Daily expense record deleted successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete daily expense record.").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "An error occurred during deletion: " + e.getMessage()).show();
            }
        }
    }

    private void loadNextId () throws SQLException {
        DailyExpenseModel dailyExpenseModel = new DailyExpenseModel();
        String id = dailyExpenseModel.getNextId();
        lblId.setText(id);
    }

    @FXML
    public void btnSaveOnAction(ActionEvent event) {

        boolean isValidAmount = txtAmount.getText().matches(amountPattern);
        boolean isValidDescription = txtDescription.getText().matches(descriptionPattern);
        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isExpenseCategorySelected = comExpense.getValue() != null;

        if (isValidAmount && isValidDescription && isValidDate && isExpenseCategorySelected) {
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

                DailyExpenseModel dailyExpenseModel = new DailyExpenseModel();
                boolean isSave = dailyExpenseModel.SavedDailyExpense(dailyExpenseDto);
                if (isSave) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "Daily expense has been saved successfully").show();
                }else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save daily expense").show();
                }
            }catch (NumberFormatException e){
                new Alert(Alert.AlertType.ERROR, "Invalid number format for Amount.").show();
            } catch (Exception e){
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to add daily expense due to a database error.").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly (Amount: e.g., 1500.00, Date: YYYY-MM-DD).").show();
            applyValidationStyles();
        }
    }

    private void clearFields() throws SQLException {
        lblId.setText("");
        txtDate.setText("");
        txtDescription.setText("");
        txtAmount.setText("");
        comExpense.getSelectionModel().clearSelection();
        resetValidationStyles();

        loadNextId();
        loadTable();
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
            DailyExpenseModel dailyExpenseModel = new DailyExpenseModel();
            ArrayList<DailyExpenseDto> dailyExpenseDtos = dailyExpenseModel.viewAllDailyExpense();
            if (dailyExpenseDtos != null) {
                ObservableList<DailyExpenseDto> observableList = FXCollections.observableArrayList(dailyExpenseDtos);
                tblExpense.setItems(observableList);
            }else {
                tblExpense.setItems(FXCollections.emptyObservableList());
            }
        }catch (Exception e){
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading daily expense data into table.").show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadNextId();
            loadExpenseCategories();
            setupTableColumns();
            clearFields();
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing controller: " + e.getMessage(), e);
        }
    }

    private void loadExpenseCategories() throws SQLException {
        ObservableList<Boolean> expenseCategories = FXCollections.observableArrayList(true, false);
        comExpense.setItems(expenseCategories);
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {

        boolean isValidAmount = txtAmount.getText().matches(amountPattern);
        boolean isValidDescription = txtDescription.getText().matches(descriptionPattern);
        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isExpenseCategorySelected = comExpense.getValue() != null;

        if (isValidAmount && isValidDescription && isValidDate && isExpenseCategorySelected) {
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

                    boolean isUpdated = DailyExpenseModel.updateDailyExpense(dailyExpenseDto);
                    if (isUpdated) {
                        clearFields();
                        new Alert(Alert.AlertType.INFORMATION, "Daily expense updated successfully!").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to update daily expense.").show();
                    }
                } catch (NumberFormatException e) {
                    new Alert(Alert.AlertType.ERROR, "Invalid number format for Amount.").show();
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "An error occurred during update: " + e.getMessage()).show();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly (Amount: e.g., 1500.00, Date: YYYY-MM-DD).").show();
            applyValidationStyles();
        }
    }

    public void tableOnClick(MouseEvent mouseEvent) {
        DailyExpenseDto dailyExpenseDto = tblExpense.getSelectionModel().getSelectedItem();
        if (dailyExpenseDto != null) {
            lblId.setText(dailyExpenseDto.getId());
            txtDate.setText(dailyExpenseDto.getDate());
            txtDescription.setText(dailyExpenseDto.getDescription());
            txtAmount.setText(String.valueOf(dailyExpenseDto.getAmount()));
            comExpense.setValue(dailyExpenseDto.isDailyExpense());
            resetValidationStyles();
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
            new Alert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage(), ButtonType.OK).show();
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
    }

    public void txtDescriptionChange(KeyEvent keyEvent) {
        String description = txtDescription.getText();
        boolean isValid = description.matches(descriptionPattern);
        if (isValid) {
            txtDescription.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtDescription.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    public void txtAmountChange(KeyEvent keyEvent) {
        String amount = txtAmount.getText();
        boolean isValid = amount.matches(amountPattern);
        if (isValid) {
            txtAmount.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtAmount.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
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