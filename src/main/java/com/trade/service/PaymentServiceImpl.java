package com.trade.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.trade.domain.PaymentMethod;
import com.trade.domain.PaymentOrderStatus;
import com.trade.extra.PaymentResponse;
import com.trade.extra.SequenceGeneratorService;
import com.trade.modal.PaymentOrder;
import com.trade.modal.User;
import com.trade.repositoy.PaymentOrderRepository;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentOrderRepository paymentOrderRepository;
    private WalletService walletService;
    private SequenceGeneratorService sequenceGeneratorService;
    
    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    @Value("${razorpay.api.secret}")
    private String apiSecretKey;

    @Value("${razorpay.api.key}")
    private String apiKey;

    @Autowired
    public PaymentServiceImpl(PaymentOrderRepository paymentOrderRepository,WalletService walletService,
    		SequenceGeneratorService sequenceGeneratorService) {
        this.paymentOrderRepository = paymentOrderRepository;
        this.walletService = walletService;
        this.sequenceGeneratorService = sequenceGeneratorService;
    }

    // ✅ CREATE PAYMENT ORDER
    @Override
    public PaymentOrder createOrder(User user, Long amount, PaymentMethod method) {

        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setId(sequenceGeneratorService.generateSequence("payment_order_sequence"));
        paymentOrder.setUser(user);
        paymentOrder.setAmount(amount);
        paymentOrder.setPaymentMethod(method);
        paymentOrder.setStatus(PaymentOrderStatus.PENDING);

        return paymentOrderRepository.save(paymentOrder);
    }

    // ✅ GET PAYMENT ORDER BY ID
    @Override
    public PaymentOrder getPaymentOrderById(Long id) {
        return paymentOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No payment found with this id"));
    }

    // ✅ VERIFY & COMPLETE PAYMENT
    @Override
    public Boolean proceedPaymentOrder(PaymentOrder paymentOrder, String paymentId) throws RazorpayException {

        if (!paymentOrder.getStatus().equals(PaymentOrderStatus.PENDING)) {
            return false;
        }

        if (paymentOrder.getPaymentMethod().equals(PaymentMethod.REZORPAY)) {

            RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecretKey);
            Payment payment = razorpay.payments.fetch(paymentId);

            String status = payment.get("status");

            if ("captured".equals(status)) {
                paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
                paymentOrderRepository.save(paymentOrder);
                return true;
            }

            paymentOrder.setStatus(PaymentOrderStatus.FAILED);
            paymentOrderRepository.save(paymentOrder);
            return false;
        }

        // ✅ STRIPE OR OTHER METHODS (AUTO SUCCESS)
        paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
        paymentOrderRepository.save(paymentOrder);
        return true;
    }

    // ✅ CREATE RAZORPAY PAYMENT LINK
    @Override
    public PaymentResponse createRazorPayPaymentLing(User user, Long amount,Long orderId) throws RazorpayException {

        Long amountInPaise = amount * 100;

        RazorpayClient razorPay = new RazorpayClient(apiKey, apiSecretKey);

        JSONObject paymentLinkRequest = new JSONObject();
        paymentLinkRequest.put("amount", amountInPaise);
        paymentLinkRequest.put("currency", "INR");

        JSONObject customer = new JSONObject();
        customer.put("name", user.getFullName());
        customer.put("email", user.getEmail());

        paymentLinkRequest.put("customer", customer);

        JSONObject notify = new JSONObject();
        notify.put("email", true);
        paymentLinkRequest.put("notify", notify);

        paymentLinkRequest.put("reminder_enable", true);
        paymentLinkRequest.put("callback_url", "http://localhost:5173/wallet?order_id=" + orderId);
        paymentLinkRequest.put("callback_method", "get");

        PaymentLink payment = razorPay.paymentLink.create(paymentLinkRequest);

        PaymentResponse response = new PaymentResponse();
        response.setPayment_url(payment.get("short_url"));

        return response;
    }

    // ✅ CREATE STRIPE PAYMENT LINK
    @Override
    public PaymentResponse createStripePayPaymentLing(User user, Long amount, Long orderId) {

        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:5173/wallet?order_id=" + orderId)
                        .setCancelUrl("http://localhost:5173/payment/cancel")
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("usd")
                                                        .setUnitAmount(amount * 100)
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData
                                                                        .builder()
                                                                        .setName("Wallet Topup")
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        try {
            Session session = Session.create(params);

            PaymentResponse response = new PaymentResponse();
            response.setPayment_url(session.getUrl());
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Stripe payment failed: " + e.getMessage());
        }
    }
    
    public void handleRazorpayWebhook(String payload, String signature) throws Exception {

        JSONObject json = new JSONObject(payload);

        if ("payment.captured".equals(json.getString("event"))) {

            JSONObject payment = json.getJSONArray("payload")
                                     .getJSONObject(0)
                                     .getJSONObject("payment")
                                     .getJSONObject("entity");

            Long orderId = payment.getLong("order_id");
            Long amount = payment.getLong("amount") / 100;

            PaymentOrder order = paymentOrderRepository.findById(orderId).orElseThrow();

            if (!order.getStatus().equals("SUCCESS")) {
                order.setStatus(PaymentOrderStatus.SUCCESS);
                paymentOrderRepository.save(order);

                walletService.addBalance(
                    walletService.getUserWallet(order.getUser()),
                    amount
                );
            }
        }
    }

}

