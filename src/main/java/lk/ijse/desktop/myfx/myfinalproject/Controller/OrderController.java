package lk.ijse.desktop.myfx.myfinalproject.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import lk.ijse.desktop.myfx.myfinalproject.Dto.CurdProductionDto;
import lk.ijse.desktop.myfx.myfinalproject.Dto.CustomerDto;
import lk.ijse.desktop.myfx.myfinalproject.Dto.OrderDetailsDto;
import lk.ijse.desktop.myfx.myfinalproject.Dto.OrderDto;
import lk.ijse.desktop.myfx.myfinalproject.Dto.TM.CartTM;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOFactory;
import lk.ijse.desktop.myfx.myfinalproject.bo.BOTypes;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.CurdProductionBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.CustomerBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.OrderBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;


public class OrderController implements Initializable {

    private final OrderBO orderBO = BOFactory.getInstance().getBO(BOTypes.ORDER);
    private final CustomerBO customerBO = BOFactory.getInstance().getBO(BOTypes.CUSTOMER);
    private final CurdProductionBO curdProductionBO = BOFactory.getInstance().getBO(BOTypes.CURD_PRODUCTION);

    @FXML private TableColumn<CartTM, String> colAction;
    @FXML private TableColumn<CartTM, String> colItemId;
    @FXML private TableColumn<CartTM, String> colItemName;
    @FXML private TableColumn<CartTM, Integer> colQty;
    @FXML private TableColumn<CartTM, Double> colTotalPrice;
    @FXML private TableColumn<CartTM, Double> colUnitPrice;
    @FXML private ComboBox<String> comCustomerID;
    @FXML private Label lblCustomerName;
    @FXML private Label lblID;
    @FXML private Label lblItemName;
    @FXML private TextField txtQuantity;
    @FXML private Label lbl_Order_Date;
    @FXML private Label lblItemQty;
    @FXML private ComboBox<String> comProductionId;
    @FXML private TableView<CartTM> table;
    @FXML private TextField txtUnitPrice;

    private final ObservableList<CartTM> cartData = FXCollections.observableArrayList();
    private int currentItemQtyOnHand = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCellValueFactories();
        table.setItems(cartData);

