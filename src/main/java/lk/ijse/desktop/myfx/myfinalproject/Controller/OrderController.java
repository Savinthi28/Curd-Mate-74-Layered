package lk.ijse.desktop.myfx.myfinalproject.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import lk.ijse.desktop.myfx.myfinalproject.DBConnection.DBConnection;
import lk.ijse.desktop.myfx.myfinalproject.Dto.CurdProductionDto;
import lk.ijse.desktop.myfx.myfinalproject.Dto.CustomerDto;
import lk.ijse.desktop.myfx.myfinalproject.Dto.OrderDetailsDto;
import lk.ijse.desktop.myfx.myfinalproject.Dto.OrderDto;
import lk.ijse.desktop.myfx.myfinalproject.Dto.TM.CartTM;
import lk.ijse.desktop.myfx.myfinalproject.Model.CurdProductionModel;
import lk.ijse.desktop.myfx.myfinalproject.Model.CustomerModel;
import lk.ijse.desktop.myfx.myfinalproject.Model.OrderModel;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;



public class OrderController implements Initializable {
    @FXML
    private TableColumn<CartTM, String> colAction;

    @FXML
    private TableColumn<CartTM, String> colItemId;

    @FXML
    private TableColumn<CartTM, String> colItemName;

    @FXML
    private TableColumn<CartTM, Integer> colQty;

    @FXML
    private TableColumn<CartTM, Double> colTotalPrice;

    @FXML
    private TableColumn<CartTM, Double> colUnitPrice;

    @FXML
    private ComboBox<String> comCustomerID;

    @FXML
    private Label lblCustomerName;

    @FXML
    private Label lblCustomerName1;

    @FXML
    private TextField txtUnitPrice;

    @FXML
    private Label lblID;

    @FXML
    private Label lblItemName;

    @FXML
    private TextField txtQuantity;

    @FXML
    private Label lbl_Order_Date;

    @FXML
    private Label lblItemQty;

    @FXML
    private ComboBox<String> comProductionId;

    @FXML
    private TableView<CartTM> table;

    private final ObservableList<CartTM> cartData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colItemId.setCellValueFactory(new PropertyValueFactory<>("productionId"));
        colItemName.setCellValueFactory(new PropertyValueFactory<>("potsSize"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colTotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("btnRemove"));
        table.setItems(cartData);

        try {
            lbl_Order_Date.setText(LocalDate.now().toString());
            loadCustomerId();
            loadNextId();
            loadProductionIds();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize order controller: " + e.getMessage(), e);
        }
    }

    private void loadProductionIds() throws SQLException {
        ArrayList<String> productionIdList = CurdProductionModel.getAllProductionIds();
        ObservableList<String> ProductionIds = FXCollections.observableArrayList(productionIdList);

        comProductionId.setItems(ProductionIds);
    }

