package lk.ijse.desktop.myfx.myfinalproject.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import lk.ijse.desktop.myfx.myfinalproject.Dto.MilkCollectionDto;
import lk.ijse.desktop.myfx.myfinalproject.Dto.StockDto;
import lk.ijse.desktop.myfx.myfinalproject.Dto.OrderDto;
import lk.ijse.desktop.myfx.myfinalproject.Dto.DailyIncomeDto;
import lk.ijse.desktop.myfx.myfinalproject.Model.MilkCollectionModel;
import lk.ijse.desktop.myfx.myfinalproject.Model.StockModel;
import lk.ijse.desktop.myfx.myfinalproject.Model.OrderModel;
import lk.ijse.desktop.myfx.myfinalproject.Model.DailyIncomeModel;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DashboardOverController implements Initializable {

    @FXML
    private Label lblMilkCollectionTotal;
    @FXML
    private Label lblCurrentMilkStock;
    @FXML
    private Label lblPendingDeliveries;
    @FXML
    private Label lblTodaySalesRevenue;

    private MilkCollectionModel milkCollectionModel;
    private StockModel stockModel;
    private OrderModel orderModel;
    private DailyIncomeModel dailyIncomeModel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        milkCollectionModel = new MilkCollectionModel();
        stockModel = new StockModel();
        orderModel = new OrderModel();
        dailyIncomeModel = new DailyIncomeModel();

        setTotalMilkCollection();
        setTotalCurrentMilkStock();
        setTotalPendingDeliveries();
        setTotalTodaySalesRevenue();
    }

    private void setTotalMilkCollection() {
        try {
            ArrayList<MilkCollectionDto> allCollections = milkCollectionModel.viewAllMilkCollection();
            double totalToday = 0;
            LocalDate today = LocalDate.now();

            for (MilkCollectionDto dto : allCollections) {
                LocalDate collectionDate = LocalDate.parse(dto.getDate());

                if (collectionDate.isEqual(today)) {
                    totalToday += dto.getQuantity();
                }
            }
            lblMilkCollectionTotal.setText(String.format("%.2f Liters", totalToday));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            lblMilkCollectionTotal.setText("Error");
        }
    }

    private void setTotalCurrentMilkStock() {
        try {
            ArrayList<StockDto> allStocks = stockModel.viewAllStock();
            int totalStockQuantity = 0;

            for (StockDto dto : allStocks) {
                totalStockQuantity += dto.getQuantity();
            }
            lblCurrentMilkStock.setText(String.format("%d Liters", totalStockQuantity));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            lblCurrentMilkStock.setText("Error");
        }
    }

    private void setTotalPendingDeliveries() {
        try {
            ArrayList<OrderDto> allOrders = orderModel.viewAllOrder();
            lblPendingDeliveries.setText(String.valueOf(allOrders.size()));
        } catch (SQLException e) {
            e.printStackTrace();
            lblPendingDeliveries.setText("Error");
        }
    }

    private void setTotalTodaySalesRevenue() {
        try {
            ArrayList<DailyIncomeDto> allIncomes = dailyIncomeModel.viewDailyIncome();
            double totalTodayRevenue = 0;
            LocalDate today = LocalDate.now();

            for (DailyIncomeDto dto : allIncomes) {
                LocalDate incomeDate = LocalDate.parse(dto.getDate());

                if (incomeDate.isEqual(today)) {
                    totalTodayRevenue += dto.getAmount();
                }
            }
            lblTodaySalesRevenue.setText(String.format("LKR %.2f", totalTodayRevenue));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            lblTodaySalesRevenue.setText("Error");
        }
    }
}