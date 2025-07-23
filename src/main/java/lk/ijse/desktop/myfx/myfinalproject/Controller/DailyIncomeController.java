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
import lk.ijse.desktop.myfx.myfinalproject.Dto.DailyIncomeDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.DailyIncomeModel;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
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

    private final String namePattern = "^[A-Za-z ]+$";
    private final String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
    private final String amountPattern = "^\\d+(\\.\\d{1,2})?$";

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFilds();
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String id = lblId.getText();

        if (id == null || id.isEmpty() || id.equals("Auto Generated")) {
            new Alert(Alert.AlertType.WARNING, "Please select a daily income record to delete from the table.").show();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Daily Income");
        alert.setContentText("Are you sure you want to delete this daily income record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDelete = new DailyIncomeModel().deleteDailyIncome(new DailyIncomeDto(id, null, null, null, 0.0));
                if (isDelete) {
                    clearFilds();
                    new Alert(Alert.AlertType.INFORMATION, "Daily income record deleted successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete daily income record.").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "An error occurred during deletion: " + e.getMessage()).show();
            }
        }
    }

    private void loadNextId () throws SQLException {
        DailyIncomeModel dailyIncomeModel = new DailyIncomeModel();
        String id = dailyIncomeModel.getNextId();
        lblId.setText(id);
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        boolean isValidName = txtName.getText().matches(namePattern);
        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidAmount = txtAmount.getText().matches(amountPattern);
        boolean isDescriptionSelected = comDescription.getValue() != null && !comDescription.getValue().isEmpty();

        if (isValidName && isValidDate && isValidAmount && isDescriptionSelected) {
            try {
                double amount = Double.parseDouble(txtAmount.getText());
                DailyIncomeDto dailyIncomeDto = new DailyIncomeDto(
                        lblId.getText(),
                        txtName.getText(),
                        txtDate.getText(),
                        comDescription.getValue(),
                        amount
                );

                DailyIncomeModel dailyIncomeModel = new DailyIncomeModel();
                boolean isSaved = dailyIncomeModel.saveDailyIncome(dailyIncomeDto);
                if (isSaved) {
                    clearFilds();
                    new Alert(Alert.AlertType.INFORMATION, "Income has been saved successfully").show();
                }else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save income").show();
                }
            }catch (NumberFormatException e){
                new Alert(Alert.AlertType.ERROR, "Invalid number format for Amount.").show();
            } catch (Exception e){
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to save income due to a database error.").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly (Name: letters only, Date: YYYY-MM-DD, Amount: e.g., 15000.00).").show();
            applyValidationStyles();
        }
    }

    private void clearFilds() throws SQLException {
        lblId.setText("");
        txtName.setText("");
        txtDate.setText("");
        comDescription.getSelectionModel().clearSelection();
        txtAmount.setText("");
        resetValidationStyles();

        loadNextId();
        loadTable();
    }

    private void loadTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        try {
            DailyIncomeModel dailyIncomeModel = new DailyIncomeModel();
            ArrayList<DailyIncomeDto> dailyIncomeDtos = dailyIncomeModel.viewDailyIncome();
            if (dailyIncomeDtos != null) {
                ObservableList<DailyIncomeDto> data = FXCollections.observableArrayList(dailyIncomeDtos);
                tblIncome.setItems(data);
            }else {
                tblIncome.setItems(FXCollections.emptyObservableList());
            }
        }catch (Exception e){
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading daily income data into table.").show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadNextId();
            loadIncomeDescription();
            setupTableColumns();
            clearFilds();
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing controller: " + e.getMessage(), e);
        }
    }

    private void setupTableColumns(){
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
    }

    private void loadIncomeDescription() throws SQLException {
        ArrayList<String> incomeDescription = DailyIncomeModel.getAllIncomeDescription();
        ObservableList<String> data = FXCollections.observableArrayList(incomeDescription);
        comDescription.setItems(data);
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {

        boolean isValidName = txtName.getText().matches(namePattern);
        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidAmount = txtAmount.getText().matches(amountPattern);
        boolean isDescriptionSelected = comDescription.getValue() != null && !comDescription.getValue().isEmpty();

        if (isValidName && isValidDate && isValidAmount && isDescriptionSelected) {
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

                    boolean isSave = DailyIncomeModel.updateDailyIncome(dailyIncomeDto);
                    if (isSave) {
                        clearFilds();
                        new Alert(Alert.AlertType.INFORMATION, "Updated Successfully").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to update income").show();
                    }
                } catch (NumberFormatException e) {
                    new Alert(Alert.AlertType.ERROR, "Invalid number format for Amount.").show();
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "An error occurred during update: " + e.getMessage()).show();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly (Name: letters only, Date: YYYY-MM-DD, Amount: e.g., 15000.00).").show();
            applyValidationStyles();
        }
    }

    public void tableOnClick(MouseEvent mouseEvent) {
        DailyIncomeDto dailyIncomeDto = tblIncome.getSelectionModel().getSelectedItem();
        if (dailyIncomeDto != null) {
            lblId.setText(dailyIncomeDto.getId());
            txtName.setText(dailyIncomeDto.getCustomerName());
            txtDate.setText(dailyIncomeDto.getDate());
            comDescription.setValue(dailyIncomeDto.getDescription());
            txtAmount.setText(String.valueOf(dailyIncomeDto.getAmount()));
            resetValidationStyles();
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
            new Alert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage(), ButtonType.OK).show();
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
    }

    public void txtNameChange(KeyEvent keyEvent) {
        String name = txtName.getText();
        boolean isValid = name.matches(namePattern);
        if (isValid) {
            txtName.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtName.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
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