        try {
            lbl_Order_Date.setText(LocalDate.now().toString());
            loadCustomerId();
            loadNextOrderId();
            loadProductionIds();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Initialization error: " + e.getMessage()).show();
            throw new RuntimeException("Failed to initialize order controller: " + e.getMessage(), e);
        }
    }

    private void setCellValueFactories() {
        colItemId.setCellValueFactory(new PropertyValueFactory<>("productionId"));
        colItemName.setCellValueFactory(new PropertyValueFactory<>("potsSize"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colTotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("btnRemove"));
    }

    private void loadProductionIds() throws SQLException {
        List<String> productionIdList = curdProductionBO.getAllCurdProductionIds();
        ObservableList<String> ProductionIds = FXCollections.observableArrayList(productionIdList);
        comProductionId.setItems(ProductionIds);
    }

    private void loadCustomerId() throws SQLException {
        List<CustomerDto> customerList = customerBO.getAllCustomer();
        List<String> customerIds = new ArrayList<>();
        for (CustomerDto customer : customerList) {
            customerIds.add(customer.getCustomerId());
        }
        ObservableList<String> observableList = FXCollections.observableArrayList(customerIds);
        comCustomerID.setItems(observableList);
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    private void loadNextOrderId() throws SQLException {
        String id = orderBO.getNextOrderId();
        lblID.setText(id);
    }

    @FXML
    public void comCustomerOnAction(ActionEvent actionEvent) {
        String selectedCustomerId = comCustomerID.getSelectionModel().getSelectedItem();
        if (selectedCustomerId != null && !selectedCustomerId.isEmpty()) {
            try {
                String customerName = customerBO.findCustomerNameById(selectedCustomerId);
                lblCustomerName.setText(customerName);
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Error fetching customer details: " + e.getMessage()).show();
                e.printStackTrace();
            } catch (NotFoundException e) {
                lblCustomerName.setText("Customer not found.");
                new Alert(Alert.AlertType.WARNING, e.getMessage()).show();
            }
        } else {
            lblCustomerName.setText("");
        }
    }

    @FXML
    public void comProductionIdOnAction(ActionEvent actionEvent) {
        String selectedItemId = comProductionId.getValue();
        if (selectedItemId != null && !selectedItemId.isEmpty()) {
            try {
                CurdProductionDto product = curdProductionBO.findCurdProductionById(selectedItemId);
                if (product != null) {
                    lblItemName.setText(String.valueOf(product.getPotsSize()));
                    lblItemQty.setText(String.valueOf(product.getQuantity()));
                    txtUnitPrice.setText(String.valueOf(product.getPotsSize() * 10.0));
                    currentItemQtyOnHand = product.getQuantity();
                    int qtyInCartForThisProduct = getTotalQuantityInCartForProduct(selectedItemId);
                    lblItemQty.setText(String.valueOf(currentItemQtyOnHand - qtyInCartForThisProduct));

                } else {
                    lblItemName.setText("");
                    lblItemQty.setText("");
                    txtUnitPrice.setText("");
                    currentItemQtyOnHand = 0;
                    new Alert(Alert.AlertType.WARNING, "Production item details not found.").show();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Error fetching item details: " + e.getMessage()).show();
                e.printStackTrace();
            }
        } else {
            lblItemName.setText("");
            lblItemQty.setText("");
            txtUnitPrice.setText("");
            currentItemQtyOnHand = 0;
        }
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
            loadNextOrderId();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error generating next order ID: " + e.getMessage()).show();
            e.printStackTrace();
        }
        lbl_Order_Date.setText(LocalDate.now().toString());
    }

    @FXML
    public void btnAddToCartOnAction(ActionEvent actionEvent) {
        try {
            String selectedItemId = comProductionId.getValue();
            String cartQtyString = txtQuantity.getText();

            if (selectedItemId == null || selectedItemId.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please select a production item.").show();
                return;
            }
            if (!cartQtyString.matches("^[0-9]+$") || cartQtyString.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please enter a valid quantity (numbers only).").show();
                return;
            }
            int cartQty = Integer.parseInt(cartQtyString);

            if (cartQty <= 0) {
                new Alert(Alert.AlertType.WARNING, "Quantity must be positive.").show();
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

            Optional<CartTM> existingCartItem = cartData.stream()
                    .filter(item -> item.getProductionId().equals(selectedItemId))
                    .findFirst();

            if (existingCartItem.isPresent()) {
                CartTM item = existingCartItem.get();
                int newQty = item.getQty() + cartQty;

                int stockAfterAddingBack = currentAvailableQty + item.getQty();

                if (newQty > stockAfterAddingBack) {
                    new Alert(Alert.AlertType.WARNING, "Cannot add more than available stock. Available: " + (stockAfterAddingBack - item.getQty())).show();
                    return;
                }
                item.setQty(newQty);
                item.setTotalPrice(newQty * unitPrice);
                lblItemQty.setText(String.valueOf(stockAfterAddingBack - newQty));
            } else {
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
                    int removedQty = newItem.getQty();
                    try {
                        CurdProductionDto product = curdProductionBO.findCurdProductionById(newItem.getProductionId());
                        if (product != null) {
                            int originalProductQty = product.getQuantity();
                            if (comProductionId.getValue() != null && comProductionId.getValue().equals(newItem.getProductionId())) {
                                lblItemQty.setText(String.valueOf(originalProductQty - getTotalQuantityInCartForProduct(newItem.getProductionId()) + removedQty));
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    cartData.remove(newItem);
                    table.refresh();
                });
                lblItemQty.setText(String.valueOf(currentAvailableQty - cartQty));
                cartData.add(newItem);
            }

            txtQuantity.clear();
            table.refresh();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Something went wrong adding to cart: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    private int getTotalQuantityInCartForProduct(String productId) {
        int totalQty = 0;
        for (CartTM item : cartData) {
            if (item.getProductionId().equals(productId)) {
                totalQty += item.getQty();
            }
        }
        return totalQty;
    }


    @FXML
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
        LocalDate date = LocalDate.parse(lbl_Order_Date.getText());

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
            boolean isPlaced = orderBO.placeOrder(orderDto);
            if (isPlaced) {
                new Alert(Alert.AlertType.INFORMATION, "Order Placed Successfully!").show();
                clearFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Order Not Placed. Please check details.").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Database error occurred while placing the order: " + e.getMessage()).show();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "An unexpected error occurred while placing the order: " + e.getMessage()).show();
        }
    }

    @FXML
    public void btnResetOnAction(ActionEvent actionEvent) {
        clearFields();
    }

    @FXML
    public void tableOnClick(MouseEvent mouseEvent) {
    }
}