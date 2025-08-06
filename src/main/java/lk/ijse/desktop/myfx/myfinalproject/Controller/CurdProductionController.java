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
import lk.ijse.desktop.myfx.myfinalproject.Dto.CurdProductionDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.CurdProductionModel;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOFactory;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOTypes;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.CurdProductionBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.InUseException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
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

    @FXML
    private Button btnClear;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnUpdate;

    private final String quantityPattern = "^\\d+$";
    private final String ingredientsPattern = "^[A-Za-z0-9,.'\\-\\s]+$";
    private final String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";

    private final CurdProductionBO curdProductionBO = BOFactory.getInstance().getBO(BOTypes.CURD_PRODUCTION);

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFields();
    }

    @FXML
    public void btnDeleteOnAction(ActionEvent event) {
        String productionId = lblId.getText();

        if (productionId == null || productionId.isEmpty() || lblId.getText().equals("CP001") || tblCurdProduction.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a production record to delete from the table.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Production");
        alert.setContentText("Are you sure you want to delete this production record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = curdProductionBO.deleteCurdProduction(productionId);
                if (isDeleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Production record deleted successfully.");
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed to delete production record.");
                }
            } catch (NotFoundException | InUseException e) {
                showAlert(Alert.AlertType.ERROR, e.getMessage());
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "An error occurred during deletion: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    private void loadNextId () throws SQLException {
        String nextId = curdProductionBO.getNextCurdProductionId();
        lblId.setText(nextId);
    }

    @FXML
    public void btnSaveOnAction(ActionEvent event)  {
        if (!isValidInputs(true)) {
            applyValidationStyles();
            return;
        }

        try {
            LocalDate productionDate = LocalDate.parse(txtProductionDate.getText());
            LocalDate expiryDate = LocalDate.parse(txtExpiryDate.getText());
            int quantity = Integer.parseInt(txtQuantity.getText());
            int potsSize = comPotsSize.getValue();

            CurdProductionDto curdProductionDto = new CurdProductionDto(
                    lblId.getText(),
                    productionDate,
                    expiryDate,
                    quantity,
                    potsSize,
                    txtIngredients.getText(),
                    comStorageId.getValue()
            );

            curdProductionBO.saveCurdProduction(curdProductionDto);
            showAlert(Alert.AlertType.INFORMATION, "Curd Production has been saved successfully!");
            clearFields();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid number format for Quantity or Pot Size.");
        } catch (DateTimeParseException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid date format. Please use YYYY-MM-DD.");
        } catch (DuplicateException e) {
            showAlert(Alert.AlertType.ERROR, e.getMessage());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "An error occurred while saving Curd Production: " + e.getMessage());
            e.printStackTrace();
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
        tblCurdProduction.getSelectionModel().clearSelection();
        updateButtonStates();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        setupFieldListeners();
        try {
            loadNextId();
            loadPotsSize();
            loadStorageId();
            loadTable();
            updateButtonStates();
            clearFields();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize CurdProductionController", e);
        }
    }

    private void showAlert(Alert.AlertType alertType, String s) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(alertType.name().replace("_", " "));
            alert.setHeaderText(null);
            alert.setContentText(s);
            alert.showAndWait();
        });
    }

    private void updateButtonStates() {
        if (btnSave == null || btnUpdate == null || btnDelete == null || btnClear == null) {
            return;
        }

        boolean isAnyFieldEmpty = txtQuantity.getText().isEmpty() ||
                txtIngredients.getText().isEmpty() ||
                txtProductionDate.getText().isEmpty() ||
                txtExpiryDate.getText().isEmpty() ||
                comPotsSize.getValue() == null ||
                comStorageId.getValue() == null || comStorageId.getValue().isEmpty();

        boolean isValid = isValidInputs(false);

        CurdProductionDto selectedItem = tblCurdProduction.getSelectionModel().getSelectedItem();

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

    private boolean isValidInputs(boolean showDialog) {
        boolean isValidQuantity = txtQuantity.getText().matches(quantityPattern);
        boolean isValidIngredients = txtIngredients.getText().matches(ingredientsPattern);
        boolean isValidProductionDate = txtProductionDate.getText().matches(datePattern);
        boolean isValidExpiryDate = txtExpiryDate.getText().matches(datePattern);
        boolean isPotSizeSelected = comPotsSize.getValue() != null && comPotsSize.getValue() > 0;
        boolean isStorageIdSelected = comStorageId.getValue() != null && !comStorageId.getValue().isEmpty();

        if (showDialog) {
            if (!isValidQuantity) {
                showAlert(Alert.AlertType.WARNING, "Quantity must be a whole number.");
                return false;
            }
            if (!isValidIngredients) {
                showAlert(Alert.AlertType.WARNING, "Ingredients should contain only letters, numbers, commas, periods, apostrophes, hyphens, and spaces.");
                return false;
            }
            if (!isValidProductionDate) {
                showAlert(Alert.AlertType.WARNING, "Production Date must be in YYYY-MM-DD format.");
                return false;
            }
            if (!isValidExpiryDate) {
                showAlert(Alert.AlertType.WARNING, "Expiry Date must be in YYYY-MM-DD format.");
                return false;
            }
            if (!isPotSizeSelected) {
                showAlert(Alert.AlertType.WARNING, "Please select a Pot Size.");
                return false;
            }
            if (!isStorageIdSelected) {
                showAlert(Alert.AlertType.WARNING, "Please select a Storage ID.");
                return false;
            }
        }
        return isValidQuantity && isValidIngredients && isValidProductionDate && isValidExpiryDate && isPotSizeSelected && isStorageIdSelected;
    }


    private void setupFieldListeners() {
        txtQuantity.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtIngredients.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtProductionDate.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtExpiryDate.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        comPotsSize.valueProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        comStorageId.valueProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        tblCurdProduction.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
    }

    private void loadStorageId() throws SQLException {
        List<String> storageList = curdProductionBO.getAllStorageIds();
        ObservableList<String> observableList = FXCollections.observableArrayList(storageList);
        comStorageId.setItems(observableList);
    }

    private void loadPotsSize() throws SQLException {
        List<Integer> potsSizeList = curdProductionBO.getAllPotsSizes();
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
            List<CurdProductionDto> curdProductionDtos = curdProductionBO.getAllCurdProductions();
            tblCurdProduction.setItems(FXCollections.observableArrayList(curdProductionDtos));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading curd production data into table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void btnUpdateOnAction(ActionEvent event) {
        if (!isValidInputs(true)) {
            applyValidationStyles();
            return;
        }

        if (tblCurdProduction.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a production record from the table to update.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Update Production");
        alert.setContentText("Are you sure you want to update this production record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                LocalDate productionDate = LocalDate.parse(txtProductionDate.getText());
                LocalDate expiryDate = LocalDate.parse(txtExpiryDate.getText());
                int quantity = Integer.parseInt(txtQuantity.getText());
                int potsSize = comPotsSize.getValue();

                CurdProductionDto curdProductionDto = new CurdProductionDto(
                        lblId.getText(),
                        productionDate,
                        expiryDate,
                        quantity,
                        potsSize,
                        txtIngredients.getText(),
                        comStorageId.getValue()
                );

                curdProductionBO.updateCurdProduction(curdProductionDto);
                showAlert(Alert.AlertType.INFORMATION, "Production record updated successfully!");
                clearFields();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid number format for Quantity or Pot Size.");
            } catch (DateTimeParseException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid date format. Please use YYYY-MM-DD.");
            } catch (NotFoundException e) {
                showAlert(Alert.AlertType.ERROR, e.getMessage());
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "An error occurred during update: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    public void tableOnClick(MouseEvent mouseEvent) {
        CurdProductionDto curdProductionDto = tblCurdProduction.getSelectionModel().getSelectedItem();
        if(curdProductionDto != null){
            lblId.setText(curdProductionDto.getProductionId());
            txtProductionDate.setText(curdProductionDto.getProductionDate().toString());
            txtExpiryDate.setText(curdProductionDto.getExpiryDate().toString());
            txtQuantity.setText(String.valueOf(curdProductionDto.getQuantity()));
            comPotsSize.setValue(curdProductionDto.getPotsSize());
            txtIngredients.setText(curdProductionDto.getIngredients());
            comStorageId.setValue(curdProductionDto.getStorageId());
            resetValidationStyles();
            updateButtonStates();
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
        updateButtonStates();
    }

    public void txtIngredientsChange(KeyEvent keyEvent) {
        String ingredients = txtIngredients.getText();
        boolean isValid = ingredients.matches(ingredientsPattern);
        if (isValid) {
            txtIngredients.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtIngredients.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void txtProductionDateChange(KeyEvent keyEvent) {
        String date = txtProductionDate.getText();
        boolean isValid = date.matches(datePattern);
        if (isValid) {
            txtProductionDate.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtProductionDate.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void txtExpiryDateChange(KeyEvent keyEvent) {
        String date = txtExpiryDate.getText();
        boolean isValid = date.matches(datePattern);
        if (isValid) {
            txtExpiryDate.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtExpiryDate.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void comPotsSizeOnAction(ActionEvent actionEvent) {
        Integer selectedPotSize = comPotsSize.getValue();
        if (selectedPotSize != null && selectedPotSize > 0) {
            comPotsSize.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comPotsSize.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void comStorageIdOnAction(ActionEvent actionEvent) {
        String selectedStorageId = comStorageId.getValue();
        if (selectedStorageId != null && !selectedStorageId.isEmpty()) {
            comStorageId.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comStorageId.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
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