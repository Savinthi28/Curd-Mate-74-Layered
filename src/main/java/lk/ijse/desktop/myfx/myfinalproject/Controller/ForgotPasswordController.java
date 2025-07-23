package lk.ijse.desktop.myfx.myfinalproject.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import lk.ijse.desktop.myfx.myfinalproject.Dto.UserDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.UserModel;
import lk.ijse.desktop.myfx.myfinalproject.Util.EmailSender;

import java.io.IOException;
import java.sql.SQLException;

public class ForgotPasswordController {

    @FXML
    private TextField txtEmail;

    @FXML
    private AnchorPane forgotPasswordPane;

    @FXML
    void btnSendPasswordOnAction(ActionEvent event) {
        String email = txtEmail.getText();

        if (email.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Pleace enter your email").show();
            return;
        }

        try {
            UserModel userModel = new UserModel();
            UserDto userDto = userModel.getUserByEmail(email);

            if (userDto != null) {
                String subject = "Password Reset By CURD MATE";
                String body = "Lovely" + userDto.getUserName() + ",\n\nYour Password " + userDto.getPassword() + "\n\nCongratulations,\nMyFinalProject ";

                boolean emailSent = EmailSender.sendEmail(email, subject, body);

                if (emailSent) {
                    new Alert(Alert.AlertType.INFORMATION, "Password Reset Link Sent").show();
                    backToLoginPage();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Something went wrong while sending the email.").show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "No user found with this email").show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Database error" + e.getMessage()).show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Page switching error" + e.getMessage()).show();
        }
    }

    @FXML
    void btnBackOnAction(ActionEvent event) throws IOException {
        backToLoginPage();
    }

    private void backToLoginPage() throws IOException {
        forgotPasswordPane.getChildren().clear();
        Parent parent = FXMLLoader.load(getClass().getResource("/View/LoginView.fxml"));
        forgotPasswordPane.getChildren().add(parent);
    }
}