package lk.ijse.desktop.myfx.myfinalproject.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import lk.ijse.desktop.myfx.myfinalproject.Model.UserModel;

import java.io.IOException;
import java.sql.SQLException;

public class LoginPageController {

    @FXML
    private AnchorPane ancPage;

    @FXML
    private TextField txtPassword;

    @FXML
    private TextField txtUserName;

    @FXML
    void btnLoginOnAction(ActionEvent event) throws IOException {

        String userId = txtUserName.getText();
        String password = txtPassword.getText();

        if (userId.isEmpty() || password.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Pleace enter your user name and password").show();
            return;
        }

        try {
            UserModel userModel = new UserModel();
            boolean isValid = userModel.isValidUser(userId, password);

            if (isValid) {
                ancPage.getChildren().clear();
                Parent parent = FXMLLoader.load(getClass().getResource("/View/DashboardView.fxml"));
                ancPage.getChildren().add(parent);
            } else {
                new Alert(Alert.AlertType.ERROR, "Invalid User ID or Password").show();
                System.out.println("Invalid User ID or Password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Database error" + e.getMessage()).show();
        }
    }

    @FXML
    public void btnForgotPasswordOnAction(ActionEvent actionEvent) throws IOException {
        ancPage.getChildren().clear();
        Parent parent = FXMLLoader.load(getClass().getResource("/View/ForgotPasswordView.fxml"));
        ancPage.getChildren().add(parent);
    }
}