package com.trade.service;

import java.util.List;

import com.trade.domain.OrderType;
import com.trade.mapper.OrderDto;
import com.trade.modal.Bitcoin;
import com.trade.modal.Order;
import com.trade.modal.OrderItem;
import com.trade.modal.User;

public interface OrderService {

	OrderDto createOrder(User user, OrderItem orderItem, OrderType orderType);
	
	OrderDto getOrderById(Long orderId);
	
	List<OrderDto> getAllUserOrder(Long userId,OrderType orderType,String assetSymbol);
	
	Order processOrder(Bitcoin coin,double quantity,OrderType orderType,
			User user)throws Exception;
	
	Order buyAssest(Bitcoin coin, double quantity, User user) throws Exception; 

	Order sellAssest(Bitcoin coin, double quantity, User user) throws Exception;
}
