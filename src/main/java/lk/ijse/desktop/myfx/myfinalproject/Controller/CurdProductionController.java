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
import lk.ijse.desktop.myfx.myfinalproject.Dto.CurdProductionDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.CurdProductionModel;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;


public class CurdProductionController implements Initializable {
    public AnchorPane getAncCurdProduction(){
        return null;
    }


    @FXML
    private AnchorPane ancCurdProduction;
    private String path;

    @FXML
    private TableColumn<CurdProductionDto, String> colExpiryDate;

    @FXML
    private TableColumn<CurdProductionDto, String> colId;

    @FXML
    private TableColumn<CurdProductionDto, String> colIngredients;

    @FXML
    private TableColumn<CurdProductionDto, Integer> colPotsSize;

    @FXML
    private TableColumn<CurdProductionDto, String> colProductionDate;

    @FXML
    private TableColumn<CurdProductionDto, Integer> colQuantity;

    @FXML
    private TableColumn<CurdProductionDto, String> colStorageID;

    @FXML
    private TableView<CurdProductionDto> tblCurdProduction;

    @FXML
    private TextField txtExpiryDate;

    @FXML
    private Label lblId;

    @FXML
    private TextField txtIngredients;

    @FXML
    private ComboBox<Integer> comPotsSize;

    @FXML
    private ComboBox<String> comStorageId;

    @FXML
    private TextField txtProductionDate;

    @FXML
    private TextField txtQuantity;

