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
import lk.ijse.desktop.myfx.myfinalproject.Dto.PotsInventoryDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.PotsInventoryModel;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class PotsInventoryController implements Initializable {
    public AnchorPane getAncPotsInventory(){
        return null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadNextId();
            loadPotsSize();
            setupTableColumns();
            clearFields();
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing controller: " + e.getMessage(), e);
        }
    }

    private void loadPotsSize() throws SQLException {
        ArrayList<Integer> potsSize = PotsInventoryModel.getAllPotsSize();
        ObservableList<Integer> observableList = FXCollections.observableArrayList(potsSize);
        comPotsSize.setItems(observableList);
    }

    @FXML
    private AnchorPane ancPotsInventory;
    private String path;

    @FXML
    private TableColumn<PotsInventoryDto, String> colCondition;

    @FXML
    private TableColumn<PotsInventoryDto, String> colId;

    @FXML
    private TableColumn<PotsInventoryDto, Integer> colPotsSize;

    @FXML
    private TableColumn<PotsInventoryDto, Integer> colQuantity;

    @FXML
    private TableView<PotsInventoryDto> tblPotsInventory;

    @FXML
    private TextField txtCondition;

    @FXML
    private Label lblId;

    @FXML
    private ComboBox<Integer> comPotsSize;

    @FXML
    private TextField txtQuantity;

    private final String quantityPattern = "^\\d+$";
    private final String conditionPattern = "^[a-zA-Z0-9 ]{3,50}$";

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFields();
    }

    @FXML
    public void btnDeleteOnAction(ActionEvent event) {
        String id = lblId.getText();

        if (id == null || id.isEmpty() || id.equals("Auto Generated")) {
            new Alert(Alert.AlertType.WARNING, "Please select a pots inventory record to delete from the table.").show();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Pots Inventory");
        alert.setContentText("Are you sure you want to delete this pots inventory record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = new PotsInventoryModel().deletePotsInventory(new PotsInventoryDto(id, 0, 0, null));
                if (isDeleted) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "Pots inventory record deleted successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete pots inventory record.").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "An error occurred during deletion: " + e.getMessage()).show();
            }
        }
    }

    private void loadNextId() throws SQLException {
        PotsInventoryModel potsInventoryModel = new PotsInventoryModel();
        String id = potsInventoryModel.getNextId();
        lblId.setText(id);
    }

    @FXML
    public void btnSaveOnAction(ActionEvent event) {
        boolean isValidQuantity = txtQuantity.getText().matches(quantityPattern);
        boolean isValidCondition = txtCondition.getText().matches(conditionPattern);
        boolean isPotsSizeSelected = comPotsSize.getValue() != null && comPotsSize.getValue() > 0;

        if (isValidQuantity && isValidCondition && isPotsSizeSelected) {
            try {
                int quantity = Integer.parseInt(txtQuantity.getText());
                int potsSize = comPotsSize.getValue();
                PotsInventoryDto potsInventoryDto = new PotsInventoryDto(
                        lblId.getText(),
                        quantity,
                        potsSize,
                        txtCondition.getText()
                );

                PotsInventoryModel potsInventoryModel = new PotsInventoryModel();
                boolean isSaved = potsInventoryModel.savePotsInventory(potsInventoryDto);
                if (isSaved) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "Pots Inventory has been saved successfully").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save pots inventory.").show();
                }
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Invalid number format for Quantity.").show();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to save pots inventory due to a database error.").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly (Quantity: positive integer, Condition: 3-50 alphanumeric characters).").show();
            applyValidationStyles();
        }
    }

    private void clearFields() throws SQLException {
        lblId.setText("");
        txtQuantity.setText("");
        comPotsSize.getSelectionModel().clearSelection();
        txtCondition.setText("");
        resetValidationStyles();

        loadNextId();
        loadTable();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPotsSize.setCellValueFactory(new PropertyValueFactory<>("potsSize"));
        colCondition.setCellValueFactory(new PropertyValueFactory<>("condition"));
    }

    private void loadTable() {
        try {
            PotsInventoryModel potsInventoryModel = new PotsInventoryModel();
            ArrayList<PotsInventoryDto> potsInventoryDtos = potsInventoryModel.viewAllPotsInventory();
            if (potsInventoryDtos != null) {
                ObservableList<PotsInventoryDto> observableList = FXCollections.observableArrayList(potsInventoryDtos);
                tblPotsInventory.setItems(observableList);
            } else {
                tblPotsInventory.setItems(FXCollections.emptyObservableList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading pots inventory data into table.").show();
        }
    }

    @FXML
    public void btnUpdateOnAction(ActionEvent event) {

        boolean isValidQuantity = txtQuantity.getText().matches(quantityPattern);
        boolean isValidCondition = txtCondition.getText().matches(conditionPattern);
        boolean isPotsSizeSelected = comPotsSize.getValue() != null && comPotsSize.getValue() > 0;

        if (isValidQuantity && isValidCondition && isPotsSizeSelected) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Update Pots Inventory");
            alert.setContentText("Are you sure you want to update this pots inventory record?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    int quantity = Integer.parseInt(txtQuantity.getText());
                    int potsSize = comPotsSize.getValue();
                    PotsInventoryDto potsInventoryDto = new PotsInventoryDto(
                            lblId.getText(),
                            quantity,
                            potsSize,
                            txtCondition.getText()
                    );

                    boolean isUpdated = PotsInventoryModel.updatePotsInventory(potsInventoryDto);
                    if (isUpdated) {
                        clearFields();
                        new Alert(Alert.AlertType.INFORMATION, "Pots inventory has been updated successfully").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to update pots inventory.").show();
                    }
                } catch (NumberFormatException e) {
                    new Alert(Alert.AlertType.ERROR, "Invalid number format for Quantity.").show();
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "An error occurred during update: " + e.getMessage()).show();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly (Quantity: positive integer, Condition: 3-50 alphanumeric characters).").show();
            applyValidationStyles();
        }
    }

    public void tableOnClick(MouseEvent mouseEvent) {
        PotsInventoryDto potsInventoryDto = tblPotsInventory.getSelectionModel().getSelectedItem();
        if (potsInventoryDto != null) {
            lblId.setText(potsInventoryDto.getId());
            txtQuantity.setText(String.valueOf(potsInventoryDto.getQuantity()));
            comPotsSize.setValue(potsInventoryDto.getPotsSize());
            txtCondition.setText(potsInventoryDto.getCondition());
            resetValidationStyles();
        }
    }

    public void btnGoToSupplierOnAction(ActionEvent actionEvent) {
        navigateTo("/View/SupplierView.fxml");
    }

    private void navigateTo(String path){
        try {
            ancPotsInventory.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(path));

            anchorPane.prefWidthProperty().bind(ancPotsInventory.widthProperty());
            anchorPane.prefHeightProperty().bind(ancPotsInventory.heightProperty());
            ancPotsInventory.getChildren().add(anchorPane);
        }catch (Exception e){
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage(), ButtonType.OK).show();
        }
    }

    public void btnGoToPotsInventoryOnAction(ActionEvent actionEvent) {
        navigateTo("/View/PotsInventoryView.fxml");
    }

    public void btnGoToPotsPurchaseOnAction(ActionEvent actionEvent) {
        navigateTo("/View/PotsPurchaseView.fxml");
    }

    public void btnGoToRawMaterialOnAction(ActionEvent actionEvent) {
        navigateTo("/View/RawMaterialPurchaseView.fxml");
    }

    public void comPotsSizeOnAction(ActionEvent actionEvent) {
        Integer selectedPotsSize = comPotsSize.getValue();
        if (selectedPotsSize != null && selectedPotsSize > 0) {
            comPotsSize.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comPotsSize.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
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

    public void txtConditionChange(KeyEvent keyEvent) {
        String condition = txtCondition.getText();
        boolean isValid = condition.matches(conditionPattern);
        if (isValid) {
            txtCondition.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtCondition.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    private void applyValidationStyles() {
        txtQuantityChange(null);
        txtConditionChange(null);
        comPotsSizeOnAction(null);
    }

    private void resetValidationStyles() {
        txtQuantity.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtCondition.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        comPotsSize.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
    }
}