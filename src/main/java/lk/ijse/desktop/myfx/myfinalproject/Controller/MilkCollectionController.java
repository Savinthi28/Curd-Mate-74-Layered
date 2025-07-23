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
import lk.ijse.desktop.myfx.myfinalproject.Dto.MilkCollectionDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.MilkCollectionModel;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class MilkCollectionController implements Initializable {

    public AnchorPane getAncMilkCollection() {
        return null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadNextId();
            loadMilkCollectionBuffaloId();
            setupTableColumns();
            clearFields();
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing controller: " + e.getMessage(), e);
        }
    }

    private void loadMilkCollectionBuffaloId() throws SQLException {
        ArrayList<String> milkCollectionBuffaloIds = MilkCollectionModel.getAllMilkCollectionBuffaloId();
        ObservableList<String> observableList = FXCollections.observableArrayList(milkCollectionBuffaloIds);
        comMilkCollection.setItems(observableList);
    }

    @FXML
    private AnchorPane ancMilkCollection;
    private String path;

    @FXML
    private TableColumn<MilkCollectionDto, String> colBuffaloId;

    @FXML
    private TableColumn<MilkCollectionDto, String> colDate;

    @FXML
    private TableColumn<MilkCollectionDto, String> colId;

    @FXML
    private TableColumn<MilkCollectionDto, Double> colQuantity;

    @FXML
    private TableView<MilkCollectionDto> tblMilkCollection;

    @FXML
    private ComboBox<String> comMilkCollection;

    @FXML
    private TextField txtDate;

    @FXML
    private Label lblId;

    @FXML
    private TextField txtQuantity;

    private final String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
    private final String quantityPattern = "^\\d+(\\.\\d{1,2})?$";

    @FXML
    public void btnGoToMilkCollectionOnAction(ActionEvent actionEvent) {
        navigateTo("/View/MilkCollectionView.fxml");
    }

    private void navigateTo(String path){
        try {
            ancMilkCollection.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(path));

            anchorPane.prefWidthProperty().bind(ancMilkCollection.widthProperty());
            anchorPane.prefHeightProperty().bind(ancMilkCollection.heightProperty());
            ancMilkCollection.getChildren().add(anchorPane);
        }catch (Exception e){
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage(), ButtonType.OK).show();
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFields();
    }

    @FXML
    public void btnDeleteOnAction(ActionEvent event) {
        String id = lblId.getText();

        if (id == null || id.isEmpty() || id.equals("Auto Generated")) {
            new Alert(Alert.AlertType.WARNING, "Please select a milk collection record to delete from the table.").show();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Milk Collection");
        alert.setContentText("Are you sure you want to delete this milk collection record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDelete = new MilkCollectionModel().deleteMikCollection(new MilkCollectionDto(id, null, 0.0, null));
                if (isDelete) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "Milk Collection record deleted successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete milk collection record.").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "An error occurred during deletion: " + e.getMessage()).show();
            }
        }
    }

    private void loadNextId () throws SQLException {
        MilkCollectionModel milkCollectionModel = new MilkCollectionModel();
        String nextId = milkCollectionModel.getNextId();
        lblId.setText(nextId);
    }

    @FXML
    public void btnSaveOnAction(ActionEvent event) {

        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidQuantity = txtQuantity.getText().matches(quantityPattern);
        boolean isBuffaloIdSelected = comMilkCollection.getValue() != null && !comMilkCollection.getValue().isEmpty();

        if (isValidDate && isValidQuantity && isBuffaloIdSelected) {
            try {
                double quantity = Double.parseDouble(txtQuantity.getText());
                MilkCollectionDto milkCollectionDto = new MilkCollectionDto(
                        lblId.getText(),
                        txtDate.getText(),
                        quantity,
                        comMilkCollection.getValue()
                );

                MilkCollectionModel milkCollectionModel = new MilkCollectionModel();
                boolean isSave = milkCollectionModel.saveMilkCollection(milkCollectionDto);
                if (isSave) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "Milk Collection has been saved successfully").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save milk collection.").show();
                }
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Invalid number format for Quantity.").show();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to save milk collection due to a database error.").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly (Date: YYYY-MM-DD, Quantity: e.g., 15.50).").show();
            applyValidationStyles();
        }
    }

    private void clearFields() throws SQLException {
        lblId.setText("");
        txtDate.setText("");
        txtQuantity.setText("");
        comMilkCollection.getSelectionModel().clearSelection();
        resetValidationStyles();

        loadNextId();
        loadTable();
    }

    private void setupTableColumns(){
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colBuffaloId.setCellValueFactory(new PropertyValueFactory<>("buffaloId"));
    }

    private void loadTable() {
        try {
            MilkCollectionModel milkCollectionModel = new MilkCollectionModel();
            ArrayList<MilkCollectionDto> milkCollectionDtos = milkCollectionModel.viewAllMilkCollection();
            if (milkCollectionDtos != null) {
                ObservableList<MilkCollectionDto> observableList = FXCollections.observableArrayList(milkCollectionDtos);
                tblMilkCollection.setItems(observableList);
            } else {
                tblMilkCollection.setItems(FXCollections.emptyObservableList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading milk collection data into table.").show();
        }
    }

    @FXML
    public void btnUpdateOnAction(ActionEvent event) {

        boolean isValidDate = txtDate.getText().matches(datePattern);
        boolean isValidQuantity = txtQuantity.getText().matches(quantityPattern);
        boolean isBuffaloIdSelected = comMilkCollection.getValue() != null && !comMilkCollection.getValue().isEmpty();

        if (isValidDate && isValidQuantity && isBuffaloIdSelected) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Update Milk Collection");
            alert.setContentText("Are you sure you want to update this milk collection record?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    double quantity = Double.parseDouble(txtQuantity.getText());
                    MilkCollectionDto milkCollectionDto = new MilkCollectionDto(
                            lblId.getText(),
                            txtDate.getText(),
                            quantity,
                            comMilkCollection.getValue()
                    );

                    boolean isSave = MilkCollectionModel.updateMilkCollection(milkCollectionDto);
                    if (isSave) {
                        clearFields();
                        new Alert(Alert.AlertType.INFORMATION, "Milk Collection has been updated successfully").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to update milk collection.").show();
                    }
                } catch (NumberFormatException e) {
                    new Alert(Alert.AlertType.ERROR, "Invalid number format for Quantity.").show();
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "An error occurred during update: " + e.getMessage()).show();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly (Date: YYYY-MM-DD, Quantity: e.g., 15.50).").show();
            applyValidationStyles();
        }
    }

    public void tableOnClick(MouseEvent mouseEvent) {
        MilkCollectionDto milkCollectionDto = tblMilkCollection.getSelectionModel().getSelectedItem();
        if (milkCollectionDto != null) {
            lblId.setText(milkCollectionDto.getId());
            txtDate.setText(milkCollectionDto.getDate());
            txtQuantity.setText(String.valueOf(milkCollectionDto.getQuantity()));
            comMilkCollection.setValue(milkCollectionDto.getBuffaloId());
            resetValidationStyles();
        }
    }

    public void btnGoToMilkStorageOnAction(ActionEvent actionEvent) {
        navigateTo("/View/MilkStorageView.fxml");
    }

    public void btnGoToQualityCheckOnAction(ActionEvent actionEvent) {
        navigateTo("/View/QualityCheckView.fxml");
    }

    public void comMilkCollectionOnAction(ActionEvent actionEvent) {
        String selectedMilkCollection = comMilkCollection.getValue();
        if (selectedMilkCollection != null && !selectedMilkCollection.isEmpty()) {
            comMilkCollection.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            comMilkCollection.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
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
        txtDateChange(null);
        txtQuantityChange(null);
        comMilkCollectionOnAction(null);
    }

    private void resetValidationStyles() {
        txtDate.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtQuantity.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        comMilkCollection.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
    }
}