    private final String quantityPattern = "^\\d+$";
    private final String ingredientsPattern = "^[A-Za-z0-9,.'\\-\\s]+$";
    private final String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFields();
    }

    @FXML
    public void btnDeleteOnAction(ActionEvent event) {
        String productionId = lblId.getText();

        if (productionId == null || productionId.isEmpty() || productionId.equals("Auto Generated")) {
            new Alert(Alert.AlertType.WARNING, "Please select a production to delete from the table.").show();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Production");
        alert.setContentText("Are you sure you want to delete this production record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDelete = new CurdProductionModel().deleteCurdProduction(new CurdProductionDto(productionId, null, null, 0, 0, null, null));
                if (isDelete) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "Production record deleted successfully.").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete production record.").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "An error occurred during deletion: " + e.getMessage()).show();
            }
        }
    }

    private void loadNextId () throws SQLException {
        CurdProductionModel curdProductionModel = new CurdProductionModel();
        String nextId = curdProductionModel.getNextId();
        lblId.setText(nextId);
    }

    @FXML
    public void btnSaveOnAction(ActionEvent event) throws ClassNotFoundException, SQLException {

        boolean isValidQuantity = txtQuantity.getText().matches(quantityPattern);
        boolean isValidIngredients = txtIngredients.getText().matches(ingredientsPattern);
        boolean isValidProductionDate = txtProductionDate.getText().matches(datePattern);
        boolean isValidExpiryDate = txtExpiryDate.getText().matches(datePattern);
        boolean isPotSizeSelected = comPotsSize.getValue() != null && comPotsSize.getValue() > 0;
        boolean isStorageIdSelected = comStorageId.getValue() != null && !comStorageId.getValue().isEmpty();

        if (isValidQuantity && isValidIngredients && isValidProductionDate && isValidExpiryDate && isPotSizeSelected && isStorageIdSelected) {
            try {
                int quantity = Integer.parseInt(txtQuantity.getText());
                int potsSize = comPotsSize.getValue();

                CurdProductionDto curdProductionDto = new CurdProductionDto(
                        lblId.getText(),
                        txtProductionDate.getText(),
                        txtExpiryDate.getText(),
                        quantity,
                        potsSize,
                        txtIngredients.getText(),
                        comStorageId.getValue()
                );

                CurdProductionModel curdProductionModel = new CurdProductionModel();
                boolean isSave = CurdProductionModel.saveCurdProduction(curdProductionDto);
                if (isSave) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "Curd Production has been saved successfully").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Error while saving Curd Production").show();
                }
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Invalid number format for Quantity or Pot Size.").show();

            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Curd Production has not been saved due to a database error.").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly and dates are in YYYY-MM-DD format.").show();
            applyValidationStyles();
        }
    }

    private void clearFields() throws SQLException {
        txtExpiryDate.setText("");
        txtIngredients.setText("");
        comPotsSize.getSelectionModel().clearSelection();
        txtProductionDate.setText("");
        txtQuantity.setText("");
        comStorageId.getSelectionModel().clearSelection();
        resetValidationStyles();

        loadNextId();
        loadTable();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadNextId();
            loadPotsSize();
            loadStorageId();
            setupTableColumns();
            clearFields();
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing controller: " + e.getMessage(), e);
        }
    }

    private void loadStorageId() throws SQLException {
        ArrayList<String> storageList = CurdProductionModel.getAllStorageId();
        ObservableList<String> observableList = FXCollections.observableArrayList(storageList);
        comStorageId.setItems(observableList);
    }

    private void loadPotsSize() throws SQLException {
        ArrayList<Integer> potsSizeList = CurdProductionModel.getAllPotsSize();
        ObservableList<Integer> observableList = FXCollections.observableArrayList(potsSizeList);
        comPotsSize.setItems(observableList);
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("productionId"));
        colProductionDate.setCellValueFactory(new PropertyValueFactory<>("productionDate"));
        colExpiryDate.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPotsSize.setCellValueFactory(new PropertyValueFactory<>("potsSize"));
        colIngredients.setCellValueFactory(new PropertyValueFactory<>("ingredients"));
        colStorageID.setCellValueFactory(new PropertyValueFactory<>("storageId"));
    }

    private void loadTable() {
        try {
            CurdProductionModel curdProductionModel = new CurdProductionModel();
            ArrayList<CurdProductionDto> curdProductionDtos = curdProductionModel.viewAllCurdProduction();
            if(curdProductionDtos != null){
                ObservableList<CurdProductionDto> observableList = FXCollections.observableArrayList(curdProductionDtos);
                tblCurdProduction.setItems(observableList);
            } else {
                tblCurdProduction.setItems(FXCollections.emptyObservableList());
            }
        } catch (Exception e){
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading curd production data into table.").show();
        }
    }

    @FXML
    public void btnUpdateOnAction(ActionEvent event) {

        boolean isValidQuantity = txtQuantity.getText().matches(quantityPattern);
        boolean isValidIngredients = txtIngredients.getText().matches(ingredientsPattern);
        boolean isValidProductionDate = txtProductionDate.getText().matches(datePattern);
        boolean isValidExpiryDate = txtExpiryDate.getText().matches(datePattern);
        boolean isPotSizeSelected = comPotsSize.getValue() != null && comPotsSize.getValue() > 0;
        boolean isStorageIdSelected = comStorageId.getValue() != null && !comStorageId.getValue().isEmpty();

        if (isValidQuantity && isValidIngredients && isValidProductionDate && isValidExpiryDate && isPotSizeSelected && isStorageIdSelected) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Update Production");
            alert.setContentText("Are you sure you want to update this production record?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    int quantity = Integer.parseInt(txtQuantity.getText());
                    int potsSize = comPotsSize.getValue();

                    CurdProductionDto curdProductionDto = new CurdProductionDto(
                            lblId.getText(),
                            txtProductionDate.getText(),
                            txtExpiryDate.getText(),
                            quantity,
                            potsSize,
                            txtIngredients.getText(),
                            comStorageId.getValue()
                    );

                    boolean isUpdated = CurdProductionModel.updateCurdProduction(curdProductionDto);
                    if (isUpdated) {
                        clearFields();
                        new Alert(Alert.AlertType.INFORMATION, "Production record updated successfully!").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Update failed!").show();
                    }
                } catch (NumberFormatException e) {
                    new Alert(Alert.AlertType.ERROR, "Invalid number format for Quantity or Pot Size.").show();
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "An error occurred during update: " + e.getMessage()).show();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly and dates are in YYYY-MM-DD format.").show();
            applyValidationStyles();
        }
    }

    public void tableOnClick(MouseEvent mouseEvent) {
        CurdProductionDto curdProductionDto = tblCurdProduction.getSelectionModel().getSelectedItem();
        if(curdProductionDto != null){
            lblId.setText(curdProductionDto.getProductionId());
            txtProductionDate.setText(curdProductionDto.getProductionDate());
            txtExpiryDate.setText(curdProductionDto.getExpiryDate());
            txtQuantity.setText(String.valueOf(curdProductionDto.getQuantity()));
            comPotsSize.setValue(curdProductionDto.getPotsSize());
            txtIngredients.setText(curdProductionDto.getIngredients());
            comStorageId.setValue(curdProductionDto.getStorageId());
            resetValidationStyles();
        }
    }

    public void btnGoToCurdProduOnAction(ActionEvent actionEvent) {
        navigateTo("/View/CurdProductionView.fxml");
    }

    private void navigateTo(String path){
        try {
            ancCurdProduction.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(path));

            anchorPane.prefWidthProperty().bind(ancCurdProduction.widthProperty());
            anchorPane.prefHeightProperty().bind(ancCurdProduction.heightProperty());
            ancCurdProduction.getChildren().add(anchorPane);
        }catch (Exception e){
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage(), ButtonType.OK).show();
        }
    }

    public void btnGoToStockOnAction(ActionEvent actionEvent) {
        navigateTo("/View/StockView.fxml");
    }

    public void txtQuantityChange(KeyEvent keyEvent) {
        String qty = txtQuantity.getText();
        boolean isValid = qty.matches(quantityPattern);
        if (isValid) {
            txtQuantity.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtQuantity.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    public void txtIngredientsChange(KeyEvent keyEvent) {
        String ingredients = txtIngredients.getText();
        boolean isValid = ingredients.matches(ingredientsPattern);
        if (isValid) {
            txtIngredients.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtIngredients.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    public void txtProductionDateChange(KeyEvent keyEvent) {
        String date = txtProductionDate.getText();
        boolean isValid = date.matches(datePattern);
        if (isValid) {
            txtProductionDate.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtProductionDate.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    public void txtExpiryDateChange(KeyEvent keyEvent) {
        String date = txtExpiryDate.getText();
        boolean isValid = date.matches(datePattern);
        if (isValid) {
            txtExpiryDate.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtExpiryDate.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    public void comPotsSizeOnAction(ActionEvent actionEvent) {
        Integer selectedPotSize = comPotsSize.getValue();
        if (selectedPotSize != null && selectedPotSize > 0) {
            comPotsSize.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comPotsSize.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    public void comStorageIdOnAction(ActionEvent actionEvent) {
        String selectedStorageId = comStorageId.getValue();
        if (selectedStorageId != null && !selectedStorageId.isEmpty()) {
            comStorageId.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comStorageId.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    private void applyValidationStyles() {
        txtQuantityChange(null);
        txtIngredientsChange(null);
        txtProductionDateChange(null);
        txtExpiryDateChange(null);
        comPotsSizeOnAction(null);
        comStorageIdOnAction(null);
    }

    private void resetValidationStyles() {
        txtQuantity.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtIngredients.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtProductionDate.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtExpiryDate.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        comPotsSize.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        comStorageId.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
    }
}