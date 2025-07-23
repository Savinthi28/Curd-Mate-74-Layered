package lk.ijse.desktop.myfx.myfinalproject.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class SupplierPotsInPotsPurRawMaterialController {

    @FXML
    private AnchorPane ancSupplier;

    @FXML
    void onPotsInventory(MouseEvent event) {
        navigateTo("/View/PotsInventoryView.fxml");
    }

    @FXML
    void onPotsPurchase(MouseEvent event) {
        navigateTo("/View/PotsPurchaseView.fxml");
    }

    @FXML
    void onRawMaterial(MouseEvent event) {
        navigateTo("/View/RawMaterialPurchaseView.fxml");
    }

    @FXML
    void onSupplier(MouseEvent event) {
        navigateTo("/View/SupplierView.fxml");
    }
    private void navigateTo(String path) {
        try {
            ancSupplier.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(path));
            anchorPane.prefWidthProperty().bind(ancSupplier.widthProperty());
            anchorPane.prefHeightProperty().bind(ancSupplier.heightProperty());

            ancSupplier.getChildren().add(anchorPane);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Something went wrong while loading the main container", ButtonType.OK).show();
            e.printStackTrace();
        }

    }
}
