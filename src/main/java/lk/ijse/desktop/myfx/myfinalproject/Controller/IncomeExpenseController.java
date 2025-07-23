package lk.ijse.desktop.myfx.myfinalproject.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class IncomeExpenseController {

    @FXML
    private AnchorPane ancIncomeExpense;

    @FXML
    void onExpense(MouseEvent event) {
        navigateTo("/View/DailyExpenseView.fxml");
    }

    @FXML
    void onIncome(MouseEvent event) {
        navigateTo("/View/DailyIncomeView.fxml");
    }
    private void navigateTo(String path) {
        try {
            ancIncomeExpense.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(path));
            anchorPane.prefWidthProperty().bind(ancIncomeExpense.widthProperty());
            anchorPane.prefHeightProperty().bind(ancIncomeExpense.heightProperty());

            ancIncomeExpense.getChildren().add(anchorPane);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Something went wrong while loading the main container", ButtonType.OK).show();
            e.printStackTrace();
        }

    }
}
