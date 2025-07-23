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

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
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

    private final String namePattern = "^[a-zA-Z0-9]{3,20}$";
    private final String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,20}$";
    private final String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFields();
    }

    @FXML
    public void btnDeleteOnAction(ActionEvent event) {
        String id = lblId.getText();

        if (id == null || id.isEmpty() || id.equals("Auto Generated")) {
            new Alert(Alert.AlertType.WARNING, "Please select a user record to delete from the table.").show();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete User");
        alert.setContentText("Are you sure you want to delete this user?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = new UserModel().deleteUser(new UserDto(id, null, null, null));
                if (isDeleted) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "User Deleted Successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete user.").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "An error occurred during deletion: " + e.getMessage()).show();
            }
        }
    }

    private void loadNextId() throws SQLException {
        UserModel userModel = new UserModel();
        String id = userModel.getNextId();
        lblId.setText(id);
    }

    @FXML
    public void btnSaveOnAction(ActionEvent event) {

        boolean isValidName = txtName.getText().matches(namePattern);
        boolean isValidPassword = txtPassword.getText().matches(passwordPattern);
        boolean isValidEmail = txtEmail.getText().matches(emailPattern);

        if (isValidName && isValidPassword && isValidEmail) {
            UserDto userDto = new UserDto(lblId.getText(), txtName.getText(), txtPassword.getText(), txtEmail.getText());

            try {
                UserModel userModel = new UserModel();
                boolean isSaved = userModel.saveUser(userDto);
                if (isSaved) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "User has been saved successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save user.").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "An error occurred during saving: " + e.getMessage()).show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly:\n" +
                    "- Username: 3-20 alphanumeric characters\n" +
                    "- Password: 8-20 characters, at least one digit, one lowercase, one uppercase, one special character\n" +
                    "- Email: Valid email format").show();
            applyValidationStyles();
        }
    }

    private void clearFields() throws SQLException {
        lblId.setText("");
        txtName.setText("");
        txtPassword.setText("");
        txtEmail.setText("");
        resetValidationStyles();

        loadNextId();
        loadTable();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadNextId();
            setupTableColumns();
            clearFields();
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing controller: " + e.getMessage(), e);
        }
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    private void loadTable() {
        try {
            UserModel userModel = new UserModel();
            ArrayList<UserDto> userDtos = userModel.viewAllUser();
            if (userDtos != null) {
                ObservableList<UserDto> observableList = FXCollections.observableArrayList(userDtos);
                tblUser.setItems(observableList);
            } else {
                tblUser.setItems(FXCollections.emptyObservableList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading user data into table.").show();
        }
    }

    @FXML
    public void btnUpdateOnAction(ActionEvent event) {

        boolean isValidName = txtName.getText().matches(namePattern);
        boolean isValidPassword = txtPassword.getText().matches(passwordPattern);
        boolean isValidEmail = txtEmail.getText().matches(emailPattern);

        if (isValidName && isValidPassword && isValidEmail) {
            UserDto userDto = new UserDto(lblId.getText(), txtName.getText(), txtPassword.getText(), txtEmail.getText());

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Update User");
            alert.setContentText("Are you sure you want to update this user?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    boolean isUpdated = UserModel.updateUser(userDto);
                    if (isUpdated) {
                        clearFields();
                        new Alert(Alert.AlertType.INFORMATION, "User has been updated successfully!").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to update user.").show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "An error occurred during update: " + e.getMessage()).show();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please ensure all fields are filled correctly:\n" +
                    "- Username: 3-20 alphanumeric characters\n" +
                    "- Password: 8-20 characters, at least one digit, one lowercase, one uppercase, one special character\n" +
                    "- Email: Valid email format").show();
            applyValidationStyles();
        }
    }

    public void tableOnClick(MouseEvent mouseEvent) {
        UserDto userDto = tblUser.getSelectionModel().getSelectedItem();
        if (userDto != null) {
            lblId.setText(String.valueOf(userDto.getId()));
            txtName.setText(userDto.getUserName());
            txtPassword.setText(userDto.getPassword());
            txtEmail.setText(userDto.getEmail());
            resetValidationStyles();
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

    public void txtPasswordChange(KeyEvent keyEvent) {
        String password = txtPassword.getText();
        boolean isValid = password.matches(passwordPattern);
        if (isValid) {
            txtPassword.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtPassword.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
    }

    public void txtEmailChange(KeyEvent keyEvent) {
        String email = txtEmail.getText();
        boolean isValid = email.matches(emailPattern);
        if (isValid) {
            txtEmail.setStyle("-fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 5;");
        } else {
            txtEmail.setStyle("-fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        }
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