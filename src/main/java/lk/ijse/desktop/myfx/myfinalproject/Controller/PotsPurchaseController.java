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
import lk.ijse.desktop.myfx.myfinalproject.Dto.PotsPurchaseDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.PotsPurchaseModel;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class PotsPurchaseController implements Initializable {
    public AnchorPane getAncPotsPurchase(){
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
        ArrayList<Integer> potsSize = PotsPurchaseModel.getAllPotsSize();
        ObservableList<Integer> observableList = FXCollections.observableArrayList(potsSize);
        comPotsSize.setItems(observableList);
    }

    @FXML
    private AnchorPane ancPotsPurchase;
    private String path;

    @FXML
    private TableColumn<PotsPurchaseDto, String> colDate;

    @FXML
    private TableColumn<PotsPurchaseDto, String> colId;

    @FXML
    private TableColumn<PotsPurchaseDto, Integer> colPotsSize;

    @FXML
    private TableColumn<PotsPurchaseDto, Double> colPrice;

    @FXML
    private TableColumn<PotsPurchaseDto, Integer> colQuantity;

    @FXML
    private TableView<PotsPurchaseDto> tblPotsPurchase;

    @FXML
    private TextField txtDate;

    @FXML
    private Label lblId;

    @FXML
    private ComboBox<Integer> comPotsSize;

    @FXML
    private TextField txtQuantity;

    @FXML
    private TextField txtUnitPrice;

    private final String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
    private final String quantityPattern = "^[1-9]\\d*$";
    private final String unitPricePattern = "^\\d+(\\.\\d{1,2})?$";

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFields();
    }

    @FXML
    public void btnDeleteOnAction(ActionEvent event) {
        String id = lblId.getText();

        if (id == null || id.isEmpty() || id.equals("Auto Generated")) {
            new Alert(Alert.AlertType.WARNING, "Please select a pots purchase record to delete from the table.").show();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Pots Purchase");
        alert.setContentText("Are you sure you want to delete this pots purchase record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = new PotsPurchaseModel().deletePotsPurchase(new PotsPurchaseDto(id, 0, null, 0, 0.0));
                if (isDeleted) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "Pots Purchase record deleted successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete pots purchase record.").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "An error occurred during deletion: " + e.getMessage()).show();
            }
        }
    }

    private void loadNextId () throws SQLException {
        PotsPurchaseModel potsPurchaseModel = new PotsPurchaseModel();
        String id = potsPurchaseModel.getNextId();
        lblId.setText(id);
    }

    @FXML
    public void btnSaveOnAction(ActionEvent event) {

        boolean isPotsSizeSelected = comPotsSize.getValue() != null && comPotsSize.getValue() > 0;
        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidQuantity = txtQuantity.getText().matches(quantityPattern);
        boolean isValidUnitPrice = txtUnitPrice.getText().matches(unitPricePattern);


        if (isPotsSizeSelected && isValidDate && isValidQuantity && isValidUnitPrice) {
            try {
                int potsSize = comPotsSize.getValue();
                int quantity = Integer.parseInt(txtQuantity.getText());
                double unitPrice = Double.parseDouble(txtUnitPrice.getText());

                PotsPurchaseDto potsPurchaseDto = new PotsPurchaseDto(
                        lblId.getText(),
                        potsSize,
                        txtDate.getText(),
                        quantity,
                        unitPrice
                );

                PotsPurchaseModel potsPurchaseModel = new PotsPurchaseModel();
                boolean isSaved = potsPurchaseModel.savePotsPurchase(potsPurchaseDto);
                if (isSaved) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "Pots Purchase saved successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save pots purchase.").show();
                }
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Invalid number format for Quantity or Unit Price.").show();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to save pots purchase due to a database error: " + e.getMessage()).show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly (Date: YYYY-MM-DD, Quantity: positive integer, Unit Price: e.g., 12.50).").show();
            applyValidationStyles();
        }
    }

    private void clearFields() throws SQLException {
        lblId.setText("");
        comPotsSize.getSelectionModel().clearSelection();
        txtDate.setText("");
        txtQuantity.setText("");
        txtUnitPrice.setText("");
        resetValidationStyles();

        loadNextId();
        loadTable();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("purchaseId"));
        colPotsSize.setCellValueFactory(new PropertyValueFactory<>("potsSize"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    private void loadTable() {
        try {
            PotsPurchaseModel potsPurchaseModel = new PotsPurchaseModel();
            ArrayList<PotsPurchaseDto> potsPurchaseDtos = potsPurchaseModel.viewAllPotsPurchase();
            if (potsPurchaseDtos != null) {
                ObservableList<PotsPurchaseDto> list = FXCollections.observableArrayList(potsPurchaseDtos);
                tblPotsPurchase.setItems(list);
            } else {
                tblPotsPurchase.setItems(FXCollections.emptyObservableList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading pots purchase data into table.").show();
        }
    }

    @FXML
    public void btnUpdateOnAction(ActionEvent event) {

        boolean isPotsSizeSelected = comPotsSize.getValue() != null && comPotsSize.getValue() > 0;
        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidQuantity = txtQuantity.getText().matches(quantityPattern);
        boolean isValidUnitPrice = txtUnitPrice.getText().matches(unitPricePattern);

        if (isPotsSizeSelected && isValidDate && isValidQuantity && isValidUnitPrice) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Update Pots Purchase");
            alert.setContentText("Are you sure you want to update this pots purchase record?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    int potsSize = comPotsSize.getValue();
                    int quantity = Integer.parseInt(txtQuantity.getText());
                    double unitPrice = Double.parseDouble(txtUnitPrice.getText());

                    PotsPurchaseDto potsPurchaseDto = new PotsPurchaseDto(
                            lblId.getText(),
                            potsSize,
                            txtDate.getText(),
                            quantity,
                            unitPrice
                    );

                    boolean isUpdated = PotsPurchaseModel.updatePotsPurchase(potsPurchaseDto);
                    if (isUpdated) {
                        clearFields();
                        new Alert(Alert.AlertType.INFORMATION, "Pots Purchase updated successfully!").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to update pots purchase.").show();
                    }
                } catch (NumberFormatException e) {
                    new Alert(Alert.AlertType.ERROR, "Invalid number format for Quantity or Unit Price.").show();
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "An error occurred during update: " + e.getMessage()).show();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly (Date: YYYY-MM-DD, Quantity: positive integer, Unit Price: e.g., 12.50).").show();
            applyValidationStyles();
    }
    }

    public void tableOnClick(MouseEvent mouseEvent) {
        PotsPurchaseDto potsPurchaseDto = tblPotsPurchase.getSelectionModel().getSelectedItem();
        if (potsPurchaseDto != null) {
            lblId.setText(potsPurchaseDto.getPurchaseId());
            comPotsSize.setValue(potsPurchaseDto.getPotsSize());
            txtDate.setText(potsPurchaseDto.getDate());
            txtQuantity.setText(String.valueOf(potsPurchaseDto.getQuantity()));
            txtUnitPrice.setText(String.valueOf(potsPurchaseDto.getPrice()));
            resetValidationStyles();
        }
    }

    public void btnGoToSupplierOnAction(ActionEvent actionEvent) {
        navigateTo("/View/SupplierView.fxml");
    }

    private void navigateTo(String path){
        try {
            ancPotsPurchase.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(path));

            anchorPane.prefWidthProperty().bind(ancPotsPurchase.widthProperty());
            anchorPane.prefHeightProperty().bind(ancPotsPurchase.heightProperty());
            ancPotsPurchase.getChildren().add(anchorPane);
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

    public void txtUnitPriceChange(KeyEvent keyEvent) {
        String unitPrice = txtUnitPrice.getText();
        boolean isValid = unitPrice.matches(unitPricePattern);
        if (isValid) {
            txtUnitPrice.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtUnitPrice.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    private void applyValidationStyles() {
        comPotsSizeOnAction(null);
        txtDateChange(null);
        txtQuantityChange(null);
        txtUnitPriceChange(null);
    }

    private void resetValidationStyles() {
        comPotsSize.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtDate.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtQuantity.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtUnitPrice.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
    }
}