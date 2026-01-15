package com.trade.mapper;

import org.springframework.stereotype.Component;

import com.trade.modal.Order;

@Component
public class OrderMapper {

	public OrderDto toDTO(Order order) {
        return new OrderDto(
                order.getUser(),
                order.getOrderType(),
                order.getPrice(),
                order.getTimeStamp(),
                order.getStatus(),
                order.getItem()
        );
    }
	
	public Order toEntity(OrderDto dto) {
	    Order order = new Order();
	    order.setUser(dto.getUser());
	    order.setOrderType(dto.getOrderType());
	    order.setPrice(dto.getPrice());
	    order.setTimeStamp(dto.getTimeStamp());
	    order.setStatus(dto.getStatus());
	    order.setItem(dto.getItem());
	    return order;
	}

}
