package com.naveenk.Shopify.Service;


import com.naveenk.Shopify.DTO.ModelDto.PaymentDto;
import com.naveenk.Shopify.DTO.ModelDto.ProductDto;
import com.naveenk.Shopify.DTO.ModelDto.TransactionDto;
import com.naveenk.Shopify.DTO.OrderSuccessDto.OrderPlacedDto;
import com.naveenk.Shopify.Enum.Coupon;
import com.naveenk.Shopify.Exceptions.InvalidCouponException;
import com.naveenk.Shopify.Exceptions.InvalidQuantityException;
import com.naveenk.Shopify.Exceptions.OrderNotFoundException;
import com.naveenk.Shopify.Exceptions.UserNotFoundException;
import com.naveenk.Shopify.Model.Order;
import com.naveenk.Shopify.Model.Transaction;
import com.naveenk.Shopify.Model.User;
import com.naveenk.Shopify.Repository.OrderRepository;
import com.naveenk.Shopify.Repository.TransactionRepository;
import com.naveenk.Shopify.Repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Log4j2
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProductService productService;

    @Autowired
    TransactionRepository transactionRepository;

    public Order verifyUserAndOrder(int userId, int orderId) throws UserNotFoundException, OrderNotFoundException {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) throw new UserNotFoundException("Invalid user id");
        User user = userOptional.get();
        List<Order> orderList = user.getOrderList();
        Order order = null;
        for (Order currOrder : orderList) {
            // in the order list of user we are checking whether the specific order is there
            // if not then that means user with given user id has not made this order
            if (currOrder.getOrderId() == orderId) {
                order = currOrder;
                break;
            }
        }
        if (Objects.isNull(order)) throw new OrderNotFoundException("Invalid order id");
        return order;
    }

    public ProductDto inventory() {

        return new ProductDto(
                productService.getOrdered(),
                productService.getPrice(),
                productService.getAvailable());
    }


    public OrderPlacedDto placeOrderWithoutCoupon(int userId, int quantity) {

        int remainingQuantity = productService.getAvailable();
        if (remainingQuantity < quantity) {
            throw new InvalidQuantityException("Invalid quantity");
        }
        Optional<User> userOptional = userRepository.findById(userId);
        User user = null;
        // if user is not already present we create a new user
        if (userOptional.isEmpty()) {
            user = new User();
            user.setUserId(userId);
        } else {
            user = userOptional.get();
        }

        // reset total quantity
        // reset total orders
        productService.setOrdered(productService.getOrdered() + quantity);
        productService.setAvailable(productService.getAvailable() - quantity);


        // save and build connections between tables
        // here we use bidirectional mapping
        // with cascading to persist data

        user = userRepository.save(user);

        Order order = new Order();
        int totAmount = quantity * productService.getPrice();
        order.setAmount(totAmount);
        order.setCoupon(Coupon.NA);
        order.setUser(user);
        order = orderRepository.save(order);

        user.getOrderList().add(order);
        userRepository.save(user);

        return new OrderPlacedDto(order.getOrderId(), user.getUserId(), quantity, totAmount, "NA");
    }

    public OrderPlacedDto placeOrder(int userId, int quantity, String coupon) throws InvalidCouponException {
        String lowercaseCoupon = coupon.trim().toLowerCase();

        if (!("off5".equals(lowercaseCoupon) || "off10".equals(lowercaseCoupon))) {
            throw new InvalidCouponException("Invalid coupon");
        }

        int remainingQuantity = productService.getAvailable();
        if (remainingQuantity < quantity) {
            throw new InvalidQuantityException("Invalid quantity");
        }

        Optional<User> userOptional = userRepository.findById(userId);
        // if user is not already present we create a new user
        User user = null;
        if (userOptional.isEmpty()) {
            user = new User();
            user.setUserId(userId);
        } else {
            user = userOptional.get();
            if ((user.isOffFiveUsed() && lowercaseCoupon.equals("off5")) || (lowercaseCoupon.equals("off10") && user.isOffTenUsed())) {
                throw new InvalidCouponException("Invalid coupon");
            }
        }
        // reset total quantity
        // reset total orders
        productService.setOrdered(productService.getOrdered() + quantity);
        productService.setAvailable(productService.getAvailable() - quantity);

        if (lowercaseCoupon.equals("off5")) user.setOffFiveUsed(true);
        if (lowercaseCoupon.equals("off10")) user.setOffTenUsed(true);

        // save and build connections between tables

        user = userRepository.save(user);

        Order order = new Order();
        // five percent discount = totPrice - (5/100)*totPrice
        // which is equal to totPrice - totPrice/20 ;
        // likewise for ten percent = totPrice - totPrice/10 ;
        int totAmount = lowercaseCoupon.equals("off5") ? (quantity * productService.getPrice()) - ((quantity * productService.getPrice()) / 20) : (quantity * productService.getPrice()) - ((quantity * productService.getPrice()) / 10);
        order.setAmount(totAmount);
        order.setUser(user);
        order.setCoupon(lowercaseCoupon.equals("off5") ? Coupon.OFF5 : Coupon.OFF10);
        order = orderRepository.save(order);

        user.getOrderList().add(order);
        userRepository.save(user);

        return new OrderPlacedDto(order.getOrderId(), user.getUserId(), quantity, totAmount, coupon.toUpperCase());
    }

    public PaymentDto payForOrder(int userId, int orderId, int amount) throws UserNotFoundException, OrderNotFoundException {
        Order order = verifyUserAndOrder(userId, orderId);
        Transaction transaction = new Transaction();

        // random is generated to mock internal server error
        // here they are bank error and payment server error
        Random random = new Random();
        int randomNumber = random.nextInt(100) + 1;

        if (order.isCompleted() || order.getAmount() > amount || randomNumber == 55 || randomNumber == 78) {
            transaction.setStatus("failed");
            transaction.setOrder(order);
            transaction = transactionRepository.save(transaction);
            order.getTransactions().add(transaction);
            orderRepository.save(order);
            if (order.isCompleted()) {
                return new PaymentDto(userId, orderId, transaction.getTransactionId(), transaction.getStatus(), "Order is already paid for");
            }
            if (order.getAmount() > amount) {
                return new PaymentDto(userId, orderId, transaction.getTransactionId(), transaction.getStatus(), "Payment Failed as amount is invalid");
            }
            if (randomNumber == 55) {
                return new PaymentDto(userId, orderId, transaction.getTransactionId(), transaction.getStatus(), "Payment Failed from bank");
            }
            return new PaymentDto(userId, orderId, transaction.getTransactionId(), transaction.getStatus(), "No response from payment server");
        } else {
            transaction.setStatus("success");
            transaction.setOrder(order);
            transaction = transactionRepository.save(transaction);
            order.getTransactions().add(transaction);
            order.setCompleted(true);
            orderRepository.save(order);
            return new PaymentDto(userId, orderId, transaction.getTransactionId(), transaction.getStatus(), "Payment successful");
        }
    }

    public List<Order> fetchOrders(int userId) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) throw new UserNotFoundException("Invalid user id");
        User user = userOptional.get();
        List<Order> orderList = user.getOrderList();
        return orderList;
    }

    public List<TransactionDto> fetchTransactions(int userId, int orderId) throws UserNotFoundException, OrderNotFoundException {
        Order order = verifyUserAndOrder(userId, orderId);
        List<Transaction> transactionList = order.getTransactions();
        List<TransactionDto> allTransactions = new ArrayList<>();
        // go through each transaction
        // create TransactionDto
        // append them to allTransactions
        for (Transaction transaction : transactionList) {
            TransactionDto transactionDto = new TransactionDto(orderId, order.getAmount(), order.getOrderDate(), order.getCoupon().toString(), transaction.getTransactionId(), transaction.getStatus());
            allTransactions.add(transactionDto);
        }
        return allTransactions;
    }

}

