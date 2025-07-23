package lk.ijse.desktop.myfx.myfinalproject.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class CollectionStorageQualitycheckController {

    @FXML
    private AnchorPane ancCollectionStorageQuality;

    @FXML
    void onMilkCollection(MouseEvent event) {
        navigateTo("/View/MilkCollectionView.fxml");
    }

    @FXML
    void onMilkStorage(MouseEvent event) {
        navigateTo("/View/MilkStorageView.fxml");
    }

    @FXML
    void onQualityCheck(MouseEvent event) {
        navigateTo("/View/QualityCheckView.fxml");
    }
    private void navigateTo(String path) {
        try {
            ancCollectionStorageQuality.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(path));
            anchorPane.prefWidthProperty().bind(ancCollectionStorageQuality.widthProperty());
            anchorPane.prefHeightProperty().bind(ancCollectionStorageQuality.heightProperty());

            ancCollectionStorageQuality.getChildren().add(anchorPane);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Something went wrong while loading the main container", ButtonType.OK).show();
            e.printStackTrace();
        }

    }

}
