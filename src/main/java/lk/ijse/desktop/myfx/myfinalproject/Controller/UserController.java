package lk.ijse.desktop.myfx.myfinalproject.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import lk.ijse.desktop.myfx.myfinalproject.Dto.UserDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.UserModel;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOFactory;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOTypes;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.UserBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserController implements Initializable {

    @FXML
    private TableColumn<UserDto, String> colId;

    @FXML
    private TableColumn<UserDto, String> colEmail;

    @FXML
    private TableColumn<UserDto, String> colName;

    @FXML
    private TableColumn<UserDto, String> colPassword;

    @FXML
    private TableView<UserDto> tblUser;

    @FXML
    private Label lblId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtPassword;

    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;

    private final String namePattern = "^[a-zA-Z0-9]{3,20}$";
    private final String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,20}$";
    private final String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

    private final UserBO userBO = BOFactory.getInstance().getBO(BOTypes.USER);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        setupFieldListeners();
        try {
            loadNextId();
            loadTable();
            clearFields();
            updateButtonStates();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Error initializing controller: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error initializing controller: " + e.getMessage(), e);
        }
    }

    private void setupFieldListeners() {
        txtName.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtPassword.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtEmail.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        tblUser.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
    }

    private void updateButtonStates() {
        if (btnSave == null || btnUpdate == null || btnDelete == null || btnClear == null) {
            return;
        }

        boolean isAnyFieldEmpty = txtName.getText().isEmpty() ||
                txtPassword.getText().isEmpty() ||
                txtEmail.getText().isEmpty();

        boolean isValid = isValidInputs(false);

        UserDto selectedItem = tblUser.getSelectionModel().getSelectedItem();

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

    private void loadNextId() throws SQLException {
        String id = userBO.getNextUserId();
        lblId.setText(id);
    }

    private void clearFields() throws SQLException {
        lblId.setText("");
        txtName.clear();
        txtPassword.clear();
        txtEmail.clear();
        resetValidationStyles();

        loadNextId();
        loadTable();
        tblUser.getSelectionModel().clearSelection();
        updateButtonStates();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    private void loadTable() {
        try {
            List<UserDto> userDtos = userBO.getAllUsers();
            tblUser.setItems(FXCollections.observableArrayList(userDtos));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error Loading Data", "Error loading user data into table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isValidInputs(boolean showDialog) {
        boolean isValidName = txtName.getText().matches(namePattern);
        boolean isValidPassword = txtPassword.getText().matches(passwordPattern);
        boolean isValidEmail = txtEmail.getText().matches(emailPattern);

        if (showDialog) {
            if (!isValidName) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Username: 3-20 alphanumeric characters.");
                return false;
            }
            if (!isValidPassword) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Password: 8-20 characters, at least one digit, one lowercase, one uppercase, one special character.");
                return false;
            }
            if (!isValidEmail) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Email: Must be a valid email format (e.g., example@domain.com).");
                return false;
            }
        }
        return isValidName && isValidPassword && isValidEmail;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFields();
    }

    @FXML
    public void btnDeleteOnAction(ActionEvent event) {
        String idToDelete = lblId.getText();

        if (tblUser.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a user record from the table to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete User");
        alert.setContentText("Are you sure you want to delete this user?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = userBO.deleteUser(idToDelete);
                if (isDeleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Deletion Successful", "User Deleted Successfully!");
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Failed to delete user.");
                }
            } catch (NotFoundException e) {
                showAlert(Alert.AlertType.ERROR, "Deletion Failed", e.getMessage());
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred during deletion: " + e.getMessage());
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

        UserDto userDto = new UserDto(
                lblId.getText(),
                txtName.getText(),
                txtPassword.getText(),
                txtEmail.getText()
        );

        try {
            userBO.saveUser(userDto);
            showAlert(Alert.AlertType.INFORMATION, "Save Successful", "User has been saved successfully!");
            clearFields();
        } catch (DuplicateException e) {
            showAlert(Alert.AlertType.ERROR, "Save Failed", e.getMessage());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred during saving: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void btnUpdateOnAction(ActionEvent event) {
        if (!isValidInputs(true)) {
            applyValidationStyles();
            return;
        }

        if (tblUser.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a user record from the table to update.");
            return;
        }

        UserDto userDto = new UserDto(
                lblId.getText(),
                txtName.getText(),
                txtPassword.getText(),
                txtEmail.getText()
        );

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Update User");
        alert.setContentText("Are you sure you want to update this user?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userBO.updateUser(userDto);
                showAlert(Alert.AlertType.INFORMATION, "Update Successful", "User has been updated successfully!");
                clearFields();
            } catch (NotFoundException e) {
                showAlert(Alert.AlertType.ERROR, "Update Failed", e.getMessage());
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred during update: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void tableOnClick(MouseEvent mouseEvent) {
        UserDto userDto = tblUser.getSelectionModel().getSelectedItem();
        if (userDto != null) {
            lblId.setText(userDto.getId());
            txtName.setText(userDto.getUserName());
            txtPassword.setText(userDto.getPassword());
            txtEmail.setText(userDto.getEmail());
            resetValidationStyles();
            updateButtonStates();
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
        updateButtonStates();
    }

    public void txtPasswordChange(KeyEvent keyEvent) {
        String password = txtPassword.getText();
        boolean isValid = password.matches(passwordPattern);
        if (isValid) {
            txtPassword.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtPassword.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    public void txtEmailChange(KeyEvent keyEvent) {
        String email = txtEmail.getText();
        boolean isValid = email.matches(emailPattern);
        if (isValid) {
            txtEmail.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtEmail.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
        updateButtonStates();
    }

    private void applyValidationStyles() {
        txtNameChange(null);
        txtPasswordChange(null);
        txtEmailChange(null);
    }

    private void resetValidationStyles() {
        txtName.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtPassword.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        txtEmail.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");
    }
}