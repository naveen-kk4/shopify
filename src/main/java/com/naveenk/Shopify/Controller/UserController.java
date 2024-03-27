package com.naveenk.Shopify.Controller;


import com.naveenk.Shopify.DTO.ModelDto.CouponDto;
import com.naveenk.Shopify.DTO.ModelDto.PaymentDto;
import com.naveenk.Shopify.DTO.ModelDto.ProductDto;
import com.naveenk.Shopify.DTO.ModelDto.TransactionDto;
import com.naveenk.Shopify.DTO.OrderErrorDto.InvalidParameter;
import com.naveenk.Shopify.DTO.OrderSuccessDto.OrderPlacedDto;
import com.naveenk.Shopify.Exceptions.InvalidCouponException;
import com.naveenk.Shopify.Exceptions.InvalidQuantityException;
import com.naveenk.Shopify.Exceptions.OrderNotFoundException;
import com.naveenk.Shopify.Exceptions.UserNotFoundException;
import com.naveenk.Shopify.Model.Order;
import com.naveenk.Shopify.Service.ProductService;
import com.naveenk.Shopify.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("fetchCoupons")
    public ResponseEntity<CouponDto> fetchCoupons() {
        return new ResponseEntity<>(new CouponDto(5,10), HttpStatus.OK);
    }

    @GetMapping("/inventory")
    public ResponseEntity<ProductDto> inventory() {

        ProductDto productDto = userService.inventory();
        return new ResponseEntity<>(productDto,HttpStatus.OK);

    }

    @PostMapping("/{userId}/order")
    public ResponseEntity<?> placeOrder(
            @PathVariable int userId,
            @RequestParam("qty") int quantity,
            @RequestParam("coupon") String coupon
    ) {
        try {
            OrderPlacedDto orderPlacedDto =  userService.placeOrder(userId,quantity,coupon);
            return new ResponseEntity<>(orderPlacedDto,HttpStatus.CREATED);
        } catch(InvalidCouponException e) {
            return new ResponseEntity<>(new InvalidParameter(e.getMessage()),HttpStatus.BAD_REQUEST);
        } catch (InvalidQuantityException e) {
            return new ResponseEntity<>(new InvalidParameter(e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{userId}/order/{qty}")
    public ResponseEntity<?> placeOrderWithoutCoupon(
            @PathVariable int userId,
            @PathVariable("qty") int quantity
    ) {
        try {
            OrderPlacedDto orderPlacedDto =  userService.placeOrderWithoutCoupon(userId,quantity);
            return new ResponseEntity<>(orderPlacedDto,HttpStatus.CREATED);
        } catch(InvalidCouponException e) {
            return new ResponseEntity<>(new InvalidParameter(e.getMessage()),HttpStatus.BAD_REQUEST);
        } catch (InvalidQuantityException e) {
            return new ResponseEntity<>(new InvalidParameter(e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{userId}/{orderId}/pay")
    public ResponseEntity<?> payForOrder(
            @PathVariable int userId,
            @PathVariable int orderId,
            @RequestParam int amount
    ) {
        try {
            PaymentDto paymentDto = userService.payForOrder(userId,orderId,amount);

            if(paymentDto.getStatus().equals("success")) {
                return new ResponseEntity<>(paymentDto,HttpStatus.OK);
            }
            else {
                if(paymentDto.getDescription().equals("Payment Failed as amount is invalid") ||
                        paymentDto.getDescription().equals("Order is already paid for")) {
                    return new ResponseEntity<>(paymentDto,HttpStatus.BAD_REQUEST);
                }
                else {
                    // internal server error
                    return new ResponseEntity<>(paymentDto,HttpStatus.GATEWAY_TIMEOUT);
                }
            }
        }
        catch(UserNotFoundException e) {
            return new ResponseEntity<>(new InvalidParameter(e.getMessage()),HttpStatus.BAD_REQUEST);
        }
        catch (OrderNotFoundException e) {
            return new ResponseEntity<>(new InvalidParameter(e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<?> fetchOrders(@PathVariable int userId) {
        try {
            List<Order> orderList = userService.fetchOrders(userId);
            return new ResponseEntity<>(orderList,HttpStatus.OK);
        }
        catch(UserNotFoundException e) {
            return new ResponseEntity<>(new InvalidParameter(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{userId}/orders/{orderId}")
    public ResponseEntity<?> fetchTransactions(
            @PathVariable int userId,
            @PathVariable int orderId
    ) {
        try {
            List<TransactionDto> transactions = userService.fetchTransactions(userId,orderId);
            return new ResponseEntity<>(transactions,HttpStatus.OK);
        }
        catch(UserNotFoundException e) {
            return new ResponseEntity<>(new InvalidParameter(e.getMessage()),HttpStatus.BAD_REQUEST);
        }
        catch(OrderNotFoundException e) {
            return new ResponseEntity<>(new InvalidParameter(e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }
}
