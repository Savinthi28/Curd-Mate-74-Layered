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
import lk.ijse.desktop.myfx.myfinalproject.Dto.StockDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.StockModel;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class StockController implements Initializable {
    public AnchorPane getAncStock(){
        return null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadNextId();
            loadProductionId();
            setupTableColumns();
            clearFields();
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing controller: " + e.getMessage(), e);
        }
    }

    private void loadProductionId() throws SQLException {
        ArrayList<String> productionIds = StockModel.getAllProductionId();
        ObservableList<String> observableList = FXCollections.observableArrayList(productionIds);
        comProductionId.setItems(observableList);
    }

    @FXML
    private AnchorPane ancStock;
    private String path;

    @FXML
    private TableColumn<StockDto, String> colDate;

    @FXML
    private TableColumn<StockDto, String> colProductionId;

    @FXML
    private TableColumn<StockDto, Integer> colQuantity;

    @FXML
    private TableColumn<StockDto, String> colStockId;

    @FXML
    private TableColumn<StockDto, String> colStockType;

    @FXML
    private TableView<StockDto> tblStock;

    @FXML
    private TextField txtDate;

    @FXML
    private ComboBox<String> comProductionId;

    @FXML
    private TextField txtQuantity;

    @FXML
    private Label lblId;

    @FXML
    private TextField txtStockType;

    private final String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
    private final String quantityPattern = "^[1-9]\\d*$";
    private final String stockTypePattern = "^[a-zA-Z ]{2,50}$";

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFields();
    }

    @FXML
    public void btnDeleteOnAction(ActionEvent event) {
        String id = lblId.getText();

        if (id == null || id.isEmpty() || id.equals("Auto Generated")) {
            new Alert(Alert.AlertType.WARNING, "Please select a stock record to delete from the table.").show();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Stock");
        alert.setContentText("Are you sure you want to delete this stock record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = new StockModel().deleteStock(new StockDto(id, null, null, 0, null));
                if (isDeleted) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "Stock Deleted Successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete stock.").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "An error occurred during deletion: " + e.getMessage()).show();
            }
        }
    }

    private void loadNextId() throws SQLException {
        StockModel stockModel = new StockModel();
        String id = stockModel.getNextId();
        lblId.setText(id);
    }

    @FXML
    public void btnSaveOnAction(ActionEvent event) {

        boolean isProductionIdSelected = comProductionId.getValue() != null && !comProductionId.getValue().isEmpty();
        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidQuantity = txtQuantity.getText().matches(quantityPattern);
        boolean isValidStockType = txtStockType.getText().matches(stockTypePattern);

        if (isProductionIdSelected && isValidDate && isValidQuantity && isValidStockType) {
            try {
                int quantity = Integer.parseInt(txtQuantity.getText());
                StockDto stockDto = new StockDto(
                        lblId.getText(),
                        comProductionId.getValue(),
                        txtDate.getText(),
                        quantity,
                        txtStockType.getText()
                );

                StockModel stockModel = new StockModel();
                boolean isSaved = stockModel.saveStock(stockDto);
                if (isSaved) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "Stock added successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Something went wrong. Stock could not be added.").show();
                }
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Invalid number format for Quantity.").show();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage()).show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly (Date: YYYY-MM-DD, Quantity: positive integer, Stock Type: alphabetic).").show();
            applyValidationStyles();
        }
    }

    private void clearFields() throws SQLException {
        lblId.setText("");
        comProductionId.getSelectionModel().clearSelection();
        txtDate.setText("");
        txtQuantity.setText("");
        txtStockType.setText("");
        resetValidationStyles();

        loadNextId();
        loadTable();
    }

    private void setupTableColumns() {
        colStockId.setCellValueFactory(new PropertyValueFactory<>("stockId"));
        colProductionId.setCellValueFactory(new PropertyValueFactory<>("productionId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colStockType.setCellValueFactory(new PropertyValueFactory<>("stockType"));
    }

    private void loadTable() {
        try {
            StockModel stockModel = new StockModel();
            ArrayList<StockDto> stockDtos = stockModel.viewAllStock();
            if (stockDtos != null) {
                ObservableList<StockDto> observableList = FXCollections.observableArrayList(stockDtos);
                tblStock.setItems(observableList);
            } else {
                tblStock.setItems(FXCollections.emptyObservableList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading stock data into table.").show();
        }
    }

    @FXML
    public void btnUpdateOnAction(ActionEvent event) {

        boolean isProductionIdSelected = comProductionId.getValue() != null && !comProductionId.getValue().isEmpty();
        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidQuantity = txtQuantity.getText().matches(quantityPattern);
        boolean isValidStockType = txtStockType.getText().matches(stockTypePattern);

        if (isProductionIdSelected && isValidDate && isValidQuantity && isValidStockType) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Update Stock");
            alert.setContentText("Are you sure you want to update this stock record?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    int quantity = Integer.parseInt(txtQuantity.getText());
                    StockDto stockDto = new StockDto(
                            lblId.getText(),
                            comProductionId.getValue(),
                            txtDate.getText(),
                            quantity,
                            txtStockType.getText()
                    );

                    boolean isUpdated = StockModel.updateSrock(stockDto);
                    if (isUpdated) {
                        clearFields();
                        new Alert(Alert.AlertType.INFORMATION, "Stock updated successfully!").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Something went wrong. Stock could not be updated.").show();
                    }
                } catch (NumberFormatException e) {
                    new Alert(Alert.AlertType.ERROR, "Invalid number format for Quantity.").show();
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "An error occurred during update: " + e.getMessage()).show();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly (Date: YYYY-MM-DD, Quantity: positive integer, Stock Type: alphabetic).").show();
            applyValidationStyles();
        }
    }

    public void tableOnClick(MouseEvent mouseEvent) {
        StockDto stockDto = tblStock.getSelectionModel().getSelectedItem();
        if (stockDto != null) {
            lblId.setText(stockDto.getStockId());
            comProductionId.setValue(stockDto.getProductionId());
            txtDate.setText(stockDto.getDate());
            txtQuantity.setText(String.valueOf(stockDto.getQuantity()));
            txtStockType.setText(stockDto.getStockType());
            resetValidationStyles();
        }
    }

    public void btnGoToCurdProduOnAction(ActionEvent actionEvent) {
        navigateTo("/View/CurdProductionView.fxml");
    }

    private void navigateTo(String path){
        try {
            ancStock.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(path));

            anchorPane.prefWidthProperty().bind(ancStock.widthProperty());
            anchorPane.prefHeightProperty().bind(ancStock.heightProperty());
            ancStock.getChildren().add(anchorPane);
        }catch (Exception e){
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage(), ButtonType.OK).show();
        }
    }

    public void btnGoToStockOnAction(ActionEvent actionEvent) {
        navigateTo("/View/StockView.fxml");
    }

    public void comProductionIdOnAction(ActionEvent actionEvent) {
        String selectedProductionId = comProductionId.getValue();
        if (selectedProductionId != null && !selectedProductionId.isEmpty()) {
            comProductionId.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comProductionId.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
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

    public void txtQuantityChange(KeyEvent keyEvent) {
        String quantity = txtQuantity.getText();
        boolean isValid = quantity.matches(quantityPattern);
        if (isValid) {
            txtQuantity.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtQuantity.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    public void txtStockTypeChange(KeyEvent keyEvent) {
        String stockType = txtStockType.getText();
        boolean isValid = stockType.matches(stockTypePattern);
        if (isValid) {
            txtStockType.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtStockType.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    private void applyValidationStyles() {
        comProductionIdOnAction(null);
        txtDateChange(null);
        txtQuantityChange(null);
        txtStockTypeChange(null);
    }

    private void resetValidationStyles() {
        comProductionId.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtDate.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtQuantity.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtStockType.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
    }
}