    private void loadCustomerId() throws SQLException {
        ArrayList<String> customerId = OrderModel.getAllCustomerId();
        ObservableList<String> observableList = FXCollections.observableArrayList(customerId);

        comCustomerID.setItems(observableList);
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    private void loadNextId() throws SQLException {
        OrderModel orderModel = new OrderModel();
        String id = orderModel.getNextId();
        lblID.setText(id);
    }

    public void comCustomerOnAction(ActionEvent actionEvent) throws SQLException {
        String selectedCustomerId = comCustomerID.getSelectionModel().getSelectedItem();
        if (selectedCustomerId != null && !selectedCustomerId.isEmpty()) {
            System.out.println(selectedCustomerId);
            String name = CustomerModel.findNameById(selectedCustomerId);
            System.out.println(name);
            lblCustomerName.setText(name);
        } else {
            lblCustomerName.setText("");
        }
    }

    public void comPotsSizeOnAction(ActionEvent actionEvent) {

        String selectedCustomerId = comCustomerID.getSelectionModel().getSelectedItem();
        System.out.println(selectedCustomerId);
    }

    private void clearFields() {
        comCustomerID.getSelectionModel().clearSelection();
        comProductionId.getSelectionModel().clearSelection();
        lblCustomerName.setText("");
        lblItemName.setText("");
        lblItemQty.setText("");
        txtUnitPrice.setText("");
        txtQuantity.setText("");
        cartData.clear();
        try {
            loadNextId();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error generating next order ID: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    public void btnAddToCartOnAction(ActionEvent actionEvent) {
        try {
            String selectedItemId = comProductionId.getValue();
            String cartQtyString = txtQuantity.getText();

            if (selectedItemId == null || selectedItemId.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please select a production item").show();
                return;
            }
            if (!cartQtyString.matches("^[0-9]+$") || cartQtyString.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please enter a valid quantity (numbers only)").show();
                return;
            }
            int cartQty = Integer.parseInt(cartQtyString);

            if (cartQty <= 0) {
                new Alert(Alert.AlertType.WARNING, "Quantity must be positive").show();
                return;
            }

            int currentAvailableQty;
            try {
                currentAvailableQty = Integer.parseInt(lblItemQty.getText());
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Available quantity is not a valid number. Please re-select item.").show();
                return;
            }

            String itemName = lblItemName.getText();
            double unitPrice;
            try {
                unitPrice = Double.parseDouble(txtUnitPrice.getText());
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Unit price is not a valid number. Please re-select item.").show();
                return;
            }

            double total = unitPrice * cartQty;

            for (CartTM existingItem : cartData) {
                if (existingItem.getProductionId().equals(selectedItemId)) {
                    int newQty = existingItem.getQty() + cartQty;

                    int originalStock = currentAvailableQty + existingItem.getQty();

                    if (newQty > originalStock) {
                        new Alert(Alert.AlertType.WARNING, "Cannot add more than available stock. Available: " +
                                (originalStock - existingItem.getQty())).show();
                        return;
                    }
                    existingItem.setQty(newQty);
                    existingItem.setTotalPrice(newQty * unitPrice);
                    lblItemQty.setText(String.valueOf(originalStock - newQty));
                    txtQuantity.clear();
                    table.refresh();
                    return;
                }
            }
            if (cartQty > currentAvailableQty) {
                new Alert(Alert.AlertType.WARNING, "Cannot add more than available stock. Available: " + currentAvailableQty).show();
                return;
            }

            Button removeBtn = new Button("Remove");
            CartTM newItem = new CartTM(
                    selectedItemId,
                    itemName,
                    cartQty,
                    unitPrice,
                    total,
                    removeBtn
            );

            removeBtn.setOnAction((ActionEvent event) -> {

                int currentStockDisplay = Integer.parseInt(lblItemQty.getText());
                lblItemQty.setText(String.valueOf(currentStockDisplay + newItem.getQty()));
                cartData.remove(newItem);
                table.refresh();
            });

            lblItemQty.setText(String.valueOf(currentAvailableQty - cartQty));
            cartData.add(newItem);
            txtQuantity.clear();
            table.refresh();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Something went wrong adding to cart: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    public void btnPlaceOrder0nAction(ActionEvent actionEvent) {
        if (table.getItems().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please add items to the cart before placing an order.").show();
            return;
        }
        if (comCustomerID.getValue() == null || comCustomerID.getValue().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please select a customer ID.").show();
            return;
        }

        String selectedCustomerId = comCustomerID.getValue();
        String orderId = lblID.getText();
        Date date = Date.valueOf(lbl_Order_Date.getText());

        ArrayList<OrderDetailsDto> cartList = new ArrayList<>();
        int orderTotalQuantity = 0;

        for (CartTM cartTM : cartData) {
            orderTotalQuantity += cartTM.getQty();
            OrderDetailsDto orderDetailsDto = new OrderDetailsDto(
                    orderId,
                    cartTM.getProductionId(),
                    cartTM.getQty(),
                    cartTM.getUnitPrice(),
                    cartTM.getTotalPrice()
            );
            cartList.add(orderDetailsDto);
        }

        OrderDto orderDto = new OrderDto(
                orderId,
                selectedCustomerId,
                date,
                orderTotalQuantity,
                cartList
        );
        try {
            boolean isPlaced = OrderModel.placeOrder(orderDto);
            if (isPlaced) {
                new Alert(Alert.AlertType.INFORMATION, "Order Placed Successfully!").show();
                clearFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Order Not Placed. Please check details.").show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "An error occurred while placing the order: " + e.getMessage()).show();
        }
    }

    public void btnResetOnAction(ActionEvent actionEvent) {
        clearFields();
    }

    public void tableOnClick(MouseEvent mouseEvent) {

    }

//    public void comProductionIdOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
//
//        String selectedItemId = comProductionId.getValue();
//        if (selectedItemId != null && !selectedItemId.isEmpty()) {
//            CurdProductionDto product = CurdProductionModel.findById(selectedItemId);
//            if (product != null) {
//                lblItemName.setText(String.valueOf(product.getPotsSize()));
//                lblItemQty.setText(String.valueOf(product.getQuantity()));
//
//            } else {
//                lblItemName.setText("");
//                lblItemQty.setText("");
//                txtUnitPrice.setText("");
//                new Alert(Alert.AlertType.WARNING, "Production item details not found.").show();
//            }
//        } else {
//            lblItemName.setText("");
//            lblItemQty.setText("");
//            txtUnitPrice.setText("");
//        }
//    }
}