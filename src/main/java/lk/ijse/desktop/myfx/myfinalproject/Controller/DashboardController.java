package lk.ijse.desktop.myfx.myfinalproject.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private AnchorPane ancMainContainer;

    @FXML private Button btnGoDashboardOnAction;
    @FXML private Button btnGoBuffaloOnAction;
    @FXML private Button btnGoIMilkOnAction;
    @FXML private Button btnGoCurdProductionOnAction;
    @FXML private Button btnGoSupplierOnAction;
    @FXML private Button btnGoOrderOnAction;
    @FXML private Button btnGoCustomerOnAction;
    @FXML private Button btnGoPaymentOnAction;
    @FXML private Button btnGoIncomeOnAction;
    @FXML private Button btnGoUserOnAction;
    @FXML private Button btnGoReportOnAction;
    @FXML private Button btnGoLogoutOnAction;

    private Button currentActiveButton;

    public AnchorPane getAncMainContainer() {
        return null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Dashboard load, I'm initializer");
        setActiveButton(btnGoDashboardOnAction);
        navigateTo("/View/DashboardOverView.fxml");
    }

    private void navigateTo(String path) {
        try {
            ancMainContainer.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(path));

            anchorPane.prefWidthProperty().bind(ancMainContainer.widthProperty());
            anchorPane.prefHeightProperty().bind(ancMainContainer.heightProperty());

            ancMainContainer.getChildren().add(anchorPane);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Something went wrong while loading the main container", ButtonType.OK).show();
            e.printStackTrace();
        }
    }

    private void setActiveButton(Button newActiveButton) {
        if (currentActiveButton != null && currentActiveButton != btnGoLogoutOnAction) {
            currentActiveButton.setStyle("-fx-background-color: #5d6d7e; -fx-background-radius: 8; -fx-text-fill: white; -fx-font-weight: bold;");
        }

        if (newActiveButton != btnGoLogoutOnAction) {
            newActiveButton.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-text-fill: #34495e; -fx-font-weight: bold;");
        }


        currentActiveButton = newActiveButton;
    }

    @FXML
    void btnGoDashboardOnAction(ActionEvent event) {
        setActiveButton((Button) event.getSource());
        navigateTo("/View/DashboardOverView.fxml");
    }

    @FXML
    void btnGoBuffaloOnAction(ActionEvent event) {
        setActiveButton((Button) event.getSource());
        navigateTo("/View/BuffaloView.fxml");
    }

    @FXML
    void btnGoCurdProductionOnAction(ActionEvent event) {
        setActiveButton((Button) event.getSource());
        navigateTo("/View/CurdProductionView.fxml");
    }

    @FXML
    void btnGoCustomerOnAction(ActionEvent event) {
        setActiveButton((Button) event.getSource());
        navigateTo("/View/CustomerView.fxml");
    }

    @FXML
    void btnGoIMilkOnAction(ActionEvent event) {
        setActiveButton((Button) event.getSource());
        navigateTo("/View/MilkCollectionView.fxml");
    }

    @FXML
    void btnGoIncomeOnAction(ActionEvent event) {
        setActiveButton((Button) event.getSource());
        navigateTo("/View/DailyIncomeView.fxml");
    }

    @FXML
    void btnGoOrderOnAction(ActionEvent event) {
        setActiveButton((Button) event.getSource());
        navigateTo("/View/OrderView.fxml");
    }

    @FXML
    void btnGoPaymentOnAction(ActionEvent event) {
        setActiveButton((Button) event.getSource());
        navigateTo("/View/PaymentView.fxml");
    }

    @FXML
    void btnGoReportOnAction(ActionEvent event) {
        setActiveButton((Button) event.getSource());
        navigateTo("/View/ReportsView.fxml");
    }

    @FXML
    void btnGoSupplierOnAction(ActionEvent event) {
        setActiveButton((Button) event.getSource());
        navigateTo("/View/SupplierView.fxml");
    }

    @FXML
    void btnGoUserOnAction(ActionEvent event) {
        setActiveButton((Button) event.getSource());
        navigateTo("/View/UserView.fxml");
    }

    public void btnGoLogoutOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ancMainContainer.getScene().getWindow();

        Parent root = FXMLLoader.load(getClass().getResource("/View/LoginView.fxml"));
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}