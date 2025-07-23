package lk.ijse.desktop.myfx.myfinalproject.Model;

import lk.ijse.desktop.myfx.myfinalproject.Dto.OrderDetailsDto;
import lk.ijse.desktop.myfx.myfinalproject.Util.CrudUtil;

import java.sql.SQLException;
import java.util.ArrayList;

public class OrderDetailsModel {
    public boolean saveOrderDetailsList(ArrayList<OrderDetailsDto> cartList) throws SQLException {
        for (OrderDetailsDto orderDetailsDto : cartList) {
            boolean isDetailsSaved = saveOrderDetails(orderDetailsDto);
            if (!isDetailsSaved) {
                return false;
            }
            boolean isUpdate = CurdProductionModel.reduceQty(orderDetailsDto);
            if (!isUpdate) {
                return false;
            }
        }
        return true;
    }

    private boolean saveOrderDetails(OrderDetailsDto orderDetailsDto) throws SQLException {
        return CrudUtil.execute("insert into Order_Details values (?,?,?,?)",
         orderDetailsDto.getOrderId(),
        orderDetailsDto.getProductionId(),
        orderDetailsDto.getQuantity(),
        orderDetailsDto.getUnitPrice());
    }
}
