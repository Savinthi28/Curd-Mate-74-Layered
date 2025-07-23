package lk.ijse.desktop.myfx.myfinalproject.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class CurdProductionStockController {

    @FXML
    private AnchorPane ancCurdProductionStock;

    @FXML
    void onCurdProduction(MouseEvent event) {
        navigateTo("/View/CurdProductionView.fxml");
    }

    @FXML
    void onStock(MouseEvent event) {
        navigateTo("/View/StockView.fxml");
    }

    private void navigateTo(String path) {
        try {
            ancCurdProductionStock.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(path));
            anchorPane.prefWidthProperty().bind(ancCurdProductionStock.widthProperty());
            anchorPane.prefHeightProperty().bind(ancCurdProductionStock.heightProperty());

            ancCurdProductionStock.getChildren().add(anchorPane);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Something went wrong while loading the main container", ButtonType.OK).show();
            e.printStackTrace();
        }

    }
}
