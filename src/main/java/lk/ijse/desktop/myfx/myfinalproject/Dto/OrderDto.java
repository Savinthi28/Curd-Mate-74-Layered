package lk.ijse.desktop.myfx.myfinalproject.Dto;

import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;

@Getter
@Setter
//@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDto {
    private String orderId;
    private String customerId;
    private LocalDate date;
    private int orderTotal;
    private ArrayList<OrderDetailsDto> cartList;


    public OrderDto(String id) {
        this.orderId = id;
    }

    public OrderDto(String orderId, String selectedCustomerId, LocalDate date, int orderTotal, ArrayList<OrderDetailsDto> cartList) {
        this.orderId = orderId;
        this.customerId = selectedCustomerId;
        this.date = date;
        this.orderTotal = orderTotal;
        this.cartList = cartList;
    }

    public OrderDto(String orderId, String customerId, LocalDate date) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.date = date;
    }
}