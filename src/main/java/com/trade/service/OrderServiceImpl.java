package com.trade.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trade.domain.OrderStatus;
import com.trade.domain.OrderType;
import com.trade.extra.SequenceGeneratorService;
import com.trade.mapper.AssetMapper;
import com.trade.mapper.OrderDto;
import com.trade.mapper.OrderMapper;
import com.trade.modal.Asset;
import com.trade.modal.Bitcoin;
import com.trade.modal.Order;
import com.trade.modal.OrderItem;
import com.trade.modal.User;
import com.trade.repositoy.CoinRepository;
import com.trade.repositoy.OrderItemRepository;
import com.trade.repositoy.OrderRepository;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final WalletService walletService;
    private final AssetService assetService;
    private final OrderMapper orderMapper;
    private final AssetMapper assetMapper;
    private CoinServiceImpl coinServiceImpl;
    private SequenceGeneratorService sequenceGeneratorService;
    
    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            WalletService walletService,
                            AssetService assetService,
                            OrderMapper orderMapper,AssetMapper assetMapper,
                            CoinServiceImpl coinServiceImpl,
                            SequenceGeneratorService sequenceGeneratorService) {
    	
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.walletService = walletService;
        this.assetService = assetService;
        this.orderMapper = orderMapper;
        this.assetMapper = assetMapper;
        this.coinServiceImpl = coinServiceImpl;
        this.sequenceGeneratorService = sequenceGeneratorService;
    }

    // ---------- PUBLIC API ----------

    @Override
    public OrderDto getOrderById(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) throw new RuntimeException("Order not found with id: " + orderId);
        return orderMapper.toDTO(orderOpt.get());
    }

    @Override
    public List<OrderDto> getAllUserOrder(Long userId, OrderType orderType, String assetSymbol) {
//        List<Order> orders = orderRepository.findByUserId(userId);
    	List<Order> orders = orderRepository.findByUser_Id(userId);

        // return empty list if no orders found (caller can decide what to do)
        return orders.stream()
                .filter(o -> orderType == null || o.getOrderType() == orderType)
                .filter(o -> assetSymbol == null
                        || (o.getItem() != null
                        && o.getItem().getCoin() != null
                        && assetSymbol.equalsIgnoreCase(o.getItem().getCoin().getSymbol())))
                .map(orderMapper::toDTO)
                .toList();
    }

    /**
     * Creates an Order entity (not the full buy/sell flow).
     * This method is kept for reuse but is private below.
     */
    private Order createOrderEntity(User user, OrderItem orderItem, OrderType orderType, BigDecimal totalPrice) {
        Order order = new Order();
        order.setId(sequenceGeneratorService.generateSequence("order_sequence"));
        order.setUser(user);
        order.setItem(orderItem); // we will set the bidirectional link after persisting
        order.setOrderType(orderType);
        order.setPrice(totalPrice);
        order.setTimeStamp(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        return orderRepository.save(order);
    }

    // ---------- ORDER ITEM helper ----------
    private OrderItem persistOrderItem(OrderItem orderItem) {
        orderItem.setId(sequenceGeneratorService.generateSequence("order_item_sequence"));
        return orderItemRepository.save(orderItem);
    }

    private BigDecimal computeTotalPrice(BigDecimal unitPrice, double quantity) {
        BigDecimal qtyBd = BigDecimal.valueOf(quantity);
        return unitPrice.multiply(qtyBd);
    }

    // ---------- CREATE ORDER (used by controllers / other services) ----------
    @Override
    public OrderDto createOrder(User user, OrderItem orderItem, OrderType orderType) {
        // convert coin price to BigDecimal safely
        BigDecimal unitPrice = BigDecimal.valueOf(orderItem.getCoin().getCurrentPrice());
        BigDecimal total = computeTotalPrice(unitPrice, orderItem.getQuantity());

        // persist OrderItem first (without order reference)
        OrderItem savedItem = persistOrderItem(orderItem);

        // create and save Order
        Order order = createOrderEntity(user, savedItem, orderType, total);

        // link saved item -> order and persist the item again (so FK is set)
        savedItem.setOrder(order);
        orderItemRepository.save(savedItem);

        // return DTO
        return orderMapper.toDTO(order);
    }

    // ---------- BUY FLOW ----------
    @Transactional
    @Override
    public Order buyAssest(Bitcoin coin, double quantity, User user) throws Exception {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be > 0");
        
        Bitcoin dbCoin = coinServiceImpl.ensureCoinExists(coin);

        
        BigDecimal unitPrice = BigDecimal.valueOf(dbCoin.getCurrentPrice());
        BigDecimal total = computeTotalPrice(unitPrice, quantity);

        // create order item and persist
//        OrderItem orderItem = new OrderItem();
//        orderItem.setCoin(dbCoin);
//        orderItem.setQuantity(quantity);
//        orderItem.setBuyPrice(unitPrice.doubleValue()); // preserve legacy field type if it's double
//        orderItem.setSellPrice(0d);
//        orderItem = persistOrderItem(orderItem);
//
//        // create and persist order
//        Order order = createOrderEntity(user, orderItem, OrderType.BUY, total);
//
//        // link item -> order and persist
//        orderItem.setOrder(order);
//        orderItemRepository.save(orderItem);
        
        
        OrderItem orderItem = new OrderItem();
        orderItem.setId(sequenceGeneratorService.generateSequence("order_item_sequence"));
        orderItem.setCoin(dbCoin);
        orderItem.setQuantity(quantity);
        orderItem.setBuyPrice(unitPrice.doubleValue());
        orderItem.setSellPrice(0d);

        Order order = createOrderEntity(user, orderItem, OrderType.BUY, total);
        orderItem.setOrder(order);

        orderItemRepository.save(orderItem);


        // charge the user's wallet (walletService.payOrderPayment should deduct for BUY)
        // if this throws, transaction will rollback
        walletService.payOrderPayment(order, user);

        // mark success and save
        order.setStatus(OrderStatus.SUCCESS);
        order = orderRepository.save(order);

        // update user's asset holdings
        Asset existing = assetService.findAssetByUserIdAndCoinId(user.getId(), dbCoin.getId());
        if (existing == null) {
            assetService.createAsset(user, dbCoin, quantity);
        } else {
            assetService.updateAsset(existing.getId(), quantity); // add quantity
        }
        return order;
    }

    // ---------- SELL FLOW ----------
    @Transactional
    @Override
    public Order sellAssest(Bitcoin coin, double quantity, User user) throws Exception {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be > 0");

        // ensure user has the asset
        Asset assetToSell = assetService.findAssetByUserIdAndCoinId(user.getId(), coin.getId());
        if (assetToSell == null) throw new RuntimeException("Asset not found for user");

        if (assetToSell.getQuantity() < quantity) throw new RuntimeException("Insufficient quantity to sell");

        BigDecimal unitPrice = BigDecimal.valueOf(coin.getCurrentPrice());
        BigDecimal total = computeTotalPrice(unitPrice, quantity);

        // create order item and persist
        OrderItem orderItem = new OrderItem();
        orderItem.setCoin(coin);
        orderItem.setQuantity(quantity);
        orderItem.setBuyPrice(assetToSell.getBuyPrice()); // previous buy price, keep as double if model requires
        orderItem.setSellPrice(unitPrice.doubleValue());
        orderItem = persistOrderItem(orderItem);

        // create and persist order
        Order order = createOrderEntity(user, orderItem, OrderType.SELL, total);

        // link item -> order and persist
        orderItem.setOrder(order);
        orderItemRepository.save(orderItem);

        // credit the user's wallet for SELL (walletService.payOrderPayment should handle credit for SELL)
        walletService.payOrderPayment(order, user);

        // update or delete asset
        Asset updated = assetService.updateAsset(assetToSell.getId(), -quantity); // subtract
        if (updated.getQuantity() <= 0) {
            assetService.deleteAsset(updated.getId());
        }

        // mark success and save
        order.setStatus(OrderStatus.SUCCESS);
        order = orderRepository.save(order);

        return order;
    }

    // ---------- PROCESS ORDER ----------
    @Override
    @Transactional
    public Order processOrder(Bitcoin coin, double quantity, OrderType orderType, User user) throws Exception {
        if (orderType == null) throw new IllegalArgumentException("orderType cannot be null");
        if (orderType == OrderType.BUY) {
            return buyAssest(coin, quantity, user);
        } else if (orderType == OrderType.SELL) {
            return sellAssest(coin, quantity, user);
        } else {
            throw new IllegalArgumentException("Unsupported order type: " + orderType);
        }
    }
}
