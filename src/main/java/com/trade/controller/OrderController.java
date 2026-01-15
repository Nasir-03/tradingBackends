package com.trade.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trade.domain.OrderType;
import com.trade.extra.CreateOrderRequest;
import com.trade.mapper.OrderDto;
import com.trade.mapper.OrderMapper;
import com.trade.modal.Bitcoin;
import com.trade.modal.Order;
import com.trade.modal.User;
import com.trade.service.CoinService;
import com.trade.service.OrderService;
import com.trade.service.UserService;

@RestController
@RequestMapping("/order")
public class OrderController {

	private OrderService orderService;
	private UserService userService;
	private CoinService coinService;
	private OrderMapper orderMapper;
	
	@Autowired
	public OrderController(OrderService orderService, UserService userService,
			CoinService coinService,OrderMapper orderMapper) {
		this.orderService = orderService;
		this.userService = userService;
		this.coinService = coinService;
		this.orderMapper = orderMapper;
	}
	
//	@PostMapping("/payOrder")
//	public ResponseEntity<Order> payOrderPayment(
//	        @RequestHeader("Authorization") String jwt,
//	        @RequestBody CreateOrderRequest req
//	) throws Exception {
//
//	    User user = userService.getUserByJwt(jwt);
//
//	    // 🔥 GUARANTEE coin row exists FIRST
//	    Bitcoin coin = coinService.getcoinDetails(req.getCoinId());
//	 
//	    Order order = orderService.processOrder(coin, req.getQuantity(), req.getOrderType(), user);
//
//	    return ResponseEntity.ok(order);
//	}

	@PostMapping("/payOrder")
	public ResponseEntity<Order> payOrderPayment(
	        @RequestHeader("Authorization") String jwt,
	        @RequestBody CreateOrderRequest req
	) throws Exception {

	    User user = userService.getUserByJwt(jwt);

	    // USE ONLY DB COIN
	    Bitcoin coin = coinService.getCoinFromDB(req.getCoinId());

	    Order order = orderService.processOrder(coin, req.getQuantity(), req.getOrderType(), user);

	    return ResponseEntity.ok(order);
	}

	
	@GetMapping("/getOrderById/{orderId}")
	public ResponseEntity<Order> getOrderById(
			@RequestHeader("Authorization")String jwt,
			@PathVariable Long orderId
			) throws Exception{
		
		User user = userService.getUserByJwt(jwt);
		
		OrderDto order = orderService.getOrderById(orderId);
		Order orders = orderMapper.toEntity(order);
		
		if (order.getUser().getId().equals(user.getId())) {
			return ResponseEntity.ok(orders);
		}else {
			throw new RuntimeException("You dont have access");
		}
	}

	@GetMapping("/getAllOrder")
	public ResponseEntity<List<OrderDto>> getAllOrderFromUser(
			@RequestHeader("Authorization") String jwt,
			@RequestParam(required = false)OrderType order_type,
			@RequestParam(required = false)String asset_symbol
			) throws Exception{
		
		Long userId = userService.getUserByJwt(jwt).getId();
		
		List<OrderDto> userOrder = orderService.getAllUserOrder(userId, order_type, asset_symbol);
	
	   return ResponseEntity.ok(userOrder);
	}
	
}
